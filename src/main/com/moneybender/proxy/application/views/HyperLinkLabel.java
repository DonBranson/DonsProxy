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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JLabel;

class HyperLinkLabel extends JLabel {

	private URL url;

	public HyperLinkLabel(String label) {
		super(label);
		setForeground(Color.BLUE);
		addMouseListener(linker);
	}

	public HyperLinkLabel() {
		this("");
	}

	public HyperLinkLabel(String label, URL url) {
		this(label);
		this.url = url;
	}

	@Override
	public void setText(String text) {
		super.setText(text);
	}
	
	public void setURL(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException ignore) {
			this.url = null;
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Rectangle2D textBounds = getFontMetrics(getFont()).getStringBounds(getText(), g);
		int y = getHeight() / 2 + (int) (textBounds.getHeight() / 2);
		int w = (int) textBounds.getWidth();
		int x = getIcon() == null ? 0 : getIcon().getIconWidth() + getIconTextGap();

		g.setColor(getForeground());
		g.drawLine(0, y, x + w, y);
	}

	private static MouseListener linker = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent event) {
			HyperLinkLabel self = (HyperLinkLabel) event.getSource();
			if (self.url == null)
				return;
			
			try {
				Desktop.getDesktop().browse(self.url.toURI());
			} catch (Exception e) {
				System.err.println("Failed to launch system browser.");
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	};

}
