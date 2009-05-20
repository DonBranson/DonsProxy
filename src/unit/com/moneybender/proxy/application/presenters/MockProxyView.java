/*
 * Created on May 13, 2008
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

package com.moneybender.proxy.application.presenters;

import com.moneybender.proxy.application.views.IProxyView;

class MockProxyView implements IProxyView {

	private boolean connectionLossEnabled;

	private boolean startEnabled;
	private String message;
	private boolean stopEnabled;
	private String logMessage;
	
	private boolean connectionLossCloseEnabled;
	private boolean connectionLossOpenEnabled;
	private boolean connectionLossInputEnabled;
	private boolean latencyInputEnabled;
	private boolean packetLossInputEnabled;
	private boolean throttleInputEnabled;
	
	private boolean listenPortEnabled;
	private boolean targetHostEnabled;
	private boolean targetPortEnabled;

	private int listenPort;
	private String targetHost;
	private int targetPort;

	private boolean latencyEnabled;
	private boolean packetLossEnabled;
	private boolean throttleEnabled;
	private int latencyValue;
	private int packetLossValue;
	private int throttleValue;

	public int getLatencyValue() {
		return latencyValue;
	}

	public boolean isStartEnabled() {
		return startEnabled;
	}

	public String getMessage() {
		return message;
	}

	public boolean isStopEnabled() {
		return stopEnabled;
	}

	public String getLogMessage() {
		return logMessage;
	}

	public int getListenPort() {
		return listenPort;
	}

	public int getPacketLossValue() {
		return packetLossValue;
	}

	public String getTargetHost() {
		return targetHost;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public int getThrottleValue() {
		return throttleValue;
	}

	public boolean isConnectionLossEnabled() {
		return connectionLossEnabled;
	}

	public boolean isLatencyEnabled() {
		return latencyEnabled;
	}

	public boolean isPacketLossEnabled() {
		return packetLossEnabled;
	}

	public boolean isThrottleEnabled() {
		return throttleEnabled;
	}

	public void setStartButtonEnabled(boolean enabled) {
		this.startEnabled = enabled;
	}

	public void setStatusText(String message) {
		this.message = message;
	}

	public void setStopButtonEnabled(boolean enabled) {
		this.stopEnabled = enabled;
	}

	public void logMessage(String message) {
		this.logMessage = message;
	}

	public void setConnectionLossCloseEnabled(boolean enabled) {
		connectionLossCloseEnabled = enabled;
	}

	public void setConnectionLossInputEnabled(boolean enabled) {
		connectionLossInputEnabled = enabled;
	}

	public void setConnectionLossOpenEnabled(boolean enabled) {
		connectionLossOpenEnabled = enabled;
	}

	public void setLatencyInputEnabled(boolean enabled) {
		latencyInputEnabled = enabled;
	}

	public void setPacketLossInputEnabled(boolean enabled) {
		packetLossInputEnabled = enabled;
	}

	public void setThrottleInputEnabled(boolean enabled) {
		throttleInputEnabled = enabled;
	}

	public boolean isConnectionLossCloseEnabled() {
		return connectionLossCloseEnabled;
	}

	public boolean isConnectionLossOpenEnabled() {
		return connectionLossOpenEnabled;
	}

	public boolean isConnectionLossInputEnabled() {
		return connectionLossInputEnabled;
	}

	public boolean isLatencyInputEnabled() {
		return latencyInputEnabled;
	}

	public boolean isPacketLossInputEnabled() {
		return packetLossInputEnabled;
	}

	public boolean isThrottleInputEnabled() {
		return throttleInputEnabled;
	}

	public void setConnectionLossEnabled(boolean connectionLossEnabled) {
		this.connectionLossEnabled = connectionLossEnabled;
	}

	public void setListenPortEnabled(boolean enabled) {
		listenPortEnabled = enabled;
	}

	public void setTargetHostEnabled(boolean enabled) {
		targetHostEnabled = enabled;
	}

	public void setTargetPortEnabled(boolean enabled) {
		targetPortEnabled = enabled;
	}

	public boolean isListenPortEnabled() {
		return listenPortEnabled;
	}

	public boolean isTargetHostEnabled() {
		return targetHostEnabled;
	}

	public boolean isTargetPortEnabled() {
		return targetPortEnabled;
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

	public void setLatencyEnabled(boolean enabled) {
		this.latencyEnabled = enabled;
	}

	public void setLatencyValue(int value) {
		this.latencyValue = value;
	}

	public void setPacketLossValue(int value) {
		this.packetLossValue = value;
	}

	public void setPacketLossEnabled(boolean enabled) {
		this.packetLossEnabled = enabled;
	}

	public void setThrottleEnabled(boolean enabled) {
		this.throttleEnabled = enabled;
	}

	public void setThrottleValue(int value) {
		this.throttleValue = value;
	}

	public void clearConsole() {
	}
	
}
