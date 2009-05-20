/*
 * Created on Nov 16, 2007
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
package com.moneybender.proxy.management;

import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.moneybender.proxy.pipes.Session;

public class MBeanManager {

	protected Logger log = Logger.getLogger(this.getClass());
	private int unregisterDelaySeconds;

	final ConcurrentHashMap<Object, ProxyMXBean> mbeanRegistry;
	
	public MBeanManager() {
		this.mbeanRegistry = new ConcurrentHashMap<Object, ProxyMXBean>();
	}

	public void register(Object object, ProxyMXBean mbean) {
		mbeanRegistry.put(object, mbean);
		registerWithMBeanServer(mbean);
	}

	public boolean isRegistered(Object object) {
		return mbeanRegistry.containsKey(object);
	}

	public void unregister(Object object) {
		if(object != null) {
			try {
				ProxyMXBean mbean = mbeanRegistry.remove(object);
				unregisterWithMBeanServer(mbean);
			}catch(Exception e) {
				log.warn("Problem unregistering MBean:" + e.getMessage());
			}
		}
	}

	private void registerWithMBeanServer(ProxyMXBean mbean) {
		String mbeanName = mbean.getMBeanName();
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		try {
			mbs.unregisterMBean(new ObjectName(mbeanName));
		} catch (Exception ignore) {
		}
		try {
			mbs.registerMBean(mbean, new ObjectName(mbeanName));
		} catch (Exception e) {
			log.warn("Failed to register MBean " + mbeanName + ":" + e.getMessage());
		}
	}

	private void unregisterWithMBeanServer(final ProxyMXBean mbean) {
		
		if(mbean == null)
			return;
		
		// N.B.:  Wait to unregister.  Makes it easier to catch before it goes away.
		if(unregisterDelaySeconds > 0) {
			Thread th = new Thread("DP-MBeanManager " + mbean.getMBeanName()) {
				@Override
				public void run() {
					try {
						Thread.sleep(unregisterDelaySeconds * 1000);
					} catch (InterruptedException ignore) {
					}
					unregisterMBean(mbean);
				}

			};
			th.setDaemon(true);
			th.start();
		} else {
			unregisterMBean(mbean);
		}
	}

	private void unregisterMBean(ProxyMXBean mbean) {
		String mbeanName = mbean.getMBeanName();
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		
		if(mbs != null && mbeanName != null){
			try{
				mbs.unregisterMBean(new ObjectName(mbeanName));
			} catch(InstanceNotFoundException ignore){
			} catch (Exception e) {
				log.warn("Failed to unregister MBean " + mbeanName + ":" + e.getMessage());
			}
		}
	}

	protected int getUnregisterDelaySeconds() {
		return unregisterDelaySeconds;
	}

	public void setUnregisterDelaySeconds(int unregisterDelaySeconds) {
		if(log.isDebugEnabled())
			log.debug("Seconds of delay before unregistering a bean:" + unregisterDelaySeconds);
		this.unregisterDelaySeconds = unregisterDelaySeconds;
	}

	public void unregisterAllSessions() {
		for (Iterator iterator = mbeanRegistry.keySet().iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			if(object instanceof Session) {
				unregister(object);
				mbeanRegistry.remove(object);
			}
		}
	}

}
