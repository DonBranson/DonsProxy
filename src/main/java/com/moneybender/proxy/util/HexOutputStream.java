/*
 * Created on Apr 22, 2008
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

public class HexOutputStream extends OutputStream {

	private final StringWriter writer;

	public HexOutputStream(StringWriter writer) {
		this.writer = writer;
	}

	@Override
	public void write(int b) throws IOException {
		String string = "0" + Integer.toString(b, 16);
		string = string.substring(string.length() - 2);
		writer.write(string);
	}

}
