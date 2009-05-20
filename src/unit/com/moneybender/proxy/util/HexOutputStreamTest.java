/*
 * Created on Apr 22, 2008
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

package com.moneybender.proxy.util;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

public class HexOutputStreamTest {

	@Test
	public void testSimple() throws Exception {
		String buffer = "Incredible\n";
		StringWriter stringWriter = new StringWriter();
		HexOutputStream out = new HexOutputStream(stringWriter);
		out.write(buffer.getBytes());
		Assert.assertEquals("496e6372656469626c650a", stringWriter.getBuffer().toString());
	}
}
