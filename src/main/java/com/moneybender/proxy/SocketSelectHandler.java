/*
 * Created on June 2, 2007
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
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.moneybender.proxy.channels.IReadBytes;
import com.moneybender.proxy.channels.IWriteBytes;
import com.moneybender.proxy.management.MBeanManager;
import com.moneybender.proxy.management.SessionMetrics;
import com.moneybender.proxy.pipes.BytesLeftException;
import com.moneybender.proxy.pipes.ClientSession;
import com.moneybender.proxy.pipes.SecurableSession;
import com.moneybender.proxy.pipes.ServerSession;
import com.moneybender.proxy.publishers.SubscriberManager;

class SocketSelectHandler {
	
	private static Logger log = Logger.getLogger(SocketSelectHandler.class);

	private SubscriberManager subscriberManager;
	private MBeanManager mbeanManager;
	private ServerSocketChannel serverChannel;
	private final int listenPort;
	private final String targetHost;
	private final int targetPort;
	private int sessionID = 0;

	private IReadBytes byteReader;
	private IWriteBytes byteWriter;
	
	private Boolean stopping;

	public SocketSelectHandler(SubscriberManager subscriberManager, MBeanManager mbeanManager, IReadBytes byteReader,
		IWriteBytes byteWriter, int listenPort, String targetHost, int targetPort)
	{
		this.subscriberManager = subscriberManager;
		this.mbeanManager = mbeanManager;
		this.byteReader = byteReader;
		this.byteWriter = byteWriter;
		this.listenPort = listenPort;
		this.targetHost = targetHost;
		this.targetPort = targetPort;
		
		this.stopping = false;
	}
	
	public void start() throws IOException {
		
		final Selector selector = Selector.open();

		InetSocketAddress socketAddress = new InetSocketAddress(listenPort);
		serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);
		try{
			serverChannel.socket().bind(socketAddress);
		} catch(BindException e){
			throw new BindException("Could not listen on port " + socketAddress.getPort());
		}
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);

		log.info("Listening on port " + listenPort + ", target at " + targetHost + ":" + targetPort);
		
		Thread selectThread = new Thread("DP-SelectLoop") {
			@Override
			public void run() {
				proxyLoop(selector);
			}
		};
		selectThread.setDaemon(true);
		selectThread.start();
	}

	public void stop() {
		stopping = true;
		synchronized(stopping) {
			try {
				stopping.wait(1000);
			} catch (InterruptedException ignore) {
			}
		}
	}

	private void proxyLoop(Selector selector) {

		while (true) {
			try {
				// Why we sleep:  If any of the byteReader decorators can leaves bytes unread, we'll
				// suck cycles without a sleep here.  This is a quick sleep, but does the trick.
				// Example ByteReaders that do this are ThrottleDecorator and LatencyDecorator.
				if(byteReader.willLeaveBytesUnread()){
					Thread.sleep(25);
				}
				int readyKeyCount = selector.select(250);
				if(readyKeyCount > 0){
					processReadyKeys(selector, selector.selectedKeys());
				}
			} catch(Exception e) {
				log.error("Error from proxy loop:" + e.getMessage());
				if(e.getMessage() == null){
					e.printStackTrace();
					break;
				}
			}
			
			if(stopping) {
				try {
					serverChannel.close();
					mbeanManager.unregisterAllSessions();
					synchronized(stopping) {
						stopping.notifyAll();
					}
				} catch (Exception ignore) {
				}
				
				break;
			}
		}
	}

	private void processReadyKeys(Selector selector, Set<SelectionKey> readyKeys) {
		Iterator<SelectionKey> iterator = readyKeys.iterator();
		while (iterator.hasNext()) {

			SelectionKey key = iterator.next();
			iterator.remove();

			if(key.isValid())
				processReadyKey(selector, key);
		}
	}

	private void processReadyKey(Selector selector, SelectionKey key) {

		SecurableSession session = (SecurableSession) key.attachment();
		try {
			
			if (key.isReadable() || key.isWritable()) {
				transferBytes(key);
				if(key.isValid())
					key.interestOps(SelectionKey.OP_READ);
			} else {
				if (key.isAcceptable())
					processAcceptableKey(selector, key);
			}
			
		} catch(BytesLeftException e) {
			
			if(key.isValid())
				key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			if(log.isDebugEnabled())
				log.debug(e.getBytesOrphaned() + " bytes orphaned for session " + session.getSessionID());

		} catch(IOException e) {

			key.cancel();

			if(!(e instanceof ClosedChannelException)) {
				log.error("Error transferring bytes:" + e.getMessage());
				if(e.getMessage() == null) {
					e.printStackTrace();
				}
			}

			if(session != null) {
				mbeanManager.unregister(session);
				mbeanManager.unregister(session.getEncrypter());
			}
			
		} catch(CancelledKeyException e) {
			log.error("Attempted to process cancelled key for session " + session.getSessionID(), e);
		}
	}

	private void processAcceptableKey(Selector selector, SelectionKey key) throws IOException {
		try{
			acceptConnection(selector, key);
		}catch(UnknownHostException e) {
			log.error("'" + targetHost + "' is not a known host.");
		}catch(ConnectException e){
			log.error("ConnectException connecting to target:" + e.getMessage());
			log.error("'" + targetHost + "' is not accepting connections on port " + targetPort);
		}
	}

	private void transferBytes(SelectionKey key) throws IOException {

		SecurableSession session = (SecurableSession) key.attachment();
		if(session.transferBytes() == -1)
			throw new ClosedChannelException();
	}

	private void acceptConnection(Selector selector, SelectionKey key) throws IOException {

		InetSocketAddress remoteAddress = new InetSocketAddress(targetHost, targetPort);
		log.info("Begin session " + sessionID +  " on " + ((ServerSocketChannel)key.channel()).socket().getLocalPort()
			+ "; connect to " + remoteAddress);
		
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel clientChannel = (SocketChannel) serverSocketChannel.accept();
		clientChannel.configureBlocking(false);
		
		SocketChannel serverChannel = SocketChannel.open();
		serverChannel.configureBlocking(true);
		try{
			serverChannel.socket().connect(remoteAddress);
			serverChannel.configureBlocking(false);
		}catch(IOException e){
			clientChannel.close();
			throw e;
		}

		SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
		SelectionKey proxyKey = serverChannel.register(selector, SelectionKey.OP_READ);

		createTwinPipes(serverChannel, clientChannel, clientKey, proxyKey);
		
		++sessionID;
	}

	private void createTwinPipes(SocketChannel serverChannel, SocketChannel clientChannel, SelectionKey clientKey, SelectionKey proxyKey) {
		Socket serverSocket = serverChannel.socket();
		Socket clientSocket = clientChannel.socket();
		
		SecurableSession serverSession = new ServerSession(sessionID, serverChannel, clientChannel, subscriberManager, byteReader, byteWriter,
			serverSocket.getInetAddress().getHostAddress(), serverSocket.getPort());
		SecurableSession clientSession = new ClientSession(sessionID, clientChannel, serverChannel, subscriberManager, byteReader, byteWriter,
			clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
		serverSession.setEncrypter(clientSession);
		clientSession.setEncrypter(serverSession);
		proxyKey.attach(serverSession);
		clientKey.attach(clientSession);

		mbeanManager.register(clientSession, new SessionMetrics(clientSession));
		mbeanManager.register(serverSession, new SessionMetrics(serverSession));
	}

}
