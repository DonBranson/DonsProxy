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

import org.junit.Assert;
import org.junit.Test;

public class PacketIdentifierTest {

	@Test
	public void testConstruction() throws Exception {
		PacketIdentifier identifier = new PacketIdentifier(3456);
		Assert.assertEquals(identifier.getSessionID(), 3456);
		Assert.assertEquals(identifier.getPacketID(), 0);
		
		identifier = new PacketIdentifier(789, 345);
		Assert.assertEquals(identifier.getSessionID(), 789);
		Assert.assertEquals(identifier.getPacketID(), 345);
	}
	
	@Test
	public void testEquality() throws Exception {
		PacketIdentifier id1 = new PacketIdentifier(3456, 5678);
		PacketIdentifier id2 = new PacketIdentifier(3456, 5678);
		Assert.assertEquals(id1, id2);
	}
	
	@Test
	public void testComparisonEquals() throws Exception {
		PacketIdentifier id1 = new PacketIdentifier(3456, 5678);
		PacketIdentifier id2 = new PacketIdentifier(3456, 5678);
		Assert.assertEquals(0, id1.compareTo(id2));
	}
	
	@Test
	public void testComparisonSession() throws Exception {
		PacketIdentifier id1 = new PacketIdentifier(3455, 5679);
		PacketIdentifier id2 = new PacketIdentifier(3456, 5678);
		Assert.assertEquals(-1, id1.compareTo(id2));
		Assert.assertEquals(1, id2.compareTo(id1));
	}
	
	@Test
	public void testComparison() throws Exception {
		PacketIdentifier id1 = new PacketIdentifier(3456, 5679);
		PacketIdentifier id2 = new PacketIdentifier(3456, 5678);
		Assert.assertEquals(1, id1.compareTo(id2));
		Assert.assertEquals(-1, id2.compareTo(id1));
	}
	
	@Test
	public void testToString() throws Exception {
		PacketIdentifier id = new PacketIdentifier(3456, 5679);
		Assert.assertEquals("3456-5679", id.toString());
	}
}
