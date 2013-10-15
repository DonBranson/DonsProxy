/*
 * Created on October 20, 2007
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
import java.nio.channels.ByteChannel;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;

import com.moneybender.proxy.channels.IReadBytes;
import com.moneybender.proxy.channels.IWriteBytes;
import com.moneybender.proxy.publishers.PacketSourceType;
import com.moneybender.proxy.publishers.SubscriberManager;

// SSL Debugging:
// http://java.sun.com/j2se/1.5.0/docs/guide/security/jsse/JSSERefGuide.html#Debug
// -Djavax.net.debug=ssl:handshake

public abstract class SecurableSession extends Session implements IEncrypt {

	private SSLEngine sslEngine;
	private IEncrypt encrypter;
	
	private boolean secured;

	protected enum ConnectMessageState {
		NONE, PARTIAL, FULL
	}

	protected SecurableSession(int sessionID, PacketSourceType sourceType, ByteChannel senderChannel,
		ByteChannel receiverChannel, SubscriberManager manager, IReadBytes byteReader, IWriteBytes byteWriter, String senderIP, int senderPort)
	{
		super(sessionID, sourceType, senderChannel, receiverChannel, manager, byteReader, byteWriter, senderIP, senderPort);
		
		secured = false;
	}

	public SSLEngine getSslEngine() {
		return sslEngine;
	}

	public void setSslEngine(SSLEngine sslEngine) {
		this.sslEngine = sslEngine;
	}

	public IEncrypt getEncrypter() {
		return encrypter;
	}

	public void setEncrypter(IEncrypt encrypter) {
		this.encrypter = encrypter;
	}

	@Override
	protected int readBytesFromChannelIntoReadBuffer() throws IOException {
		
		if(isSecured() && !getEncrypter().isSecured()){
			return 0;
		}
		
		return super.readBytesFromChannelIntoReadBuffer();
	}

	protected boolean handshake(ByteBuffer incomingBuffer) throws IOException {
		
		SSLSession sslSession = getSslEngine().getSession();
		ByteBuffer applicationData = null;
		
		int saveLimit = 0;
		int savePosition = 0;
	
		SSLEngineResult result = null;
		while(true) {
	
			if(result != null && result.getStatus() == Status.BUFFER_UNDERFLOW) {
				// Leave the remaining bytes for the next pass.
				incomingBuffer.limit(saveLimit);
				incomingBuffer.position(savePosition);
				
				return false;
			}
			
			if(log.isTraceEnabled()) {
				log.trace("handshake 1 - lim=" + incomingBuffer.limit() + "; pos=" + incomingBuffer.position() + "; shake=" + getSslEngine().getHandshakeStatus());
			}
			
			switch (getSslEngine().getHandshakeStatus()) {
				case NEED_UNWRAP:
					if(!incomingBuffer.hasRemaining())
						return false;
					if(applicationData == null)
						applicationData = createApplicationBuffer(sslSession);
	
					saveLimit = incomingBuffer.limit();
					savePosition = incomingBuffer.position();
					incomingBuffer.flip();
					result = getSslEngine().unwrap(incomingBuffer, applicationData);
					incomingBuffer.compact();
					
					if (result.getHandshakeStatus() == HandshakeStatus.FINISHED) {
						// A server-side finish
						if(log.isTraceEnabled()) {
							log.trace("handshake 2a - lim=" + incomingBuffer.limit() + "; pos=" + incomingBuffer.position() + "; shake=" + getSslEngine().getHandshakeStatus());
							log.trace("handshake 2b - alim=" + applicationData.limit() + "; apos=" + applicationData.position());
						}
						return true;
					}
					
					break;
					
				case NEED_TASK:
					runDelegatedTasks(getSslEngine(), result);
					break;
					
				case NEED_WRAP:
					// A client-side finish
					ByteBuffer outgoingBuffer = createOutgoingBuffer(sslSession);
					result = getSslEngine().wrap(applicationData, outgoingBuffer);
					writeBytesToChannel(outgoingBuffer, getSenderChannel());

					if (result.getHandshakeStatus() == HandshakeStatus.FINISHED) {
						if(log.isTraceEnabled()) {
							log.trace("handshake 3 - lim=" + incomingBuffer.limit() + "; pos=" + incomingBuffer.position() + "; shake=" + getSslEngine().getHandshakeStatus());
						}
						return true;
					}
					
					break;
					
				case NOT_HANDSHAKING:
					getSslEngine().beginHandshake();
					applicationData = ByteBuffer.allocate(4096);
					break;
	
				case FINISHED:
					if(log.isTraceEnabled()) {
						log.trace("handshake 4 - lim=" + incomingBuffer.limit() + "; pos=" + incomingBuffer.position() + "; shake=" + getSslEngine().getHandshakeStatus());
					}
					return true;
	
			}
		}
	}

	protected byte[] decrypt(ByteBuffer encryptedData) throws IOException {

		SSLSession sslSession = getSslEngine().getSession();
		ByteBuffer applicationData = null;
		
		int saveLimit = 0;
		int savePosition = 0;
	
		SSLEngineResult result = null;
		encryptedData.flip();
		while(true) {

			if(result != null && result.getStatus() == Status.BUFFER_UNDERFLOW) {
				// Leave the remaining bytes for the next pass.
				encryptedData.limit(saveLimit);
				encryptedData.position(savePosition);
			
				if(log.isTraceEnabled())
					log.trace("decrypt 1 - " + result + "; lim=" + encryptedData.limit() + "; pos=" + encryptedData.position() + "; shake=" + getSslEngine().getHandshakeStatus());
				
				break;
			}

			if(result != null && result.getStatus() == Status.BUFFER_OVERFLOW) {
				throw new IOException("Can't allocate buffer to handle decrypted data.");
			}

			if(!encryptedData.hasRemaining()) {

				if(log.isTraceEnabled())
					log.trace("decrypt 2 - " + result + "; lim=" + encryptedData.limit() + "; pos=" + encryptedData.position() + "; shake=" + getSslEngine().getHandshakeStatus());
				
				break;
			}

			if(applicationData == null) {
				int sizeToAllocate = sslSession.getApplicationBufferSize();
				if(encryptedData.remaining() * 2 > sizeToAllocate)
					sizeToAllocate = encryptedData.remaining() * 2;
				applicationData = ByteBuffer.allocate(sizeToAllocate);
			}

			saveLimit = encryptedData.limit();
			savePosition = encryptedData.position();
			
			result = getSslEngine().unwrap(encryptedData, applicationData);
			if(getSslEngine().getHandshakeStatus() == HandshakeStatus.NEED_WRAP){
				ByteBuffer outgoingBuffer = ByteBuffer.allocate(sslSession.getPacketBufferSize());;
				result = getSslEngine().wrap(ByteBuffer.allocate(0), outgoingBuffer);
				writeBytesToChannel(outgoingBuffer, getSenderChannel());
			}
			
			if(result.getStatus() == Status.CLOSED) {
				if(log.isTraceEnabled())
					log.trace("decrypt 3 - " + result + "; lim=" + encryptedData.limit() + "; pos=" + encryptedData.position() + "; shake=" + getSslEngine().getHandshakeStatus());
				getSenderChannel().close();
				break;
			}
		}		

		encryptedData.compact();
		applicationData.flip();

		byte[] decryptedData = new byte[applicationData.limit()];
		applicationData.get(decryptedData, 0, applicationData.limit());

		return decryptedData;
	}

	public ByteBuffer encrypt(byte[] cleartextData) throws IOException {

		int totalBytesConsumed = 0;
		
		SSLSession sslSession = getSslEngine().getSession();
		ByteBuffer encryptedData = ByteBuffer.allocate(sslSession.getApplicationBufferSize());

		SSLEngineResult result = null;
		ByteBuffer clearBuffer = ByteBuffer.wrap(cleartextData);
		ByteBuffer tempBuffer = ByteBuffer.allocate(17 * 1024);		// The most cleartext encrypt will read is 16384
		
		tempBuffer.clear();
		result = sslEngine.wrap(clearBuffer, tempBuffer);
		
		if(result.getStatus() == SSLEngineResult.Status.CLOSED)
			log.warn("Processed SSL CLOSE on session " + getSessionID());
		
		if(result.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW)
			throw new IOException(SSLEngineResult.Status.BUFFER_OVERFLOW.toString());

		if(result.bytesConsumed() != 0) {
			tempBuffer.flip();
			if(encryptedData.remaining() < tempBuffer.remaining()){
				ByteBuffer newEncryptedBuffer = ByteBuffer.allocate(encryptedData.capacity() + encryptedData.limit());
				encryptedData.flip();
				newEncryptedBuffer.put(encryptedData);
				encryptedData = newEncryptedBuffer;
			}
			encryptedData.put(tempBuffer);

			totalBytesConsumed += result.bytesConsumed();
		}
		
		if(totalBytesConsumed != cleartextData.length) {
			log.warn("Encrypted " + result.bytesConsumed() + " of " + cleartextData.length + " bytes.  result=" + result
				+ " encryptedBuffer=" + encryptedData + "; session=" + getSessionID());
			log.info("Orphaned cleartext:" + new String(cleartextData));
		}

		if (log.isTraceEnabled())
			log.trace("State after wrap: " + result);

		return encryptedData;
	}

	@Override
	protected void setSourceType(PacketSourceType sourceType) {
		super.setSourceType(sourceType);
		if(getSourceType() == PacketSourceType.SECURE_CLIENT || getSourceType() == PacketSourceType.SECURE_SERVER){
			secured = true;
		} else {
			secured = false;
		}
	}
	
	public boolean isSecured() {
		return secured;
	}

	protected void encryptToWriteBuffer(byte[] decryptedData) throws IOException {
		ByteBuffer encryptedData = getEncrypter().encrypt(decryptedData);
		ByteBuffer writeBuffer = getWriteBuffer();
		
		if(writeBuffer.remaining() < encryptedData.limit()){
			ByteBuffer newWriteBuffer = ByteBuffer.allocate(writeBuffer.capacity() + encryptedData.limit());
			writeBuffer.flip();
			newWriteBuffer.put(writeBuffer);
			setWriteBuffer(newWriteBuffer);
		}

		encryptedData.flip();
		getWriteBuffer().put(encryptedData);
	}

	protected void runDelegatedTasks(SSLEngine engine, SSLEngineResult result) {
		Runnable delegatedTask;
		if (result.getHandshakeStatus() == HandshakeStatus.NEED_TASK) {
			while ((delegatedTask = engine.getDelegatedTask()) != null) {
				if (log.isTraceEnabled())
					log.trace("Run delegated task...");
				delegatedTask.run();
			}
		}

		if (log.isTraceEnabled())
			log.trace("State after runDelegatedTasks: Handshake:" + getSslEngine().getHandshakeStatus());
	}

	protected abstract void createSSLEngine() throws IOException;
}