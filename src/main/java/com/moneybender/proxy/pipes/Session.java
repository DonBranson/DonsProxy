/*
 * Created on June 8, 2007
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

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.net.ssl.SSLSession;

import org.apache.log4j.Logger;

import com.moneybender.proxy.channels.IReadBytes;
import com.moneybender.proxy.channels.IWriteBytes;
import com.moneybender.proxy.publishers.PacketHeader;
import com.moneybender.proxy.publishers.PacketIdentifier;
import com.moneybender.proxy.publishers.PacketSourceType;
import com.moneybender.proxy.publishers.SubscriberManager;

public abstract class Session {

	protected Logger log = Logger.getLogger(this.getClass());

	private final int sessionID;
	private final ByteChannel receiverChannel;
	private final ByteChannel senderChannel;
	private final SubscriberManager manager;
	private final String senderIP;
	private final int senderPort;
	private byte[] backingBuffer;
	private ByteBuffer readBuffer;
	private ByteBuffer writeBuffer;
	private int packetID;
	private PacketSourceType sourceType;
	
	private int totalBytesRead;
	private int totalBytesWritten;
	
	private IReadBytes byteReader;
	private IWriteBytes byteWriter;
	
	public Session(int sessionID, PacketSourceType sourceType, ByteChannel senderChannel, ByteChannel receiverChannel,
		SubscriberManager manager, IReadBytes byteReader, IWriteBytes byteWriter, String senderIP, int senderPort)
	{
		this.sessionID = sessionID;
		this.setSourceType(sourceType);
		this.senderChannel = senderChannel;
		this.receiverChannel = receiverChannel;
		this.manager = manager;
		this.byteReader = byteReader;
		this.byteWriter = byteWriter;
		this.senderIP = senderIP;
		this.senderPort = senderPort;
		
		this.backingBuffer = new byte[16 * 1024];
		this.readBuffer = ByteBuffer.wrap(backingBuffer);
		this.writeBuffer = this.readBuffer; 
		this.packetID = 0;

		this.totalBytesRead = 0;
		this.totalBytesWritten = 0;
	}

	protected ByteChannel getSenderChannel() {
		return senderChannel;
	}

	protected ByteChannel getReceiverChannel() {
		return receiverChannel;
	}

	public int getSessionID() {
		return sessionID;
	}

	protected ByteBuffer getReadBuffer() {
		return readBuffer;
	}

	protected int getNextPacketID() {
		return ++packetID;
	}

	protected PacketSourceType getSourceType() {
		return sourceType;
	}

	protected SubscriberManager getManager() {
		return manager;
	}

	protected void setWriteBuffer(ByteBuffer writeBuffer) {
		this.writeBuffer = writeBuffer;
	}

	protected ByteBuffer getWriteBuffer() {
		return writeBuffer;
	}

	public int transferBytes() throws IOException {
	
		IOException thrownExceptionOnRead = null;
		
		int bytesRead = 0;
		try{
			if(log.isTraceEnabled())
				log.trace(getSourceType() + " session " + getSessionID() + " put bytes on the wire.");

			bytesRead = readBytesFromChannelIntoReadBuffer();
		}catch(IOException e){
			thrownExceptionOnRead = e;
		}

		// TODO: Write a test that verifies that we write bytes when possible, even after the read channel is closed.
		int bytesToWrite = writeBuffer.position();
		int bytesWritten = 0;
		try {
			bytesWritten = writeBytesFromWriteBufferToChannel();
		}catch(IOException e){
			throw new IOException(getSourceType() + " on session " + getSessionID()
				+ " had an error writing bytes: " + e.getMessage(), e.getCause());
		}

		if(thrownExceptionOnRead != null){
			if(thrownExceptionOnRead.getMessage() != null
			&& thrownExceptionOnRead.getMessage().equals("Connection reset by peer"))
				return -1;

			throw new IOException(getSourceType() + " on session " + getSessionID()
					+ " had an error reading bytes: " + thrownExceptionOnRead.getMessage(), thrownExceptionOnRead.getCause());
		}
		if(bytesWritten < bytesToWrite)
			throw new BytesLeftToWriteException(this, writeBuffer.position(), " Orphaning " + writeBuffer.position()
				+ " writable bytes.");
		if(readBuffer.position() != 0)
			throw new BytesLeftToReadException(this, readBuffer.position(), " Orphaning " + readBuffer.position()
				+ " readable bytes.");

		return bytesRead;
	}

	protected int readBytesFromChannelIntoReadBuffer() throws IOException {
		int start = readBuffer.position();
		int bytesRead = 0;
		
		boolean goodRead = false;
		do {
			try {
				bytesRead = readBytesFromChannel(readBuffer, senderChannel);
				goodRead = true;
			}catch(BufferOverflowException e){
				if(log.isTraceEnabled())
					log.trace("Buffer overflow reading bytes from channel on session " + getSessionID());

				ByteBuffer newBuffer = ByteBuffer.allocate(readBuffer.capacity() * 5 / 4);
				readBuffer.flip();
				newBuffer.put(readBuffer);
				readBuffer = newBuffer;
			}
		}while(!goodRead);

		if (bytesRead == -1) {

			log.info(getSourceType() + " closed session " + getSessionID());
			senderChannel.close();
			
			PacketHeader header = makeEndingPacketHeader();
			manager.publishRequest(header, new byte[0]);
			manager.publishResponse(header, new byte[0]);

			return -1;
		}
		
		totalBytesRead += bytesRead;
		
		if(bytesRead > 0 && log.isDebugEnabled())
			log.debug(getSourceType() + " on session " + getSessionID() + " sent " + bytesRead + " bytes.");

		if (readBuffer.position() > 0) {
			processWaitingBytes(start);
		}

		return bytesRead;
	}
	
	private void processWaitingBytes(int start) throws IOException {
		byte[] transferBuffer = processBytesRead(readBuffer, start);
		if (transferBuffer != null && transferBuffer.length > 0) {
			publish(transferBuffer);
		}
	}

	private int writeBytesFromWriteBufferToChannel() throws IOException {
		if (writeBuffer.position() == 0) {
			if(!senderChannel.isOpen()) {
				receiverChannel.close();
			}
		} else {
			int bytesWritten = 0;
			if(receiverChannel.isOpen()) {
				bytesWritten = writeBytesToChannel(writeBuffer, receiverChannel);
				
				if(bytesWritten != -1)
					totalBytesWritten += bytesWritten;
				
			} else {
				log.error("Closing session " + getSessionID() + " with " + writeBuffer.position() + " bytes remaining from " + getSourceType());
				senderChannel.close();
			}

			if(log.isDebugEnabled())
				log.debug("Transferred " + bytesWritten + " bytes from " + getSourceType() + " on session " + getSessionID());

			return bytesWritten;
		}
		
		return 0;
	}

	protected byte[] processBytesRead(ByteBuffer buffer, int start) throws IOException {
		int end = buffer.position();
		byte[] transferBuffer = new byte[end - start];
		System.arraycopy(backingBuffer, start, transferBuffer, 0, transferBuffer.length);
	
		return transferBuffer;
	}

	private int readBytesFromChannel(ByteBuffer buffer, ReadableByteChannel channel) throws IOException
	{
		return byteReader.readBytesFromChannel(buffer, channel);
	}

	protected int writeBytesToChannel(ByteBuffer buffer, WritableByteChannel channel) throws IOException
	{
		try {
			return byteWriter.writeBytesToChannel(buffer, channel);
		} catch (IOException e) {
			senderChannel.close();
			
			if(e instanceof ClosedChannelException)
				throw new IOException("Error writing bytes - receiver closed.", e);

			throw e;
		}
	}

	protected ByteBuffer createApplicationBuffer(SSLSession sslSession) {
		int applicationBufferSize = sslSession.getApplicationBufferSize();
		ByteBuffer applicationData = ByteBuffer.allocate(applicationBufferSize);
		return applicationData;
	}

	protected ByteBuffer createOutgoingBuffer(SSLSession sslSession) {
		int networkBufferSize = sslSession.getPacketBufferSize();
		ByteBuffer outgoingDataBuffer = ByteBuffer.allocate(networkBufferSize);
		return outgoingDataBuffer;
	}

	protected boolean hasHeaderEnd(byte[] buffer, int startingIndex) {
		// rfc2616, Section 2.2. - "HTTP/1.1 defines the sequence CR LF as the end-of-line marker for all 
		// protocol elements except the entity-body"
		int consecutiveCRs = 0;
		for(; startingIndex < buffer.length - 1; startingIndex++) {
			if(buffer[startingIndex] == '\r' && buffer[startingIndex + 1] == '\n'){
				if(++consecutiveCRs == 2){
					return true;
				}
				++startingIndex;
			} else {
				consecutiveCRs = 0;
			}
		}
		
		return false;
	}

	public int getWriteBufferCapacity() {
		return writeBuffer.capacity();
	}

	public int getWriteBufferLimit() {
		return writeBuffer.limit();
	}

	public int getWriteBufferPosition() {
		return writeBuffer.position();
	}

	public int getTotalBytesWritten() {
		return totalBytesWritten;
	}

	public int getReadBufferCapacity() {
		return readBuffer.capacity();
	}

	public int getReadBufferLimit() {
		return readBuffer.limit();
	}

	public int getReadBufferPosition() {
		return readBuffer.position();
	}

	public int getTotalBytesRead() {
		return totalBytesRead;
	}

	public String getSourceTypeName() {
		return sourceType.toString();
	}

	protected void setSourceType(PacketSourceType sourceType) {
		this.sourceType = sourceType;
	}

	private PacketHeader makePacketHeader(byte[] buffer) {
		return new PacketHeader(getSourceType(), getSessionID(), getNextPacketID(), buffer.length, senderIP, senderPort, System.currentTimeMillis());
	}

	private PacketHeader makeEndingPacketHeader() {
		return new PacketHeader(getSourceType(), getSessionID(), PacketIdentifier.SESSION_CLOSE, 0, senderIP, senderPort, System.currentTimeMillis());
	}

	protected void publishRequest(byte[] buffer) {
		manager.publishRequest(makePacketHeader(buffer), buffer);
	}

	protected void publishResponse(byte[] buffer) {
		manager.publishResponse(makePacketHeader(buffer), buffer);
	}

	public boolean isOpenReceiverChannel() {
		return receiverChannel.isOpen();
	}

	public boolean isOpenSenderChannel() {
		return senderChannel.isOpen();
	}

	abstract protected void publish(byte[] buffer);
}