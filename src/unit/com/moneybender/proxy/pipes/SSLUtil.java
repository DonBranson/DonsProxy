/*
 * Created on Jul 12, 2007
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
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;

import org.apache.log4j.Logger;
import org.junit.Assert;

import com.moneybender.proxy.publishers.PacketSourceType;

public class SSLUtil {
	
	private static Logger log = Logger.getLogger(SSLUtil.class);

	private static final String[] connectReponseHeaders = {
		 "HTTP/1.0 200 Connection Established",
		 "Proxy-agent: Apache"
	};

	private static final String[] connectRequestHeaders = {
		"CONNECT 10.5.128.5:443 HTTP/1.0",
		"Host: 10.5.128.5:443",
		"User-Agent: curl/7.15.5 (i686-suse-linux-gnu) libcurl/7.15.5 OpenSSL/0.9.8d zlib/1.2.3 libidn/0.6.8",
		"Proxy-Connection: Keep-Alive"
	};
	
	protected static String connectResponse = "";
	protected static String connectRequest = "";

	static {
		String lineSeparator = "\r\n";	// rfc2616, Section 2.2.
		for (int line = 0; line < connectReponseHeaders.length; line++) {
			connectResponse += connectReponseHeaders[line];
			connectResponse += lineSeparator;
		}
		connectResponse += lineSeparator;

		for (int line = 0; line < connectRequestHeaders.length; line++) {
			connectRequest += connectRequestHeaders[line];
			connectRequest += lineSeparator;
		}
		connectRequest += lineSeparator;
	}
	
	protected static SSLEngine makeServerSessionSecure(MockChannel senderChannel, Session serverSession, int splitPacket)
		throws IOException, SSLException
	{
		SSLEngine serverEngine = createServerSSLEngine();
		SSLSession sslSession = serverEngine.getSession();
		
		int networkBufferSize = sslSession.getPacketBufferSize() * 4;
		int applicationBufferSize = sslSession.getApplicationBufferSize();

		ByteBuffer serverIncomingNetworkBuffer = ByteBuffer.allocate(networkBufferSize);
		ByteBuffer serverIncomingApplicationBuffer = ByteBuffer.allocate(applicationBufferSize);
		ByteBuffer serverOutgoingNetworkBuffer = ByteBuffer.allocate(networkBufferSize);
		ByteBuffer serverOutgoingApplicationBuffer = ByteBuffer.allocate(1);

		// We're the server, so send the CONNECT response.
		senderChannel.write(ByteBuffer.wrap(connectResponse.getBytes()));
		serverSession.transferBytes();
		
		// We start off not in handshaking mode
		Assert.assertEquals(HandshakeStatus.NOT_HANDSHAKING, serverEngine.getHandshakeStatus());

		// Begin handshaking
		SSLEngineResult result;
		result = serverEngine.wrap(serverOutgoingApplicationBuffer, serverOutgoingNetworkBuffer);
		Assert.assertEquals(HandshakeStatus.NEED_UNWRAP, result.getHandshakeStatus());

		runDelegatedTasks(serverEngine, result.getHandshakeStatus());
		Assert.assertEquals(HandshakeStatus.NEED_UNWRAP, serverEngine.getHandshakeStatus());

		// The other end (the client) initiates the handshake.
		serverIncomingNetworkBuffer.clear();
		int bytesRead = senderChannel.read(serverIncomingNetworkBuffer);
		serverIncomingNetworkBuffer.flip();
		result = serverEngine.unwrap(serverIncomingNetworkBuffer, serverIncomingApplicationBuffer);
		Assert.assertEquals(bytesRead, result.bytesConsumed());
		Assert.assertEquals(HandshakeStatus.NEED_TASK, result.getHandshakeStatus());

		runDelegatedTasks(serverEngine, result.getHandshakeStatus());
		Assert.assertEquals(HandshakeStatus.NEED_WRAP, serverEngine.getHandshakeStatus());

		
		// This end (the fake server) responds to the client's overtures.
		result = serverEngine.wrap(serverOutgoingApplicationBuffer, serverOutgoingNetworkBuffer);
		Assert.assertEquals(HandshakeStatus.NEED_UNWRAP, result.getHandshakeStatus());
		
		serverOutgoingNetworkBuffer.flip();
		int writableBytes = serverOutgoingNetworkBuffer.limit() - serverOutgoingNetworkBuffer.position();
		int bytesWritten = senderChannel.write(serverOutgoingNetworkBuffer);
		
		Assert.assertEquals(writableBytes, bytesWritten);

		// Trigger exchange handling in ServerSession
		serverSession.transferBytes();

		// The server engine has now generated three handshake messages; read them from the 'wire'
		serverIncomingNetworkBuffer.clear();
		bytesRead = senderChannel.read(serverIncomingNetworkBuffer);
		serverIncomingNetworkBuffer.flip();
		
		// The client will go to the next phase where certs may be exchanged.
		// handshake.msg.1
		int handshakeBytesConsumed = 0;
		result = serverEngine.unwrap(serverIncomingNetworkBuffer, serverIncomingApplicationBuffer);
		handshakeBytesConsumed += result.bytesConsumed();
		Assert.assertEquals(HandshakeStatus.NEED_TASK, result.getHandshakeStatus());

		runDelegatedTasks(serverEngine, result.getHandshakeStatus());
		Assert.assertEquals(HandshakeStatus.NEED_UNWRAP, serverEngine.getHandshakeStatus());

		// handshake.msg.2
		result = serverEngine.unwrap(serverIncomingNetworkBuffer, serverIncomingApplicationBuffer);
		handshakeBytesConsumed += result.bytesConsumed();
		Assert.assertEquals(HandshakeStatus.NEED_UNWRAP, result.getHandshakeStatus());

		// handshake.msg.3
		result = serverEngine.unwrap(serverIncomingNetworkBuffer, serverIncomingApplicationBuffer);
		handshakeBytesConsumed += result.bytesConsumed();
		Assert.assertEquals(HandshakeStatus.NEED_WRAP, result.getHandshakeStatus());

		Assert.assertEquals(bytesRead, handshakeBytesConsumed);
		
		// Generate response to the messages
		// handshake.msg.1
		result = serverEngine.wrap(serverOutgoingApplicationBuffer, serverOutgoingNetworkBuffer);
		Assert.assertEquals(HandshakeStatus.NEED_WRAP, result.getHandshakeStatus());

		// handshake.msg.2
		result = serverEngine.wrap(serverOutgoingApplicationBuffer, serverOutgoingNetworkBuffer);
		Assert.assertEquals(HandshakeStatus.FINISHED, result.getHandshakeStatus());

		// Send bytes to the client ServerSession
		serverOutgoingNetworkBuffer.flip();
		writableBytes = serverOutgoingNetworkBuffer.limit() - serverOutgoingNetworkBuffer.position();
		bytesWritten = senderChannel.write(serverOutgoingNetworkBuffer);
		serverSession.transferBytes();
		
		Assert.assertEquals(PacketSourceType.SECURE_SERVER, serverSession.getSourceType());
		Assert.assertEquals(HandshakeStatus.NOT_HANDSHAKING, serverEngine.getHandshakeStatus());
		
		return serverEngine;
	}

	protected static SSLEngine makeClientSessionSecure(MockChannel senderChannel, ClientSession clientSession) throws IOException, SSLException {
		SSLEngine clientEngine = createClientSSLEngine();
		SSLSession sslSession = clientEngine.getSession();
		
		int networkBufferSize = sslSession.getPacketBufferSize() * 4;
		int applicationBufferSize = sslSession.getApplicationBufferSize();

		ByteBuffer clientIncomingNetworkBuffer = ByteBuffer.allocate(networkBufferSize);
		ByteBuffer clientIncomingApplicationBuffer = ByteBuffer.allocate(applicationBufferSize);
		ByteBuffer clientOutgoingNetworkBuffer = ByteBuffer.allocate(networkBufferSize);
		ByteBuffer clientOutgoingApplicationBuffer = ByteBuffer.allocate(1);

		// We're the client, so send the CONNECT request.
		senderChannel.write(ByteBuffer.wrap(connectRequest.getBytes()));
		clientSession.transferBytes();
		
		// We start off not in handshaking mode
		Assert.assertEquals(HandshakeStatus.NOT_HANDSHAKING, clientEngine.getHandshakeStatus());

		// Begin handshaking
		clientEngine.beginHandshake();
		Assert.assertEquals(HandshakeStatus.NEED_WRAP, clientEngine.getHandshakeStatus());

		SSLEngineResult result;
		result = clientEngine.wrap(clientOutgoingApplicationBuffer, clientOutgoingNetworkBuffer);
		Assert.assertEquals(HandshakeStatus.NEED_UNWRAP, result.getHandshakeStatus());

		clientOutgoingNetworkBuffer.flip();
		int writableBytes = clientOutgoingNetworkBuffer.limit() - clientOutgoingNetworkBuffer.position();
		int bytesWritten = senderChannel.write(clientOutgoingNetworkBuffer);
		clientSession.transferBytes();
		Assert.assertEquals(writableBytes, bytesWritten);

		// Handle handshake.msg.1
		clientIncomingNetworkBuffer.clear();
		int bytesRead = senderChannel.read(clientIncomingNetworkBuffer);
		Assert.assertTrue(bytesRead > 0);
		clientIncomingNetworkBuffer.flip();

		int handshakeBytesConsumed = 0;
		result = clientEngine.unwrap(clientIncomingNetworkBuffer, clientIncomingApplicationBuffer);
		handshakeBytesConsumed += result.bytesConsumed();
		Assert.assertEquals(HandshakeStatus.NEED_TASK, result.getHandshakeStatus());

		runDelegatedTasks(clientEngine, result.getHandshakeStatus());
		Assert.assertEquals(HandshakeStatus.NEED_WRAP, clientEngine.getHandshakeStatus());

		// Respond to handshake.msg.1
		result = clientEngine.wrap(clientOutgoingApplicationBuffer, clientOutgoingNetworkBuffer);
		Assert.assertEquals(HandshakeStatus.NEED_WRAP, result.getHandshakeStatus());

		result = clientEngine.wrap(clientOutgoingApplicationBuffer, clientOutgoingNetworkBuffer);
		Assert.assertEquals(HandshakeStatus.NEED_WRAP, result.getHandshakeStatus());

		result = clientEngine.wrap(clientOutgoingApplicationBuffer, clientOutgoingNetworkBuffer);
		Assert.assertEquals(HandshakeStatus.NEED_UNWRAP, result.getHandshakeStatus());

		clientOutgoingNetworkBuffer.flip();
		writableBytes = clientOutgoingNetworkBuffer.limit() - clientOutgoingNetworkBuffer.position();
		bytesWritten = senderChannel.write(clientOutgoingNetworkBuffer);
		clientSession.transferBytes();

		// Handle handshake.msg.2
		clientIncomingNetworkBuffer.clear();
		bytesRead = senderChannel.read(clientIncomingNetworkBuffer);
		Assert.assertTrue(bytesRead > 0);
		clientIncomingNetworkBuffer.flip();

		result = clientEngine.unwrap(clientIncomingNetworkBuffer, clientIncomingApplicationBuffer);
		handshakeBytesConsumed += result.bytesConsumed();
		Assert.assertEquals(HandshakeStatus.NEED_UNWRAP, result.getHandshakeStatus());

		result = clientEngine.unwrap(clientIncomingNetworkBuffer, clientIncomingApplicationBuffer);
		handshakeBytesConsumed += result.bytesConsumed();
		Assert.assertEquals(HandshakeStatus.FINISHED, result.getHandshakeStatus());
		
		return clientEngine;
	}
	
	protected static void sendMessageThroughEncryption(MockChannel senderChannel, MockChannel receiverChannel, Session session,
		SSLEngine decryptingEngine, SSLEngine encryptingEngine)
		throws IOException
	{
		// Encrypt a message to send from the client
		byte[] originalMessage = "I hear a man on a white horse approaching!".getBytes();
		ByteBuffer encryptedMessage = encrypt(encryptingEngine, originalMessage);
		encryptedMessage.flip();
		senderChannel.write(encryptedMessage);

		// transferBytes() causes the clientSession to decrypt with his decrypt(),
		// re-encrypt using the serverSession's encrypt() (which we're testing),
		// then write to the clientSession's receiverChannel
		receiverChannel.clear();
		session.transferBytes();

		ByteBuffer receivedMessage = ByteBuffer.allocate(1024);
		receiverChannel.read(receivedMessage);
		
		byte[] decryptedBytes = decrypt(decryptingEngine, receivedMessage);
		Assert.assertEquals(new String(originalMessage), new String(decryptedBytes));
	}

	protected static byte[] decrypt(SSLEngine sslEngine, ByteBuffer encryptedData) throws IOException {

		SSLSession sslSession = sslEngine.getSession();
		int applicationBufferSize = sslSession.getApplicationBufferSize() * 4;
		ByteBuffer applicationData = ByteBuffer.allocate(applicationBufferSize);

		encryptedData.flip();
		sslEngine.unwrap(encryptedData, applicationData);
		encryptedData.compact();

		applicationData.flip();
		byte[] decryptedData = new byte[applicationData.limit()];
		applicationData.get(decryptedData, 0, applicationData.limit());

		return decryptedData;
	}

	protected static ByteBuffer encrypt(SSLEngine sslEngine, byte[] originalMessage) throws IOException {
		SSLSession sslSession = sslEngine.getSession();
		int applicationBufferSize = sslSession.getApplicationBufferSize() * 4;
		ByteBuffer encryptedData = ByteBuffer.allocate(applicationBufferSize);

		SSLEngineResult result = null;
		ByteBuffer clearBuffer = ByteBuffer.wrap(originalMessage);
		ByteBuffer tempBuffer = ByteBuffer.allocate(17 * 1024);		// The most cleartext encrypt will read is 16384

		while(clearBuffer.position() < clearBuffer.limit()) {
			tempBuffer.clear();
			result = sslEngine.wrap(clearBuffer, tempBuffer);
			if(result.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW)
				throw new IOException(SSLEngineResult.Status.BUFFER_OVERFLOW.toString());
			if(result.bytesConsumed() == 0)
				break;

			tempBuffer.flip();
			if(encryptedData.remaining() < tempBuffer.remaining()){
				ByteBuffer newEncryptedBuffer = ByteBuffer.allocate(encryptedData.capacity() + encryptedData.limit());
				encryptedData.flip();
				newEncryptedBuffer.put(encryptedData);
				encryptedData = newEncryptedBuffer;
			}
			encryptedData.put(tempBuffer);
		}
		
		return encryptedData;
	}

	private static SSLEngine createServerSSLEngine() throws IOException {
		SSLEngine sslEngine = SSLContextFactory.getInstance().createSSLEngine();
		sslEngine.setUseClientMode(false);
		sslEngine.setNeedClientAuth(false);
		sslEngine.setWantClientAuth(false);
		return sslEngine;
	}
	
	private static SSLEngine createClientSSLEngine() throws IOException {
		SSLEngine sslEngine = SSLContextFactory.getInstance().createSSLEngine();
		sslEngine.setUseClientMode(true);
		return sslEngine;
	}
	
	protected static void runDelegatedTasks(SSLEngine engine, HandshakeStatus status) {
		Runnable delegatedTask;
		if (status == HandshakeStatus.NEED_TASK) {
			while ((delegatedTask = engine.getDelegatedTask()) != null) {
				if(log.isDebugEnabled())
					log.debug("Run delegated task...");
				delegatedTask.run();
			}
		}
	}

}
