/*
 * Created on Jun 29, 2007
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
package com.moneybender.proxy.pipes;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.net.ssl.SSLEngine;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.moneybender.proxy.ProxySettings;
import com.moneybender.proxy.channels.ByteReader;
import com.moneybender.proxy.channels.ByteWriter;
import com.moneybender.proxy.channels.IReadBytes;
import com.moneybender.proxy.channels.IWriteBytes;
import com.moneybender.proxy.publishers.PacketSourceType;
import com.moneybender.proxy.publishers.SubscriberManager;

public class ClientSessionTest {
	
	private static Logger log = Logger.getLogger(ClientSessionTest.class);

	SubscriberManager manager;
	boolean clientEncryptCalled;
	boolean serverEncryptCalled;

	protected IReadBytes byteReader;
	protected IWriteBytes byteWriter;

	@Before
	public void setUp() throws Exception {
		manager = new SubscriberManager(ProxySettings.DEFAULT_REQUEST_PORT,
			ProxySettings.DEFAULT_RESPONSE_PORT, ProxySettings.DEFAULT_DUPLEX_PORT);
		byteReader = new ByteReader();
		byteWriter = new ByteWriter();
	}
	
	@After
	public void tearDown() throws Exception {
		if(manager != null)
			manager.close();
	}
	
	@Test
	public void testBasicReadbytes() throws Exception {
		byte[] newBytes = "haveSomeBytes".getBytes();
		MockChannel senderChannel = new MockChannel("sender");
		MockChannel receiverChannel = new MockChannel("receiver");
		senderChannel.write(ByteBuffer.wrap(newBytes));

		ClientSession session = new ClientSession(17, senderChannel, receiverChannel, manager, byteReader, byteWriter, null, -1);
		session.transferBytes();
		Assert.assertEquals(newBytes.length, senderChannel.getBytesWrittenCount());
		Assert.assertEquals(newBytes.length, receiverChannel.getBytesWrittenCount());
	}
	
	@Test
	public void testReadConnectRequest() throws Exception {
		
		byte[] newBytes = SSLUtil.connectRequest.getBytes();
		MockChannel senderChannel = new MockChannel("sender");
		MockChannel receiverChannel = new MockChannel("receiver");
		senderChannel.write(ByteBuffer.wrap(newBytes));

		ClientSession session = new ClientSession(17, senderChannel, receiverChannel, manager, byteReader, byteWriter, null, -1);
		session.transferBytes();
		Assert.assertEquals(newBytes.length, senderChannel.getBytesWrittenCount());
		Assert.assertEquals(newBytes.length, receiverChannel.getBytesWrittenCount());
	}
	
	@Test
	public void testConversionToSecureSession() throws Exception {

		MockChannel senderChannel = new MockChannel("client");
		ClientSession session = new ClientSession(18, senderChannel, new MockChannel("server"), manager, byteReader, byteWriter, null, -1);
		try{
			SSLUtil.makeClientSessionSecure(senderChannel, session);
			Assert.fail("Should have failed while converting session to secure when there's no encryter.");
		}catch(Throwable t){
			Assert.assertTrue(t instanceof AssertionError);
		}
		Assert.assertEquals(PacketSourceType.CLIENT, session.getSourceType());

		session.setEncrypter(new MockEncrypter());
		SSLUtil.makeClientSessionSecure(senderChannel, session);
		Assert.assertEquals(PacketSourceType.SECURE_CLIENT, session.getSourceType());
	}
	
	@Test
	public void testCleartextMessages() throws Exception {
		MockChannel senderChannel = new MockChannel("server");
		MockChannel receiverChannel = new MockChannel("client");
		
		ClientSession session = new ClientSession(18, senderChannel, receiverChannel, manager, byteReader, byteWriter, null, -1);

		byte[] originalMessage = "My ears had heard of you, but now my eyes have seen you.  Job 42:5".getBytes();
		int bytesWritten = senderChannel.write(ByteBuffer.wrap(originalMessage));
		if(log.isDebugEnabled())
			log.debug("bytes written:" + bytesWritten);
		
		session.transferBytes();
		ByteBuffer receivedMessage = ByteBuffer.allocate(1024);
		int bytesRead = receiverChannel.read(receivedMessage);
		if(log.isDebugEnabled())
			log.debug("bytes read:" + bytesRead);
		receivedMessage.flip();
		byte[] extractedMessage = new byte[receivedMessage.limit()];
		receivedMessage.get(extractedMessage);
		Assert.assertEquals(new String(originalMessage), new String(extractedMessage));
	}
	
	@Test
	public void testMessageDecryption() throws Exception {
		
		MockChannel senderChannel = new MockChannel("server");
		MockChannel receiverChannel = new MockChannel("client");

		ClientSession session = new ClientSession(18, senderChannel, receiverChannel, manager, byteReader, byteWriter, null, -1);
		session.setEncrypter(new MockSecureEncrypter());
		SSLEngine clientEngine = SSLUtil.makeClientSessionSecure(senderChannel, session);

		byte[] originalMessage = "He accosted me with trepidation and passed on... (Poe)".getBytes();
		ByteBuffer encryptedMessage = SSLUtil.encrypt(clientEngine, originalMessage);
		encryptedMessage.flip();
		int bytesWritten = senderChannel.write(encryptedMessage);
		Assert.assertTrue(bytesWritten > 0);

		byte[] encryptedBytes = new byte[encryptedMessage.limit() - encryptedMessage.position()];
		encryptedMessage.get(encryptedBytes);
		Assert.assertNotSame(new String(originalMessage), new String(encryptedBytes));

		receiverChannel.clear();
		session.transferBytes();
		ByteBuffer receivedMessage = ByteBuffer.allocate(1024);
		receiverChannel.read(receivedMessage);
		receivedMessage.flip();
		
		byte[] receivedMessageBytes = new byte[receivedMessage.limit() - receivedMessage.position()];
		receivedMessage.get(receivedMessageBytes);
		Assert.assertEquals(new String(originalMessage), new String(receivedMessageBytes));
	}
	
	@Test
	public void testClientSessionEncryption() throws Exception {

		MockChannel serverSenderChannel = new MockChannel("server");
		MockChannel serverReceiverChannel = new MockChannel("client");

		MockChannel clientSenderChannel = new MockChannel("server");
		MockChannel clientReceiverChannel = new MockChannel("client");

		clientEncryptCalled = false;
		serverEncryptCalled = false;

		ClientSession clientSession = new ClientSession(18, clientSenderChannel, clientReceiverChannel, manager, byteReader, byteWriter, null, -1){
			@Override
			public ByteBuffer encrypt(byte[] cleartextData) throws IOException {
				clientEncryptCalled = true;
				return super.encrypt(cleartextData);
			}
		};
		SecurableSession serverSession = new ServerSession(18, serverSenderChannel, serverReceiverChannel, manager, byteReader, byteWriter, null, -1){
			@Override
			public ByteBuffer encrypt(byte[] cleartextData) throws IOException {
				serverEncryptCalled = true;
				return super.encrypt(cleartextData);
			}
		};

		serverSession.setEncrypter(clientSession);
		SSLEngine serverEngine = SSLUtil.makeServerSessionSecure(serverSenderChannel, serverSession, -1);

		clientSession.setEncrypter(serverSession);
		SSLEngine clientEngine = SSLUtil.makeClientSessionSecure(clientSenderChannel, clientSession);
		
		serverSenderChannel.clear();
		clientSenderChannel.clear();

		SSLUtil.sendMessageThroughEncryption(serverSenderChannel, serverReceiverChannel, serverSession, clientEngine, serverEngine);
		SSLUtil.sendMessageThroughEncryption(serverSenderChannel, serverReceiverChannel, serverSession, clientEngine, serverEngine);
		SSLUtil.sendMessageThroughEncryption(serverSenderChannel, serverReceiverChannel, serverSession, clientEngine, serverEngine);
		Assert.assertTrue(clientEncryptCalled);
		Assert.assertFalse(serverEncryptCalled);
	}
	
	@Test
	public void testThatReadDoesNotReadBytesWhenWeAreSecuredAndOurEncryterIsNotSecured() throws Exception {

		MockChannel senderChannel = new MockChannel("server");
		MockChannel receiverChannel = new MockChannel("client");

		ClientSession session = new ClientSession(18, senderChannel, receiverChannel, manager, byteReader, byteWriter, null, -1);
		session.setEncrypter(new MockEncrypter());
		SSLEngine clientEngine = SSLUtil.makeClientSessionSecure(senderChannel, session);

		byte[] originalMessage = "Men with whiskers 'neath their noses oughta kiss like Eskimoses.  (Berma Shave)".getBytes();
		ByteBuffer encryptedMessage = SSLUtil.encrypt(clientEngine, originalMessage);
		encryptedMessage.flip();
		int bytesWritten = senderChannel.write(encryptedMessage);
		Assert.assertTrue(bytesWritten > 0);

		byte[] encryptedBytes = new byte[encryptedMessage.limit() - encryptedMessage.position()];
		encryptedMessage.get(encryptedBytes);
		Assert.assertNotSame(new String(originalMessage), new String(encryptedBytes));

		receiverChannel.clear();
		int bytesTransferred = session.transferBytes();
		Assert.assertEquals(0, bytesTransferred);

		session.setEncrypter(new MockSecureEncrypter());

		bytesTransferred = session.transferBytes();
		Assert.assertNotSame(0, bytesTransferred);
	}
}
