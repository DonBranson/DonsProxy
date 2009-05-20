/*
 * Created on Apr 24, 2008
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

import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;

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
public abstract class AbstractSubscriberView extends JPanel implements ISubscriberView {

	private JPanel topPanel;
	private JPanel bottomPanel;
	private JPanel messagePanel;
	private JScrollPane jScrollPane1;
	private JButton clearButton;
	private JButton stopButton;
	private JButton startButton;
	private JTextArea console;
	private JLabel statusMessageLabel;

	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new PrintSubscriberView());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public AbstractSubscriberView() {
		super();
		initGUI();
	}

	protected void initGUI() {
		try {
			GroupLayout thisLayout = new GroupLayout((JComponent)this);
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(773, 300));
			this.setOpaque(false);
			{
				topPanel = new JPanel();
				GroupLayout topPanelLayout = new GroupLayout((JComponent)topPanel);
				topPanel.setLayout(topPanelLayout);
				topPanel.setOpaque(false);
				{
					startButton = new JButton();
					startButton.setName("startButton");
					startButton.setOpaque(false);
					startButton.setAction(getAppActionMap().get("startAction"));
				}
				{
					stopButton = new JButton();
					stopButton.setName("stopButton");
					stopButton.setOpaque(false);
					stopButton.setAction(getAppActionMap().get("stopAction"));
				}
				{
					clearButton = new JButton();
					clearButton.setName("clearButton");
					clearButton.setOpaque(false);
					clearButton.setAction(getAppActionMap().get("clearAction"));
				}
				topPanelLayout.setHorizontalGroup(topPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(startButton, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
					.addGap(22)
					.addComponent(stopButton, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
					.addGap(22)
					.addComponent(clearButton, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(499, 499));
				topPanelLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {startButton, stopButton, clearButton});
				topPanelLayout.setVerticalGroup(topPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(topPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(startButton, GroupLayout.Alignment.LEADING, 0, 27, Short.MAX_VALUE)
					    .addComponent(stopButton, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					    .addComponent(clearButton, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap());
				topPanelLayout.linkSize(SwingConstants.VERTICAL, new Component[] {startButton, stopButton, clearButton});
			}
			{
				bottomPanel = new JPanel();
				GroupLayout bottomPanelLayout = new GroupLayout((JComponent)bottomPanel);
				bottomPanel.setLayout(bottomPanelLayout);
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
					bottomPanelLayout.setHorizontalGroup(bottomPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(jScrollPane1, 0, 749, Short.MAX_VALUE)
					.addContainerGap());
					bottomPanelLayout.setVerticalGroup(bottomPanelLayout.createSequentialGroup()
					.addComponent(jScrollPane1, 0, 219, Short.MAX_VALUE));
			}
			{
				messagePanel = new JPanel();
				GroupLayout messagePanelLayout = new GroupLayout((JComponent)messagePanel);
				messagePanel.setLayout(messagePanelLayout);
				messagePanel.setOpaque(false);
				{
					statusMessageLabel = new JLabel();
					statusMessageLabel.setLayout(null);
					statusMessageLabel.setName("statusMessageLabel");
				}
					messagePanelLayout.setVerticalGroup(messagePanelLayout.createSequentialGroup()
					.addComponent(statusMessageLabel, 0, 31, Short.MAX_VALUE));
					messagePanelLayout.setHorizontalGroup(messagePanelLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(statusMessageLabel, 0, 896, Short.MAX_VALUE)
					.addContainerGap());
			}
			thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
				.addComponent(topPanel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
				.addComponent(bottomPanel, 0, 219, Short.MAX_VALUE)
				.addComponent(messagePanel, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE));
			thisLayout.setHorizontalGroup(thisLayout.createParallelGroup()
				.addComponent(topPanel, GroupLayout.Alignment.LEADING, 0, 773, Short.MAX_VALUE)
				.addComponent(bottomPanel, GroupLayout.Alignment.LEADING, 0, 773, Short.MAX_VALUE)
				.addComponent(messagePanel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 773, GroupLayout.PREFERRED_SIZE));
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
	
	public AbstractSubscriberView(LayoutManager layout) {
		super(layout);
	}

	public AbstractSubscriberView(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public AbstractSubscriberView(LayoutManager layout,
			boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public abstract void startAction();
	public abstract void stopAction();
	public abstract void clearAction();

	public void writeCharacterToLog(int b) {
		console.append(String.valueOf((char)b));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JScrollBar scrollBar = jScrollPane1.getVerticalScrollBar();
				scrollBar.setValue(scrollBar.getMaximum());
			}
		});
	}

	public void setStatusText(String message) {
		statusMessageLabel.setText(message);
	}

	public void setStartButtonEnabled(boolean enabled) {
		startButton.setEnabled(enabled);
	}

	public void setStopButtonEnabled(boolean enabled) {
		stopButton.setEnabled(enabled);
	}
	
	public void clearLog() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				console.setText("");
			}
		});
	}
	
}