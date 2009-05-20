/*
 * Created on Apr 13, 2008
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import com.moneybender.proxy.Proxy;
import com.moneybender.proxy.ProxySettings;
import com.moneybender.proxy.application.views.IProxyView;
import com.moneybender.proxy.channels.ByteReaderFactory;
import com.moneybender.proxy.channels.decorators.ConnectionLossDecorator;

public class ProxyPresenter {

	private static final String PROXY_STATE_FILE =  "proxysettings.xml";
	private static final String DECORATOR_STATE_FILE =  "decoratorsettings.xml";

	private Proxy proxy = null;

	private final IProxyView view;

	public ProxyPresenter(IProxyView view) {
		this.view = view;
		loadProxySettings("");
		loadDecoratorSettings("");
		setNotRunning();
	}

	private void setNotRunning() {
		view.setStatusText("Ready.");
		view.setStartButtonEnabled(true);
		view.setStopButtonEnabled(false);
		view.setConnectionLossInputEnabled(true);
		view.setConnectionLossCloseEnabled(false);
		view.setConnectionLossOpenEnabled(false);
		view.setLatencyInputEnabled(true);
		view.setThrottleInputEnabled(true);
		view.setPacketLossInputEnabled(true);
		view.setListenPortEnabled(true);
		view.setTargetHostEnabled(true);
		view.setTargetPortEnabled(true);
	}
	
	private void setRunning() {
		view.setStatusText("Proxy started.");
		view.setStartButtonEnabled(false);
		view.setStopButtonEnabled(true);
		view.setConnectionLossInputEnabled(false);
		if(view.isConnectionLossEnabled()) {
			view.setConnectionLossCloseEnabled(true);
		} else {
			view.setConnectionLossCloseEnabled(false);
		}
		view.setConnectionLossOpenEnabled(false);
		view.setLatencyInputEnabled(false);
		view.setThrottleInputEnabled(false);
		view.setPacketLossInputEnabled(false);
		view.setListenPortEnabled(false);
		view.setTargetHostEnabled(false);
		view.setTargetPortEnabled(false);
	}
	
	private void setStarting() {
		view.setStatusText("Starting proxy...");
	}
	
	private void setStopping() {
		view.setStatusText("Stopping proxy...");
	}
	
	public void startProxy() {

		if (proxy == null) {
			setStarting();
			try {
				saveCurrentProxySettings("");
				saveCurrentDecoratorSettings("");

				processDecorators();

				proxy = new Proxy();
				proxy.start(createproxySettings());
				setRunning();
			} catch (IOException e) {
				proxy = null;
				view.setStatusText(e.getMessage());
			}
		}
	}

	private ProxySettings createproxySettings() {
		return new ProxySettings(view.getListenPort(), view.getTargetHost(),
			view.getTargetPort(), ProxySettings.DEFAULT_REQUEST_PORT,
			ProxySettings.DEFAULT_RESPONSE_PORT, ProxySettings.DEFAULT_DUPLEX_PORT);
	}

	public void loadProxySettings(String configurationName) {

		String configurationFile = makeProxyConfigurationFileName(configurationName);

		ApplicationContext context = Application.getInstance().getContext();
		try {
			
			ProxySettings settings = (ProxySettings) context.getLocalStorage().load(configurationFile);
			if(settings != null){
				view.setListenPort(settings.getListenPort());
				view.setTargetHost(settings.getTargetHost());
				view.setTargetPort(settings.getTargetPort());
			}

		} catch (IOException e) {
			System.err.println("Error loading proxy settings from " + configurationFile + ":" + e.getMessage());
		}
	}

	public void deleteConfiguration(String configurationName) {
		try {
			if(configurationName == null)
				return;

			String configDir = Application.getInstance().getContext().getLocalStorage().getDirectory().getCanonicalPath()
				+ System.getProperty("file.separator") + configurationName;

			deleteDirectory(configDir);
		} catch (IOException e) {
			System.err.println("Error deleting configuration " + configurationName + ":" + e.getMessage());
		}
	}

	private void deleteDirectory(String directory) throws IOException {
		
		String[] files = new File(directory).list();
		for (int i = 0; i < files.length; i++) {
			String fileName = directory + System.getProperty("file.separator") + files[i];
			File file = new File(fileName);
			if(file.isDirectory()) {
				deleteDirectory(file.getCanonicalPath());
			}
			file.delete();
		}
		new File(directory).delete();
	}


	public void saveCurrentProxySettings(String configurationName) {

		String configurationFile = makeProxyConfigurationFileName(configurationName);

		ApplicationContext context = Application.getInstance().getContext();
		try {
			context.getLocalStorage().save(createproxySettings(), configurationFile);
		} catch (IOException e) {
			System.err.println("Error saving current proxy settings to " + configurationFile + ":" + e.getMessage());
		}
	}

	public void loadDecoratorSettings(String configurationName) {

		String configurationFile = makeDecoratorConfigurationFileName(configurationName);

		ApplicationContext context = Application.getInstance().getContext();
		try {
			
			HashMap<String, Object> settings = (HashMap<String, Object>) context.getLocalStorage().load(configurationFile);

			if(settings != null){
				Object setting;
				
				setting = settings.get(ByteReaderFactory.LATENCY_PROPERTY_NAME + ".enabled");
				if(setting != null)
					view.setLatencyEnabled((Boolean) setting);
				
				setting = settings.get(ByteReaderFactory.LATENCY_PROPERTY_NAME);
				if(setting != null)
					view.setLatencyValue((Integer) setting);
				
				setting = settings.get(ByteReaderFactory.PACKET_LOSS_PROPERTY_NAME + ".enabled");
				if(setting != null)
					view.setPacketLossEnabled((Boolean) setting);
				
				setting = settings.get(ByteReaderFactory.PACKET_LOSS_PROPERTY_NAME);
				if(setting != null)
					view.setPacketLossValue((Integer) setting);

				setting = settings.get(ByteReaderFactory.THROTTLE_PROPERTY_NAME + ".enabled");
				if(setting != null)
					view.setThrottleEnabled((Boolean) setting);

				setting = settings.get(ByteReaderFactory.THROTTLE_PROPERTY_NAME);
				if(setting != null)
					view.setThrottleValue((Integer) setting);

				setting = settings.get(ByteReaderFactory.CONNECTION_LOSS_PROPERTY_NAME + ".enabled");
				if(setting != null)
					view.setConnectionLossEnabled((Boolean) setting);
			}

		} catch (IOException e) {
			System.err.println("Error loading proxy settings from " + configurationFile + ":" + e.getMessage());
		}
	}

	public void saveCurrentDecoratorSettings(String configurationName) {
		
		String configurationFile = makeDecoratorConfigurationFileName(configurationName);
		
		HashMap<String, Object> settings = new HashMap<String, Object>();
		settings.put(ByteReaderFactory.LATENCY_PROPERTY_NAME + ".enabled", view.isLatencyEnabled());
		settings.put(ByteReaderFactory.LATENCY_PROPERTY_NAME, view.getLatencyValue());
		settings.put(ByteReaderFactory.PACKET_LOSS_PROPERTY_NAME + ".enabled", view.isPacketLossEnabled());
		settings.put(ByteReaderFactory.PACKET_LOSS_PROPERTY_NAME, view.getPacketLossValue());
		settings.put(ByteReaderFactory.THROTTLE_PROPERTY_NAME + ".enabled", view.isThrottleEnabled());
		settings.put(ByteReaderFactory.THROTTLE_PROPERTY_NAME, view.getThrottleValue());
		settings.put(ByteReaderFactory.CONNECTION_LOSS_PROPERTY_NAME + ".enabled", view.isConnectionLossEnabled());
		
		ApplicationContext context = Application.getInstance().getContext();
		try {
			context.getLocalStorage().save(settings, configurationFile);
		} catch (IOException e) {
			System.err.println("Error saving current proxy settings to " + configurationFile + ":" + e.getMessage());
		}
	}

	private String makeProxyConfigurationFileName(String configurationName) {
		ApplicationContext context = Application.getInstance().getContext();
		try {
			new File(context.getLocalStorage().getDirectory().getCanonicalPath() + "/" + configurationName).mkdirs();
		} catch (IOException e) {
			System.err.println("Error creating configuration named " + configurationName + ":" + e.getMessage());
		}
		return configurationName + "/" + PROXY_STATE_FILE;
	}

	private String makeDecoratorConfigurationFileName(String configurationName) {
		ApplicationContext context = Application.getInstance().getContext();
		try {
			new File(context.getLocalStorage().getDirectory().getCanonicalPath() + "/" + configurationName).mkdirs();
		} catch (IOException e) {
			System.err.println("Error creating configuration named " + configurationName + ":" + e.getMessage());
		}
		return configurationName + "/" + DECORATOR_STATE_FILE;
	}

	private void processDecorators() {
		checkLatency();
		checkPacketLoss();
		checkThrottle();
		checkConnectionLoss();
	}

	private void checkConnectionLoss() {
		if (view.isConnectionLossEnabled()) {
			System.setProperty(ByteReaderFactory.CONNECTION_LOSS_PROPERTY_NAME, "true");
		} else {
			System.setProperty(ByteReaderFactory.CONNECTION_LOSS_PROPERTY_NAME, "false");
		}
	}

	private void checkThrottle() {
		if (view.isThrottleEnabled()) {
			System.setProperty(ByteReaderFactory.THROTTLE_PROPERTY_NAME, Integer.toString(view.getThrottleValue()));
		} else {
			System.clearProperty(ByteReaderFactory.THROTTLE_PROPERTY_NAME);
		}
	}

	private void checkPacketLoss() {
		if (view.isPacketLossEnabled()) {
			System.setProperty(ByteReaderFactory.PACKET_LOSS_PROPERTY_NAME, Integer.toString(view.getPacketLossValue()));
		} else {
			System.clearProperty(ByteReaderFactory.PACKET_LOSS_PROPERTY_NAME);
		}
	}

	private void checkLatency() {
		if (view.isLatencyEnabled()) {
			System.setProperty(ByteReaderFactory.LATENCY_PROPERTY_NAME, Integer.toString(view.getLatencyValue()));
		} else {
			System.clearProperty(ByteReaderFactory.LATENCY_PROPERTY_NAME);
		}
	}

	public void stop() {
		if (proxy != null) {
			setStopping();
			proxy.stop();
			proxy = null;
			setNotRunning();
		}
	}

	public void networkOff() {
		ConnectionLossDecorator.networkOff();
		view.setConnectionLossCloseEnabled(false);
		view.setConnectionLossOpenEnabled(true);
	}

	public void networkOn() {
		ConnectionLossDecorator.networkOn();
		view.setConnectionLossCloseEnabled(true);
		view.setConnectionLossOpenEnabled(false);
	}

	public void clear() {
		view.clearConsole();
	}

}
