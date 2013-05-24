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
import java.io.IOException;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.moneybender.proxy.publishers.PacketIdentifier;
import com.moneybender.proxy.publishers.PacketSourceType;
import com.moneybender.proxy.util.HexReader;

public class HttpXmlSubscriberPayloadTest extends AbstractTestHttpXmlSubscriber {

	private Logger log = Logger.getLogger(this.getClass());

	@Test
	public void testNoPayload() throws Exception {
		sendRequestPacket(new HttpTestDataFactory().getGetRequest(), 1);
		sendResponsePacket(new HttpTestDataFactory().getOkayResponseStart(), 1);
		sendEndingPacket();

		validateGetOutput(getOutput(), false);
	}
	
	@Test
	public void testPayloadInHeaderPacket() throws Exception {
		
		sendRequestPacket(new HttpTestDataFactory().getPostRequest(), 1);
		sendResponsePacket(new HttpTestDataFactory().getOkayResponse(), 1);
		sendEndingPacket();

		validatePostOutput(getOutput(), true);
	}
	
	@Test
	public void testPayloadAfterHeaderPacket() throws Exception {
		
		sendRequestPacket(new HttpTestDataFactory().getPostRequest(), 1);
		sendResponsePacket(new HttpTestDataFactory().getOkayResponse(), 1);
		sendEndingPacket();

		validatePostOutput(getOutput(), true);
	}

	@Test
	public void testPayloadOnlyInSecondPacket() throws Exception {
		
		sendRequestPacket(new HttpTestDataFactory().getPostRequestNeat(), 1);
		sendRequestPacket(new HttpTestDataFactory().getPostRequestPayload(), 1);
		sendResponsePacket(new HttpTestDataFactory().getOkayResponse(), 1);
		sendEndingPacket();

		validatePostOutput(getOutput(), true);
	}

	@Test
	public void testPayloadOnlyInThirdPacket() throws Exception {
		
		sendRequestPacket(new HttpTestDataFactory().getPostRequestStart(), 1);
		sendRequestPacket(new HttpTestDataFactory().getPostRequestFinish(), 1);
		sendRequestPacket(new HttpTestDataFactory().getPostRequestPayload(), 1);
		sendResponsePacket(new HttpTestDataFactory().getOkayResponse(), 1);
		sendEndingPacket();

		validatePostOutput(getOutput(), true);
	}
	
	@Override
	protected void sendRequestPacket(String request, int requestCount) {
		getHeader().setDataLength(request.getBytes().length);
		getHeader().setPacketID(new PacketIdentifier(543, requestCount));
		getHeader().setSourceType(PacketSourceType.CLIENT);
		publishRequest(getHeader(), request.getBytes());
	}
	
	private void validateGetOutput(String output, boolean checkForPayload) throws IOException {
		log.info(output);
		BufferedReader reader = new BufferedReader(new StringReader(getOutput()));
		String line = reader.readLine().trim();
		Assert.assertEquals("<?xml version=\"1.0\"?>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<sessions>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<session id=\"543\">", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<request count=\"0\" timestamp=\"-2\" command=\"GET\" url=\"http://www.google.com/\" protocol=\"HTTP/1.1\">", line);

		validateGetRequestHeaders(reader);

		if(checkForPayload) {
			line = reader.readLine().trim();
			Assert.assertEquals("<data>", line);

			line = reader.readLine().trim();
			Assert.assertEquals("</data>", line);
		}

		line = reader.readLine().trim();
		Assert.assertEquals("</request>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<response count=\"0\" timestamp=\"-1\" protocol=\"HTTP/1.1\" code=\"200\" message=\"OK is what I say\">", line);

		validateResponseHeaders(reader);

		if(checkForPayload) {
			line = reader.readLine().trim();
			Assert.assertEquals("<data type=\"hex\">", line);

			line = reader.readLine().trim();
			Assert.assertEquals("</data>", line);
		}

		line = reader.readLine().trim();
		Assert.assertEquals("</response>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("</session>", line);
	}

	private void validatePostOutput(String output, boolean checkForPayload) throws IOException {
		log.info(output);
		BufferedReader reader = new BufferedReader(new StringReader(getOutput()));
		String line = reader.readLine().trim();
		Assert.assertEquals("<?xml version=\"1.0\"?>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<sessions>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<session id=\"543\">", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<request count=\"0\" timestamp=\"-2\" command=\"POST\" url=\"/mail/channel/\" protocol=\"HTTP/1.1\">", line);

		validatePostRequestHeaders(reader);

		if(checkForPayload) {
			line = reader.readLine().trim();
			Assert.assertEquals("<data>", line);
			
			while(line != null) {
				line = reader.readLine().trim();
				if(line.startsWith("</data"))
					break;
				Assert.assertEquals("<header", line.replaceAll(" name=.*", ""));
				Assert.assertTrue(line.matches("^.*<header name=\".*\"><!\\[CDATA\\[.*\\]\\]></header>.*$"));
			}

			Assert.assertEquals("</data>", line);
		}

		line = reader.readLine().trim();
		Assert.assertEquals("</request>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("<response count=\"0\" timestamp=\"-1\" protocol=\"HTTP/1.1\" code=\"200\" message=\"OK is what I say\">", line);

		validateResponseHeaders(reader);

		if(checkForPayload) {
			line = reader.readLine().trim();
			Assert.assertEquals("<data type=\"hex\">", line);

			line = reader.readLine().trim();
			Assert.assertEquals(new HttpTestDataFactory().getOkayResponseFinish(), new String(new HexReader().getBytes(line)));

			line = reader.readLine().trim();
			Assert.assertEquals("</data>", line);
		}

		line = reader.readLine().trim();
		Assert.assertEquals("</response>", line);

		line = reader.readLine().trim();
		Assert.assertEquals("</session>", line);
	}

}
