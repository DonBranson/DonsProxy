/*
 * Created on May 11, 2007
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
package com.moneybender.proxy;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.moneybender.proxy.channels.ByteReaderFactory;
import com.moneybender.proxy.channels.ByteWriterFactory;
import com.moneybender.proxy.channels.IReadBytes;
import com.moneybender.proxy.channels.IWriteBytes;
import com.moneybender.proxy.management.DecoratorControl;
import com.moneybender.proxy.management.MBeanControl;
import com.moneybender.proxy.management.MBeanManager;
import com.moneybender.proxy.management.ProxyMetrics;
import com.moneybender.proxy.publishers.SubscriberManager;

public class Proxy {

	private static Logger log = Logger.getLogger(Proxy.class);
	private SocketSelectHandler socketHandler;
	private MBeanManager mbeanManager;
	private SubscriberManager subscriberManager;
	private MBeanControl mbeanControl;
	private ProxyMetrics proxyMetrics;
	private DecoratorControl decoratorControl;

	public static void main(String[] args) {
		
		try {
			Thread.currentThread().setName("DP-Proxy");
			Proxy proxy = new Proxy();
			proxy.start(new ProxySettings());
			synchronized(proxy) {
				proxy.wait();
			}
		} catch (IOException e) {
			log.error("Error on startup: " + e.getMessage());
		} catch (InterruptedException ignore) {
		}
	}

	public void start(ProxySettings settings) throws IOException {
		mbeanManager = new MBeanManager();
		mbeanControl = new MBeanControl(mbeanManager, 10);
		mbeanManager.register(mbeanManager, mbeanControl);

		decoratorControl = new DecoratorControl();
		try {
			mbeanManager.register(decoratorControl, decoratorControl);

			subscriberManager = new SubscriberManager(settings.getRequestPort(), settings.getResponsePort(), settings.getDuplexPort());
			IReadBytes byteReader = new ByteReaderFactory().getByteReader(decoratorControl, mbeanManager);
			IWriteBytes byteWriter = new ByteWriterFactory().getByteWriter(decoratorControl, mbeanManager);
			proxyMetrics = new ProxyMetrics(settings);
			mbeanManager.register(this, proxyMetrics);

			socketHandler = new SocketSelectHandler(subscriberManager, mbeanManager, byteReader, byteWriter, settings.getListenPort(),
				settings.getTargetHost(), settings.getTargetPort());
			socketHandler.start();
		} catch (IOException e) {
			mbeanManager.unregister(this);
			mbeanManager.unregister(decoratorControl);

			stop();

			synchronized (this) {
				this.notifyAll();
			}
			
			throw e;
		}
	}
	
	public void stop() {
		
		unregisterAllMBeans();

		if(socketHandler != null) {
			socketHandler.stop();
			socketHandler = null;
		}

		if(subscriberManager != null) {
			subscriberManager.close();
			subscriberManager = null;
		}

		synchronized (this) {
			this.notifyAll();
		}
	}

	private void unregisterAllMBeans() {
		if(mbeanManager != null) {
			mbeanManager.setUnregisterDelaySeconds(0);
			mbeanManager.unregister(decoratorControl);
			mbeanManager.unregister(this);
			mbeanManager.unregister(mbeanManager);
			mbeanManager = null;
		}
	}

}
