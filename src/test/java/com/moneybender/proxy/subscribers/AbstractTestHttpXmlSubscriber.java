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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.moneybender.proxy.ProxySettings;
import com.moneybender.proxy.publishers.PacketHeader;
import com.moneybender.proxy.publishers.PacketIdentifier;
import com.moneybender.proxy.publishers.PacketSourceType;
import com.moneybender.proxy.publishers.SubscriberManager;

abstract public class AbstractTestHttpXmlSubscriber {

	private SubscriberManager manager;
	private PacketHeader header;
	private PacketIdentifier packetIdentifier;

	private ByteArrayOutputStream baos;
	private boolean sawException = false;

	@Before
	public void setUp() throws Exception {
		header = new PacketHeader();

		packetIdentifier = new PacketIdentifier(543);
		header.setSourceType(PacketSourceType.CLIENT);
		header.setPacketID(packetIdentifier);
		header.setDataLength(101);
		header.setSenderIP("10.5.128.3");
		header.setSenderPort(2003);
		header.setTimestamp(-2);

		manager = new SubscriberManager(ProxySettings.DEFAULT_REQUEST_PORT,
			ProxySettings.DEFAULT_RESPONSE_PORT, ProxySettings.DEFAULT_DUPLEX_PORT);

		baos = startSubscriber();
		Thread.sleep(500);	// Give the subscriber thread time to connect and be ready.
	}
	
	@After
	public void tearDown() throws Exception {
		if(manager != null){
			manager.close();
		}
		Thread.sleep(250);	// Give stuff some time to shut down.
	}

	protected String getOutput() {
		return baos.toString();
	}

	protected SubscriberManager getManager() {
		return manager;
	}

	protected PacketHeader getHeader() {
		return header;
	}

	protected PacketIdentifier getPacketIdentifier() {
		return packetIdentifier;
	}

	protected boolean didSeeException() {
		return sawException;
	}
	
	protected void validateGetRequestHeaders(BufferedReader reader) throws IOException {
		String line;
		for (int i = 0; i < new HttpTestDataFactory().getGetRequestLineCount() - 2; i++) {
			line = reader.readLine().trim();
			String lineStart = line.replaceAll(" .*", "").trim();
			Assert.assertEquals(line, "<header", lineStart);
		}
	}

	protected void validatePostRequestHeaders(BufferedReader reader) throws IOException {
		String line;
		for (int i = 0; i < new HttpTestDataFactory().getPostRequestLineCount() - 2; i++) {
			line = reader.readLine().trim();
			String lineStart = line.replaceAll(" .*", "").trim();
			Assert.assertEquals(line, "<header", lineStart);
		}
	}

	protected void validateResponseHeaders(BufferedReader reader) throws IOException {
		String line;
		for (int i = 0; i < new HttpTestDataFactory().getOkayResponseLineCount() - 2; i++) {
			line = reader.readLine().trim();
			Assert.assertTrue("" + i, line.startsWith("<header "));
		}
	}

	protected void sendRequestPacket(String request, int requestCount) {
		header.setDataLength(request.getBytes().length);
		header.setPacketID(new PacketIdentifier(543, requestCount));
		header.setSourceType(PacketSourceType.CLIENT);
		header.setTimestamp(-1);
		publishRequest(header, request.getBytes());
	}
	
	protected void sendResponsePacket(String response, int requestCount) {
		header.setDataLength(response.getBytes().length);
		header.setPacketID(new PacketIdentifier(543, 1));
		header.setSourceType(PacketSourceType.SERVER);
		header.setTimestamp(-1);
		publishResponse(header, response.getBytes());
	}
	
	protected void sendEndingPacket() throws InterruptedException {
		header.setPacketID(new PacketIdentifier(543, PacketIdentifier.SESSION_CLOSE));
		header.setDataLength(0);
		publishRequest(header, new byte[0]);
	}

	private ByteArrayOutputStream startSubscriber() throws InterruptedException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		Thread subscriberThread = new Thread(){
			@Override
			public void run() {
				SubscriberSettings settings = new SubscriberSettings("localhost", ProxySettings.DEFAULT_DUPLEX_PORT);
				try {
					new HttpXmlSubscriber(new PrintStream(baos)).start(settings, System.out);
				}catch(Throwable t) {
					sawException = true;
				}
			}
		};
		subscriberThread.setDaemon(true);
		subscriberThread.start();
		return baos;
	}
	
	protected void publishRequest(PacketHeader header, byte[] buffer){
		manager.publishRequest(header, buffer);
		try { Thread.sleep(100); } catch (InterruptedException ignore) { }
	}

	private void publishResponse(PacketHeader header, byte[] buffer){
		manager.publishResponse(header, buffer);
		try { Thread.sleep(100); } catch (InterruptedException ignore) { }
	}

}
