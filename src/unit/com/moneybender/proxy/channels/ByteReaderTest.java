/*
 * Created on Nov 18, 2007
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
package com.moneybender.proxy.channels;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

import com.moneybender.proxy.pipes.MockChannel;

public class ByteReaderTest {

	@Test
	public void testBasicRead() throws Exception {
		
		byte[] newBytes = "haveSomeBytes".getBytes();
		MockChannel testChannel = new MockChannel(null);
		testChannel.write(ByteBuffer.wrap(newBytes));

		IReadBytes reader = new ByteReader();
		ByteBuffer targetBuffer = ByteBuffer.allocate(newBytes.length);
		int bytesRead = reader.readBytesFromChannel(targetBuffer, testChannel);
		Assert.assertEquals(newBytes.length, bytesRead);
	}
}
