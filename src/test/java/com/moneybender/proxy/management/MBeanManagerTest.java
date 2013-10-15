/*
 * Created on Nov 16, 2007
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
package com.moneybender.proxy.management;

import org.junit.Assert;
import org.junit.Test;

import com.moneybender.proxy.pipes.ClientSession;
import com.moneybender.proxy.pipes.Session;

public class MBeanManagerTest {

	@Test
	public void testBeanRegistrationCycle() throws Exception {
		MBeanManager manager = new MBeanManager();
		Session session = new ClientSession(0, null, null, null, null, null, null, -1);
		
		SessionMetrics sessionMetrics = new SessionMetrics(session);
		manager.register(session, sessionMetrics);
		Assert.assertTrue(manager.isRegistered(session));

		manager.unregister(session);
		Assert.assertFalse(manager.isRegistered(session));
	}
	
}
