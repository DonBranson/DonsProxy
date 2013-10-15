/*
 * Created on Jan 19, 2008
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

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.moneybender.proxy.publishers.PacketHeader;
import com.moneybender.proxy.publishers.PacketIdentifier;
import com.moneybender.proxy.publishers.PacketSourceType;

public class PacketStreamTest {
	
	String getRequest;
	PacketStream stream;

	@Before
	public void setUp() throws Exception {
		PacketHeader header = getSamplePacketHeader();
		stream = new PacketStream();
		getRequest = new HttpTestDataFactory().getGetRequest();
		stream.savePacket(header, getRequest.getBytes());
		stream.savePacket(header, getRequest.getBytes());
	}
	
	@Test
	public void testSavePacket() throws Exception {

		List<Packet> sessionData = stream.getStream();
		Assert.assertNotNull(sessionData);
		Assert.assertEquals(2, sessionData.size());
		Assert.assertEquals(getRequest, new String(sessionData.get(0).getPayload()));
		Assert.assertEquals(getRequest, new String(sessionData.get(1).getPayload()));
	}
	
	private PacketHeader getSamplePacketHeader() {
		PacketHeader header = new PacketHeader();
		PacketIdentifier packetIdentifier = new PacketIdentifier(556);
		header.setSourceType(PacketSourceType.CLIENT);
		header.setPacketID(packetIdentifier);
		header.setDataLength(101);
		header.setSenderIP("10.5.128.3");
		header.setSenderPort(2003);
		return header;
	}
	
}
