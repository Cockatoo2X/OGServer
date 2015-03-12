package net.ogserver.http;

/*
* Copyright (c) 2015
* Christian Tucker.  All rights reserved.
*
* The use of OGServer is free of charge for personal and commercial use. *
*
* THIS SOFTWARE IS PROVIDED 'AS IS' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
* BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
* PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE DISCLAIMED.  
* IN NO EVENT SHALL THE AUTHOR  BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
* THE POSSIBILITY OF SUCH DAMAGE.
*  
*   * Policy subject to change.
*/

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.ogserver.common.Session;

import com.sun.net.httpserver.HttpServer;

public class WebServer implements Runnable {

	private final static ScheduledExecutorService serverTick = Executors.newScheduledThreadPool(1);
			
	private HttpServer server;
	
	public WebServer() {
		try {
			server = HttpServer.create(new InetSocketAddress(8080), 0);
			server.createContext("/", new WebHandler());
			server.setExecutor(Executors.newCachedThreadPool());
			server.start();
			System.out.println("Webserver available on http://localhost:8080/");
			serverTick.scheduleAtFixedRate(this, 0, 1000, TimeUnit.MILLISECONDS);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	private int ticks;
	
	public static String currentBytesIn;
	public static String averageBytesIn;
	public static String currentBytesOut;
	public static String averageBytesOut;
	
	public void run() {
		try {
			ticks++;
			currentBytesIn = "TCP Current input/second = " + Session.bytesInCurrent + "b/s";
			averageBytesIn = "TCP Average input/second = " + new DecimalFormat("##.##").format((float)Session.bytesIn / ticks) + "b/s";
			currentBytesOut = "TCP Current output/second = " + Session.bytesOutCurrent + "b/s";
			averageBytesOut = "TCP Average output/second = " + new DecimalFormat("##.##").format((float)Session.bytesOut / ticks) + "b/s";
			Session.bytesInCurrent = 0;
			Session.bytesOutCurrent = 0;
		} catch(Exception e) {
			e.printStackTrace();
			serverTick.shutdown();
		}
	}
	
	public HttpServer getServer() {
		return server;
	}
	
	public static void main(String[] args) {
		new WebServer(); 
	}
}