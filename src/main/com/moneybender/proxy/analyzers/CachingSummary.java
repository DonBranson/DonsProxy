/*
 * Created on Mar 3, 2008
 *
 * Copyright (c), 2008 Don Branson.  All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.moneybender.proxy.analyzers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

public class CachingSummary extends HttpXmlHandler {

	protected static Logger log = Logger.getLogger(CachingSummary.class);
	
	private Map<String, RequestInfo> requestInfo;
	private RequestInfo currentRequest;
	private String currentName;
	private long currentRequestTime;
	
	public static void main(String[] args) {
		try {
			CachingSummary analyzer = new CachingSummary();
			analyzer.run(args[0]);
			analyzer.dumpCacheInfo(args[1]);
			log.info("Analyzed " + args[0] + " and left results in " + args[1]);
			
		} catch(SAXParseException e) {
			log.error("Error on line " + e.getLineNumber() + ", column " + e.getColumnNumber() + ":" + e.getMessage());
		} catch (IOException e) {
			log.error("Error parsing file: " + e.getMessage());
		} catch (Exception e) {
			log.error("Error parsing file: " + e.getMessage(), e);
		}

	}

	@Override
	protected void run(String inputFile) throws Exception {
		
		requestInfo = new TreeMap<String, RequestInfo>();
		
		super.run(inputFile);
		
	}
	
	@Override
	public void startRequest(Attributes attributes) {
		String normalizedUri = attributes.getValue("url").replaceAll("[?].*$", "");
		currentRequest = requestInfo.get(normalizedUri);
		if(currentRequest == null)
			currentRequest = new RequestInfo(normalizedUri);
		currentRequestTime = Long.parseLong(attributes.getValue("timestamp"));
	}
	
	@Override
	public void startResponse(Attributes attributes) {
		currentRequest.setResponseTime(Long.parseLong(attributes.getValue("timestamp")) - currentRequestTime);
	}
	
	@Override
	public void startHeader(Attributes attributes) {
		currentName = attributes.getValue("name");
	}
	
	@Override
	public void endRequest() {
	}
	
	@Override
	public void endResponse() {
		requestInfo.put(currentRequest.getRequest(), currentRequest);
	}
	
	@Override
	public void endHeader(String value) {

		if(currentName != null && currentName.equals("Cache-Control")) {
			currentRequest.setCachingPolicy(value);
		}
		if(currentName != null && currentName.equals("Content-Type")) {
			currentRequest.setContentType(value);
		}
	}
	
	private void dumpCacheInfo(String outFile) throws IOException {
		
		PrintStream out = new PrintStream(new FileOutputStream(outFile));
		
		out.println("<html>");
		out.println("<table style=\"text-align: left;\" border=\"1\"  cellpadding=\"5\" cellspacing=\"0\">");
		out.println("<tr><th>Content Type</th><th>Caching Policy</th><th>Request<br>Count</th><th>Avg.<br>Response Time<br>(millis)</th><th>Request</th></tr>");
		for (Iterator<String> iterator = requestInfo.keySet().iterator(); iterator.hasNext();) {
			String request = iterator.next();
			RequestInfo info = requestInfo.get(request);
			out.print("<tr style=\"vertical-align: top; white-space:nowrap;\">");

			String contentType = info.getContentType();
			if(contentType == null){
				contentType = "&nbsp";
			}
			out.print("<td>" + contentType + "</td>");

			String cachingPolicy = info.getCachingPolicy();
			if(cachingPolicy == null){
				cachingPolicy = "&nbsp";
			}
			out.print("<td>" + cachingPolicy + "</td>");

			out.print("<td>" + info.getRequestCount() + "</td>");

			out.print("<td style=\"text-align:right\">" + info.getAverageResponseTime() + "</td>");

			out.print("<td>" + info.getRequest() + "</td>");
			
			out.println("</tr>");
		}
		out.println("</table>");
		out.close();
	}

	@Override
	public void endData() {
	}

	@Override
	public void startData(Attributes attributes) {
	}

}
