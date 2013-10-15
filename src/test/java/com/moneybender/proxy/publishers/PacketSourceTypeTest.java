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

package com.moneybender.proxy.publishers;

import junit.framework.Assert;

import org.junit.Test;


public class PacketSourceTypeTest {

	@Test
	public void testClient() throws Exception {
		Assert.assertTrue(PacketSourceType.CLIENT.isClient());
		Assert.assertTrue(PacketSourceType.SECURE_CLIENT.isClient());
		Assert.assertFalse(PacketSourceType.SERVER.isClient());
		Assert.assertFalse(PacketSourceType.SECURE_SERVER.isClient());
		Assert.assertFalse(PacketSourceType.UNKNOWN.isClient());
	}
	
	@Test
	public void testServer() throws Exception {
		Assert.assertTrue(PacketSourceType.SERVER.isServer());
		Assert.assertTrue(PacketSourceType.SECURE_SERVER.isServer());
		Assert.assertFalse(PacketSourceType.CLIENT.isServer());
		Assert.assertFalse(PacketSourceType.SECURE_CLIENT.isServer());
		Assert.assertFalse(PacketSourceType.UNKNOWN.isClient());
	}
	
}
