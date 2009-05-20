/*
 * Created on April 13, 2008
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

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;

import com.moneybender.proxy.application.presenters.ProxyPresenter;

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
public class DecoratorInputPanel extends javax.swing.JPanel {

	private JCheckBox latencyEnabled;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JCheckBox connectionLossEnabled;
	private JButton openButton;
	private JButton closeButton;
	private JTextField throttleValue;
	private JTextField packetLossValue;
	private JTextField latencyValue;
	private JCheckBox throttleEnabled;
	private JCheckBox packetLossEnabled;
	private ProxyPresenter presenter;

	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new DecoratorInputPanel());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public DecoratorInputPanel() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			GroupLayout thisLayout = new GroupLayout((JComponent)this);
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(313, 135));
			this.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
			this.setOpaque(false);
			{
				latencyEnabled = new JCheckBox();
				latencyEnabled.setName("latencyEnabled");
				latencyEnabled.setSize(100, 18);
				latencyEnabled.setOpaque(false);
			}
			{
				packetLossEnabled = new JCheckBox();
				packetLossEnabled.setName("packetLossEnabled");
				packetLossEnabled.setSize(100, 18);
				packetLossEnabled.setOpaque(false);
			}
			{
				throttleEnabled = new JCheckBox();
				throttleEnabled.setName("throttleEnabled");
				throttleEnabled.setSize(100, 18);
				throttleEnabled.setOpaque(false);
			}
			{
				connectionLossEnabled = new JCheckBox();
				connectionLossEnabled.setName("connectionLossEnabled");
				connectionLossEnabled.setSize(100, 18);
				connectionLossEnabled.setOpaque(false);
			}
			{
				jLabel2 = new JLabel();
				jLabel2.setName("jLabel2");
			}
			{
				latencyValue = new JTextField();
				latencyValue.setName("latencyValue");
				latencyValue.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			}
			{
				jLabel3 = new JLabel();
				jLabel3.setName("jLabel3");
			}
			{
				throttleValue = new JTextField();
				throttleValue.setName("throttleValue");
				throttleValue.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			}
			{
				jLabel4 = new JLabel();
				jLabel4.setName("jLabel4");
			}
			{
				packetLossValue = new JTextField();
				packetLossValue.setName("packetLossValue");
				packetLossValue.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			}
				thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(latencyValue, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					    .addComponent(jLabel2, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					    .addComponent(latencyEnabled, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(packetLossValue, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					    .addComponent(jLabel3, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					    .addComponent(packetLossEnabled, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(throttleValue, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					    .addComponent(jLabel4, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					    .addComponent(throttleEnabled, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(getCloseButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					    .addComponent(getOpenButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					    .addComponent(getConnectionLossEnabled(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
					.addContainerGap());
				thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(thisLayout.createParallelGroup()
					    .addComponent(getConnectionLossEnabled(), GroupLayout.Alignment.LEADING, 0, 126, Short.MAX_VALUE)
					    .addComponent(throttleEnabled, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
					    .addComponent(packetLossEnabled, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
					    .addComponent(latencyEnabled, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addGroup(thisLayout.createParallelGroup()
					    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
					        .addComponent(getCloseButton(), GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
					        .addGap(23)
					        .addComponent(getOpenButton(), GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
					        .addGap(23))
					    .addGroup(thisLayout.createSequentialGroup()
					        .addGroup(thisLayout.createParallelGroup()
					            .addComponent(throttleValue, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
					            .addComponent(packetLossValue, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
					            .addComponent(latencyValue, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
					        .addGap(7)
					        .addGroup(thisLayout.createParallelGroup()
					            .addComponent(jLabel4, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
					            .addComponent(jLabel3, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
					            .addComponent(jLabel2, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)))));
				thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getConnectionLossEnabled(), throttleEnabled});
				thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {jLabel2, jLabel3, jLabel4});
			Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JButton getCloseButton() {
		if(closeButton == null) {
			closeButton = new JButton();
			closeButton.setName("closeButton");
			closeButton.setAction(getAppActionMap().get("closeConnectionAction"));
			closeButton.setOpaque(false);
		}
		return closeButton;
	}
	
	public JButton getOpenButton() {
		if(openButton == null) {
			openButton = new JButton();
			openButton.setName("openButton");
			openButton.setAction(getAppActionMap().get("openConnectionAction"));
			openButton.setOpaque(false);
		}
		return openButton;
	}

	public JCheckBox getLatencyEnabled() {
		return latencyEnabled;
	}

	public JTextField getThrottleValue() {
		return throttleValue;
	}

	public JTextField getPacketLossValue() {
		return packetLossValue;
	}

	public JTextField getLatencyValue() {
		return latencyValue;
	}

	public JCheckBox getThrottleEnabled() {
		return throttleEnabled;
	}

	public JCheckBox getPacketLossEnabled() {
		return packetLossEnabled;
	}
	
	public JCheckBox getConnectionLossEnabled() {
		return connectionLossEnabled;
	}
	
    /**
    * Returns the action map used by this application.
     * Actions defined using the Action annotation
     * are returned by this method
     */
	private ApplicationActionMap getAppActionMap() {
		return Application.getInstance().getContext().getActionMap(this);
	}
	
	@Action
	public void closeConnectionAction() {
		presenter.networkOff();
	}
	
	@Action
	public void openConnectionAction() {
		presenter.networkOn();
	}

	public void setProxyPresenter(ProxyPresenter presenter) {
		this.presenter = presenter;
	}

}
