/*
 * Created on November 7, 2007
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
package com.moneybender.proxy.management;

public interface SessionMetricsMXBean extends ProxyMXBean {
	
	public String getSourceType();
	
	public int getReadBufferCapacity();
	public int getReadBufferPosition();
	public int getReadBufferLimit();
	public int getTotalBytesRead();

	public int getWriteBufferCapacity();
	public int getWriteBufferPosition();
	public int getWriteBufferLimit();
	public int getTotalBytesWritten();

	public boolean isOpenReceiverChannel();
	public boolean isOpenSenderChannel();
}
