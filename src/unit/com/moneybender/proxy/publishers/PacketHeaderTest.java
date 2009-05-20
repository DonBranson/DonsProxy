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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PacketHeaderTest {

	private final long now = System.currentTimeMillis();
	private PacketHeader header;
	private PacketIdentifier packetIdentifier;

	@Before
	public void setup() throws Exception {
		header = new PacketHeader();

		packetIdentifier = new PacketIdentifier(1243);
		header.setSourceType(PacketSourceType.SERVER);
		header.setPacketID(packetIdentifier);
		header.setDataLength(100);
		header.setSenderIP("10.5.128.1");
		header.setSenderPort(2001);
		header.setTimestamp(now);
	}
	
	@Test
	public void testConstruction() throws Exception {

		Assert.assertEquals(100, header.getDataLength());
		
		Assert.assertEquals(packetIdentifier, header.getPacketIdentifier());
		
		Assert.assertEquals("10.5.128.1", header.getSenderIP());
		header.setSenderIP(null);
		Assert.assertEquals("", header.getSenderIP());

		Assert.assertEquals(2001, header.getSenderPort());
	}
	
	@Test
	public void testLongConstructor() throws Exception {
		PacketHeader header = new PacketHeader(PacketSourceType.CLIENT, 5, 138, 878, "127.0.0.1", 80, now);
		Assert.assertEquals(2, header.getVersion());
		Assert.assertEquals(PacketSourceType.CLIENT, header.getSourceType());
		Assert.assertEquals("127.0.0.1", header.getSenderIP());
		Assert.assertEquals(80, header.getSenderPort());
		Assert.assertEquals(878, header.getDataLength());
		Assert.assertEquals(new PacketIdentifier(5, 138), header.getPacketIdentifier());
		Assert.assertEquals(now, header.getTimestamp());
	}
	
	@Test
	public void testExternalization() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(bos);

		header.writeExternal(oo);
		oo.close();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInput oi = new ObjectInputStream(bis);
		
		PacketHeader newHeader = new PacketHeader();
		newHeader.readExternal(oi);
		
		Assert.assertEquals(2, newHeader.getVersion());
		Assert.assertEquals(PacketSourceType.SERVER, header.getSourceType());
		Assert.assertEquals(100, newHeader.getDataLength());
		Assert.assertEquals(packetIdentifier, newHeader.getPacketIdentifier());
		Assert.assertEquals("10.5.128.1", newHeader.getSenderIP());
		Assert.assertEquals(2001, newHeader.getSenderPort());
		Assert.assertEquals(now, newHeader.getTimestamp());
	}
	
	@Test
	public void testExternalizationOfAllTypes() throws Exception {
		testExternalizationOfType(PacketSourceType.CLIENT);
		testExternalizationOfType(PacketSourceType.SERVER);
		testExternalizationOfType(PacketSourceType.SECURE_CLIENT);
		testExternalizationOfType(PacketSourceType.SECURE_SERVER);
	}

	private void testExternalizationOfType(PacketSourceType type)
		throws IOException, ClassNotFoundException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(bos);

		header.setSourceType(type);
		header.writeExternal(oo);
		oo.close();
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInput oi = new ObjectInputStream(bis);
		
		PacketHeader newHeader = new PacketHeader();
		newHeader.readExternal(oi);
		Assert.assertEquals(type, newHeader.getSourceType());
	}
	
	@Test
	public void testBadVersionOnWire() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);

		out.writeLong(7616175263804826238L);
		out.writeByte(1);
		out.close();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInput oi = new ObjectInputStream(bis);
		
		PacketHeader newHeader = new PacketHeader();
		try{
			newHeader.readExternal(oi);
			Assert.fail("Read external should have detected bad version number.");
		}catch(IOException success){
		}
		
	}
	
	@Test
	public void testToString() throws Exception {
		String output = header.toString();
		Assert.assertTrue(output.indexOf("Version") >= 0);
		Assert.assertTrue(output.indexOf("sourceType") >= 0);
		Assert.assertTrue(output.indexOf("senderIP") >= 0);
		Assert.assertTrue(output.indexOf("senderPort") >= 0);
		Assert.assertTrue(output.indexOf("dataLength") >= 0);
		Assert.assertTrue(output.indexOf("packetID") >= 0);
	}
	
}
