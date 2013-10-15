/*
 * Created on Jun 29, 2007
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import org.apache.log4j.Logger;

public class MockChannel implements ByteChannel {
	
	private static Logger log = Logger.getLogger(MockChannel.class);
	
	protected final String channelName;
	protected boolean isOpen;
	protected boolean isWriteOpen;
	
	protected int bytesReadCount;
	protected int bytesWrittenCount;

	protected ByteArrayOutputStream dataSink;
	
	public MockChannel(String channelName){
		this.channelName = channelName;
		this.isOpen = true;
		this.isWriteOpen = true;
		this.bytesReadCount = 0;
		this.bytesWrittenCount = 0;
		this.dataSink = new ByteArrayOutputStream();
	}

	public int read(ByteBuffer dst) throws IOException {
		if(!isOpen){
			return -1;
		}
		
		byte[] bytes = dataSink.toByteArray();

		if(log.isDebugEnabled())
			log.debug("Read " + bytes.length + " bytes from channel '" + channelName + "' to ByteBuffer");

		dst.put(bytes);
		dataSink.reset();
		
		bytesReadCount += bytes.length;
		return bytes.length;
	}

	public void close() throws IOException {
		isOpen = false;
	}

	public boolean isOpen() {
		return isOpen && isWriteOpen;
	}
	
	public void closeForWriteOnly(){
		isWriteOpen = false;
	}

	public int write(ByteBuffer src) throws IOException {
		if(!isOpen || !isWriteOpen){
			return -1;
		}
		
		int bytesWritten = src.limit() - src.position();
		byte[] buffer = new byte[bytesWritten];
		src.get(buffer);
		src.clear();

		if(log.isDebugEnabled())
			log.debug("Write " + bytesWritten + " bytes from ByteBuffer to channel '" + channelName + "'");

		dataSink.write(buffer);
		this.bytesWrittenCount += bytesWritten;
		
		return bytesWritten;
	}

	public int getBytesReadCount() {
		return bytesReadCount;
	}

	public int getBytesWrittenCount() {
		return bytesWrittenCount;
	}

	public void clear() {
		this.dataSink = new ByteArrayOutputStream();
	}
	
}
