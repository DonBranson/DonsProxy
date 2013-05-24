/*
 * Created on October 19, 2007
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
package com.moneybender.proxy.subscribers;

import com.moneybender.proxy.ProxySettings;

public class SubscriberSettings {

	public static final String PROXY_HOST_PROPERTY = "proxy.host";
	public static final String PROXY_PORT_PROPERTY = "proxy.port";
	
	private String proxyHost;
	private int proxyPort;

	public SubscriberSettings() {
		this.proxyHost = System.getProperty(PROXY_HOST_PROPERTY, "localhost").trim();
		this.proxyPort = Integer.parseInt(System.getProperty(PROXY_PORT_PROPERTY,
			Integer.toString(ProxySettings.DEFAULT_DUPLEX_PORT)).trim());
	}
	
	public SubscriberSettings(String proxyHost, int proxyPort) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}
	
	public String getProxyHost() {
		return proxyHost;
	}
	public int getProxyPort() {
		return proxyPort;
	}
	
}
