/*
 * Created on June 9, 2007
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
package com.moneybender.proxy.pipes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.moneybender.proxy.ProxySettings;
import com.moneybender.proxy.channels.ByteReader;
import com.moneybender.proxy.channels.ByteWriter;
import com.moneybender.proxy.publishers.PacketSourceType;
import com.moneybender.proxy.publishers.SubscriberManager;

public class SessionTest {
	
	protected Logger log = Logger.getLogger(this.getClass());
	
	static SubscriberManager manager;
	
	@BeforeClass
	public static void setUp() throws Exception {
		manager = new SubscriberManager(ProxySettings.DEFAULT_REQUEST_PORT,
			ProxySettings.DEFAULT_RESPONSE_PORT, ProxySettings.DEFAULT_DUPLEX_PORT);
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		if(manager != null)
			manager.close();
	}
	
	@Test
	public void testConstruction() throws Exception {
		Session session = new MockSession(6, null, SocketChannel.open(), manager);
		Assert.assertEquals(6, session.getSessionID());
		Assert.assertNull(session.getSenderChannel());
		Assert.assertNotNull(session.getReceiverChannel());
		Assert.assertEquals(1, session.getNextPacketID());
		Assert.assertEquals(2, session.getNextPacketID());
		Assert.assertEquals(PacketSourceType.CLIENT, session.getSourceType());
		Assert.assertNotNull(session.getManager());
	}
	
	@Test
	public void testSecureClientSetup() throws Exception {
		SecurableSession session = new MockSession(6, null, SocketChannel.open(), manager);
		session.createSSLEngine();
		SSLEngine sslEngine = session.getSslEngine();
		
		Assert.assertNotNull(sslEngine);
		
		session.setEncrypter(session);
		Assert.assertSame(session, session.getEncrypter());
	}
	
	@Test
	public void testResizePacketBuffer() throws Exception {
		Session session = new MockSession(6, null, SocketChannel.open(), manager);

		session.setWriteBuffer(ByteBuffer.wrap("0123456789".getBytes()));
		Assert.assertEquals(10, session.getWriteBuffer().capacity());
	}
	
	@Test
	public void testTransferBytes() throws Exception {

		byte[] newBytes = "haveSomeBytes".getBytes();
		MockChannel testChannel = new MockChannel(null);
		testChannel.write(ByteBuffer.wrap(newBytes));

		Session session = new MockSession(6, testChannel, testChannel, manager);

		int bytesTransferred = session.transferBytes();
		Assert.assertEquals(newBytes.length, bytesTransferred);
	}
	
	@Test
	public void testSenderClosesFirst() throws Exception {

		byte[] newBytes = "haveSomeBytes".getBytes();
		MockChannel senderChannel = new MockChannel("server");
		senderChannel.write(ByteBuffer.wrap(newBytes));

		Session session = new MockSession(6, senderChannel, new MockChannel("client"), manager);
		senderChannel.close();

		int bytesTransferred = session.transferBytes();
		Assert.assertEquals(-1, bytesTransferred);
	}
	
	@Test
	public void testReceiverClosesFirst() throws Exception {
		
		byte[] newBytes = "haveSomeBytes".getBytes();
		MockChannel senderChannel = new MockChannel("server");

		MockChannel receiverChannel = new MockChannel("client");
		Session session = new MockSession(6, senderChannel, receiverChannel, manager);
		receiverChannel.closeForWriteOnly();
		senderChannel.write(ByteBuffer.wrap(newBytes));

		try {
			session.transferBytes();
			Assert.fail("Should have received the exception BytesLeftToWriteException");
		} catch (BytesLeftToWriteException e) {
		}
		Assert.assertEquals(13, senderChannel.getBytesReadCount());
		Assert.assertEquals(0, receiverChannel.getBytesWrittenCount());
	}
	
	@Test
	public void testExceptionOnRead() throws Exception {
		
		byte[] newBytes = "haveSomeBytes".getBytes();
		MockChannel testChannel = new MockBadReaderChannel(null);
		testChannel.write(ByteBuffer.wrap(newBytes));

		Session session = new MockSession(6, testChannel, testChannel, manager);

		Assert.assertTrue(testChannel.isOpen());
		try{
			session.transferBytes();
			Assert.fail("Should have received an IOException");
		}catch(IOException success){
		}
		Assert.assertFalse(testChannel.isOpen());
	}
	
	@Test
	public void testExceptionOnWrite() throws Exception {
		byte[] newBytes = "haveSomeBytes".getBytes();
		MockChannel testChannel = new MockBadWriterChannel(null);

		new MockSession(6, testChannel, testChannel, manager);

		Assert.assertTrue(testChannel.isOpen());
		try{
			testChannel.write(ByteBuffer.wrap(newBytes));
			Assert.fail("Should have received an IOException");
		}catch(IOException success){
		}
		Assert.assertTrue(testChannel.isOpen());
	}

	@Test
	public void testCreateApplicationBuffer() throws Exception {
		SecurableSession session = new MockSession(6, null, SocketChannel.open(), manager);
		session.createSSLEngine();
		ByteBuffer buffer = session.createApplicationBuffer(session.getSslEngine().getSession());
		Assert.assertTrue(buffer.capacity() > 0);
	}
	
	@Test
	public void testCreateOutgoingBuffer() throws Exception {
		SecurableSession session = new MockSession(6, null, SocketChannel.open(), manager);
		session.createSSLEngine();
		ByteBuffer buffer = session.createOutgoingBuffer(session.getSslEngine().getSession());
		Assert.assertTrue(buffer.capacity() > 0);
	}
	
	@Test
	public void testWriteMoreThanChannelCanReceive() throws Exception {
		byte[] newBytes = "haveSomeBytes".getBytes();
		MockChannel senderChannel = new MockChannel(null);
		MockChannel receiverChannel = new MockChannelWithSmallBuffer(null);
		senderChannel.write(ByteBuffer.wrap(newBytes));

		Session session = new MockSession(6, senderChannel, receiverChannel, manager);

		try {
			session.transferBytes();
			Assert.fail("Should have failed with exception");
		}catch (BytesLeftToWriteException expected) {
			log.info("Caught expected exception.");
		}
	}
	
	class MockSession extends SecurableSession{

		public MockSession(int sessionID, ByteChannel senderChannel, ByteChannel receiverChannel,
				SubscriberManager manager)
		{
			super(sessionID, PacketSourceType.CLIENT, senderChannel, receiverChannel, manager, new ByteReader(), new ByteWriter(), null, -1);
		}

		@Override
		protected void createSSLEngine() throws IOException {
			SSLEngine sslEngine = SSLContextFactory.getInstance().createSSLEngine();
			sslEngine.setUseClientMode(false);
			sslEngine.setNeedClientAuth(false);
			sslEngine.setWantClientAuth(false);

			setSslEngine(sslEngine);
		}

		@Override
		public boolean isSecured() {
			return false;
		}

		@Override
		protected void publish(byte[] buffer) {
		}
		
	}

	class MockBadReaderChannel extends MockChannel{

		MockBadReaderChannel(String channelName) {
			super(channelName);
		}

		@Override
		public int read(ByteBuffer arg0) throws IOException {
			throw new IOException("Test bad reader");
		}
	}
	
	class MockBadWriterChannel extends MockChannel{

		MockBadWriterChannel(String channelName) {
			super(channelName);
		}

		@Override
		public int write(ByteBuffer src) throws IOException {
			throw new IOException("Test bad writer");
		}
	}

	class MockChannelWithSmallBuffer extends MockChannel {

		private Logger log = Logger.getLogger(MockChannel.class);

		MockChannelWithSmallBuffer(String channelName) {
			super(channelName);
			this.dataSink = new ByteArrayOutputStream(10);
		}
		
		@Override
		public int write(ByteBuffer src) throws IOException {
			if(!isOpen || !isWriteOpen){
				return -1;
			}
			
			int bytesWritten = 5;
			byte[] buffer = new byte[bytesWritten];
			src.get(buffer);
			src.position(bytesWritten);

			if(log.isDebugEnabled())
				log.debug("Write " + bytesWritten + " bytes from ByteBuffer to channel '" + channelName + "'");

			dataSink.write(buffer);
			this.bytesWrittenCount += bytesWritten;
			
			return bytesWritten;
		}

	}
	
	class MockChannelWriteInTwoBlocks extends MockChannel {

		MockChannelWriteInTwoBlocks(String channelName) {
			super(channelName);
		}

		@Override
		public int write(ByteBuffer src) throws IOException {
			ByteBuffer[] buffers = split(src);
			
			int totalBytesWritten = 0;
			for (int i = 0; i < buffers.length; i++) {
				ByteBuffer byteBuffer = buffers[i];
				totalBytesWritten += super.write(byteBuffer);
			}

			return totalBytesWritten;
		}
		private ByteBuffer[] split(ByteBuffer src) {
			ByteBuffer[] buffers = new ByteBuffer[1]; 
			return buffers;
		}
	}
	
}
