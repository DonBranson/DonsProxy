/*
 * Created on Oct 29, 2007
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
package com.moneybender.proxy.pipes;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

public class SecurableSessionTest {
	
	@Test
	public void testWriteToWriteBuffer() throws Exception {
		ClientSession session = new ClientSession(17, null, null, null, null, null, null, -1);
		session.setEncrypter(new MockEncrypter());
		session.setWriteBuffer(ByteBuffer.allocate(10));
		
		byte[] newBytes = "1234567890".getBytes();
		session.encryptToWriteBuffer(newBytes);
		
		Assert.assertEquals(new String(newBytes), new String(session.getWriteBuffer().array()));
	}
	
	@Test
	public void testEncryptToWriteBufferWithOverflowCondition() throws Exception {

		ClientSession session = new ClientSession(17, null, null, null, null, null, null, -1);
		session.setEncrypter(new MockEncrypter());
		session.setWriteBuffer(ByteBuffer.allocate(10));
		
		byte[] firstBytes = "1234567890".getBytes();
		session.encryptToWriteBuffer(firstBytes);
		
		ByteBuffer writeBuffer = session.getWriteBuffer();
		Assert.assertEquals(new String(firstBytes), new String(session.getWriteBuffer().array()));

		Assert.assertEquals(10, writeBuffer.capacity());
		byte[] newBytes = "abcdefghij".getBytes();
		session.encryptToWriteBuffer(newBytes);
		writeBuffer = session.getWriteBuffer();
		Assert.assertEquals(20, writeBuffer.capacity());
		Assert.assertEquals(new String(firstBytes) + new String(newBytes), new String(session.getWriteBuffer().array()));
	}
}
