/*
 * Created on Apr 24, 2008
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.moneybender.proxy.application.views.ISubscriberView;
import com.moneybender.proxy.subscribers.HttpXmlSubscriber;
import com.moneybender.proxy.subscribers.ISubscribeToPacketData;

public class HttpXmlSubscriberPresenter extends AbstractSubscriberPresenter {

	public HttpXmlSubscriberPresenter(ISubscriberView view) {
		setView(view);
		setNotRunning();
	}
	
	@Override
	protected ISubscribeToPacketData makeSubscriber() {

		String outFileName = HttpXmlSubscriber.class.getSimpleName() + ".xml";
		try {
			new PrintStream(getTextAreaOutputStream()).println("Writing XML output to " + outFileName);
			return new HttpXmlSubscriber(new PrintStream(new FileOutputStream(outFileName)));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

}
