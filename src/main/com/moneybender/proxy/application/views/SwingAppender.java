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

import javax.swing.SwingUtilities;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

class SwingAppender extends AppenderSkeleton {

	private final ILogger logger;

	public SwingAppender(ILogger logger) {
		this.logger = logger;
	}

	@Override
	protected void append(final LoggingEvent event) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				logger.logMessage(event.getRenderedMessage());
			}
		});
	}

	@Override
	public void close() {
		// Never close
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

}