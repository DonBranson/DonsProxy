/*
 * Created on Nov 29, 2007
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
package com.moneybender.proxy.channels.decorators;

import com.moneybender.proxy.channels.IReadBytes;

public class ThrottleDecorator extends ReadDelayDecorator {

	private final int microsecondsPerByte;

	public ThrottleDecorator(IReadBytes decoratedWriter, int bandwidthLimit) {
		
		super(decoratedWriter);
		
		double bytesPerSecond = bandwidthLimit * 1000;
		this.microsecondsPerByte = (int)((1 / bytesPerSecond) * 1000 * 1000);
		
		saveEarliestNextIO(0);
	}

	@Override
	protected void saveEarliestNextIO(int bytesRead) {
		setEarliestNextIO(System.currentTimeMillis() + ((bytesRead * microsecondsPerByte) / 1000));
	}
}
