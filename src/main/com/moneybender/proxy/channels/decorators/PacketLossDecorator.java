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
import java.nio.channels.ReadableByteChannel;

import org.apache.log4j.Logger;

import com.moneybender.proxy.channels.IReadBytes;

public class PacketLossDecorator implements IReadBytes {
	
	protected Logger log = Logger.getLogger(this.getClass());

	private final IReadBytes decoratedReader;
	private final int dropEveryNthPacket;
	private static int packetCount = 0;

	public PacketLossDecorator(IReadBytes decoratedReader, int dropEveryNthPacket) {
		this.decoratedReader = decoratedReader;
		this.dropEveryNthPacket = dropEveryNthPacket;
	}

	public int readBytesFromChannel(ByteBuffer buffer, ReadableByteChannel channel) throws IOException {

		ByteBuffer tempBuffer = ByteBuffer.allocate(buffer.capacity());
		int bytesRead = decoratedReader.readBytesFromChannel(tempBuffer, channel);
		if(bytesRead != 0 && ++packetCount % dropEveryNthPacket == 0) {

			if(log.isDebugEnabled())
				log.debug("Dropping " + bytesRead + " bytes.");

			return 0;
		}
		
		tempBuffer.flip();
		buffer.put(tempBuffer);
		return bytesRead;
	}

	public boolean willLeaveBytesUnread() {
		return decoratedReader.willLeaveBytesUnread();
	}

}
