/*
 * Created on Jun 11, 2007
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

import com.moneybender.proxy.channels.IReadBytes;
import com.moneybender.proxy.channels.IWriteBytes;
import com.moneybender.proxy.publishers.PacketSourceType;
import com.moneybender.proxy.publishers.SubscriberManager;

public class ServerSession extends SecurableSession {

	private enum ConnectionState {
		CLEARTEXT, CONNECT_ACKNOWLEDGED, SECURE
	};
	
	private ConnectionState state;
	private ConnectMessageState connectResponseState;

	public ServerSession(int sessionID, ByteChannel senderChannel, ByteChannel receiverChannel,
		SubscriberManager manager, IReadBytes byteReader, IWriteBytes byteWriter, String senderIP, int senderPort) {
		super(sessionID, PacketSourceType.SERVER, senderChannel, receiverChannel, manager, byteReader, byteWriter, senderIP, senderPort);

		state = ConnectionState.CLEARTEXT;
		connectResponseState = ConnectMessageState.NONE;
	}

	@Override
	protected byte[] processBytesRead(ByteBuffer buffer, int start) throws IOException {

		if (log.isDebugEnabled())
			log.debug("Session " + getSessionID() + " state = " + state);

		byte[] readBuffer = null;

		switch (state) {

			case CLEARTEXT:
				readBuffer = super.processBytesRead(buffer, start);
				
				// We do this because it's possible that the buffer contains the beginning or end of
				// a connect response, but not the other.
				if(connectResponseState == ConnectMessageState.NONE){
					connectResponseState = receivedConnectResponse(readBuffer);
				} else if(connectResponseState == ConnectMessageState.PARTIAL){
					if(hasHeaderEnd(readBuffer, 0)){
						connectResponseState = ConnectMessageState.FULL;
					}
				}
				
				if (connectResponseState == ConnectMessageState.FULL && getEncrypter() != null) {
					writeBytesToChannel(buffer, getReceiverChannel());

					// We could let publish happen after the handshake, but I prefer to go ahead and let the subscribers know now. 
					publish(readBuffer);
					readBuffer = null;

					createSSLEngine();
					setWriteBuffer(createOutgoingBuffer(getSslEngine().getSession()));
					handshake(ByteBuffer.allocate(0));
					state = ConnectionState.CONNECT_ACKNOWLEDGED;
				}
				
				break;

			case CONNECT_ACKNOWLEDGED:
				if(handshake(buffer)){
					log.info(getSourceType() + " on session " + getSessionID() + " is now a " + PacketSourceType.SECURE_SERVER);
					if(log.isTraceEnabled())
						log.trace("ack - lim=" + buffer.limit() + "; pos=" + buffer.position() + "; shake=" + getSslEngine().getHandshakeStatus());

					setSourceType(PacketSourceType.SECURE_SERVER);
					state = ConnectionState.SECURE;
					
					// If our partner completed handshaking first, there could be bytes waiting for transfer.
					if(getEncrypter().isSecured()) {
						getEncrypter().transferBytes();
					}
				}
				
				break;

			case SECURE:
				byte[] decryptedData = decrypt(buffer);

				if(decryptedData.length > 0) {
					if(log.isTraceEnabled())
						log.trace(getSourceType() + " on session " + getSessionID() + " sending encrypted data.");
					encryptToWriteBuffer(decryptedData);
					readBuffer = decryptedData;
				}
				
				break;

			default:
				throw new IOException(this.getClass().getSimpleName() + " in an unknown state: " + state);
		}

		return readBuffer;
	}

	private ConnectMessageState receivedConnectResponse(byte[] publishBuffer) throws IOException {

		if(publishBuffer.length == 0)
			return ConnectMessageState.NONE;
		
		byte[] connectionResponsePrefix = "HTTP/?.? 200 Connection Established".getBytes();
		int index;
		for (index = 0; index < connectionResponsePrefix.length && index < publishBuffer.length; index++) {
			if (connectionResponsePrefix[index] == '?') {
				continue;
			}
			if (connectionResponsePrefix[index] != publishBuffer[index]) {
				return ConnectMessageState.NONE;
			}
		}
		
		log.info(getSourceType() + " sent CONNECT response on session " + getSessionID());
		if (log.isTraceEnabled())
			log.trace("Connect Response:\n" + new String(publishBuffer));
		
		if(hasHeaderEnd(publishBuffer, index)){
			return ConnectMessageState.FULL;
		}

		return ConnectMessageState.PARTIAL;
	}

	@Override
	protected void createSSLEngine() throws IOException {

		SSLEngine sslEngine = SSLContextFactory.getInstance().createSSLEngine();
		sslEngine.setUseClientMode(true);

		setSslEngine(sslEngine);
	}

	@Override
	protected void publish(byte[] buffer) {
		publishResponse(buffer);
	}
}