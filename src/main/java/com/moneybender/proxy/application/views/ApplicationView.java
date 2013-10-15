/*
 * Created on Apr 27, 2008
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
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;

import com.moneybender.proxy.application.presenters.ApplicationPresenter;
import com.moneybender.proxy.application.presenters.ProxyPresenter;

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
public class ApplicationView extends JFrame implements IApplicationView {

	private JMenuBar jMenuBar1;
	private JMenuItem aboutItem;
	private JMenu helpMenu;
	private JMenuItem saveConfiguration;
	private JSeparator jSeparator1;
	private JMenuItem loadConfig;
	private JMenuItem fileExit;
	private AbstractSubscriberView printSubscriberView;
	private HttpHeadSubscriberView httpHeadSubscriberView;
	private LdapHeadSubscriberView ldapHeadSubscriberView;
	private HttpXmlSubscriberView httpXmlSubscriberPanel;
	private JPanel httpHeadPanel;
	private JPanel ldapHeadPanel;
	private JPanel httpXmlPanel;
	private JPanel printPanel;
	private ProxyView proxyView;
	private JTabbedPane tabbedPanel;
	private AbstractAction exitAction;
	private JMenu jMenu1;
	private ApplicationPresenter presenter;

	public ApplicationView() {
		super();
		initGUI();
		presenter = new ApplicationPresenter(this);
		presenter.ready();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			GroupLayout thisLayout = new GroupLayout(
					(JComponent) getContentPane());
			getContentPane().setLayout(thisLayout);
			thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
					.addComponent(getTabbedPanel(), 0, 911, Short.MAX_VALUE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0,
							GroupLayout.PREFERRED_SIZE));
			thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
					.addComponent(getTabbedPanel(), 0, 573, Short.MAX_VALUE));
			this.setTitle("DonsProxy");
			this.setPreferredSize(new java.awt.Dimension(911, 608));
			{
				jMenuBar1 = new JMenuBar();
				setJMenuBar(jMenuBar1);
				;
				jMenuBar1.setName("jMenuBar1");
				{
					jMenu1 = new JMenu();
					jMenuBar1.add(jMenu1);
					jMenuBar1.add(getHelpMenu());
					jMenu1.setText("File");
					jMenu1.setOpaque(false);
					{
						fileExit = new JMenuItem();
						jMenu1.add(getLoadConfig());
						jMenu1.add(getSaveConfiguration());
						jMenu1.add(getJSeparator1());
						jMenu1.add(fileExit);
						fileExit.setText("Exit");
						fileExit.setAction(getExitAction());
						fileExit.setOpaque(false);
					}
				}
			}
			pack();
			this.setSize(911, 608);
			
			this.addWindowListener(new MainFrameListener());
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

			Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(getContentPane());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AbstractAction getExitAction() {
		if (exitAction == null) {
			exitAction = new AbstractAction("Exit", null) {
				public void actionPerformed(ActionEvent evt) {
					presenter.shutdown();
				}
			};
		}
		return exitAction;
	}

	private JTabbedPane getTabbedPanel() {
		if (tabbedPanel == null) {
			tabbedPanel = new JTabbedPane();
			tabbedPanel.setBackground(new java.awt.Color(237, 233, 227));
			tabbedPanel.setOpaque(true);
			tabbedPanel.addTab("Proxy", null, getProxyPanel(), null);
			tabbedPanel.addTab("Print", null, getPrintPanel(), null);
			tabbedPanel.addTab("HttpHead", null, getHttpHeadPanel(), null);
			tabbedPanel.addTab("LdapHead", null, getLdapHeadPanel(), null);
			tabbedPanel.addTab("HttpXml", null, getHttpXmlPanel(), null);
		}
		return tabbedPanel;
	}

	private ProxyView getProxyPanel() {
		if (proxyView == null) {
			proxyView = new ProxyView();
			proxyView.setOpaque(true);
			GroupLayout proxyPanelLayout = new GroupLayout(
					(JComponent) proxyView);
			proxyPanelLayout.setVerticalGroup(proxyPanelLayout
					.createSequentialGroup());
			proxyPanelLayout.setHorizontalGroup(proxyPanelLayout
					.createSequentialGroup());
		}
		return proxyView;
	}

	private JPanel getPrintPanel() {
		if (printPanel == null) {
			printPanel = new JPanel();
			GroupLayout decoratorPanelLayout = new GroupLayout(
					(JComponent) printPanel);
			printPanel.setLayout(decoratorPanelLayout);
			printPanel.setName("printPanel");
			decoratorPanelLayout
					.setHorizontalGroup(decoratorPanelLayout
							.createSequentialGroup().addComponent(
									getPrintSubscriberPanel(), 0, 898,
									Short.MAX_VALUE));
			decoratorPanelLayout
					.setVerticalGroup(decoratorPanelLayout
							.createSequentialGroup().addComponent(
									getPrintSubscriberPanel(), 0, 504,
									Short.MAX_VALUE));
		}
		return printPanel;
	}

	private JPanel getHttpHeadPanel() {
		if (httpHeadPanel == null) {
			httpHeadPanel = new JPanel();
			GroupLayout decoratorPanelLayout = new GroupLayout(
					(JComponent) httpHeadPanel);
			httpHeadPanel.setLayout(decoratorPanelLayout);
			httpHeadPanel.setName("httpHeadPanel");
			decoratorPanelLayout.setHorizontalGroup(decoratorPanelLayout
					.createSequentialGroup().addComponent(
							getHttpHeadSubscriberPanel(), 0, 898,
							Short.MAX_VALUE));
			decoratorPanelLayout.setVerticalGroup(decoratorPanelLayout
					.createSequentialGroup().addComponent(
							getHttpHeadSubscriberPanel(), 0, 504,
							Short.MAX_VALUE));
		}
		return httpHeadPanel;
	}

	private JPanel getLdapHeadPanel() {
		if (ldapHeadPanel == null) {
			ldapHeadPanel = new JPanel();
			GroupLayout decoratorPanelLayout = new GroupLayout(
					(JComponent) ldapHeadPanel);
			ldapHeadPanel.setLayout(decoratorPanelLayout);
			ldapHeadPanel.setName("ldapHeadPanel");
			decoratorPanelLayout.setHorizontalGroup(decoratorPanelLayout
					.createSequentialGroup().addComponent(
							getLdapHeadSubscriberPanel(), 0, 898,
							Short.MAX_VALUE));
			decoratorPanelLayout.setVerticalGroup(decoratorPanelLayout
					.createSequentialGroup().addComponent(
							getLdapHeadSubscriberPanel(), 0, 504,
							Short.MAX_VALUE));
		}
		return ldapHeadPanel;
	}

	private JPanel getHttpXmlPanel() {
		if (httpXmlPanel == null) {
			httpXmlPanel = new JPanel();
			GroupLayout decoratorPanelLayout = new GroupLayout(
					(JComponent) httpXmlPanel);
			httpXmlPanel.setLayout(decoratorPanelLayout);
			httpXmlPanel.setName("httpXmlPanel");
			decoratorPanelLayout.setHorizontalGroup(decoratorPanelLayout
					.createSequentialGroup().addComponent(
							getHttpXmlSubscriberPanel(), 0, 898,
							Short.MAX_VALUE));
			decoratorPanelLayout.setVerticalGroup(decoratorPanelLayout
					.createSequentialGroup().addComponent(
							getHttpXmlSubscriberPanel(), 0, 504,
							Short.MAX_VALUE));
		}
		return httpXmlPanel;
	}

	private AbstractSubscriberView getPrintSubscriberPanel() {
		if (printSubscriberView == null) {
			printSubscriberView = new PrintSubscriberView();
		}
		return printSubscriberView;
	}

	private AbstractSubscriberView getHttpHeadSubscriberPanel() {
		if (httpHeadSubscriberView == null) {
			httpHeadSubscriberView = new HttpHeadSubscriberView();
		}
		return httpHeadSubscriberView;
	}

	private AbstractSubscriberView getLdapHeadSubscriberPanel() {
		if (ldapHeadSubscriberView == null) {
			ldapHeadSubscriberView = new LdapHeadSubscriberView();
		}
		return ldapHeadSubscriberView;
	}

	private AbstractSubscriberView getHttpXmlSubscriberPanel() {
		if (httpXmlSubscriberPanel == null) {
			httpXmlSubscriberPanel = new HttpXmlSubscriberView();
		}
		return httpXmlSubscriberPanel;
	}

	@Override
	public void dispose() {
		printSubscriberView.stopAction();
		httpHeadSubscriberView.stopAction();
		ldapHeadSubscriberView.stopAction();
		proxyView.stopAction();
		super.dispose();
	}

	public Container getMainContainer() {
		return this;
	}
	
	private JMenuItem getLoadConfig() {
		if(loadConfig == null) {
			loadConfig = new JMenuItem();
			loadConfig.setName("loadConfig");
			loadConfig.setAction(getAppActionMap().get("loadAction"));
			loadConfig.setAccelerator(KeyStroke.getKeyStroke("alt pressed L"));
			loadConfig.setOpaque(false);
		}
		return loadConfig;
	}
	
    /**
    * Returns the action map used by this application.
     * Actions defined using the Action annotation
     * are returned by this method
     */
	private ApplicationActionMap getAppActionMap() {
		return Application.getInstance().getContext().getActionMap(this);
	}
    
	private JSeparator getJSeparator1() {
		if(jSeparator1 == null) {
			jSeparator1 = new JSeparator();
		}
		return jSeparator1;
	}
	
	private JMenuItem getSaveConfiguration() {
		if(saveConfiguration == null) {
			saveConfiguration = new JMenuItem();
			saveConfiguration.setName("saveConfiguration");
			saveConfiguration.setAction(getAppActionMap().get("saveAction"));
			saveConfiguration.setAccelerator(KeyStroke.getKeyStroke("alt pressed S"));
			saveConfiguration.setOpaque(false);
		}
		return saveConfiguration;
	}
	
	private class MainFrameListener extends WindowAdapter {
		
		@Override
		public void windowClosing(WindowEvent e) {
			presenter.shutdown();
		}
	}

	@Action
	public void loadAction() {
		makeConfigurationChooserView();
	}
	
	@Action
	public void saveAction() {
		makeConfigurationSaverView();
	}

	@Action
	public void aboutAction() {
		showAboutDialog();
	}

	private void showAboutDialog() {
		HelpAboutDialog dialog = new HelpAboutDialog(this);
		dialog.setTitle("About Don's Proxy");
		dialog.setLocation(getX() + 50, getY() + 50);
		dialog.setVisible(true);
	}

	public ProxyPresenter getProxyPresenter() {
		return proxyView.getPresenter();
	}

	public void makeConfigurationChooserView() {
		ConfigurationChooserView chooser = new ConfigurationChooserView(presenter);
		chooser.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		chooser.setTitle("Select a configuration");
		chooser.setLocation(getX() + 50, getY() + 50);
		chooser.setList(getConfigurationNames());
		chooser.setVisible(true);
		chooser.repaint();
	}

	public void makeConfigurationSaverView() {
		ConfigurationSaverView saver = new ConfigurationSaverView(presenter);
		saver.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		saver.setTitle("Save current configuration");
		saver.setLocation(getX() + 50, getY() + 50);
		saver.setList(getConfigurationNames());
		saver.setVisible(true);
		saver.repaint();
	}

	private String[] getConfigurationNames() {
		
		File directory = Application.getInstance().getContext().getLocalStorage().getDirectory();
		
		return directory.list(new FilenameFilter(){

			public boolean accept(File dir, String name) {
				File file = new File(dir + System.getProperty("file.separator") + name);
				if(file.isDirectory())
					return true;
				return false;
			}
			
		});
	}
	
	private JMenu getHelpMenu() {
		if(helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setName("helpMenu");
			helpMenu.add(getAboutItem());
			helpMenu.setText("Help");
			helpMenu.setOpaque(false);
		}
		return helpMenu;
	}
	
	private JMenuItem getAboutItem() {
		if(aboutItem == null) {
			aboutItem = new JMenuItem();
			aboutItem.setName("aboutItem");
			aboutItem.setText("About");
			aboutItem.setAction(getAppActionMap().get("aboutAction"));
			aboutItem.setOpaque(false);
		}
		return aboutItem;
	}
	
}
