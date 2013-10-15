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

package com.moneybender.proxy.channels.decorators;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.moneybender.proxy.channels.ByteReader;
import com.moneybender.proxy.channels.ByteWriter;
import com.moneybender.proxy.channels.IReadBytes;
import com.moneybender.proxy.channels.IWriteBytes;
import com.moneybender.proxy.pipes.MockChannel;

public class ConnectionLossDecoratorTest {
	
	@Before
	public void setUp() throws Exception {
		ConnectionLossDecorator.networkOn();
	}

	@Test
	public void testDataTransfer() throws Exception {

		byte[] newBytes = "haveSomeBytes".getBytes();
		MockChannel testChannel = new MockChannel(null);
		ByteBuffer buffer = ByteBuffer.allocate(newBytes.length);
		ByteBuffer targetBuffer = ByteBuffer.allocate(newBytes.length);

		IWriteBytes writer = new ByteWriter();
		IReadBytes reader = new ConnectionLossDecorator(new ByteReader());

		writeSomeTestBytes(newBytes, testChannel, writer, buffer);
		int bytesRead = reader.readBytesFromChannel(targetBuffer, testChannel);
		Assert.assertEquals(newBytes.length, bytesRead);
		Assert.assertTrue("Data corrupted on copy", Arrays.equals(newBytes, targetBuffer.array()));

		ConnectionLossDecorator.networkOff();
		
		writeSomeTestBytes(newBytes, testChannel, writer, buffer);
		bytesRead = reader.readBytesFromChannel(targetBuffer, testChannel);
		Assert.assertEquals(-1, bytesRead);
	}
	
	@Test
	public void testMultipleChannel() throws Exception {
		byte[] newBytes = "haveSomeBytes".getBytes();
		MockChannel testChannel1 = new MockChannel(null);
		ByteBuffer buffer1 = ByteBuffer.allocate(newBytes.length);
		ByteBuffer targetBuffer1 = ByteBuffer.allocate(newBytes.length);

		MockChannel testChannel2 = new MockChannel(null);
		ByteBuffer buffer2 = ByteBuffer.allocate(newBytes.length);
		ByteBuffer targetBuffer2 = ByteBuffer.allocate(newBytes.length);

		IWriteBytes writer = new ByteWriter();
		IReadBytes reader1 = new ConnectionLossDecorator(new ByteReader());
		IReadBytes reader2 = new ConnectionLossDecorator(new ByteReader());

		writeSomeTestBytes(newBytes, testChannel1, writer, buffer1);
		int bytesRead = reader1.readBytesFromChannel(targetBuffer1, testChannel1);
		Assert.assertEquals(newBytes.length, bytesRead);
		Assert.assertTrue("Data corrupted on copy", Arrays.equals(newBytes, targetBuffer1.array()));

		writeSomeTestBytes(newBytes, testChannel2, writer, buffer2);
		bytesRead = reader2.readBytesFromChannel(targetBuffer2, testChannel2);
		Assert.assertEquals(newBytes.length, bytesRead);
		Assert.assertTrue("Data corrupted on copy", Arrays.equals(newBytes, targetBuffer2.array()));

		ConnectionLossDecorator.networkOff();
		
		writeSomeTestBytes(newBytes, testChannel1, writer, buffer1);
		bytesRead = reader1.readBytesFromChannel(targetBuffer1, testChannel1);
		Assert.assertEquals(-1, bytesRead);
		
		writeSomeTestBytes(newBytes, testChannel2, writer, buffer2);
		bytesRead = reader2.readBytesFromChannel(targetBuffer2, testChannel2);
		Assert.assertEquals(-1, bytesRead);
	}

	private void writeSomeTestBytes(byte[] newBytes, MockChannel testChannel, IWriteBytes writer, ByteBuffer buffer)
		throws IOException
	{
		buffer.put(newBytes);
		int bytesWritten = writer.writeBytesToChannel(buffer, testChannel);
		Assert.assertEquals(newBytes.length, bytesWritten);
	}
}
