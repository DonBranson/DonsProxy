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
import java.nio.channels.ReadableByteChannel;

import org.apache.log4j.Logger;

import com.moneybender.proxy.channels.IReadBytes;

public class ConnectionLossDecorator implements IReadBytes {

	protected Logger log = Logger.getLogger(this.getClass());

	private final IReadBytes decoratedReader;
	private static boolean networkGone;

	static {
		networkGone = false;
	}
	
	public ConnectionLossDecorator(IReadBytes decoratedReader) {
		this.decoratedReader = decoratedReader;
	}

	public int readBytesFromChannel(ByteBuffer buffer, ReadableByteChannel channel) throws IOException {
		if(networkGone) {
			channel.close();
		}
		return decoratedReader.readBytesFromChannel(buffer, channel);
	}

	public static void networkOff() {
		networkGone = true;
	}

	public static void networkOn() {
		networkGone = false;
	}

	public boolean willLeaveBytesUnread() {
		return decoratedReader.willLeaveBytesUnread();
	}

}
