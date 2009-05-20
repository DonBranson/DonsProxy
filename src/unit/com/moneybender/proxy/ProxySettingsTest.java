/*
 * Created on Oct 18, 2007
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
package com.moneybender.proxy;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class ProxySettingsTest {

	@Test
	public void testPropertyLoadFromSystemProperties() throws Exception {
		Random random = new Random();
		int listenPort = random.nextInt();
		String targetHost = Integer.toString(random.nextInt());
		int targetPort = random.nextInt();
		int requestPort = random.nextInt();
		int responsePort = random.nextInt();
		int duplexPort = random.nextInt();

		System.setProperty(ProxySettings.LISTEN_PORT_PROPERTY, Integer.toString(listenPort));
		System.setProperty(ProxySettings.TARGET_HOST_PROPERTY, targetHost);
		System.setProperty(ProxySettings.TARGET_PORT_PROPERTY, Integer.toString(targetPort));
		System.setProperty(ProxySettings.REQUEST_PORT_PROPERTY, Integer.toString(requestPort));
		System.setProperty(ProxySettings.RESPONSE_PORT_PROPERTY, Integer.toString(responsePort));
		System.setProperty(ProxySettings.DUPLEX_PORT_PROPERTY, Integer.toString(duplexPort));
		
		ProxySettings proxySettings = new ProxySettings();
		
		Assert.assertEquals(listenPort, proxySettings.getListenPort());
		Assert.assertEquals(targetHost, proxySettings.getTargetHost());
		Assert.assertEquals(targetPort, proxySettings.getTargetPort());
		Assert.assertEquals(requestPort, proxySettings.getRequestPort());
		Assert.assertEquals(responsePort, proxySettings.getResponsePort());
		Assert.assertEquals(duplexPort, proxySettings.getDuplexPort());
	}
}
