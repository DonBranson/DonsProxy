/*
 * Created on May 4, 2008
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;

import com.moneybender.proxy.application.presenters.ApplicationPresenter;

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
public class ConfigurationChooserView extends JDialog implements IConfigurationChooser {

	private JPanel mainPanel;
	private JButton cancelButton;
	private JButton deleteButton;
	private JButton openButton;
	private JList configurationList;
	private final ApplicationPresenter presenter;

	public ConfigurationChooserView(ApplicationPresenter presenter) {
		super((Frame) null);
		this.presenter = presenter;
		initGUI();
		initDoubleClickHandler();
		initEnterHandler();
	}

	private void initEnterHandler() {
		configurationList.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 0x0A) {
					openAction();
				}
			}
		});
	}

	private void initDoubleClickHandler() {
		configurationList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					openAction();
				}
			}
		});
	}

	private void initGUI() {
		try {
			{
				mainPanel = new JPanel();
				GroupLayout mainPanelLayout = new GroupLayout((JComponent) mainPanel);
				mainPanel.setLayout(mainPanelLayout);
				getContentPane().add(mainPanel, BorderLayout.CENTER);
				mainPanel.setPreferredSize(new java.awt.Dimension(466, 266));
				mainPanel.setOpaque(false);
				{
					DefaultListModel listModel = new DefaultListModel();
					configurationList = new JList(listModel);
					configurationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					configurationList.setName("configurationList");
					configurationList.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				}
				{
					deleteButton = new JButton();
					deleteButton.setName("deleteButton");
					deleteButton.setAction(getAppActionMap().get("deleteAction"));
					deleteButton.setOpaque(false);
				}
				{
					openButton = new JButton();
					openButton.setName("openButton");
					openButton.setAction(getAppActionMap().get("openAction"));
					openButton.setOpaque(false);
				}
				{
					cancelButton = new JButton();
					cancelButton.setName("cancelButton");
					cancelButton.setAction(getAppActionMap().get("cancelAction"));
					cancelButton.setOpaque(false);
				}
				mainPanelLayout.setHorizontalGroup(mainPanelLayout.createSequentialGroup().addContainerGap()
					.addComponent(configurationList, 0, 345, Short.MAX_VALUE).addPreferredGap(
						LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
						mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(openButton,
							GroupLayout.Alignment.LEADING, 0, 71, Short.MAX_VALUE).addComponent(cancelButton,
							GroupLayout.Alignment.LEADING, 0, 71, Short.MAX_VALUE).addComponent(deleteButton,
							GroupLayout.Alignment.LEADING, 0, 71, Short.MAX_VALUE)).addContainerGap());
				mainPanelLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] { cancelButton, deleteButton,
					openButton });
				mainPanelLayout.setVerticalGroup(mainPanelLayout.createSequentialGroup().addContainerGap().addGroup(
					mainPanelLayout.createParallelGroup().addGroup(
						GroupLayout.Alignment.LEADING,
						mainPanelLayout.createSequentialGroup().addComponent(openButton, GroupLayout.PREFERRED_SIZE,
							GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(
							LayoutStyle.ComponentPlacement.UNRELATED).addComponent(deleteButton,
							GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(cancelButton,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(165)).addComponent(configurationList, GroupLayout.Alignment.LEADING, 0, 250,
						Short.MAX_VALUE)).addContainerGap());
			}
			this.setSize(459, 308);
			Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(getContentPane());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getChosenConfigurationName() {
		return (String) configurationList.getSelectedValue();
	}

	public void setList(String[] configurationNames) {
		configurationList.setListData(configurationNames);
	}

	/**
	 * Returns the action map used by this application. Actions defined using
	 * the Action annotation are returned by this method
	 */
	private ApplicationActionMap getAppActionMap() {
		return Application.getInstance().getContext().getActionMap(this);
	}

	@Action
	public void cancelAction() {
		presenter.cancel(this);
	}

	@Action
	public void openAction() {
		presenter.loadSelectedConfiguration(this);
	}

	@Action
	public void deleteAction() {
		presenter.deleteSelectedConfiguration(this);
	}

}
