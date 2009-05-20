/*
 * Created on Apr 27, 2008
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

import org.jdesktop.application.Application;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProxyPresenterTest {

	private MockProxyView view;
	private ProxyPresenter presenter;

	@Before
	public void setUp() throws Exception {
		view = new MockProxyView();
		presenter = new ProxyPresenter(view);
		assertNotRunning();
		view.setConnectionLossEnabled(false);
	}
	
	@Test
	public void testProxyLifecycle() throws Exception {
		presenter.startProxy();
		assertRunning();
		presenter.stop();
		assertNotRunning();
	}
	
	@Test
	public void testWithConnectionLossChecked() throws Exception {
		view.setConnectionLossEnabled(true);
		presenter.startProxy();
		assertRunningWithConnectionLoss();
		presenter.stop();
		assertNotRunning();
	}
	
	@Test
	public void testSaveAndLoadConfig() throws Exception {
		view.setListenPort(9998);
		view.setTargetHost("aaa");
		view.setTargetPort(9997);
		presenter.startProxy();		// Saves settings as side-effect
		presenter.stop();
		
		Assert.assertEquals(9998, view.getListenPort());
		Assert.assertEquals("aaa", view.getTargetHost());
		Assert.assertEquals(9997, view.getTargetPort());

		view.setListenPort(0);
		view.setTargetHost("");
		view.setTargetPort(1);
		Assert.assertEquals(0, view.getListenPort());
		Assert.assertEquals("", view.getTargetHost());
		Assert.assertEquals(1, view.getTargetPort());

		presenter = new ProxyPresenter(view);
		Assert.assertEquals(9998, view.getListenPort());
		Assert.assertEquals("aaa", view.getTargetHost());
		Assert.assertEquals(9997, view.getTargetPort());
	}
	
	@Test
	public void testLoadAndSaveDecoratorSettings() throws Exception {
		view.setLatencyEnabled(false);
		view.setLatencyValue(111);
		view.setThrottleEnabled(false);
		view.setThrottleValue(222);
		view.setPacketLossEnabled(false);
		view.setPacketLossValue(333);
		view.setConnectionLossEnabled(false);
		presenter.startProxy();		// Saves settings as side-effect
		presenter.stop();
		
		Assert.assertFalse(view.isLatencyEnabled());
		Assert.assertEquals(111, view.getLatencyValue());
		Assert.assertFalse(view.isThrottleEnabled());
		Assert.assertEquals(222, view.getThrottleValue());
		Assert.assertFalse(view.isPacketLossEnabled());
		Assert.assertEquals(333, view.getPacketLossValue());
		Assert.assertFalse(view.isConnectionLossEnabled());

		view.setLatencyEnabled(true);
		view.setLatencyValue(1111);
		view.setThrottleEnabled(true);
		view.setThrottleValue(2222);
		view.setPacketLossEnabled(true);
		view.setPacketLossValue(3333);
		view.setConnectionLossEnabled(true);

		Assert.assertTrue(view.isLatencyEnabled());
		Assert.assertEquals(1111, view.getLatencyValue());
		Assert.assertTrue(view.isThrottleEnabled());
		Assert.assertEquals(2222, view.getThrottleValue());
		Assert.assertTrue(view.isPacketLossEnabled());
		Assert.assertEquals(3333, view.getPacketLossValue());
		Assert.assertTrue(view.isConnectionLossEnabled());

		presenter = new ProxyPresenter(view);
		Assert.assertFalse(view.isLatencyEnabled());
		Assert.assertEquals(111, view.getLatencyValue());
		Assert.assertFalse(view.isThrottleEnabled());
		Assert.assertEquals(222, view.getThrottleValue());
		Assert.assertFalse(view.isPacketLossEnabled());
		Assert.assertEquals(333, view.getPacketLossValue());
		Assert.assertFalse(view.isConnectionLossEnabled());
	}
	
	@Test
	public void testNetworkConnectionLifecycle() throws Exception {
		view.setConnectionLossEnabled(true);
		presenter.startProxy();
		assertRunningWithConnectionLoss();

		Assert.assertTrue(view.isConnectionLossCloseEnabled());
		Assert.assertFalse(view.isConnectionLossOpenEnabled());
		presenter.networkOff();
		Assert.assertFalse(view.isConnectionLossCloseEnabled());
		Assert.assertTrue(view.isConnectionLossOpenEnabled());
		presenter.networkOn();
		Assert.assertTrue(view.isConnectionLossCloseEnabled());
		Assert.assertFalse(view.isConnectionLossOpenEnabled());
		
		presenter.stop();
		assertNotRunning();
	}
	
	@Test
	public void testSaveAndLoadNamedConfiguration() throws Exception {
		String configurationName = this.getClass().getSimpleName();

		view.setListenPort(9998);
		view.setThrottleValue(2223);
		Assert.assertEquals(9998, view.getListenPort());
		Assert.assertEquals(2223, view.getThrottleValue());

		presenter.saveCurrentProxySettings(configurationName);
		presenter.saveCurrentDecoratorSettings(configurationName);

		view.setListenPort(1000);
		view.setThrottleValue(2224);
		Assert.assertEquals(1000, view.getListenPort());
		Assert.assertEquals(2224, view.getThrottleValue());

		presenter.loadProxySettings(configurationName);
		presenter.loadDecoratorSettings(configurationName);
		Assert.assertEquals(9998, view.getListenPort());
		Assert.assertEquals(2223, view.getThrottleValue());
	}
	
	@Test
	public void testDeleteConfiguration() throws Exception {
		String testConfigName = "deleteme";
		
		presenter.deleteConfiguration(null);

		String configDir = Application.getInstance().getContext().getLocalStorage().getDirectory().getCanonicalPath()
			+ System.getProperty("file.separator") + testConfigName;
		presenter.saveCurrentProxySettings(testConfigName);
		Assert.assertTrue(new File(configDir).exists());
		presenter.deleteConfiguration(testConfigName);
		Assert.assertFalse(new File(configDir).exists());
	}
	
	@Test
	public void testDeleteConfigurationWithSubdir() throws Exception {
		String testConfigName = "deleteme";
		
		presenter.deleteConfiguration(null);

		String configDir = Application.getInstance().getContext().getLocalStorage().getDirectory().getCanonicalPath()
			+ System.getProperty("file.separator") + testConfigName;
		presenter.saveCurrentProxySettings(testConfigName);
		
		new File(configDir + System.getProperty("file.separator") + "subdir").mkdirs();
		
		Assert.assertTrue(new File(configDir).exists());
		presenter.deleteConfiguration(testConfigName);
		Assert.assertFalse(new File(configDir).exists());
	}

	private void assertRunning() {
		Assert.assertFalse(view.isStartEnabled());
		Assert.assertTrue(view.isStopEnabled());
		Assert.assertFalse(view.isConnectionLossCloseEnabled());
		Assert.assertFalse(view.isConnectionLossOpenEnabled());
		Assert.assertFalse(view.isConnectionLossInputEnabled());
		Assert.assertFalse(view.isLatencyInputEnabled());
		Assert.assertFalse(view.isPacketLossInputEnabled());
		Assert.assertFalse(view.isThrottleInputEnabled());
		Assert.assertFalse(view.isListenPortEnabled());
		Assert.assertFalse(view.isTargetHostEnabled());
		Assert.assertFalse(view.isTargetPortEnabled());
	}

	private void assertRunningWithConnectionLoss() {
		Assert.assertFalse(view.isStartEnabled());
		Assert.assertTrue(view.isStopEnabled());
		Assert.assertTrue(view.isConnectionLossCloseEnabled());
		Assert.assertFalse(view.isConnectionLossOpenEnabled());
		Assert.assertFalse(view.isConnectionLossInputEnabled());
		Assert.assertFalse(view.isLatencyInputEnabled());
		Assert.assertFalse(view.isPacketLossInputEnabled());
		Assert.assertFalse(view.isThrottleInputEnabled());
		Assert.assertFalse(view.isListenPortEnabled());
		Assert.assertFalse(view.isTargetHostEnabled());
		Assert.assertFalse(view.isTargetPortEnabled());
	}

	private void assertNotRunning() {
		Assert.assertTrue(view.isStartEnabled());
		Assert.assertFalse(view.isStopEnabled());
		Assert.assertFalse(view.isConnectionLossCloseEnabled());
		Assert.assertFalse(view.isConnectionLossOpenEnabled());
		Assert.assertTrue(view.isConnectionLossInputEnabled());
		Assert.assertTrue(view.isLatencyInputEnabled());
		Assert.assertTrue(view.isPacketLossInputEnabled());
		Assert.assertTrue(view.isThrottleInputEnabled());
		Assert.assertTrue(view.isListenPortEnabled());
		Assert.assertTrue(view.isTargetHostEnabled());
		Assert.assertTrue(view.isTargetPortEnabled());
	}
	
}
