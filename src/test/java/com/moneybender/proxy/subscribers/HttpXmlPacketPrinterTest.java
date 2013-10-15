/*
 * Created on Apr 5, 2008
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

package com.moneybender.proxy.subscribers;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class HttpXmlPacketPrinterTest {

	private ByteArrayOutputStream byteArrayOutputStream;
	private PrintStream printStream;
	private HttpXmlPacketPrinter packetPrinter;

	@Before
	public void setUp() throws Exception {
		byteArrayOutputStream = new ByteArrayOutputStream();
		printStream = new PrintStream(byteArrayOutputStream);
		packetPrinter = new HttpXmlPacketPrinter(printStream);
	}
	
	@Test
	public void testValuedHeaderParsing() throws Exception {
		BufferedReader reader = new BufferedReader(new StringReader("FakeValuedHeader: Test"));
		packetPrinter.printHeaders(reader, 0);
		
		String result = byteArrayOutputStream.toString().trim();
		Assert.assertEquals("<header name=\"FakeValuedHeader\"><![CDATA[Test]]></header>", result);
	}
	
	@Test
	public void testValueLessHeaderParsing() throws Exception {
		BufferedReader reader = new BufferedReader(new StringReader("FakeValueLessHeader"));
		packetPrinter.printHeaders(reader, 0);
		
		String result = byteArrayOutputStream.toString().trim();
		Assert.assertEquals("<header name=\"FakeValueLessHeader\"></header>", result);
	}
	
}
