/*
 * Created on April 12, 2008
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
public class ProxyView extends JPanel implements IProxyView, ILogger {

	private JTextArea console;
	private JScrollPane jScrollPane1;
	private JButton clearButton;
	private DecoratorInputPanel decoratorInputPanel;
	private JLabel statusMessageLabel;
	private JPanel messagePanel;
	private JPanel bottomPanel;
	private ProxyInputPanel inputPanel;
	private JButton stopButton;
	private JButton startButton;
	private JPanel topPanel;
	private ProxyPresenter presenter;

	public ProxyView() {
		super();
		initGUI();
		hookIntoLogging();
		presenter = new ProxyPresenter(this);
		decoratorInputPanel.setProxyPresenter(presenter);
	}
	
	private void initGUI() {
		try {
			this.setPreferredSize(new java.awt.Dimension(900, 400));
			this.setSize(900, 400);
			GroupLayout thisLayout = new GroupLayout((JComponent)this);
			this.setLayout(thisLayout);
			this.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
			this.setOpaque(false);
			{
				topPanel = new JPanel();
				GroupLayout jPanel1Layout = new GroupLayout((JComponent)topPanel);
				topPanel.setLayout(jPanel1Layout);
				topPanel.setOpaque(false);
				{
					startButton = new JButton();
					startButton.setName("startButton");
					startButton.setAction(getAppActionMap().get("startAction"));
					startButton.setAction(getAppActionMap().get("startAction"));
					startButton.setOpaque(false);
				}
				{
					inputPanel = new ProxyInputPanel();
				}
				{
					decoratorInputPanel = new DecoratorInputPanel();
				}
				{
					clearButton = new JButton();
					clearButton.setName("clearButton");
					clearButton.setAction(getAppActionMap().get("clearAction"));
					clearButton.setOpaque(false);
				}
				{
					stopButton = new JButton();
					stopButton.setName("stopButton");
					stopButton.setAction(getAppActionMap().get("stopAction"));
					stopButton.setOpaque(false);
				}
				jPanel1Layout.setHorizontalGroup(jPanel1Layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(jPanel1Layout.createParallelGroup()
					    .addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
					        .addComponent(startButton, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
					        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					        .addComponent(stopButton, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
					        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					        .addComponent(clearButton, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
					        .addGap(264))
					    .addComponent(inputPanel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 489, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addComponent(decoratorInputPanel, GroupLayout.PREFERRED_SIZE, 313, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(70, 70));
				jPanel1Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {clearButton, stopButton, startButton});
				jPanel1Layout.setVerticalGroup(jPanel1Layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(jPanel1Layout.createParallelGroup()
					    .addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
					        .addComponent(inputPanel, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
					        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					            .addComponent(startButton, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
					            .addComponent(stopButton, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
					            .addComponent(clearButton, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
					        .addGap(20))
					    .addComponent(decoratorInputPanel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE))
					.addGap(6));
				jPanel1Layout.linkSize(SwingConstants.VERTICAL, new Component[] {clearButton, stopButton, startButton});
			}
			{
				bottomPanel = new JPanel();
				GroupLayout jPanel1Layout1 = new GroupLayout((JComponent)bottomPanel);
				bottomPanel.setLayout(jPanel1Layout1);
				bottomPanel.setOpaque(false);
				{
					jScrollPane1 = new JScrollPane();
					jScrollPane1.setOpaque(false);
					{
						console = new JTextArea();
						jScrollPane1.setViewportView(console);
						console.setEditable(false);
					}
				}
				jPanel1Layout1.setHorizontalGroup(jPanel1Layout1.createSequentialGroup()
					.addContainerGap()
					.addComponent(jScrollPane1, 0, 722, Short.MAX_VALUE)
					.addContainerGap());
				jPanel1Layout1.setVerticalGroup(jPanel1Layout1.createSequentialGroup()
					.addComponent(jScrollPane1, 0, 254, Short.MAX_VALUE));
			}
			{
				messagePanel = new JPanel();
				GroupLayout messagePanelLayout = new GroupLayout((JComponent)messagePanel);
				messagePanel.setLayout(messagePanelLayout);
				messagePanel.setOpaque(false);
				{
					statusMessageLabel = new JLabel();
					GroupLayout statusMessageLabelLayout = new GroupLayout((JComponent)statusMessageLabel);
					statusMessageLabel.setLayout(null);
					statusMessageLabel.setName("statusMessageLabel");
					statusMessageLabelLayout.setVerticalGroup(statusMessageLabelLayout.createParallelGroup());
					statusMessageLabelLayout.setHorizontalGroup(statusMessageLabelLayout.createParallelGroup());
				}
				messagePanelLayout.setVerticalGroup(messagePanelLayout.createSequentialGroup()
					.addComponent(statusMessageLabel, 0, 31, Short.MAX_VALUE));
				messagePanelLayout.setHorizontalGroup(messagePanelLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(statusMessageLabel, 0, 896, Short.MAX_VALUE)
					.addContainerGap());
			}
					thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
				.addComponent(topPanel, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)
				.addComponent(bottomPanel, 0, 212, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(messagePanel, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE));
			thisLayout.setHorizontalGroup(thisLayout.createParallelGroup()
						.addComponent(topPanel, GroupLayout.Alignment.LEADING, 0, 896, Short.MAX_VALUE)
						.addComponent(bottomPanel, GroupLayout.Alignment.LEADING, 0, 896, Short.MAX_VALUE)
						.addComponent(messagePanel, GroupLayout.Alignment.LEADING, 0, 896, Short.MAX_VALUE));
				Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public void startAction() {
		presenter.startProxy();
	}
	
	@Action
	public void stopAction() {
		presenter.stop();
	}

	@Action
	public void clearAction() {
		presenter.clear();
	}

	public ProxyInputPanel getInputPanel() {
		return inputPanel;
	}

	public void setStatusText(String message) {
		statusMessageLabel.setText(message);
		statusMessageLabel.repaint();
	}
	
	public void logMessage(String message) {
		console.append(message + "\n");
		JScrollBar scrollBar = jScrollPane1.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
	}

	public JTextArea getConsole() {
		return console;
	}

	public void setStartButtonEnabled(boolean enabled) {
		startButton.setEnabled(enabled);
	}

	public void setStopButtonEnabled(boolean enabled) {
		stopButton.setEnabled(enabled);
	}

	public int getLatencyValue() {
		try {
			return Integer.parseInt(decoratorInputPanel.getLatencyValue().getText().trim());
		} catch (NumberFormatException e) {
			logMessage("Error parsing user-supplied value for latency:" + e.getMessage() + ", using default");
			return 250;
		}
	}

	public int getPacketLossValue() {
		try {
			return Integer.parseInt(decoratorInputPanel.getPacketLossValue().getText().trim());
		} catch (NumberFormatException e) {
			logMessage("Error parsing user-supplied value for packet loss rate:" + e.getMessage() + ", using default");
			return 100;
		}
	}

	public int getThrottleValue() {
		try {
			return Integer.parseInt(decoratorInputPanel.getThrottleValue().getText().trim());
		} catch (NumberFormatException e) {
			logMessage("Error parsing user-supplied value for bandwidth:" + e.getMessage() + ", using default");
			return 20;
		}
	}

	public boolean isConnectionLossEnabled() {
		return decoratorInputPanel.getConnectionLossEnabled().isSelected();
	}

	public boolean isLatencyEnabled() {
		return decoratorInputPanel.getLatencyEnabled().isSelected();
	}

	public boolean isPacketLossEnabled() {
		return decoratorInputPanel.getPacketLossEnabled().isSelected();
	}

	public boolean isThrottleEnabled() {
		return decoratorInputPanel.getThrottleEnabled().isSelected();
	}

	public int getListenPort() {
		return inputPanel.getListenPort();
	}

	public String getTargetHost() {
		return inputPanel.getTargetHost();
	}

	public int getTargetPort() {
		return inputPanel.getTargetPort();
	}

	private void hookIntoLogging() {
		BasicConfigurator.resetConfiguration();
		SwingAppender appender = new SwingAppender(this);
		appender.setThreshold(Level.INFO);
		Logger.getRootLogger().addAppender(appender);
	}

	public void setConnectionLossInputEnabled(boolean enabled) {
		decoratorInputPanel.getConnectionLossEnabled().setEnabled(enabled);
	}

	public void setConnectionLossCloseEnabled(boolean enabled) {
		decoratorInputPanel.getCloseButton().setEnabled(enabled);
	}

	public void setConnectionLossOpenEnabled(boolean enabled) {
		decoratorInputPanel.getOpenButton().setEnabled(enabled);
	}

	public void setLatencyInputEnabled(boolean enabled) {
		decoratorInputPanel.getLatencyEnabled().setEnabled(enabled);
		decoratorInputPanel.getLatencyValue().setEnabled(enabled);
	}

	public void setPacketLossInputEnabled(boolean enabled) {
		decoratorInputPanel.getPacketLossEnabled().setEnabled(enabled);
		decoratorInputPanel.getPacketLossValue().setEnabled(enabled);
	}

	public void setThrottleInputEnabled(boolean enabled) {
		decoratorInputPanel.getThrottleEnabled().setEnabled(enabled);
		decoratorInputPanel.getThrottleValue().setEnabled(enabled);
	}

	public void setListenPortEnabled(boolean enabled) {
		inputPanel.getListenPortText().setEnabled(enabled);
	}

	public void setTargetHostEnabled(boolean enabled) {
		inputPanel.getTargetHostText().setEnabled(enabled);
	}

	public void setTargetPortEnabled(boolean enabled) {
		inputPanel.getTargetPortText().setEnabled(enabled);
	}

	public void setListenPort(int listenPort) {
		inputPanel.getListenPortText().setText(Integer.toString(listenPort));
	}

	public void setTargetHost(String targetHost) {
		inputPanel.getTargetHostText().setText(targetHost);
	}

	public void setTargetPort(int targetPort) {
		inputPanel.getTargetPortText().setText(Integer.toString(targetPort));
	}

	public void setConnectionLossEnabled(boolean enabled) {
		decoratorInputPanel.getConnectionLossEnabled().setSelected(enabled);
	}

	public void setLatencyEnabled(boolean enabled) {
		decoratorInputPanel.getLatencyEnabled().setSelected(enabled);
	}

	public void setPacketLossEnabled(boolean enabled) {
		decoratorInputPanel.getPacketLossEnabled().setSelected(enabled);
	}

	public void setThrottleEnabled(boolean enabled) {
		decoratorInputPanel.getThrottleEnabled().setSelected(enabled);
	}

	public void setLatencyValue(int value) {
		decoratorInputPanel.getLatencyValue().setText(Integer.toString(value));
	}
	
	public void setPacketLossValue(int value) {
		decoratorInputPanel.getPacketLossValue().setText(Integer.toString(value));
	}
	
	public void setThrottleValue(int value) {
		decoratorInputPanel.getThrottleValue().setText(Integer.toString(value));
	}

	public ProxyPresenter getPresenter() {
		return presenter;
	}

	public void clearConsole() {
		console.setText("");
	}
	
}
