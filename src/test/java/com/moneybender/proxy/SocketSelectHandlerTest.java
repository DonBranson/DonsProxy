/*
 * Created on Jun 28, 2007
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
package com.moneybender.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.moneybender.proxy.channels.ByteReader;
import com.moneybender.proxy.channels.ByteWriter;
import com.moneybender.proxy.management.MBeanManager;
import com.moneybender.proxy.publishers.SubscriberManager;

public class SocketSelectHandlerTest {
	
	private static Logger log = Logger.getLogger(SocketSelectHandlerTest.class);

	private SubscriberManager manager;
	private SocketSelectHandler socketHandler;
	private ServerSocket mockProxy;
	private boolean listenReady;
	private int listenPort;

	@Before
	public void setUp() throws Exception {

		Thread.sleep(125);

		manager = new SubscriberManager(ProxySettings.DEFAULT_REQUEST_PORT,
			ProxySettings.DEFAULT_RESPONSE_PORT, ProxySettings.DEFAULT_DUPLEX_PORT);
		final MBeanManager mbeanManager = new MBeanManager();

		listenReady = false;
		mockProxy = new ServerSocket(0);
		Thread thread = new Thread(){

			@Override
			public void run() {
				ByteReader byteReader = new ByteReader();
				ByteWriter byteWriter = new ByteWriter();
				try {
					ServerSocket socket = new ServerSocket(0);
					listenPort = socket.getLocalPort();
					socket.close();
					socketHandler = new SocketSelectHandler(manager, mbeanManager, byteReader, byteWriter,
						listenPort, "localhost", mockProxy.getLocalPort());
					socketHandler.start();
					listenReady = true;
					synchronized (this) {
						this.notifyAll();
					}
				} catch (IOException e) {
					log.error("Error starting socket selection handler:" + e.getMessage());
				}
			}
		};
		thread.setDaemon(true);
		thread.start();

		synchronized (thread) {
			thread.wait(10000);
		}
		
		Assert.assertTrue(listenReady);
	}
	
	@After
	public void tearDown() throws Exception {
		if(mockProxy != null)
			mockProxy.close();
		if(manager != null)
			manager.close();
		if(socketHandler != null)
			socketHandler.stop();
	}
	
	@Test
	public void testAcceptConnection() throws Exception {

		Socket socket = new Socket("localhost", listenPort);
		Socket acceptedSocket = mockProxy.accept();
		acceptedSocket.getOutputStream().write(5);
		int i = socket.getInputStream().read();
		Assert.assertEquals("Bytes not sent through socket.", 5, i);
		socket.close();
	}

	@Test
	public void testConnectRefusedHandling() throws Exception {

		mockProxy.close();
		mockProxy = null;
		
		Thread.sleep(125);

		Socket socket = new Socket("localhost", listenPort);

		try{
			int i = socket.getInputStream().read();
			Assert.assertEquals("Socket should have been closed by the proxy.", -1, i);
		}catch(SocketException success){
		}
	}
}
