/*
 * Created on Apr 11, 2008
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

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

abstract public class HttpXmlHandler extends DefaultHandler {
	
	protected static Logger log = Logger.getLogger(DefaultHandler.class);
	private StringBuffer saxParserBuffer;
	
	protected void run(String inputFile) throws Exception {
		
		new File(inputFile);
		log.info("Processing file " + inputFile);

		SAXParser parser = new SAXParser();
		parser.setContentHandler(this);
		parser.setErrorHandler(this);
		parser.parse(inputFile);
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		saxParserBuffer.append(ch, start, length);
	}
	
	@Override
	public void error(SAXParseException e) throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

		saxParserBuffer = new StringBuffer();

		if(name.equals("request")) {
			startRequest(attributes);
		} else if(name.equals("response")){
			startResponse(attributes);
		} else if(name.equals("data")){
			startData(attributes);
		} else if(name.equals("header")) {
			startHeader(attributes);
		}
	}
	

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		if(name.equals("request")) {
			endRequest();
		} else if(name.equals("response")){
			endResponse();
		} else if(name.equals("data")){
			endData();
		} else if(name.equals("header")) {
			endHeader(new String(saxParserBuffer));
		}
	}
	
	public abstract void startRequest(Attributes attributes);
	public abstract void startResponse(Attributes attributes);
	public abstract void startHeader(Attributes attributes);
	public abstract void startData(Attributes attributes);
	public abstract void endRequest();
	public abstract void endResponse();
	public abstract void endHeader(String value);
	public abstract void endData();
}
