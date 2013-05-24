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
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class SubscriberManager {

	private Logger log = Logger.getLogger(SubscriberManager.class);

	private Object requestSubscriberLock = new Object();
	private Vector<ObjectOutput> requestSubscribers = new Vector<ObjectOutput>();

	private Object responseSubscriberLock = new Object();
	private Vector<ObjectOutput> responseSubscribers = new Vector<ObjectOutput>();

	private TCPListenerThread requestSubsriberThread;
	private TCPListenerThread responseSubsriberThread;
	private TCPListenerThread duplexSubsriberThread;
	private LinkedBlockingQueue<Runnable> publishable;
	private ThreadPoolExecutor executor;
	
	private List<Socket> openConnections = new LinkedList<Socket>();

	public SubscriberManager(int requestPort, int responsePort, int duplexPort) throws IOException{

		requestSubsriberThread = new RequestSubscriberHandler("DP-Request Subscribers", requestPort);
		responseSubsriberThread = new ResponseSubscriberHandler("DP-Response Subscribers", responsePort);
		duplexSubsriberThread = new DuplexSubscriberHandler("DP-Duplex Subscribers", duplexPort);
		
		if(log.isDebugEnabled())
			log.debug("Ready.");
		
		publishable = new LinkedBlockingQueue<Runnable>();
		executor = new ThreadPoolExecutor(1, 1, Integer.MAX_VALUE, TimeUnit.MILLISECONDS, publishable);
	}

	public void publishRequest(PacketHeader header, byte[] buffer) {
		try {
			executor.execute(new PublishableRequest(this, header, buffer));
		}catch(RejectedExecutionException e) {
			log.error("Error publishing request packet:" + e.getMessage());
			if(log.isDebugEnabled()) {
				log.debug("Executor state: shutdown=" + executor.isShutdown()
				+ "; terminated=" + executor.isTerminated() + "; terminating=" + executor.isTerminating());
			}
		}
	}

	public void publishResponse(PacketHeader header, byte[] buffer) {
		try {
			executor.execute(new PublishableResponse(this, header, buffer));
		}catch(RejectedExecutionException e) {
			log.error("Error publishing response packet:" + e.getMessage());
			if(log.isDebugEnabled()) {
				log.debug("Executor state: shutdown=" + executor.isShutdown()
				+ "; terminated=" + executor.isTerminated() + "; terminating=" + executor.isTerminating());
			}
		}
	}

	void publishRequestNow(PacketHeader header, byte[] buffer) {
		publish(header, buffer, requestSubscribers);
	}

	void publishResponseNow(PacketHeader header, byte[] buffer) {
		publish(header, buffer, responseSubscribers);
	}

	private void publish(PacketHeader header, byte[] buffer, Vector<ObjectOutput> subscriberStreams) {
		if(log.isDebugEnabled())
			log.debug("Publish to " + subscriberStreams.size() + " subscribers.");
		for(int i=0; i<subscriberStreams.size(); ++i) {
			ObjectOutput oo = subscriberStreams.get(i);
			try {
				if(log.isDebugEnabled())
					log.debug("Publish.");
				synchronized (oo) {
					header.writeExternal(oo);
					oo.write(buffer, 0, header.getDataLength());
					oo.flush();
				}
			} catch (IOException e) {
				if(log.isDebugEnabled()) {
					log.debug(e.getClass().getSimpleName());
					log.debug("Error publishing to a subscriber:" + e.getMessage());
				}
			} catch (Exception e) {
				log.error("Error publishing packet:" + header.toString());
				e.printStackTrace();
			}
		}
	}
	
	public int getRequestSubscriberCount() {
		return requestSubscribers.size();
	}

	public int getResponseSubscriberCount() {
		return responseSubscribers.size();
	}

	public void close() {
		requestSubsriberThread.close();
		responseSubsriberThread.close();
		duplexSubsriberThread.close();
		executor.shutdown();
		
		for (Socket socket : openConnections) {
			try {
				socket.close();
			} catch (IOException ignore) {
			}
		}
		openConnections.clear();
		
		try { Thread.sleep(100); } catch (InterruptedException ignored) { }
	}

	private final class DuplexSubscriberHandler extends TCPListenerThread {
		private DuplexSubscriberHandler(String name, int port) throws IOException {
			super(name, port);
		}

		@Override
		public void onConnect(Socket socket) throws IOException {
			
			openConnections.add(socket);
			
			synchronized (requestSubscriberLock) {
				synchronized (responseSubscriberLock) {
					// N.B.:  Corrupt object stream if getObjectOutputStream is called once for each. 
					ObjectOutput stream = getObjectOutputStream(socket);
					requestSubscribers.add(stream);
					responseSubscribers.add(stream);
					if (log.isDebugEnabled())
						log.debug("Duplex subscriber attached from " + socket.getRemoteSocketAddress());
				}
			}
		}
	}

	private ObjectOutput getObjectOutputStream(Socket socket) throws IOException {
		return new ObjectOutputStream(socket.getOutputStream());
	}
	
	private final class ResponseSubscriberHandler extends TCPListenerThread {
		private ResponseSubscriberHandler(String name, int port) throws IOException {
			super(name, port);
		}

		@Override
		public void onConnect(Socket socket) throws IOException {
			
			openConnections.add(socket);
			
			synchronized (responseSubscriberLock) {
				responseSubscribers.add(getObjectOutputStream(socket));
				if (log.isDebugEnabled())
					log.debug("Response subscriber attached from " + socket.getRemoteSocketAddress());
			}
		}
	}

	private final class RequestSubscriberHandler extends TCPListenerThread {
		private RequestSubscriberHandler(String name, int port) throws IOException {
			super(name, port);
		}

		@Override
		public void onConnect(Socket socket) throws IOException {
			
			openConnections.add(socket);
			
			synchronized (requestSubscriberLock) {
				requestSubscribers.add(getObjectOutputStream(socket));
				if (log.isDebugEnabled())
					log.debug("Request subscriber attached from " + socket.getRemoteSocketAddress());
			}
		}
	}

}
