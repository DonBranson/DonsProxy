/*
 * Created on Jan 27, 2008
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
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.moneybender.proxy.util.HexReader;

public class HttpXmlSubscriberMultiPacketTest extends AbstractTestHttpXmlSubscriber {

	private Logger log = Logger.getLogger(this.getClass());
	
	@Test
	public void testSingleRequestInTwoPackets() throws Exception {

		sendRequestPacket(new HttpTestDataFactory().getGetRequestStart(), 1);
		sendRequestPacket(new HttpTestDataFactory().getGetRequestFinish(), 2);
		sendResponsePacket(new HttpTestDataFactory().getOkayResponseStart(), 1);
		sendEndingPacket();

		log.info(getOutput());
		
		BufferedReader reader = new BufferedReader(new StringReader(getOutput()));
		String line = reader.readLine().trim();
		Assert.assertEquals("<?xml version=\"1.0\"?>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<sessions>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<session id=\"543\">", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<request count=\"0\" timestamp=\"-1\" command=\"GET\" url=\"http://www.google.com/\" protocol=\"HTTP/1.1\">", line);

		validateGetRequestHeaders(reader);
		
		line = reader.readLine().trim();
		Assert.assertEquals("</request>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<response count=\"0\" timestamp=\"-1\" protocol=\"HTTP/1.1\" code=\"200\" message=\"OK is what I say\">", line);

		validateResponseHeaders(reader);

		line = reader.readLine().trim();
		Assert.assertEquals("</response>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("</session>", line);
	}

	@Test
	public void testSingleResponseInTwoPackets() throws Exception {

		sendRequestPacket(new HttpTestDataFactory().getGetRequest(), 1);
		sendResponsePacket(new HttpTestDataFactory().getOkayResponseStart(), 1);
		sendResponsePacket(new HttpTestDataFactory().getOkayResponseFinish(), 2);
		sendEndingPacket();

		log.info(getOutput());
		
		BufferedReader reader = new BufferedReader(new StringReader(getOutput()));
		String line = reader.readLine().trim();
		Assert.assertEquals("<?xml version=\"1.0\"?>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<sessions>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<session id=\"543\">", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<request count=\"0\" timestamp=\"-1\" command=\"GET\" url=\"http://www.google.com/\" protocol=\"HTTP/1.1\">", line);

		validateGetRequestHeaders(reader);
		
		line = reader.readLine().trim();
		Assert.assertEquals("</request>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<response count=\"0\" timestamp=\"-1\" protocol=\"HTTP/1.1\" code=\"200\" message=\"OK is what I say\">", line);

		validateResponseHeaders(reader);

		line = reader.readLine().trim();
		Assert.assertEquals("<data type=\"hex\">", line);

		line = reader.readLine().trim();
		Assert.assertEquals(new HttpTestDataFactory().getOkayResponseFinish(), new String(new HexReader().getBytes(line)));
		
		line = reader.readLine().trim();
		Assert.assertEquals("</data>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("</response>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("</session>", line);
	}
	
}
