/*
 * Created on May 13, 2007
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

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.moneybender.proxy.publishers.PacketHeader;
import com.moneybender.proxy.publishers.PacketIdentifier;

abstract class AbstractSubscriber implements ISubscribeToPacketData {
	
	private static Logger log = Logger.getLogger(AbstractSubscriber.class);

	private Socket socket;
	private ObjectInputStream ois;

	private PrintStream printStream;

	public void start(SubscriberSettings settings, OutputStream out) throws IOException {

		printStream = new PrintStream(out);
		printStream.println(this.getClass().getSimpleName() + " starting...");

		socket = new Socket(settings.getProxyHost(), settings.getProxyPort());
		ois = new ObjectInputStream(socket.getInputStream());
		while (true) {
			try {

				PacketHeader header = new PacketHeader();
				header.readExternal(ois);

				if (log.isDebugEnabled())
					log.debug(header.getSourceType() + " packet "
							+ header.getPacketIdentifier() + " of "
							+ header.getDataLength() + " bytes.");

				if (header.getPacketIdentifier().getPacketID() == PacketIdentifier.SESSION_CLOSE) {
					processEndPacket(header, out);
					synchronized (out) {
						out.notifyAll();
					}
				} else {
					// The Proxy shouldn't send empty packets, but
					// double-checking here.
					if (header.getDataLength() > 0) {
						byte[] buffer = getPayload(header, ois);
						processPacket(header, buffer, out);
						synchronized (out) {
							out.notifyAll();
						}
					}
				}

			} catch (EOFException e) {
				handleShutdown(out);
				break;
			} catch (IOException e) {
				if (e.getMessage() != null
				&& (e.getMessage().equals("Connection reset") || e.getMessage().equalsIgnoreCase("socket closed"))) {
					handleShutdown(out);
				} else {
					printError(e);
				}
				break;
			} catch (ClassNotFoundException e) {
				printError(e);
				break;
			}
		}
	}

	protected void handleShutdown(OutputStream out) {
		printStream.println(this.getClass().getSimpleName() + " stopped.");
	}

	private void printError(Exception e) {
		if (e.getMessage() == null)
			printStream.println("Error reading packet: " + e);
		else
			printStream.println("Error reading packet: " + e.getMessage());
	}

	protected byte[] getPayload(PacketHeader header, InputStream in)
			throws IOException {
		// Copy exactly the number of bytes claimed by the header...
		byte[] buffer = new byte[header.getDataLength()];
		new DataInputStream(in).readFully(buffer);
		return buffer;
	}

	abstract protected void processPacket(PacketHeader header, byte[] payload,
			OutputStream out) throws IOException;

	protected void processEndPacket(PacketHeader header, OutputStream out)
			throws IOException {

	}

	public void stop() {
		try {
			if (socket != null)
				socket.close();
		} catch (IOException ignore) {
		}
	}

	public PrintStream getPrintStream() {
		return printStream;
	}

}
