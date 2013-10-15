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
package com.moneybender.proxy.publishers;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import org.junit.Assert;
import org.junit.Test;

public class TCPListenerThreadTest {

	@Test
	public void testShutdown() throws Exception {
		MockListenerThread listener = new MockListenerThread("DP-Request Subscribers", 0){
			
			@Override
			public void onConnect(Socket socket) {
			}
			
		};
		Socket socket = new Socket("localhost", listener.getListenPort());
		Assert.assertTrue(socket.isConnected());
		
		listener.close();
		
		Thread.sleep(100);
		
		try{
			socket = new Socket("localhost", listener.getListenPort());
			Assert.fail("Should not have been able to connect to listen port.");
		}catch (ConnectException e){
		}
	}
	
	@Test
	public void testListener() throws IOException {

		MockListenerThread subscribers = new MockListenerThread("DP-Request Subscribers", 0);

		for (int i = 1; i <= 5; i++) {
			
			Socket socket = new Socket("localhost", subscribers.getListenPort());
			Assert.assertTrue(socket.isConnected());

			try{
				synchronized (subscribers.lock) {
					try {
						subscribers.lock.wait(100);
					} catch (InterruptedException ignored) {
					}
				}
				Assert.assertTrue(subscribers.connectCount == i);
			}finally{
				socket.close();
			}
		}
		
		subscribers.close();
	}

	class MockListenerThread extends TCPListenerThread {

		public Object lock = new Object();

		public int connectCount;

		public MockListenerThread(String threadName, int port) throws IOException {
			super(threadName, port);
			connectCount = 0;
		}

		public int getListenPort() {
			return socket.getLocalPort();
		}

		@Override
		public void onConnect(Socket socket) {
			++connectCount;
			Assert.assertNotNull(socket);
			try {
				socket.close();
			} catch (IOException e) {
				Assert.fail("Should not have received error closing socket.");
			}
		}

	}
}
