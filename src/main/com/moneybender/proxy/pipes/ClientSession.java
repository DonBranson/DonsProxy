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

public class ClientSession extends SecurableSession {

	private enum ConnectionState {
		CLEARTEXT, WAITING_FOR_CLIENT_HELLO, WAITING_FOR_CLIENT_CERTIFICATE, SECURE
	};

	private ConnectionState state;
	private ConnectMessageState connectRequestState;

	public ClientSession(int sessionID, ByteChannel senderChannel, ByteChannel receiverChannel,
		SubscriberManager manager, IReadBytes byteReader, IWriteBytes byteWriter, String senderIP, int senderPort) {
		super(sessionID, PacketSourceType.CLIENT, senderChannel, receiverChannel, manager, byteReader, byteWriter, senderIP, senderPort);

		state = ConnectionState.CLEARTEXT;
		connectRequestState = ConnectMessageState.NONE;
	}

	@Override
	protected byte[] processBytesRead(ByteBuffer buffer, int start) throws IOException {

		if(log.isDebugEnabled())
			log.debug("Session " + getSessionID() + " state = " + state);

		byte[] readBuffer = null;

		switch(state) {

			case CLEARTEXT:
				readBuffer = super.processBytesRead(buffer, start);

				// We do this because it's possible that the buffer contains the beginning or end of
				// a connect response, but not the other.
				if(connectRequestState == ConnectMessageState.NONE){
					connectRequestState = receivedConnectRequest(readBuffer);
				} else if(connectRequestState == ConnectMessageState.PARTIAL){
					if(hasHeaderEnd(readBuffer, 0)){
						connectRequestState = ConnectMessageState.FULL;
					}
				}
				
				if (connectRequestState == ConnectMessageState.FULL && getEncrypter() != null) {
					writeBytesToChannel(buffer, getReceiverChannel());

					// We could let publish happen after the handshake, but I prefer to go ahead and let the subscribers know now. 
					publish(readBuffer);
					readBuffer = null;

					createSSLEngine();
					setWriteBuffer(createOutgoingBuffer(getSslEngine().getSession()));
					state = ConnectionState.WAITING_FOR_CLIENT_HELLO;
				}
				break;
				
			case WAITING_FOR_CLIENT_HELLO:
				handshake(buffer);
				state = ConnectionState.WAITING_FOR_CLIENT_CERTIFICATE;
				break;
				
			case WAITING_FOR_CLIENT_CERTIFICATE:
				if(handshake(buffer)) {
					log.info(getSourceType() + " on session " + getSessionID() + " is now a " + PacketSourceType.SECURE_CLIENT);
					
					setSourceType(PacketSourceType.SECURE_CLIENT);
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

	@Override
	protected void createSSLEngine() throws IOException {

		SSLEngine sslEngine = SSLContextFactory.getInstance().createSSLEngine();
		sslEngine.setUseClientMode(false);
		sslEngine.setNeedClientAuth(false);
		sslEngine.setWantClientAuth(false);
		
		// FIXME:  There's some kind of problem with SSLv3 where the signature we use to sign packets for the
		// client is not accepted by the client.
		sslEngine.setEnabledProtocols(new String[]{"TLSv1"});

		setSslEngine(sslEngine);
	}

	private ConnectMessageState receivedConnectRequest(byte[] publishBuffer) throws IOException {

		if(publishBuffer.length == 0)
			return ConnectMessageState.NONE;
		
		byte[] connectPrefix = "CONNECT".getBytes();
		int index;

		for (index = 0; index < connectPrefix.length; index++) {
			if (connectPrefix[index] != publishBuffer[index]) {
				return ConnectMessageState.NONE;
			}
		}

		log.info(getSourceType() + " sent CONNECT request on session " + getSessionID());
		if (log.isTraceEnabled())
			log.trace("Connect Request:\n" + new String(publishBuffer));

		if(hasHeaderEnd(publishBuffer, index)){
			return ConnectMessageState.FULL;
		}

		return ConnectMessageState.PARTIAL;
	}

	@Override
	protected void publish(byte[] buffer) {
		publishRequest(buffer);
	}

}