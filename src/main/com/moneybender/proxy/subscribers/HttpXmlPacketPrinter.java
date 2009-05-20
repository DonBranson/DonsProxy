/*
 * Created on Jan 27, 2008
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

package com.moneybender.proxy.subscribers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.moneybender.proxy.util.HexOutputStream;

class HttpXmlPacketPrinter {
	
	private final HttpParser httpParser;
	private final PrintStream printStream;
	private Packet previousPacket;
	
	private enum ScanState {
		SEEKINGHEADERS, INHEADERS, FOUNDPAYLOAD, INPAYLOAD
	};
	
	private ScanState state;

	protected HttpXmlPacketPrinter(PrintStream printStream) {
		this.httpParser = new HttpParser();
		this.printStream = printStream;
		this.previousPacket = null;
		
		this.state = ScanState.SEEKINGHEADERS;
	}

	protected void printPacket(int requestCount, Packet packet) throws IOException {

		try {
			if(previousPacket != null && packet.getSourceType() != previousPacket.getSourceType()) {
				printPacketEnd(previousPacket);
				state = ScanState.SEEKINGHEADERS;
			}

			if(state == ScanState.FOUNDPAYLOAD) {
				printPayloadStart(packet);
				state = ScanState.INPAYLOAD;
			}

			// N.B.:  This reader gets side-effected.
			BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet.getPayload())));

			if(previousPacket == null || packet.getSourceType() != previousPacket.getSourceType()) {
				state = ScanState.INHEADERS;
				if(httpParser.hasKnownCommand(packet.getPayload())) {
					printPacketStart(reader, requestCount, packet);
				}
			}
			
			if(state == ScanState.INHEADERS) {
				state = printHeaders(reader, requestCount);
			}
			
			if(state == ScanState.FOUNDPAYLOAD || state == ScanState.INPAYLOAD) {
				printPayload(reader, packet);
			}

		} finally {
			previousPacket = packet;
		}
	}

	private void printPayload(BufferedReader reader, Packet packet) throws IOException {

		String line = reader.readLine();
		if(line == null)
			return;
		
		if(state == ScanState.FOUNDPAYLOAD) {
			printPayloadStart(packet);
		}
		
		if(packet.getSourceType().isClient()) {
			printRequestPayload(reader, line);
		} else {
			printResponsePayload(packet);
		}

		state = ScanState.INPAYLOAD;
	}
	
	private void printPayloadStart(Packet packet) {
		if(packet.getSourceType().isClient()) {
			printStream.println("\t\t\t<data>");
		} else {
			printStream.println("\t\t\t<data type=\"hex\">");
		}
	}
	
	protected void printResponsePayload(Packet packet) throws IOException {
		ByteArrayInputStream bytesIn = new ByteArrayInputStream(packet.getPayload());
		BufferedReader reader = new BufferedReader(new InputStreamReader(bytesIn));
		
		if(state == ScanState.FOUNDPAYLOAD) {
			
			String line;
			while((line = reader.readLine()) != null){
				if(line.trim().length() == 0) {
					break;
				}
			}
		}
		
		hexPrint(reader);
		printStream.println();

		state = ScanState.INPAYLOAD;
	}

	private void hexPrint(BufferedReader reader) throws IOException {
		StringWriter stringWriter = new StringWriter();
		HexOutputStream hexOutputStream = new HexOutputStream(stringWriter);
		int c;
		while((c = reader.read()) != -1) {
			hexOutputStream.write(c);
		}
		
		printStream.print(stringWriter.getBuffer());
	}

	private void printRequestPayload(BufferedReader reader, String line) throws IOException {
		while(line != null) {
			StringTokenizer st = new StringTokenizer(line, "&");
			while(st.hasMoreTokens()) {
				String token = st.nextToken();
				int equalsPosition = token.indexOf('=');
				String name = token.substring(0, equalsPosition).trim();
				String value = token.substring(equalsPosition + 1).trim();
				printHttpHeader(name, value);
			}
			
			line = reader.readLine();
		}
	}

	private void printHttpHeader(String name, String value) {
		
		printStream.print("\t\t\t\t<header name=\"" + name + "\">");
		printStream.println("<![CDATA[" + value + "]]></header>");
	}

	private void printPacketStart(BufferedReader reader, int requestCount, Packet packet) throws IOException {
		Iterator<String> tokens = httpParser.getFirstLineTokens(reader.readLine()).iterator();

		if(packet.getSourceType().isClient()) {
			printStream.print("\t\t<request count=\"" + requestCount + "\" timestamp=\"" + packet.getHeader().getTimestamp() + "\"");
			printStream.print(" command=\"" + tokens.next() + "\"");
			printStream.print(" url=\"" + tokens.next().replaceAll("&", "&amp;") + "\"");
			printStream.println(" protocol=\"" + tokens.next() + "\">");
		} else {
			printStream.print("\t\t<response count=\"" + requestCount + "\" timestamp=\"" + packet.getHeader().getTimestamp() + "\"");
			printStream.print(" protocol=\"" + tokens.next() + "\"");
			printStream.print(" code=\"" + tokens.next() + "\"");
			printStream.println(" message=\"" + buildMessage(tokens) + "\">");
		}
	}

	protected ScanState printHeaders(BufferedReader reader, int requestCount) throws IOException {
		String line;
		while((line = reader.readLine()) != null){
			if(line.trim().length() == 0) {
				return ScanState.FOUNDPAYLOAD;
			}
			int colonPosition = line.indexOf(':');
			String name = "";
			String value = null;
			if(colonPosition == -1) {
				name = line.trim();
			} else {
				name = line.substring(0, colonPosition).trim();
				value = line.substring(colonPosition + 1).trim();
			}
			// FIXME: Go to elements instead of attributes, so that embedded quotes are not an issue.
			printStream.print("\t\t\t\t<header name=\"" + name + "\">");
			if(value != null) {
				printStream.print("<![CDATA[" + value + "]]>");
			}
			printStream.println("</header>");
		}
		
		return ScanState.INHEADERS;
	}

	private String buildMessage(Iterator<String> tokens) {
		String message = "";
		while(tokens.hasNext()) {
			message += tokens.next() + " ";
		}
		message = message.trim();
		return message;
	}

	protected void printPacketEnd() throws IOException {
		if(previousPacket != null) {
			printPacketEnd(previousPacket);
		}
	}
	
	private void printPacketEnd(Packet packet) throws IOException {
		if(state == ScanState.INPAYLOAD) {
			printStream.println("\t\t\t</data>");
		}
		if(packet.getSourceType().isClient())
			printStream.println("\t\t</request>");
		else
			printStream.println("\t\t</response>");
	}

}
