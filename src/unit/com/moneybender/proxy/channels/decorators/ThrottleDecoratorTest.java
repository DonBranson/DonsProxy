/*
 * Created on Nov 29, 2007
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
package com.moneybender.proxy.channels.decorators;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.moneybender.proxy.channels.ByteReader;
import com.moneybender.proxy.channels.ByteWriter;
import com.moneybender.proxy.channels.IReadBytes;
import com.moneybender.proxy.channels.IWriteBytes;
import com.moneybender.proxy.pipes.MockChannel;

public class ThrottleDecoratorTest {

	protected Logger log = Logger.getLogger(this.getClass());

	private static final int INJECTED_THROTTLE = 1;

	@Test
	public void testDataTransfer() throws Exception {

		byte[] newBytes = new byte[1024];
		byte[] data = "haveSomeBytes".getBytes();
		System.arraycopy(data, 0, newBytes, 0, data.length);
		
		MockChannel testChannel = new MockChannel(null);
		ByteBuffer buffer = ByteBuffer.allocate(newBytes.length);
		ByteBuffer targetBuffer = ByteBuffer.allocate(newBytes.length);

		IWriteBytes writer = new ByteWriter();
		IReadBytes reader = new ThrottleDecorator(new ByteReader(), INJECTED_THROTTLE);

		writeSomeTestBytes(newBytes, testChannel, writer, buffer);
		int bytesRead = reader.readBytesFromChannel(targetBuffer, testChannel);
		Assert.assertEquals(INJECTED_THROTTLE * 1024, bytesRead);
		Assert.assertTrue("Data corrupted on copy", Arrays.equals(newBytes, targetBuffer.array()));
		
		writeSomeTestBytes(newBytes, testChannel, writer, buffer);
		targetBuffer.clear();
		bytesRead = reader.readBytesFromChannel(targetBuffer, testChannel);
		Assert.assertEquals(0, bytesRead);

		Thread.sleep(500);
		targetBuffer.clear();
		bytesRead = reader.readBytesFromChannel(targetBuffer, testChannel);
		Assert.assertEquals(0, bytesRead);
		
		Thread.sleep(2000);
		targetBuffer.clear();
		bytesRead = reader.readBytesFromChannel(targetBuffer, testChannel);
		Assert.assertEquals(newBytes.length, bytesRead);
		
		Assert.assertTrue("Data corrupted on copy", Arrays.equals(newBytes, targetBuffer.array()));
	}

	private void writeSomeTestBytes(byte[] newBytes, MockChannel testChannel, IWriteBytes writer, ByteBuffer buffer)
		throws IOException
	{
		buffer.put(newBytes);
		int bytesWritten = writer.writeBytesToChannel(buffer, testChannel);
		Assert.assertEquals(newBytes.length, bytesWritten);
	}
}
