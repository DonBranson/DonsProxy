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
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

abstract class TCPListenerThread extends Thread {
	
	private static Logger log = Logger.getLogger(TCPListenerThread.class);
	final ServerSocket socket;

	public TCPListenerThread(String threadName, final int port) throws IOException {
		if(log.isDebugEnabled())
			log.debug("Listen on port " + port);

		try{
			socket = new ServerSocket(port);
		} catch(BindException e){
			throw new BindException("Could not listen on port " + port);
		}
		Thread th = new Thread(threadName){
			@Override
			public void run() {
				try {
					while(true){
						onConnect(socket.accept());
					}
				} catch (IOException e) {
					if(!socket.isClosed()){
						log.error(e.getClass().getSimpleName() + " handling accept on " + port +":" + e.getMessage());
					}
				}
				
			}

		};
		th.setDaemon(true);
		th.start();
	}
	
	public abstract void onConnect(Socket socket) throws IOException;

	public void close() {
		if(log.isDebugEnabled())
			log.debug("Close (" + this.getClass().getSimpleName() + ")");

		if(socket != null){
			try {
				socket.close();
			} catch (IOException dontCare) {
			}
		}
	}

}
