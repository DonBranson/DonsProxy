/*
 * Created on Dec 1, 2007
 * 
 * Copyright (c), 2007 Don Branson.  All Rights Reserved.
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.moneybender.proxy.publishers.PacketHeader;

public class HttpXmlSubscriber extends AbstractHttpSubscriber {

	private static Logger log = Logger.getLogger(HttpXmlSubscriber.class);

	private enum Direction {
		REQUEST, RESPONSE
	};

	private Map<Integer, PacketStream> streams;
	private PrintStream xmlOutputStream;

	public static void main(String[] args) {
		Thread.currentThread().setName("DP-" + HttpXmlSubscriber.class.getSimpleName());

		String outFileName = HttpXmlSubscriber.class.getSimpleName() + ".xml";
		System.out.println("Writing XML output to " + outFileName);
		try {
			HttpXmlSubscriber subscriber = new HttpXmlSubscriber(new PrintStream(new FileOutputStream(outFileName)));
			subscriber.start(new SubscriberSettings(), System.out);
		} catch (FileNotFoundException e) {
			log.error("Error writing to '" + outFileName + "': " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Could not start HttpXmlSubscriber: " + e.getMessage());
		}
	}

	private HttpXmlSubscriber() {
	}

	public HttpXmlSubscriber(PrintStream xmlOutputStream) {
		this.xmlOutputStream = xmlOutputStream;
	}

	@Override
	public void start(SubscriberSettings settings, final OutputStream out) throws IOException {

		writeXmlHeader();
		streams = new HashMap<Integer, PacketStream>();
		super.start(settings, out);
	}

	@Override
	protected void processPacket(PacketHeader header, byte[] buffer, OutputStream out) throws IOException {

		int sessionID = header.getPacketIdentifier().getSessionID();
		if (log.isDebugEnabled()) {
			if (header.getPacketIdentifier().getPacketID() == 1 && header.getSourceType().isClient())
				log.debug("Session " + sessionID + " started.");
		}

		PacketStream stream = streams.get(sessionID);
		if (stream == null) {
			stream = new PacketStream();
			streams.put(sessionID, stream);
		}
		stream.savePacket(header, buffer);
	}

	@Override
	protected void processEndPacket(PacketHeader header, OutputStream out) throws IOException {
		int sessionID = header.getPacketIdentifier().getSessionID();
		printSession(sessionID, out);
		streams.remove(sessionID);
	}

	private void printSession(int sessionId, OutputStream out) throws IOException {

		Direction currentDirection = Direction.RESPONSE;

		PacketStream packetStream = streams.get(sessionId);
		if (packetStream != null) {
			xmlOutputStream.println("\t<session id=\"" + sessionId + "\">");

			HttpXmlPacketPrinter packetPrinter = new HttpXmlPacketPrinter(xmlOutputStream);
			int requestCount = 0;
			for (Packet packet : packetStream.getStream()) {

				packetPrinter.printPacket(requestCount, packet);

				Direction newDirection = getDirection(packet);
				if (newDirection == Direction.RESPONSE && currentDirection == Direction.REQUEST)
					++requestCount;
				currentDirection = newDirection;
			}
			packetPrinter.printPacketEnd();

			xmlOutputStream.println("\t</session>");
			xmlOutputStream.flush();
			getPrintStream().println("Recorded session " + sessionId);
		}
	}

	private Direction getDirection(Packet packet) {
		if (packet.getHeader().getSourceType().isServer())
			return Direction.RESPONSE;
		return Direction.REQUEST;
	}

	private void writeXmlHeader() {
		xmlOutputStream.println("<?xml version=\"1.0\"?>");
		xmlOutputStream.println("<sessions>");
		xmlOutputStream.flush();
	}

	@Override
	public void handleShutdown(OutputStream out) {
		super.handleShutdown(out);

		completeOutput(out);
		xmlOutputStream.println("</sessions>");
	}

	private void completeOutput(OutputStream out) {
		Set<Integer> sessionId = streams.keySet();
		for (Iterator<Integer> iterator = sessionId.iterator(); iterator.hasNext();) {
			try {
				printSession(iterator.next(), out);
			} catch (IOException e) {
				getPrintStream().println("Error printing session data:" + e.getMessage());
			}
		}
	}
}
