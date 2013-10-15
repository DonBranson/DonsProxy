/*
 * Created on Apr 6, 2008
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

package com.moneybender.proxy.management;

import com.moneybender.proxy.channels.decorators.ConnectionLossDecorator;

public class DecoratorControl implements DecoratorMXBean {

	private String latencyDecoratorState;
	private String packetLossDecoratorState;
	private String throttleDecoratorState;
	private String connectionLossDecoratorState;
	
	public DecoratorControl() {
		this.latencyDecoratorState = "Inactive";
		this.packetLossDecoratorState = "Inactive";
		this.throttleDecoratorState = "Inactive";
		this.connectionLossDecoratorState = "Inactive";
	}
	
	public String getMBeanName() {
		return "com.moneybender.proxy:type=" + this.getClass().getSimpleName();
	}

	public String getLatencyDecoratorState() {
		return latencyDecoratorState;
	}

	public String getPacketLossDecoratorState() {
		return packetLossDecoratorState;
	}

	public String getThrottleDecoratorState() {
		return throttleDecoratorState;
	}

	public String getConnectionLossDecoratorState() {
		return connectionLossDecoratorState;
	}

	public void networkOff() {
		ConnectionLossDecorator.networkOff();
	}

	public void networkOn() {
		ConnectionLossDecorator.networkOn();
	}

	public void setLatencyDecoratorState(String latencyDecoratorState) {
		this.latencyDecoratorState = latencyDecoratorState;
	}

	public void setPacketLossDecoratorState(String packetLossDecoratorState) {
		this.packetLossDecoratorState = packetLossDecoratorState;
	}

	public void setThrottleDecoratorState(String throttleDecoratorState) {
		this.throttleDecoratorState = throttleDecoratorState;
	}

	public void setConnectionLossDecoratorState(String connectionLossDecoratorState) {
		this.connectionLossDecoratorState = connectionLossDecoratorState;
	}

}
