/*
 * Created on May 11, 2007
 * 
 * Copyright (c) 2007, Don Branson.  All Rights Reserved.
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
package com.moneybender.proxy.publishers;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.moneybender.proxy.ProxySettings;

public class SubscriberManagerTest {
	
	private static Logger log = Logger.getLogger(SubscriberManagerTest.class);

	private SubscriberManager manager;
	private PacketHeader header;
	private PacketIdentifier packetIdentifier;

	@Before
	public void setUp() throws Exception {
		manager = new SubscriberManager(ProxySettings.DEFAULT_REQUEST_PORT,
			ProxySettings.DEFAULT_RESPONSE_PORT, ProxySettings.DEFAULT_DUPLEX_PORT);

		header = new PacketHeader();

		packetIdentifier = new PacketIdentifier(543);
		header.setSourceType(PacketSourceType.CLIENT);
		header.setPacketID(packetIdentifier);
		header.setDataLength(101);
		header.setSenderIP("10.5.128.2");
		header.setSenderPort(2002);
		Thread.sleep(100);
	}
	
	@After
	public void tearDown() throws Exception {
		if(manager != null){
			manager.close();
		}
	}
	
	@Test
	public void testSubscriberHandler() throws Exception {
		Socket socket = new Socket("localhost", ProxySettings.DEFAULT_REQUEST_PORT);
		Assert.assertTrue(socket.isConnected());
		socket.close();

		socket = new Socket("localhost", ProxySettings.DEFAULT_RESPONSE_PORT);
		Assert.assertTrue(socket.isConnected());
		socket.close();

		socket = new Socket("localhost", ProxySettings.DEFAULT_DUPLEX_PORT);
		Assert.assertTrue(socket.isConnected());
		socket.close();
	}
	
	@Test
	public void testPublish() throws Exception {
		
		ObjectInput requestsStream = new ObjectInputStream(new Socket("localhost", ProxySettings.DEFAULT_REQUEST_PORT).getInputStream());
		ObjectInput responsesStream = new ObjectInputStream(new Socket("localhost", ProxySettings.DEFAULT_RESPONSE_PORT).getInputStream());
		ObjectInput duplexStream = new ObjectInputStream(new Socket("localhost", ProxySettings.DEFAULT_DUPLEX_PORT).getInputStream());
		
		Thread.sleep(50);

		byte[] buffer = "Test data".getBytes();

		header.setDataLength(buffer.length);
		publishRequest(header, buffer);
		checkPublishedPacket(requestsStream, buffer);
		checkPublishedPacket(duplexStream, buffer);

		publishResponse(header, buffer);
		checkPublishedPacket(responsesStream, buffer);
		checkPublishedPacket(duplexStream, buffer);
		
		requestsStream.close();
		responsesStream.close();
		duplexStream.close();
	}

	@Test
	public void testPublishWithDepartingSubscriber() throws Exception {
		
		ObjectInput requestsStream = new ObjectInputStream(new Socket("localhost", ProxySettings.DEFAULT_REQUEST_PORT).getInputStream());
		ObjectInput responsesStream = new ObjectInputStream(new Socket("localhost", ProxySettings.DEFAULT_RESPONSE_PORT).getInputStream());
		ObjectInput duplexStream = new ObjectInputStream(new Socket("localhost", ProxySettings.DEFAULT_DUPLEX_PORT).getInputStream());
		
		Thread.sleep(50);

		byte[] buffer = "Test data".getBytes();

		header.setDataLength(buffer.length);
		publishRequest(header, buffer);
		Thread.sleep(50);
		checkPublishedPacket(requestsStream, buffer);
		checkPublishedPacket(duplexStream, buffer);

		duplexStream.close();

		publishResponse(header, buffer);
		Thread.sleep(50);
		checkPublishedPacket(responsesStream, buffer);
		try {
			checkPublishedPacket(duplexStream, buffer);
			Assert.fail("Should not have receive data on the closed socket.s");
		} catch (IOException expected) {
		}
		
		requestsStream.close();
		responsesStream.close();
	}
	
	private void checkPublishedPacket(ObjectInput oi, byte[] buffer) throws IOException, ClassNotFoundException {
		if(log.isDebugEnabled())
			log.debug("checkPublishedPacket()");

		PacketHeader newHeader = new PacketHeader();
		newHeader.readExternal(oi);
		
		byte[] inBuffer = new byte[oi.available()];
		oi.read(inBuffer);

		Assert.assertEquals(2, newHeader.getVersion());
		Assert.assertEquals(PacketSourceType.CLIENT, newHeader.getSourceType());
		Assert.assertEquals(9, newHeader.getDataLength());
		Assert.assertEquals(packetIdentifier, newHeader.getPacketIdentifier());
		Assert.assertEquals("10.5.128.2", newHeader.getSenderIP());
		Assert.assertEquals(2002, newHeader.getSenderPort());

		Assert.assertTrue(Arrays.equals(buffer, inBuffer));
	}
	
	private void publishRequest(PacketHeader header, byte[] buffer) throws InterruptedException {
		manager.publishRequest(header, buffer);
		Thread.sleep(100);
	}

	private void publishResponse(PacketHeader header, byte[] buffer) throws InterruptedException {
		manager.publishResponse(header, buffer);
		Thread.sleep(100);
	}
}
