/*
 * Created on Apr 27, 2008
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

package com.moneybender.proxy.application.views;

public interface IProxyView {

	// Proxy control:
	public void setStatusText(String message);
	public void setStartButtonEnabled(boolean enabled);
	public void setStopButtonEnabled(boolean enabled);
	public void setListenPortEnabled(boolean enabled);
	public void setTargetHostEnabled(boolean enabled);
	public void setTargetPortEnabled(boolean enabled);

	// Decorator control:
	public void setLatencyInputEnabled(boolean enabled);
	public void setPacketLossInputEnabled(boolean enabled);
	public void setThrottleInputEnabled(boolean enabled);
	public void setConnectionLossInputEnabled(boolean enabled);
	public void setConnectionLossCloseEnabled(boolean enabled);
	public void setConnectionLossOpenEnabled(boolean enabled);

	public int getListenPort();
	public String getTargetHost();
	public int getTargetPort();

	public boolean isConnectionLossEnabled();
	public boolean isThrottleEnabled();
	public int getThrottleValue();
	public boolean isPacketLossEnabled();
	public int getPacketLossValue();
	public boolean isLatencyEnabled();
	public int getLatencyValue();
	
	public void setListenPort(int listenPort);
	public void setTargetHost(String targetHost);
	public void setTargetPort(int targetPort);

	public void setLatencyEnabled(boolean enabled);
	public void setPacketLossEnabled(boolean enabled);
	public void setThrottleEnabled(boolean enabled);
	public void setConnectionLossEnabled(boolean enabled);
	
	public void setLatencyValue(int value);
	public void setPacketLossValue(int value);
	public void setThrottleValue(int value);
	public void clearConsole();
}
