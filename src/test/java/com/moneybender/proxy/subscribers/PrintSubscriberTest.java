/*
 * Created on October 18, 2007
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.moneybender.proxy.ProxySettings;
import com.moneybender.proxy.publishers.PacketHeader;
import com.moneybender.proxy.publishers.PacketIdentifier;
import com.moneybender.proxy.publishers.PacketSourceType;
import com.moneybender.proxy.publishers.SubscriberManager;

public class PrintSubscriberTest {

	private SubscriberManager manager;
	private PacketHeader header;
	private PacketIdentifier packetIdentifier;

	private ByteArrayOutputStream baos;
	private String lineSeparator;
	
	@Before
	public void setUp() throws Exception {
		header = new PacketHeader();

		packetIdentifier = new PacketIdentifier(543);
		header.setSourceType(PacketSourceType.CLIENT);
		header.setPacketID(packetIdentifier);
		header.setDataLength(101);
		header.setSenderIP("10.5.128.3");
		header.setSenderPort(2003);

		lineSeparator = new HttpTestDataFactory().getLineSeparator();

		manager = new SubscriberManager(ProxySettings.DEFAULT_REQUEST_PORT,
			ProxySettings.DEFAULT_RESPONSE_PORT, ProxySettings.DEFAULT_DUPLEX_PORT);
		baos = startPrintSubscriber();
		Thread.sleep(250);	// Give the subscriber thread time to connect and be ready.
	}
	
	@After
	public void tearDown() throws Exception {
		if(manager != null){
			manager.close();
		}
	}
	
	@Test
	public void testConnecting() throws Exception {
		
		byte[] buffer = "HTTP/1.1 200 OK\nContent-Type: text/xml\n\npayload".getBytes();
		header.setDataLength(buffer.length);

		publishRequest(header, buffer);
		
		BufferedReader reader = new BufferedReader(new StringReader(baos.toString()));
		String line1 = reader.readLine();
		Assert.assertNotNull(line1);
	}
	
	@Test
	public void testZeroLengthPacket() throws Exception {
		header.setDataLength(0);
		publishRequest(header, new byte[1]);
	}

	@Test
	public void testGetRequest() throws Exception {
		
		String request = new HttpTestDataFactory().getGetRequest();
		header.setDataLength(request.getBytes().length);
		header.setPacketID(new PacketIdentifier(543, 1));

		publishRequest(header, request.getBytes());

		BufferedReader reader = new BufferedReader(new StringReader(baos.toString()));
		String line0 = reader.readLine();
		Assert.assertEquals("PrintSubscriber starting...", line0);
		String line1 = reader.readLine();
		Assert.assertNotNull(line1);
		String line1b = reader.readLine();
		Assert.assertNotNull(line1b);
		Assert.assertTrue(line1b.matches("^Sender is .*$"));
		String line2 = reader.readLine();
		Assert.assertNotNull(line2);
		Assert.assertTrue(line2.matches("^Packet ID:543-1"));
		int expectedLength = request.getBytes().length + line0.length() + line1.length() + line1b.length()
			+ line2.length() + lineSeparator.length() * 5; 
		Assert.assertEquals(expectedLength, baos.size());
	}
	
	@Test
	public void testConnectRequest() throws Exception {
		
		String requestHeaders = new HttpTestDataFactory().getConnectRequest();
		String requestPayload = new HttpTestDataFactory().getGenericPayload();
		
		header.setDataLength(requestHeaders.getBytes().length + requestPayload.getBytes().length);
		header.setPacketID(new PacketIdentifier(543, 2));

		publishRequest(header, (requestHeaders + " " + requestPayload).getBytes());

		BufferedReader reader = new BufferedReader(new StringReader(baos.toString()));
		String line0 = reader.readLine();
		Assert.assertEquals("PrintSubscriber starting...", line0);
		String line1 = reader.readLine();
		Assert.assertNotNull(line1);
		String line1b = reader.readLine();
		Assert.assertNotNull(line1b);
		Assert.assertTrue(line1b.matches("^Sender is .*$"));
		String line2 = reader.readLine();
		Assert.assertNotNull(line2);
		Assert.assertTrue(line2.matches("^Packet ID:543-2"));
		int expectedLength = requestHeaders.getBytes().length + line0.length() + line1.length()  + line1b.length()
			+ line2.length() + lineSeparator.length() * 5
			+ requestPayload.length(); 
		Assert.assertEquals(expectedLength, baos.size());
	}

	@Test
	public void testAllHeaderVariants() throws Exception {
		
		String[] knownHeaders = new HttpTestDataFactory().getKnownHeaderExamples();
		
		int expectedLength = 0;
		for (int line = 0; line < knownHeaders.length; line++) {
			String request = knownHeaders[line] + lineSeparator;

			header.setDataLength(request.getBytes().length);
			header.setPacketID(new PacketIdentifier(543, 3));

			publishRequest(header, request.getBytes());

			expectedLength += request.length();
			BufferedReader reader = new BufferedReader(new StringReader(baos.toString()));
			String line0 = reader.readLine();
			Assert.assertNotNull(line0);
			if(line == 0) {
				expectedLength += line0.length() + lineSeparator.length();
			}
			String line1 = reader.readLine();
			Assert.assertNotNull(line1);
			line1 = reader.readLine();
			Assert.assertNotNull(line1);
			Assert.assertTrue(line1.matches("^Sender is .*$"));
			String line2 = reader.readLine();
			Assert.assertNotNull(line2);
			Assert.assertTrue(line2.matches("^Packet ID:543-3"));
			expectedLength += line1.length() + line2.length() + lineSeparator.length() * 4; 
			Assert.assertEquals("Header variant " + request, expectedLength, baos.size());
		}
	}

	@Test
	public void testAllNonHeaderVariants() throws Exception {
		
		String[] notHeaders = new HttpTestDataFactory().getNonHeaderExamples();

		for (int line = 0; line < notHeaders.length; line++) {
			String request = notHeaders[line] + lineSeparator;

			header.setDataLength(request.getBytes().length);
			header.setPacketID(new PacketIdentifier(543, 4));

			publishRequest(header, request.getBytes());

			BufferedReader reader = new BufferedReader(new StringReader(baos.toString()));
			String line0 = reader.readLine();
			Assert.assertNotNull(line0);
			String line1 = reader.readLine();
			Assert.assertNotNull(line1);
			line1 = reader.readLine();
			Assert.assertNotNull(line1);
			Assert.assertTrue(line1.matches("^Sender is .*$"));
			String line2 = reader.readLine();
			Assert.assertNotNull(line2);
			Assert.assertTrue(line2.matches("^Packet ID:543-4"));
		}
	}

	private ByteArrayOutputStream startPrintSubscriber() throws InterruptedException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		Thread subscriberThread = new Thread(){
			@Override
			public void run() {
				SubscriberSettings settings = new SubscriberSettings("localhost", ProxySettings.DEFAULT_REQUEST_PORT);
				try {
					new PrintSubscriber().start(settings, baos);
				} catch (IOException e) {
					Assert.fail(e.getMessage());
				}
			}
		};
		subscriberThread.setDaemon(true);
		subscriberThread.start();
		return baos;
	}
	
	private void publishRequest(PacketHeader header, byte[] buffer) throws InterruptedException {
		manager.publishRequest(header, buffer);
		Thread.sleep(100);
	}

}
