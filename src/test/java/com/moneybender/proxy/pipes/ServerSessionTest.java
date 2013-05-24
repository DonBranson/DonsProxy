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
import java.util.Arrays;

import javax.net.ssl.SSLEngine;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.moneybender.proxy.ProxySettings;
import com.moneybender.proxy.channels.ByteReader;
import com.moneybender.proxy.channels.ByteWriter;
import com.moneybender.proxy.publishers.PacketSourceType;
import com.moneybender.proxy.publishers.SubscriberManager;

public class ServerSessionTest {
	
	private static Logger log = Logger.getLogger(ServerSessionTest.class);

	SubscriberManager manager;
	boolean clientEncryptCalled;
	boolean serverEncryptCalled;
	
	static int sessionID;
	
	private ByteReader byteReader;
	private ByteWriter byteWriter;

	@BeforeClass
	static public void beforeClass() throws Exception {
		sessionID = 17;
	}

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
		MockChannel senderChannel = new MockChannel("server");
		MockChannel receiverChannel = new MockChannel("client");
		senderChannel.write(ByteBuffer.wrap(newBytes));

		Session session = new ServerSession(sessionID++, senderChannel, receiverChannel, manager, byteReader, byteWriter, null, -1);
		session.transferBytes();
		Assert.assertEquals(newBytes.length, senderChannel.getBytesWrittenCount());
	}
	
	@Test
	public void testEmptyReadbytes() throws Exception {
		byte[] newBytes = new byte[0];
		MockChannel senderChannel = new MockChannel("server");
		MockChannel receiverChannel = new MockChannel("client");
		senderChannel.write(ByteBuffer.wrap(newBytes));

		Session session = new ServerSession(sessionID++, senderChannel, receiverChannel, manager, byteReader, byteWriter, null, -1);
		session.transferBytes();
		Assert.assertEquals(newBytes.length, senderChannel.getBytesWrittenCount());
	}
	
	@Test
	public void testReadConnectResponse() throws Exception {

		byte[] connectReponseBytes = SSLUtil.connectResponse.getBytes();
		MockChannel senderChannel = new MockChannel("server");
		MockChannel receiverChannel = new MockChannel("client");
		
		Session session = new ServerSession(sessionID++, senderChannel, receiverChannel, manager, byteReader, byteWriter, null, -1);
		senderChannel.write(ByteBuffer.wrap(connectReponseBytes));
		session.transferBytes();

		Assert.assertEquals(connectReponseBytes.length, receiverChannel.getBytesWrittenCount());
	}

	@Test
	public void testConversionToSecureSession() throws Exception {

		MockChannel senderChannel = new MockChannel("server");
		SecurableSession session = new ServerSession(sessionID++, senderChannel, new MockChannel("client"), manager, byteReader, byteWriter, null, -1);

		try{
			SSLUtil.makeServerSessionSecure(senderChannel, session, -1);
			Assert.fail("Should have failed while converting session to secure when there's no encrytor.");
		}catch(Throwable t){
			Assert.assertTrue(t instanceof AssertionError);
		}
		Assert.assertEquals(PacketSourceType.SERVER, session.getSourceType());

		session.setEncrypter(new MockEncrypter());
		SSLUtil.makeServerSessionSecure(senderChannel, session, -1);
		Assert.assertEquals(PacketSourceType.SECURE_SERVER, session.getSourceType());
	}
	
	@Test
	public void testConversionToSecureSessionWhenDataComesInTwoBlocks() throws Exception {
		MockChannel senderChannel = new MockChannel("server");
		SecurableSession session = new ServerSession(sessionID++, senderChannel, new MockChannel("client"), manager, byteReader, byteWriter, null, -1);

		try{
			SSLUtil.makeServerSessionSecure(senderChannel, session, 1);
			Assert.fail("Should have failed while converting session to secure when there's no encrytor.");
		}catch(Throwable t){
			Assert.assertTrue(t instanceof AssertionError);
		}
		Assert.assertEquals(PacketSourceType.SERVER, session.getSourceType());

		session.setEncrypter(new MockEncrypter());
		SSLUtil.makeServerSessionSecure(senderChannel, session, -1);
		Assert.assertEquals(PacketSourceType.SECURE_SERVER, session.getSourceType());
	}

	@Test
	public void testCleartextMessages() throws Exception {
		MockChannel senderChannel = new MockChannel("server");
		MockChannel receiverChannel = new MockChannel("client");
		
		Session session = new ServerSession(sessionID++, senderChannel, receiverChannel, manager, byteReader, byteWriter, null, -1);

		byte[] originalMessage = "In the beginning was the Word, and the Word was with God, and the Word was God.  John 1:1".getBytes();
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

		SecurableSession session = new ServerSession(sessionID++, senderChannel, receiverChannel, manager, byteReader, byteWriter, null, -1);
		session.setEncrypter(new MockSecureEncrypter());
		SSLEngine serverEngine = SSLUtil.makeServerSessionSecure(senderChannel, session, -1);

		byte[] originalMessage = "During the whole of a dull, dark, and dreary day, when the clouds hung oppresively low in the sky... (Poe)".getBytes();
		ByteBuffer encryptedMessage = SSLUtil.encrypt(serverEngine, originalMessage);
		encryptedMessage.flip();
		int bytesWritten = senderChannel.write(encryptedMessage);
		Assert.assertTrue(bytesWritten > 0);

		receiverChannel.clear();
		int bytesTransferred = session.transferBytes();
		Assert.assertTrue(bytesTransferred == bytesWritten);

		ByteBuffer receivedMessage = ByteBuffer.allocate(originalMessage.length);
		int bytesRead = receiverChannel.read(receivedMessage);
		Assert.assertTrue(bytesRead == originalMessage.length);

		receivedMessage.flip();
		byte[] receivedMessageBytes = new byte[originalMessage.length];
		receivedMessage.get(receivedMessageBytes);
		Assert.assertTrue(Arrays.equals(originalMessage, receivedMessageBytes));
	}

	@Test
	public void testServerSessionEncryptMethod() throws Exception {

		MockChannel serverSenderChannel = new MockChannel("server");
		MockChannel serverReceiverChannel = new MockChannel("client");

		MockChannel clientSenderChannel = new MockChannel("server");
		MockChannel clientReceiverChannel = new MockChannel("client");

		clientEncryptCalled = false;
		serverEncryptCalled = false;

		ClientSession clientSession = new ClientSession(sessionID++, clientSenderChannel, clientReceiverChannel, manager, byteReader, byteWriter, null, -1){
			@Override
			public ByteBuffer encrypt(byte[] cleartextData) throws IOException {
				clientEncryptCalled = true;
				return super.encrypt(cleartextData);
			}
		};
		SecurableSession serverSession = new ServerSession(sessionID++, serverSenderChannel, serverReceiverChannel, manager, byteReader, byteWriter, null, -1){
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

		SSLUtil.sendMessageThroughEncryption(clientSenderChannel, clientReceiverChannel, clientSession, serverEngine, clientEngine);
		SSLUtil.sendMessageThroughEncryption(clientSenderChannel, clientReceiverChannel, clientSession, serverEngine, clientEngine);
		SSLUtil.sendMessageThroughEncryption(clientSenderChannel, clientReceiverChannel, clientSession, serverEngine, clientEngine);
		Assert.assertFalse(clientEncryptCalled);
		Assert.assertTrue(serverEncryptCalled);
	}

	@Test
	public void testDecryptLargeBlock() throws Exception {
		byte[] largeBlock = new byte[768 * 1024];
		for (int i = 0; i < largeBlock.length; i++) {
			largeBlock[i] = (byte)i;
		}
		
		MockChannel senderChannel = new MockChannel("server");
		MockChannel receiverChannel = new MockChannel("client");

		SecurableSession session = new ServerSession(sessionID++, senderChannel, receiverChannel, manager, byteReader, byteWriter, null, -1);
		session.setEncrypter(new MockSecureEncrypter());
		SSLEngine serverEngine = SSLUtil.makeServerSessionSecure(senderChannel, session, -1);

		ByteBuffer encryptedMessage = SSLUtil.encrypt(serverEngine, largeBlock);
		encryptedMessage.flip();
		int bytesWritten = senderChannel.write(encryptedMessage);
		Assert.assertTrue(bytesWritten > 0);

		receiverChannel.clear();
		int bytesTransferred = session.transferBytes();
		Assert.assertEquals(bytesWritten, bytesTransferred);

		ByteBuffer receivedMessage = ByteBuffer.allocate(largeBlock.length);
		int bytesRead = receiverChannel.read(receivedMessage);
		Assert.assertEquals(largeBlock.length, bytesRead);

		receivedMessage.flip();
		byte[] receivedMessageBytes = new byte[largeBlock.length];
		receivedMessage.get(receivedMessageBytes);
		Assert.assertTrue(Arrays.equals(largeBlock, receivedMessageBytes));
	}
	
	@Test
	public void testThatReadDoesNotReadBytesWhenWeAreSecuredAndOurEncryterIsNotSecured() throws Exception {
		MockChannel senderChannel = new MockChannel("server");
		MockChannel receiverChannel = new MockChannel("client");

		SecurableSession session = new ServerSession(sessionID++, senderChannel, receiverChannel, manager, byteReader, byteWriter, null, -1);
		session.setEncrypter(new MockEncrypter());
		SSLEngine serverEngine = SSLUtil.makeServerSessionSecure(senderChannel, session, -1);

		byte[] originalMessage = "A foolish consistency is the hobgoblin of small minds.  (Emerson)".getBytes();
		ByteBuffer encryptedMessage = SSLUtil.encrypt(serverEngine, originalMessage);
		encryptedMessage.flip();
		int bytesWritten = senderChannel.write(encryptedMessage);
		Assert.assertTrue(bytesWritten > 0);

		receiverChannel.clear();
		int bytesTransferred = session.transferBytes();
		Assert.assertEquals(0, bytesTransferred);

		session.setEncrypter(new MockSecureEncrypter());

		bytesTransferred = session.transferBytes();
		Assert.assertNotSame(0, bytesTransferred);
	}
	
	@Test
	public void testThatDecrypterWorksWhenDataComesInTwoBlocks() throws Exception {
		MockChannel senderChannel = new MockChannel("server");
		MockChannel receiverChannel = new MockChannel("client");

		SecurableSession session = new ServerSession(sessionID++, senderChannel, receiverChannel, manager, byteReader, byteWriter, null, -1);
		session.setEncrypter(new MockSecureEncrypter());
		SSLEngine serverEngine = SSLUtil.makeServerSessionSecure(senderChannel, session, -1);

		byte[] originalMessage = "During the whole of a dull, dark, and dreary day, when the clouds hung oppresively low in the sky... (Poe)".getBytes();
		ByteBuffer encryptedMessage = SSLUtil.encrypt(serverEngine, originalMessage);
		encryptedMessage.flip();
		Assert.assertTrue(encryptedMessage.limit() > 100);

		ByteBuffer encryptedMessagePartOne = encryptedMessage.duplicate();
		encryptedMessagePartOne.limit(encryptedMessage.limit() - 25);
		int bytesWritten = senderChannel.write(encryptedMessagePartOne);

		receiverChannel.clear();
		int bytesTransferred = 0;
		try {
			bytesTransferred = session.transferBytes();
		} catch (BytesLeftException expected) {
		}

		ByteBuffer encryptedMessagePartTwo = encryptedMessage.duplicate();
		encryptedMessagePartTwo.position(encryptedMessage.limit() - 25);
		
		bytesWritten += senderChannel.write(encryptedMessagePartTwo);
		Assert.assertTrue(bytesWritten > 0);

		receiverChannel.clear();
		bytesTransferred += session.transferBytes();	// Transfer separated by logical time.
		Assert.assertTrue(bytesTransferred > 0);

		ByteBuffer receivedMessage = ByteBuffer.allocate(1024);
		int bytesRead = receiverChannel.read(receivedMessage);
		Assert.assertTrue(bytesRead > 0);

		receivedMessage.flip();
		byte[] receivedMessageBytes = new byte[receivedMessage.limit() - receivedMessage.position()];
		receivedMessage.get(receivedMessageBytes);
		Assert.assertEquals(new String(originalMessage), new String(receivedMessageBytes));
	}
	
	@Test
	public void testThatDecryptCanHandleMultipleDecrytableBlocksFromOnePacket() throws Exception {
		MockChannel senderChannel = new MockChannel("server");
		MockChannel receiverChannel = new MockChannel("client");

		SecurableSession session = new ServerSession(sessionID++, senderChannel, receiverChannel, manager, byteReader, byteWriter, null, -1);
		session.setEncrypter(new MockSecureEncrypter());
		SSLEngine serverEngine = SSLUtil.makeServerSessionSecure(senderChannel, session, -1);

		byte[] originalMessage1 = "During the whole of a dull, dark, and dreary day, when the clouds hung oppresively low in the sky... (Poe)".getBytes();
		byte[] originalMessage2 = (new String(originalMessage1) + "0").getBytes();
		byte[] originalMessage3 = (new String(originalMessage1) + "00").getBytes();
		ByteBuffer encryptedMessage = SSLUtil.encrypt(serverEngine, originalMessage1);
		encryptedMessage.flip();
		int bytesWritten = senderChannel.write(encryptedMessage);
		Assert.assertTrue(bytesWritten > 0);

		encryptedMessage = SSLUtil.encrypt(serverEngine, originalMessage2);
		encryptedMessage.flip();
		bytesWritten += senderChannel.write(encryptedMessage);
		Assert.assertTrue(bytesWritten > 0);

		encryptedMessage = SSLUtil.encrypt(serverEngine, originalMessage3);
		encryptedMessage.flip();
		bytesWritten += senderChannel.write(encryptedMessage);
		Assert.assertTrue(bytesWritten > 0);

		// Throw a partial on the end
		encryptedMessage = SSLUtil.encrypt(serverEngine, originalMessage1);
		encryptedMessage.flip();
		encryptedMessage.limit(encryptedMessage.limit() / 2);
		bytesWritten += senderChannel.write(encryptedMessage);
		Assert.assertTrue(bytesWritten > 0);

		receiverChannel.clear();
		try {
			session.transferBytes();
		} catch (BytesLeftException expected) {
		}

		ByteBuffer receivedMessage = ByteBuffer.allocate(1024);
		int bytesRead = receiverChannel.read(receivedMessage);
		Assert.assertEquals(originalMessage1.length + originalMessage2.length + originalMessage3.length, bytesRead);

		receivedMessage.flip();
		byte[] receivedMessageBytes = new byte[receivedMessage.limit() - receivedMessage.position()];
		receivedMessage.get(receivedMessageBytes);
		Assert.assertEquals(new String(originalMessage1) + new String(originalMessage2) + new String(originalMessage3),
			new String(receivedMessageBytes));
	}
	
}
