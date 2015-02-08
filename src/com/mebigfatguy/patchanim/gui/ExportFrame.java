/*
 * patchanim - A bezier surface patch color blend animation builder
 * Copyright (C) 2008-2015 Dave Brosius
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.mebigfatguy.patchanim.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.mebigfatguy.patchanim.gui.events.ExportEvent;
import com.mebigfatguy.patchanim.gui.events.ExportListener;
import com.mebigfatguy.patchanim.main.PatchAnimBundle;

/**
 * a progress dialog for exporting images
 */
public class ExportFrame extends JDialog implements ExportListener {
	private static final long serialVersionUID = 3111097499092146056L;
	private JProgressBar bar;
	
	/**
	 * constructs the export progress dialog
	 */
	public ExportFrame() {
		initComponents();
	}

	/**
	 * initialize the gui components
	 */
	private void initComponents() {
		ResourceBundle rb = PatchAnimBundle.getBundle();
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout(4, 4));
		bar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
		bar.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
		cp.add(bar, BorderLayout.CENTER);
		pack();
		setTitle(rb.getString(PatchAnimBundle.EXPORTINGFILE));
	}

	/**
	 * implements the ExportListener to update the progress bar
	 * 
	 * @param ee the export event describing what file number is being exported
	 */
	@Override
	public void imageExported(final ExportEvent ee) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				bar.setMaximum(ee.getTotalImages());
				bar.setValue(ee.getCurrentImage());
			}
		});
	}

}
