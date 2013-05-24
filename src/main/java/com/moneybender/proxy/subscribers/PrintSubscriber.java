/*
 * Created on October 17, 2007
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import com.moneybender.proxy.publishers.PacketHeader;

public class PrintSubscriber extends AbstractSubscriber {

	public static void main(String[] args) {
		Thread.currentThread().setName("DP-PrintSubscriber");

		PrintSubscriber subscriber = new PrintSubscriber();
		try {
			subscriber.start(new SubscriberSettings(), System.out);
		} catch (IOException e) {
			System.out.println("Could not start PrintSubscriber: " + e.getMessage());
		}
	}

	@Override
	protected void processPacket(PacketHeader header, byte[] buffer, OutputStream out) throws IOException {

		PrintStream printStream = new PrintStream(out);
		printStream.println();
		printStream.println("Sender is " + header.getSourceType() + " at " + header.getSenderIP() + ":" + header.getSenderPort());
		printStream.println("Packet ID:" + header.getPacketIdentifier());

		printStream.write(buffer);
		printStream.println();
		printStream.flush();
	}
}
