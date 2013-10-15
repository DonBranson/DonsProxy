/*
 * Created on Nov 30, 2007
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
import java.nio.channels.ReadableByteChannel;

import org.apache.log4j.Logger;

import com.moneybender.proxy.channels.IReadBytes;

public abstract class ReadDelayDecorator implements IReadBytes {

	private Logger log = Logger.getLogger(ReadDelayDecorator.class);

	private final IReadBytes decoratedReader;
	private long earliestNextIO;

	public ReadDelayDecorator(IReadBytes decoratedReader) {
		this.decoratedReader = decoratedReader;
		
		saveEarliestNextIO(0);
	}

	public int readBytesFromChannel(ByteBuffer buffer, ReadableByteChannel channel) throws IOException {
		
		if (log.isDebugEnabled())
			log.debug("Write after sleep");

		if(System.currentTimeMillis() >= earliestNextIO){
			int bytesRead = decoratedReader.readBytesFromChannel(buffer, channel);
			saveEarliestNextIO(bytesRead);
			return bytesRead;
		} else {
			return 0;
		}
	}

	protected void setEarliestNextIO(long earliestNextIO) {
		this.earliestNextIO = earliestNextIO;
	}

	abstract protected void saveEarliestNextIO(int bytesRead);

	public boolean willLeaveBytesUnread() {
		return true;
	}
}
