/*
 * Created on Nov 17, 2007
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
package com.moneybender.proxy.management;

public class MBeanControl implements MBeanControlMXBean {

	private MBeanManager manager;

	public MBeanControl(MBeanManager manager, int unregisterDelaySeconds) {
		this.manager = manager;
		manager.setUnregisterDelaySeconds(unregisterDelaySeconds);
	}

	public String getMBeanName() {
		return "com.moneybender.proxy:type=" + this.getClass().getSimpleName();
	}

	public int getUnregisterDelaySeconds() {
		return manager.getUnregisterDelaySeconds();
	}

	public void setUnregisterDelaySeconds(int seconds) {
		manager.setUnregisterDelaySeconds(seconds);
	}
	
	public void setBlockUnregister() {
		manager.setUnregisterDelaySeconds(-1);
	}

	public boolean getBlockUnregister() {
		if(manager.getUnregisterDelaySeconds() < 0)
			return true;

		return false;
	}
}
