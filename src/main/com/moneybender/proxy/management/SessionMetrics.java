/*
 * Created on Nov 8, 2007
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

import com.moneybender.proxy.pipes.Session;

public class SessionMetrics implements SessionMetricsMXBean {
	
	Session session;
	
	public SessionMetrics(Session session) {
		this.session = session;
	}
	
	public String getMBeanName() {
		String id = ("00000" + Integer.toString(session.getSessionID()));
		id = id.substring(id.length() - 5);
		return "com.moneybender.proxy:type=" + this.getClass().getSimpleName() + "-" + id + "-" + getSourceType();
	}

	
	public String getSourceType() {
		return session.getSourceTypeName();
	}
	

	public int getReadBufferCapacity() {
		return session.getReadBufferCapacity();
	}

	public int getReadBufferLimit() {
		return session.getReadBufferLimit();
	}

	public int getReadBufferPosition() {
		return session.getReadBufferPosition();
	}

	public int getTotalBytesRead() {
		return session.getTotalBytesRead();
	}


	public int getWriteBufferCapacity() {
		return session.getWriteBufferCapacity();
	}

	public int getWriteBufferLimit() {
		return session.getWriteBufferLimit();
	}

	public int getWriteBufferPosition() {
		return session.getWriteBufferPosition();
	}

	public int getTotalBytesWritten() {
		return session.getTotalBytesWritten();
	}


	public boolean isOpenReceiverChannel() {
		return session.isOpenReceiverChannel();
	}

	public boolean isOpenSenderChannel() {
		return session.isOpenSenderChannel();
	}
}
