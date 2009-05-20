/*
 * Created on May 13, 2007
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
package com.moneybender.proxy.subscribers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.moneybender.proxy.ProxySettings;
import com.moneybender.proxy.publishers.PacketHeader;
import com.moneybender.proxy.publishers.SubscriberManager;

public class AbstractSubscriberTest {

	private SubscriberManager manager;

	@Before
	public void setUp() throws Exception {
		manager = new SubscriberManager(ProxySettings.DEFAULT_REQUEST_PORT,
			ProxySettings.DEFAULT_RESPONSE_PORT, ProxySettings.DEFAULT_DUPLEX_PORT);
	}
	
	@After
	public void tearDown() throws Exception {
		if(manager != null){
			manager.close();
		}
	}
	
	@Test
	public void testGetPayload() throws Exception {
		
		AbstractSubscriber subscriber = new AbstractSubscriber() {

			@Override
			protected void processPacket(PacketHeader header, byte[] payload, OutputStream out) throws IOException {
			}
			
		};
		
		String payload = "AVeryLargeInvertebrate";
		byte[] buffer = payload.getBytes();
		ByteArrayInputStream payloadStream = new ByteArrayInputStream(buffer);
		
		PacketHeader header = new PacketHeader();
		header.setDataLength(payloadStream.available());

		manager.publishRequest(header, buffer);
		Thread.sleep(100);

		byte[] payloadReceived = subscriber.getPayload(header, payloadStream);
		Assert.assertTrue(payloadReceived.length > 0);
		Assert.assertEquals(payload, new String(payloadReceived));
	}

}
