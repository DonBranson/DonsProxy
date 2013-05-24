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

import java.awt.Container;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.moneybender.proxy.application.views.IApplicationView;
import com.moneybender.proxy.application.views.IConfigurationChooser;
import com.moneybender.proxy.application.views.IConfigurationSaver;

public class ApplicationPresenterTest {

	private MockView view;

	@Before
	public void setUp() throws Exception {
		view = new MockView();
	}
	
	@Test
	public void testShutdown() throws Exception {

		ApplicationPresenter presenter = new ApplicationPresenter(view);
		Assert.assertFalse(view.disposedCalled);
		Assert.assertFalse(view.openCalled);
		presenter.shutdown();
		Assert.assertTrue(view.disposedCalled);
		Assert.assertFalse(view.openCalled);
	}
	
	@Test
	public void testSaveLoadDelete() throws Exception {
		ApplicationPresenter presenter = new ApplicationPresenter(new MockView());
		
		presenter.saveSelectedConfiguration(new MockSaver());
		presenter.loadSelectedConfiguration(new MockChooser());
		presenter.deleteSelectedConfiguration(new MockChooser());
	}
	
	private class MockView implements IApplicationView {

		private boolean disposedCalled = false;
		private boolean openCalled = false;

		public void dispose() {
			disposedCalled = true;
		}

		public Container getMainContainer() {
			return new Container();
		}

		public ProxyPresenter getProxyPresenter() {
			return new ProxyPresenter(new MockProxyView());
		}

		public void makeConfigurationChooserView() {
		}

		public void makeConfigurationSaverView() {
		}

		public void setTitle(String newTitle) {
		}
		
	}
	
	public class MockChooser implements IConfigurationChooser {

		public void dispose() {
		}

		public String getChosenConfigurationName() {
			return ApplicationPresenterTest.this.getClass().getSimpleName();
		}

		public void setList(String[] configurationNames) {
		}

	}

	public class MockSaver implements IConfigurationSaver {

		public void dispose() {
		}

		public String getChosenConfigurationName() {
			return ApplicationPresenterTest.this.getClass().getSimpleName();
		}

		public String getTypedName() {
			return "test2";
		}

		public void setList(String[] configurationNames) {
		}

	}

}
