/*
 * Created on Jan 27, 2008
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

package com.moneybender.proxy.subscribers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

class HttpParser {

	protected List<String> knownCommands;
	protected int longestCommand = 0;
	
	protected HttpParser() {
		knownCommands = Arrays.asList(new String[] { "GET", "HEAD", "POST", "PUT", "DELETE",
				"TRACE", "HTTP", "OPTIONS", "CONNECT", "DELETE", "TRACE" });
		for (int i = 0; i < knownCommands.size(); i++) {
			String command = knownCommands.get(i);
			longestCommand = Math.max(longestCommand, command.length());
		}
	}

	protected String getFirstLineOfBuffer(byte[] buffer) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer)));
		String line = reader.readLine();
		return line;
	}

	protected boolean hasKnownCommand(byte[] buffer) throws IOException {
		return knownCommands.contains(getCommand(buffer));
	}

	protected String getCommand(byte[] buffer) throws IOException {
		String line = getFirstLineOfBuffer(buffer);
		
		int subStringEnd = Math.min(line.length(), longestCommand + 1);
		return line.substring(0, subStringEnd).replaceAll("[^a-zA-Z].*", "");
	}

	protected List<String> getFirstLineTokens(byte[] buffer) throws IOException {
		return getFirstLineTokens(getFirstLineOfBuffer(buffer));
	}
	
	protected List<String> getFirstLineTokens(String line) throws IOException {
		List<String> tokens = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(line, " ");
		while (st.hasMoreTokens())
			tokens.add(st.nextToken());
			
		return tokens;
	}

}
