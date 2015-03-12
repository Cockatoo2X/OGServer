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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class WebHandler implements HttpHandler {

	private static String EOL = "<br>";
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String requestType = exchange.getRequestMethod();
		if(requestType.equalsIgnoreCase("GET")) {
			Headers  responseHeaders = exchange.getResponseHeaders();
			responseHeaders.set("Content-Type", "text/html");
			exchange.sendResponseHeaders(200, 0);
			OutputStream responseBody = exchange.getResponseBody();
			
			File index = new File("http/index.html");
			byte[] indexBytes = new byte[(int)index.length()];
			FileInputStream fileInputStream = new FileInputStream(index);
			fileInputStream.read(indexBytes);
			responseBody.write(indexBytes);
			responseBody.close();
			fileInputStream.close();
			
		} else if(requestType.equalsIgnoreCase("POST")) {
			Headers  responseHeaders = exchange.getResponseHeaders();
			responseHeaders.set("Content-Type", "text/html");
			exchange.sendResponseHeaders(200, 0);
			OutputStream responseBody = exchange.getResponseBody();
			responseBody.write(
				(WebServer.currentBytesIn + EOL
					+ WebServer.currentBytesOut + EOL
					+ WebServer.averageBytesIn + EOL
					+ WebServer.averageBytesOut).getBytes()
			);
			responseBody.close();
		}
	}
	
}
