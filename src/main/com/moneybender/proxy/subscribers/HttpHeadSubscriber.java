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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import com.moneybender.proxy.publishers.PacketHeader;

public class HttpHeadSubscriber extends AbstractHttpSubscriber {

	public static void main(String[] args) {
		Thread.currentThread().setName("DP-" + HttpHeadSubscriber.class.getSimpleName());
		HttpHeadSubscriber subscriber = new HttpHeadSubscriber();
		try {
			subscriber.start(new SubscriberSettings(), System.out);
		} catch (IOException e) {
			System.out.println("Could not start HttpHeadSubscriber: " + e.getMessage());
		}
	}

	@Override
	protected void processPacket(PacketHeader header, byte[] payload, OutputStream out) throws IOException {

		if (hasKnownCommand(payload)) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(payload)));
			String line = reader.readLine();

			PrintStream printStream = new PrintStream(out);
			
			printStream.println("Sender is " + header.getSourceType() + " at " + header.getSenderIP() + ":" + header.getSenderPort());
			printStream.println("Packet ID:" + header.getPacketIdentifier());
			
			while (line != null) {
				if (line.trim().length() == 0) // Look for end of headers
					break;
				printStream.println(line);
				line = reader.readLine();
			}
			printStream.println();
			printStream.flush();
		}

	}

}
