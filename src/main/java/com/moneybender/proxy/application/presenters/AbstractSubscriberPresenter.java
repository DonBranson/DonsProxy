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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import com.moneybender.proxy.application.views.ISubscriberView;
import com.moneybender.proxy.subscribers.ISubscribeToPacketData;
import com.moneybender.proxy.subscribers.SubscriberSettings;

public abstract class AbstractSubscriberPresenter {

	private ISubscribeToPacketData subscriber;
	private TextAreaOutputStream textAreaOutputStream;
	private ISubscriberView view;

	public void setView(ISubscriberView view) {
		this.view = view;
	}
	
	public void start() {
		Thread th = new Thread("DP-SubscriberView"){

			@Override
			public void run() {
				subscriber = makeSubscriber();
				try {
					subscriber.start(new SubscriberSettings(), getTextAreaOutputStream());
					setNotRunning();
				} catch (IOException e) {
					new PrintStream(textAreaOutputStream).println("Failed to start subscriber: " + e.getMessage());
					setNotRunning();
				}
			}

		};
		th.setDaemon(true);
		th.start();
		setRunning();
	}

	protected void setNotRunning() {
		view.setStartButtonEnabled(true);
		view.setStopButtonEnabled(false);
		view.setStatusText("Ready.");
	}
	
	protected void setRunning() {
		view.setStartButtonEnabled(false);
		view.setStopButtonEnabled(true);
		view.setStatusText("Subscriber started.");
	}
	
	protected abstract ISubscribeToPacketData makeSubscriber();

	private class TextAreaOutputStream extends OutputStream {

		@Override
		public void write(int b) throws IOException {
			view.writeCharacterToLog(b);
		}
		
	}

	public void stop() {
		if(subscriber != null)
			subscriber.stop();
		setNotRunning();
	}

	public void setSubscriber(ISubscribeToPacketData subscriber) {
		this.subscriber = subscriber;
	}

	public TextAreaOutputStream getTextAreaOutputStream() {
		if(textAreaOutputStream == null){
			textAreaOutputStream = new TextAreaOutputStream();
		}
		return textAreaOutputStream;
	}

	public void clear() {
		view.clearLog();
	}

}