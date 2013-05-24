/*
 * Created on October 19, 2007
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

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class SubscriberSettingsTest {

	@Test
	public void testPropertyLoadFromSystemProperties() throws Exception {
		Random random = new Random();
		String proxyHost = Integer.toString(random.nextInt());
		int proxyPort = random.nextInt();

		System.setProperty(SubscriberSettings.PROXY_HOST_PROPERTY, proxyHost);
		System.setProperty(SubscriberSettings.PROXY_PORT_PROPERTY, Integer.toString(proxyPort));
		
		SubscriberSettings SubscriberSettings = new SubscriberSettings();
		
		Assert.assertEquals(proxyHost, SubscriberSettings.getProxyHost());
		Assert.assertEquals(proxyPort, SubscriberSettings.getProxyPort());
	}

}
