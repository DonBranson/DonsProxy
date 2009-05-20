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

public class PacketIdentifier implements Comparable<PacketIdentifier> {

	public static final int SESSION_CLOSE = -1;
	
	private int sessionID;
	private int packetID;
	
	public PacketIdentifier(int sessionID) {
		this(sessionID, 0);
	}

	public PacketIdentifier(int sessionID, int packetID) {
		this.sessionID = sessionID;
		this.packetID = packetID;
	}

	public int getSessionID() {
		return sessionID;
	}

	public int getPacketID() {
		return packetID;
	}

	@Override
	public boolean equals(Object other) {
		if(!(other instanceof PacketIdentifier))
			return false;

		PacketIdentifier identifier = (PacketIdentifier) other;
		if(identifier.sessionID != sessionID)
			return false;
		if(identifier.packetID != packetID)
			return false;

		return true;
	}

	public int compareTo(PacketIdentifier other) {

		if(sessionID < other.sessionID)
			return -1;
		if(sessionID > other.sessionID)
			return 1;

		if(packetID < other.packetID)
			return -1;
		if(packetID > other.packetID)
			return 1;

		return 0;
	}

	@Override
	public String toString() {
		return sessionID + "-" + packetID;
	}
}
