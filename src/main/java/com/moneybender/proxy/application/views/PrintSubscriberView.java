/*
 * Created on April 15, 2008
 * 
 * Copyright (c) 2008, Don Branson.  All Rights Reserved.
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

import org.jdesktop.application.Action;

import com.moneybender.proxy.application.presenters.PrintSubscriberPresenter;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class PrintSubscriberView extends AbstractSubscriberView {

	PrintSubscriberPresenter presenter;
	
	@Override
	protected void initGUI() {
		super.initGUI();
		presenter = new PrintSubscriberPresenter(this);
	}
	
	@Override
	@Action
	public void startAction() {
		presenter.start();
	}

	@Override
	@Action
	public void stopAction() {
		presenter.stop();
	}

	@Override
	@Action
	public void clearAction() {
		presenter.clear();
	}
}
