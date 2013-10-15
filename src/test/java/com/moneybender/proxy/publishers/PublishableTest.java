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
package com.moneybender.proxy.publishers;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.moneybender.proxy.ProxySettings;

public class PublishableTest {

	private SubscriberManager manager;

	@Before
	public void setUp() throws Exception {
		manager = new SubscriberManager(ProxySettings.DEFAULT_REQUEST_PORT,
			ProxySettings.DEFAULT_RESPONSE_PORT, ProxySettings.DEFAULT_DUPLEX_PORT);
	}
	
	@After
	public void tearDown() throws Exception {
		if(manager != null)
			manager.close();
	}
	
	@Test
	public void testBasic() throws Exception {
		PacketHeader header = new PacketHeader();
		PacketIdentifier packetIdentifier;

		packetIdentifier = new PacketIdentifier(543);
		header.setSourceType(PacketSourceType.CLIENT);
		header.setPacketID(packetIdentifier);
		header.setDataLength(101);
		header.setSenderIP("10.5.128.2");
		header.setSenderPort(2002);

		byte[] buffer = new byte[10];
		Publishable publishable = new PublishableRequest(manager, header, buffer);
		Assert.assertNotNull(publishable);

		publishable = new PublishableResponse(manager, header, buffer);
		Assert.assertNotNull(publishable);
	}
}
