/*
 * Created on Mar 1, 2008
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

public class HexReaderTest {

	@Test
	public void testConversion() throws Exception {
		String sampleString = "He died with his boots on.\r\n";
		
		StringWriter stringWriter = new StringWriter();
		HexOutputStream out = new HexOutputStream(stringWriter);
		out.write(sampleString.getBytes());
		
		byte[] buffer = new HexReader().getBytes(new String(stringWriter.getBuffer()));
		
		Assert.assertEquals(sampleString, new String(buffer));
	}
}
