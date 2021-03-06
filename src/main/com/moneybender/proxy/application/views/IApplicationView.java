/*
 * Created on Apr 26, 2008
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

package com.moneybender.proxy.application.views;

import java.awt.Container;

import com.moneybender.proxy.application.presenters.ProxyPresenter;

public interface IApplicationView {

	public void dispose();
	public Container getMainContainer();
	public ProxyPresenter getProxyPresenter();
	public void makeConfigurationSaverView();
	public void makeConfigurationChooserView();
	public void setTitle(String newTitle);
}
