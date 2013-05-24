/*
 * Created on Mar 1, 2008
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

package com.moneybender.proxy.util;

public class HexReader {

	public byte[] getBytes(String string) {
		byte[] buffer = new byte[string.length() / 2];

		int outIndex = 0;
		for(int inIndex = 0; inIndex < string.length(); inIndex += 2) {
			buffer[outIndex++] = (byte)Integer.parseInt(string.substring(inIndex, inIndex + 2), 16);
		}
		
		return buffer;
	}

}
