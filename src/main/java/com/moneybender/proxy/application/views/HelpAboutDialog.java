/*
 * Created on May 20, 2008
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.border.BevelBorder;

import org.jdesktop.application.Application;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class HelpAboutDialog extends JDialog implements IDialog {
	private JLabel jLabel1;
	private HyperLinkLabel donsProxyLabel;
	private HyperLinkLabel appFrameworkLink;
	private JTextArea appFrameworkLabel;
	private JPanel jPanel6;
	private HyperLinkLabel donationLink;
	private JTextArea donationRequestText;
	private JPanel jPanel5;
	private JTextArea copyrightText;
	private JPanel jPanel4;
	private JPanel jPanel3;
	private JPanel jPanel2;
	private JPanel jPanel1;
	private HyperLinkLabel apacheLinkLabel;
	private JTextArea jTextArea1;
	private JTextArea jiglooText;
	private HyperLinkLabel jiglooLink;
	private HyperLinkLabel licenseLabel;

	//This code was edited or generated using CloudGarden's Jigloo\n* SWT/Swing GUI Builder, which is free for non-commercial\n* use. 

	public HelpAboutDialog(JFrame frame) {
		super(frame);
		initGUI();
	}

	private void initGUI() {
		try {
			GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
			getContentPane().setLayout(thisLayout);
			{
				jPanel1 = new JPanel();
				GroupLayout jPanel1Layout = new GroupLayout((JComponent)jPanel1);
				jPanel1.setLayout(jPanel1Layout);
				jPanel1.setName("jPanel1");
				jPanel1.setOpaque(false);
				{
					jPanel4 = new JPanel();
					GroupLayout jPanel4Layout = new GroupLayout((JComponent)jPanel4);
					jPanel4.setLayout(jPanel4Layout);
					jPanel4.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
					jPanel4.setOpaque(false);
					{
						jTextArea1 = new JTextArea();
						jTextArea1.setLineWrap(true);
						jTextArea1.setWrapStyleWord(true);
						jTextArea1.setName("jTextArea1");
						jTextArea1.setFocusTraversalKeysEnabled(false);
						jTextArea1.setEditable(false);
						jTextArea1.setOpaque(false);
					}
					{
						apacheLinkLabel = new HyperLinkLabel();
						apacheLinkLabel.setName("apacheLinkLabel");
						apacheLinkLabel.setURL("http://www.apache.org");
					}
					jPanel4Layout.setHorizontalGroup(jPanel4Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(jPanel4Layout.createParallelGroup()
						    .addComponent(jTextArea1, GroupLayout.Alignment.LEADING, 0, 424, Short.MAX_VALUE)
						    .addComponent(apacheLinkLabel, GroupLayout.Alignment.LEADING, 0, 424, Short.MAX_VALUE))
						.addContainerGap());
					jPanel4Layout.setVerticalGroup(jPanel4Layout.createSequentialGroup()
						.addComponent(jTextArea1, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(apacheLinkLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addContainerGap());
				}
				{
					jPanel2 = new JPanel();
					GroupLayout jPanel2Layout = new GroupLayout((JComponent)jPanel2);
					jPanel2.setLayout(jPanel2Layout);
					jPanel2.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
					jPanel2.setOpaque(false);
					{
						licenseLabel = new HyperLinkLabel();
						licenseLabel.setName("licenseLabel");
						licenseLabel.setURL("http://www.apache.org/licenses/LICENSE-2.0");
					}
					{
						copyrightText = new JTextArea();
						copyrightText.setName("copyrightText");
						copyrightText.setEditable(false);
						copyrightText.setOpaque(false);
					}
					{
						donsProxyLabel = new HyperLinkLabel();
						donsProxyLabel.setName("donsProxyLabel");
						donsProxyLabel.setURL("http://donsproxy.sourceforge.net");
					}
					{
						jLabel1 = new JLabel();
						jLabel1.setName("jLabel1");
					}
					jPanel2Layout.setHorizontalGroup(jPanel2Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(jPanel2Layout.createParallelGroup()
						    .addComponent(donsProxyLabel, GroupLayout.Alignment.LEADING, 0, 424, Short.MAX_VALUE)
						    .addComponent(jLabel1, GroupLayout.Alignment.LEADING, 0, 424, Short.MAX_VALUE)
						    .addComponent(licenseLabel, GroupLayout.Alignment.LEADING, 0, 424, Short.MAX_VALUE)
						    .addComponent(copyrightText, GroupLayout.Alignment.LEADING, 0, 424, Short.MAX_VALUE))
						.addContainerGap());
					jPanel2Layout.setVerticalGroup(jPanel2Layout.createSequentialGroup()
						.addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(copyrightText, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(donsProxyLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(licenseLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(18, 18));
				}
				{
					jPanel3 = new JPanel();
					GroupLayout jPanel3Layout = new GroupLayout((JComponent)jPanel3);
					jPanel3.setLayout(jPanel3Layout);
					jPanel3.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
					jPanel3.setOpaque(false);
					{
						jiglooText = new JTextArea();
						jiglooText.setName("jiglooText");
						jiglooText.setWrapStyleWord(true);
						jiglooText.setLineWrap(true);
						jiglooText.setFocusTraversalKeysEnabled(false);
						jiglooText.setEditable(false);
						jiglooText.setOpaque(false);
					}
					{
						jiglooLink = new HyperLinkLabel();
						jiglooLink.setName("jiglooLink");
						jiglooLink.setURL("http://www.cloudgarden.com/jigloo/index.html");
					}
					jPanel3Layout.setHorizontalGroup(jPanel3Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(jPanel3Layout.createParallelGroup()
						    .addComponent(jiglooText, GroupLayout.Alignment.LEADING, 0, 424, Short.MAX_VALUE)
						    .addComponent(jiglooLink, GroupLayout.Alignment.LEADING, 0, 424, Short.MAX_VALUE))
						.addContainerGap());
					jPanel3Layout.setVerticalGroup(jPanel3Layout.createSequentialGroup()
						.addComponent(jiglooText, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
						.addComponent(jiglooLink, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addContainerGap());
				}
				{
					jPanel5 = new JPanel();
					GroupLayout jPanel5Layout = new GroupLayout((JComponent)jPanel5);
					jPanel5.setLayout(jPanel5Layout);
					jPanel5.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
					jPanel5.setOpaque(false);
					{
						donationRequestText = new JTextArea();
						donationRequestText.setName("donationRequestText");
						donationRequestText.setEditable(false);
						donationRequestText.setOpaque(false);
					}
					{
						donationLink = new HyperLinkLabel();
						donationLink.setName("donationLink");
						donationLink.setURL("http://sourceforge.net/donate/index.php?group_id=211367");
					}
						jPanel5Layout.setHorizontalGroup(jPanel5Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(jPanel5Layout.createParallelGroup()
						    .addComponent(donationRequestText, GroupLayout.Alignment.LEADING, 0, 424, Short.MAX_VALUE)
						    .addComponent(donationLink, GroupLayout.Alignment.LEADING, 0, 424, Short.MAX_VALUE))
						.addContainerGap());
						jPanel5Layout.setVerticalGroup(jPanel5Layout.createSequentialGroup()
						.addComponent(donationRequestText, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(donationLink, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED));
				}
				{
					jPanel6 = new JPanel();
					GroupLayout jPanel6Layout = new GroupLayout((JComponent)jPanel6);
					jPanel6.setLayout(jPanel6Layout);
					jPanel6.setOpaque(false);
					jPanel6.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
					{
						appFrameworkLabel = new JTextArea();
						appFrameworkLabel.setName("appFrameworkLabel");
						appFrameworkLabel.setOpaque(false);
					}
					{
						appFrameworkLink = new HyperLinkLabel();
						appFrameworkLink.setName("appFrameworkLink");
						appFrameworkLink.setURL("https://appframework.dev.java.net");
					}
					jPanel6Layout.setHorizontalGroup(jPanel6Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(jPanel6Layout.createParallelGroup()
						    .addComponent(appFrameworkLabel, GroupLayout.Alignment.LEADING, 0, 505, Short.MAX_VALUE)
						    .addGroup(GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
						        .addComponent(appFrameworkLink, 0, 493, Short.MAX_VALUE)
						        .addGap(12))));
					jPanel6Layout.setVerticalGroup(jPanel6Layout.createSequentialGroup()
						.addComponent(appFrameworkLabel, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
						.addComponent(appFrameworkLink, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
						.addContainerGap());
				}
				jPanel1Layout.setHorizontalGroup(jPanel1Layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(jPanel1Layout.createParallelGroup()
					    .addComponent(jPanel4, GroupLayout.Alignment.LEADING, 0, 497, Short.MAX_VALUE)
					    .addComponent(jPanel2, GroupLayout.Alignment.LEADING, 0, 497, Short.MAX_VALUE)
					    .addComponent(jPanel3, GroupLayout.Alignment.LEADING, 0, 497, Short.MAX_VALUE)
					    .addComponent(jPanel5, GroupLayout.Alignment.LEADING, 0, 497, Short.MAX_VALUE)
					    .addComponent(jPanel6, GroupLayout.Alignment.LEADING, 0, 497, Short.MAX_VALUE))
					.addContainerGap());
				jPanel1Layout.setVerticalGroup(jPanel1Layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(jPanel5, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addComponent(jPanel6, 0, 51, Short.MAX_VALUE)
					.addContainerGap());
			}
			thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
				.addComponent(jPanel1, 0, 304, Short.MAX_VALUE));
			thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
				.addComponent(jPanel1, 0, 476, Short.MAX_VALUE));
			this.setSize(553, 385);
			Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(getContentPane());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}
}
