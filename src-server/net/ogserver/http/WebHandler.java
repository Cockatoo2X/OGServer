package net.ogserver.http;

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
