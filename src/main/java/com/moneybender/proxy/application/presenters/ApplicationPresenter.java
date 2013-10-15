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

package com.moneybender.proxy.application.presenters;

import org.apache.log4j.Logger;

import com.moneybender.proxy.application.views.IApplicationView;
import com.moneybender.proxy.application.views.IConfigurationChooser;
import com.moneybender.proxy.application.views.IConfigurationSaver;
import com.moneybender.proxy.application.views.IDialog;

public class ApplicationPresenter {
	
	private static final String APPLICATION_NAME = "DonsProxy";

	private final IApplicationView view;

	public ApplicationPresenter(IApplicationView view) {
		this.view = view;
	}

	public void shutdown() {
		view.dispose();
	}

	public void ready() {
	}

	public void loadSelectedConfiguration(IConfigurationChooser chooser) {
		chooser.dispose();

		ProxyPresenter presenter = view.getProxyPresenter();
		String configurationName = chooser.getChosenConfigurationName();
		
		setTitle(configurationName);

		if(configurationName == null){
			
			Logger.getLogger(this.getClass()).info("No configuration selected.");

		} else {
			presenter.loadProxySettings(configurationName);
			presenter.loadDecoratorSettings(configurationName);
			
			Logger.getLogger(this.getClass()).info("Loaded configuration '" + configurationName + "'");
		}
	}
	
	private void setTitle(String configurationName) {
		if(configurationName == null || configurationName.trim().length() == 0){
			view.setTitle(APPLICATION_NAME);
		} else {
			view.setTitle(APPLICATION_NAME + " - " + configurationName);
		}
	}

	public void deleteSelectedConfiguration(IConfigurationChooser chooser) {
		chooser.dispose();

		ProxyPresenter presenter = view.getProxyPresenter();
		String configurationName = chooser.getChosenConfigurationName();
		
		setTitle(null);
		if(configurationName == null){
			
			Logger.getLogger(this.getClass()).info("No configuration selected.");

		} else {

			presenter.deleteConfiguration(configurationName);

			Logger.getLogger(this.getClass()).info("Deleted configuration '" + configurationName + "'");
		}
	}

	public void saveNewConfiguration(IConfigurationSaver saver){
		saver.dispose();

		ProxyPresenter presenter = view.getProxyPresenter();
		String configurationName = saver.getTypedName();

		setTitle(configurationName);

		if(configurationName == null || configurationName.trim().length() == 0){
			
			Logger.getLogger(this.getClass()).info("Not creating a null configuration");

		} else {
			presenter.saveCurrentProxySettings(configurationName);
			presenter.saveCurrentDecoratorSettings(configurationName);
			
			Logger.getLogger(this.getClass()).info("Created configuration '" + configurationName + "'");
		}
	}
	
	public void saveSelectedConfiguration(IConfigurationSaver saver){

		saver.dispose();
		
		ProxyPresenter presenter = view.getProxyPresenter();
		String configurationName = saver.getChosenConfigurationName();
		setTitle(configurationName);
		presenter.saveCurrentProxySettings(configurationName);
		presenter.saveCurrentDecoratorSettings(configurationName);
		
		Logger.getLogger(this.getClass()).info("Updated configuration '" + configurationName + "'");
	}

	public void cancel(IDialog dialog) {
		dialog.dispose();
	}

}
