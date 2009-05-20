/*
 * Created on May 12, 2007
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
package com.moneybender.proxy.publishers;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class PacketHeader implements Externalizable {

	private static final long serialVersionUID = 7616175263804826238L;
	
	private final byte VERSION;
	private PacketSourceType sourceType;
	private String senderIP;
	private int senderPort;
	private int dataLength;
	private PacketIdentifier packetIdentifier;
	private long timestamp;

	public PacketHeader() {
		this.VERSION = 2;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public PacketHeader(PacketSourceType sourceType, int sessionID, int packetID, int bytesRead, String senderIP, int senderPort, long timestamp) {
		this.VERSION = 2;
		this.sourceType = sourceType;
		this.senderIP = senderIP;
		this.senderPort = senderPort;
		this.dataLength = bytesRead;
		this.packetIdentifier = new PacketIdentifier(sessionID, packetID);
		this.timestamp = timestamp;
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(serialVersionUID);
		out.writeByte(VERSION);
		out.writeInt(sourceType.ordinal());
		out.writeUTF(senderIP);
		out.writeInt(senderPort);
		out.writeInt(dataLength);
		out.writeInt(packetIdentifier.getSessionID());
		out.writeInt(packetIdentifier.getPacketID());
		out.writeLong(timestamp);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		long serialVersionUID = in.readLong();
		if(serialVersionUID != PacketHeader.serialVersionUID)
			throw new IOException(getClass().getName() + " not found on input stream.");
		byte version = in.readByte();

		if(version != 2)
			throw new IOException("Attempted to deserialize unknown version: " + version);

		int sourceTypeOrdinal = in.readInt();
		if(sourceTypeOrdinal == PacketSourceType.CLIENT.ordinal())
			sourceType = PacketSourceType.CLIENT;
		else if(sourceTypeOrdinal == PacketSourceType.SERVER.ordinal())
			sourceType = PacketSourceType.SERVER;
		else if(sourceTypeOrdinal == PacketSourceType.SECURE_CLIENT.ordinal())
			sourceType = PacketSourceType.SECURE_CLIENT;
		else if(sourceTypeOrdinal == PacketSourceType.SECURE_SERVER.ordinal())
			sourceType = PacketSourceType.SECURE_SERVER;
		else
			sourceType = PacketSourceType.UNKNOWN;
			
		senderIP = in.readUTF();
		senderPort = in.readInt();
		dataLength = in.readInt();
		int sID = in.readInt();
		int rID = in.readInt();
		packetIdentifier = new PacketIdentifier(sID, rID);
		timestamp = in.readLong();
	}

	public int getVersion() {
		return VERSION;
	}

	public PacketSourceType getSourceType() {
		return sourceType;
	}
	
	public int getDataLength() {
		return dataLength;
	}

	public PacketIdentifier getPacketIdentifier() {
		return packetIdentifier;
	}

	public String getSenderIP() {
		return senderIP;
	}

	public int getSenderPort() {
		return senderPort;
	}

	public void setSourceType(PacketSourceType sourceType) {
		this.sourceType = sourceType;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public void setPacketID(PacketIdentifier packetID) {
		this.packetIdentifier = packetID;
	}

	public void setSenderIP(String senderIP) {
		if(senderIP == null)
			senderIP = "";
		this.senderIP = senderIP;
	}

	public void setSenderPort(int senderPort) {
		this.senderPort = senderPort;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Version=" + VERSION);
		sb.append("; sourceType=" + sourceType); 
		sb.append("; senderIP=" + senderIP); 
		sb.append("; senderPort=" + senderPort); 
		sb.append("; dataLength=" + dataLength); 
		sb.append("; packetID=" + packetIdentifier); 
		sb.append("; timestamp=" + timestamp); 
		
		return sb.toString();
	}

}
