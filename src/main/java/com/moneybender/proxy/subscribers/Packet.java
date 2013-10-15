/*
 * Created on Jan 20, 2008
 *
 * Copyright (c), 2008 Don Branson.  All Rights Reserved.
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

package com.moneybender.proxy.subscribers;

import com.moneybender.proxy.publishers.PacketHeader;
import com.moneybender.proxy.publishers.PacketSourceType;

class Packet {

	private PacketHeader header;
	private byte[] payload;

	public Packet(PacketHeader header, byte[] payload) {
		this.header = header;
		this.payload = payload;
	}

	protected PacketHeader getHeader() {
		return header;
	}

	protected byte[] getPayload() {
		return payload;
	}
	
	protected PacketSourceType getSourceType() {
		return header.getSourceType();
	}
}
