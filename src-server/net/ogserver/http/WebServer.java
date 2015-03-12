package net.ogserver.http;

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