/*
 * Created on May 28, 2008
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

package com.moneybender.proxy.analyzers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

public class ScriptWriter extends HttpXmlHandler {
	
	private static File outFile;
	private String currentUrl;
	private String currentCommand;
	private String currentProtocol;
	private String currentHeader;
	private boolean inRequest;
	private String hostValue;

	public static void main(String[] args) {
		
		try {
			ScriptWriter analyzer = new ScriptWriter();
			outFile = new File(args[1]);
			outFile.delete();
			analyzer.run(args[0]);
			analyzer.writeScript(outFile);
			log.info("Analyzed " + args[0] + " and left results in " + args[1]);
			
		} catch(SAXParseException e) {
			log.error("Error on line " + e.getLineNumber() + ", column " + e.getColumnNumber() + ":" + e.getMessage());
		} catch (IOException e) {
			log.error("Error parsing file: " + e.getMessage());
		} catch (Exception e) {
			log.error("Error parsing file: " + e.getMessage(), e);
		}
	}

	private void writeScript(File outFile) {
	}

	@Override
	public void startRequest(Attributes attributes) {
		inRequest = true;
		currentUrl = attributes.getValue("url");
		currentCommand = attributes.getValue("command");
		currentProtocol = attributes.getValue("protocol");
		hostValue = "";
	}

	@Override
	public void startResponse(Attributes attributes) {
	}
	
	@Override
	public void startHeader(Attributes attributes) {
		currentHeader = attributes.getValue("name");
	}

	@Override
	public void startData(Attributes attributes) {
		inRequest = false;
	}

	@Override
	public void endRequest() {
		inRequest = false;

		try {
			PrintStream out = new PrintStream(new FileOutputStream(outFile, true));
			out.println("curl -D - -o ScriptWriter.out http://" + hostValue + "/" + currentUrl + " \\");
			out.println("\t-X " + currentCommand + " \\");
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void endResponse() {
	}

	@Override
	public void endHeader(String value) {
		if(currentHeader.equals("Host")){
			hostValue = value;
		}
		if(inRequest){
			try {
				PrintStream out = new PrintStream(new FileOutputStream(outFile, true));
				out.println("\t-H \"" + currentHeader + ":" + value + "\" \\");
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	@Override
	public void endData() {
	}

}
