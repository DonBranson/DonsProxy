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

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import org.jdesktop.application.Application;

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
public class ProxyInputPanel extends javax.swing.JPanel {

	private JTextField listenPortText;
	private JLabel jLabel1;
	private JTextField targetHostText;
	private JLabel jLabel3;
	private JLabel jLabel2;
	private JTextField targetPortText;

	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new ProxyInputPanel());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public ProxyInputPanel() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			this.setPreferredSize(new java.awt.Dimension(489, 86));
			GroupLayout thisLayout = new GroupLayout((JComponent)this);
			this.setLayout(thisLayout);
			this.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
			this.setOpaque(false);
			{
				listenPortText = new JTextField();
				listenPortText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED)); 
				listenPortText.setName("listenPortText");
			}
			{
				jLabel1 = new JLabel();
				jLabel1.setName("jLabel1");
			}
			{
				targetPortText = new JTextField();
				targetPortText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				targetPortText.setName("targetPortText");
			}
			{
				targetHostText = new JTextField();
				targetHostText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				targetHostText.setName("targetHostText");
			}
			{
				jLabel3 = new JLabel();
				jLabel3.setName("jLabel3");
			}
			{
				jLabel2 = new JLabel();
				jLabel2.setName("jLabel2");
			}
			thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(thisLayout.createParallelGroup()
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				        .addComponent(targetHostText, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
				        .addComponent(listenPortText, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				    .addGroup(thisLayout.createSequentialGroup()
				        .addGap(10)
				        .addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				            .addComponent(jLabel2, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				            .addComponent(jLabel1, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 1, Short.MAX_VALUE)
				.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(targetPortText, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				    .addComponent(jLabel3, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addContainerGap(18, 18));
			thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
				.addComponent(listenPortText, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
				.addGap(18)
				.addGroup(thisLayout.createParallelGroup()
				    .addComponent(jLabel3, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
				    .addComponent(jLabel2, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE))
				.addGroup(thisLayout.createParallelGroup()
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addComponent(targetPortText, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
				        .addGap(169))
				    .addComponent(targetHostText, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE))
				.addGap(8));
			Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getListenPort() {
		return Integer.parseInt("0" + listenPortText.getText().trim());
	}

	public String getTargetHost() {
		return targetHostText.getText();
	}

	public int getTargetPort() {
		return Integer.parseInt("0" + targetPortText.getText().trim());
	}

	public JTextField getListenPortText() {
		return listenPortText;
	}

	public JTextField getTargetHostText() {
		return targetHostText;
	}

	public JTextField getTargetPortText() {
		return targetPortText;
	}

}
