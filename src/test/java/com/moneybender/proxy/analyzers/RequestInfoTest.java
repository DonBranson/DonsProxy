/*
 * Created on Mar 9, 2008
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

package com.moneybender.proxy.analyzers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RequestInfoTest {

	private RequestInfo requestInfo;

	@Before
	public void setUp() throws Exception {
		requestInfo = new RequestInfo("test");
	}
	
	@Test
	public void testSetCachingPolicy() throws Exception {
		requestInfo.setCachingPolicy("no-store");
		requestInfo.setCachingPolicy("no-cache");
		Assert.assertEquals("no-store; no-cache", requestInfo.getCachingPolicy());
	}
	
	@Test
	public void testAverageCalculation() throws Exception {
		Assert.assertEquals(0, requestInfo.getAverageResponseTime());
		requestInfo.setResponseTime(100);
		requestInfo.setResponseTime(200);
		requestInfo.setResponseTime(300);
		Assert.assertEquals(200, requestInfo.getAverageResponseTime());
	}
}
