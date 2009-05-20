/*
 * Created on October 18, 2007
 * 
 * Copyright (c) 2007, Don Branson.  All Rights Reserved.
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
package com.moneybender.proxy;

public class ProxySettings {

	public static final String LISTEN_PORT_PROPERTY = "listen.port";
	public static final String TARGET_HOST_PROPERTY = "target.host";
	public static final String TARGET_PORT_PROPERTY = "target.port";
	public static final String REQUEST_PORT_PROPERTY = "request.port";
	public static final String RESPONSE_PORT_PROPERTY = "response.port";
	public static final String DUPLEX_PORT_PROPERTY = "duplex.port";
	
	public static final int DEFAULT_LISTEN_PORT = 8080;
	public static final String DEFAULT_TARGET_HOST = "localhost";
	public static final int DEFAULT_TARGET_PORT = 8081;
	public static final int DEFAULT_REQUEST_PORT = 2005;
	public static final int DEFAULT_RESPONSE_PORT = 2006;
	public static final int DEFAULT_DUPLEX_PORT = 2007;
	
	private int listenPort;
	private String targetHost;
	private int targetPort;
	private int requestPort;
	private int responsePort;
	private int duplexPort;

	public ProxySettings(){
		this.listenPort = Integer.parseInt(System.getProperty(LISTEN_PORT_PROPERTY, Integer.toString(DEFAULT_LISTEN_PORT)).trim());
		this.targetHost = System.getProperty(TARGET_HOST_PROPERTY, DEFAULT_TARGET_HOST).trim();
		this.targetPort = Integer.parseInt(System.getProperty(TARGET_PORT_PROPERTY, Integer.toString(DEFAULT_TARGET_PORT)).trim());
		this.requestPort = Integer.parseInt(System.getProperty(REQUEST_PORT_PROPERTY, Integer.toString(DEFAULT_REQUEST_PORT)).trim());
		this.responsePort = Integer.parseInt(System.getProperty(RESPONSE_PORT_PROPERTY, Integer.toString(DEFAULT_RESPONSE_PORT)).trim());
		this.duplexPort = Integer.parseInt(System.getProperty(DUPLEX_PORT_PROPERTY, Integer.toString(DEFAULT_DUPLEX_PORT)).trim());
	}
	
	public ProxySettings(int listenPort, String targetHost, int targetPort,
			int requestPort, int responsePort, int duplexPort) {
		this.listenPort = listenPort;
		this.targetHost = targetHost;
		this.targetPort = targetPort;
		this.requestPort = requestPort;
		this.responsePort = responsePort;
		this.duplexPort = duplexPort;
	}

	public int getListenPort() {
		return listenPort;
	}

	public String getTargetHost() {
		return targetHost;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public int getRequestPort() {
		return requestPort;
	}

	public int getResponsePort() {
		return responsePort;
	}

	public int getDuplexPort() {
		return duplexPort;
	}

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	public void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
	}

	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}

	public void setRequestPort(int requestPort) {
		this.requestPort = requestPort;
	}

	public void setResponsePort(int responsePort) {
		this.responsePort = responsePort;
	}

	public void setDuplexPort(int duplexPort) {
		this.duplexPort = duplexPort;
	}
	
}
