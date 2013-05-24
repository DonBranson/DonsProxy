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
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;

import com.moneybender.proxy.application.presenters.ApplicationPresenter;

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
public class ConfigurationSaverView extends JDialog implements IConfigurationSaver {

	private JPanel mainPanel;
	private JButton cancelButton;
	private JButton newConfiguration;
	private JTextField newConfigurationName;
	private JButton saveButton;
	private JList configurationList;
	private final ApplicationPresenter presenter;

	public ConfigurationSaverView(ApplicationPresenter presenter) {
		super((Frame)null);
		this.presenter = presenter;
		initGUI();
		initDoubleClickHandler();
		initEntryHandler();
	}
	
	private void initEntryHandler() {

		newConfiguration.setEnabled(false);
		newConfigurationName.addKeyListener(makeNewConfigurationEnterKeyListener());
		newConfigurationName.getDocument().addDocumentListener(makeNewConfigurationDocumentListener());
		
		saveButton.setEnabled(false);
		configurationList.addListSelectionListener(makeSaveConfigurationSelectionListener());
	}

	private ListSelectionListener makeSaveConfigurationSelectionListener() {
		return new ListSelectionListener(){

			public void valueChanged(ListSelectionEvent e) {
				if(configurationList.getSelectedIndex() == -1){
					saveButton.setEnabled(false);
				} else {
					saveButton.setEnabled(true);
					newConfigurationName.setText("");
				}
			}
			
		};
	}

	private KeyAdapter makeNewConfigurationEnterKeyListener() {
		return new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if(e.getKeyCode() == 0x0A) {
					newAction();
				}
			}
			
		};
	}

	private DocumentListener makeNewConfigurationDocumentListener() {
		return new DocumentListener(){

			public void changedUpdate(DocumentEvent e) {
				setNewButtonState();
			}

			public void insertUpdate(DocumentEvent e) {
				setNewButtonState();
			}

			public void removeUpdate(DocumentEvent e) {
				setNewButtonState();
			}
			
			private void setNewButtonState() {
				if(newConfigurationName.getText().trim().length() > 0){
					newConfiguration.setEnabled(true);
					configurationList.clearSelection();
				} else {
					newConfiguration.setEnabled(false);
				}
			}

		};
	}

	private void initDoubleClickHandler() {
		configurationList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getClickCount() == 2) {
					saveAction();
				}
			}
		});
	}

	private void initGUI() {
		try {
			{
				mainPanel = new JPanel();
				GroupLayout mainPanelLayout = new GroupLayout((JComponent)mainPanel);
				mainPanel.setLayout(mainPanelLayout);
				getContentPane().add(mainPanel, BorderLayout.CENTER);
				mainPanel.setPreferredSize(new java.awt.Dimension(466, 266));
				mainPanel.setOpaque(false);
				{
					DefaultListModel listModel =  new DefaultListModel();
					configurationList = new JList(listModel);
					configurationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					configurationList.setName("configurationList");
					configurationList.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				}
				{
					newConfigurationName = new JTextField();
				}
				{
					newConfiguration = new JButton();
					newConfiguration.setName("newConfiguration");
					newConfiguration.setAction(getAppActionMap().get("newAction"));
					newConfiguration.setOpaque(false);
				}
				{
					saveButton = new JButton();
					saveButton.setName("saveButton");
					saveButton.setAction(getAppActionMap().get("saveAction"));
					saveButton.setOpaque(false);
				}
				{
					cancelButton = new JButton();
					cancelButton.setName("cancelButton");
					cancelButton.setAction(getAppActionMap().get("cancelAction"));
					cancelButton.setOpaque(false);
				}
				mainPanelLayout.setHorizontalGroup(mainPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(mainPanelLayout.createParallelGroup()
					    .addGroup(GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
					        .addGap(0, 0, GroupLayout.PREFERRED_SIZE)
					        .addComponent(configurationList, 0, 343, Short.MAX_VALUE))
					    .addComponent(newConfigurationName, GroupLayout.Alignment.LEADING, 0, 343, Short.MAX_VALUE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addGroup(mainPanelLayout.createParallelGroup()
					    .addComponent(cancelButton, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
					    .addComponent(saveButton, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
					    .addGroup(GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
					        .addComponent(newConfiguration, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
					        .addGap(9)))
					.addContainerGap());
				mainPanelLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {newConfiguration, saveButton, cancelButton});
				mainPanelLayout.setVerticalGroup(mainPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(newConfigurationName, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					    .addComponent(newConfiguration, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addGroup(mainPanelLayout.createParallelGroup()
					    .addGroup(GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
					        .addComponent(saveButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					        .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					        .addGap(166))
					    .addComponent(configurationList, GroupLayout.Alignment.LEADING, 0, 221, Short.MAX_VALUE))
					.addContainerGap());
				mainPanelLayout.linkSize(SwingConstants.VERTICAL, new Component[] {newConfiguration, saveButton, cancelButton});
			}
			this.setSize(459, 308);
			Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(getContentPane());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getChosenConfigurationName() {
		String selectedValue = (String) configurationList.getSelectedValue();
		if(selectedValue != null)
			selectedValue = selectedValue.trim();
		return selectedValue;
	}

	public void setList(String[] configurationNames) {
		configurationList.setListData(configurationNames);
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
	public void cancelAction() {
		presenter.cancel(this);
	}
	
	@Action
	public void saveAction() {
		presenter.saveSelectedConfiguration(this);
	}

	public String getTypedName() {
		return newConfigurationName.getText().trim();
	}
	
	@Action
	public void newAction() {
		presenter.saveNewConfiguration(this);
	}

}
