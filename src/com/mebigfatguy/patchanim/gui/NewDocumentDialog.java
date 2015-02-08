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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mebigfatguy.patchanim.main.PatchAnimBundle;

public class NewDocumentDialog extends JDialog {
	
	private static final long serialVersionUID = 6141957372893989399L;
	
	private JComboBox orderCombo;
	private JCheckBox useAlpha;
	private JButton ok;
	private JButton cancel;
	private boolean isOk = false;
	
	public NewDocumentDialog() {
		initComponents();
		initListeners();
	}
	
	private void initComponents() {
		ResourceBundle rb = PatchAnimBundle.getBundle();
		Container cp = getContentPane();
		setTitle(rb.getString(PatchAnimBundle.NEWDOCUMENT));
		cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
		JLabel orderLabel = new JLabel(rb.getString(PatchAnimBundle.SETORDER));
		orderCombo = new JComboBox(new Object[] { Integer.valueOf(2),
												  Integer.valueOf(3),
										          Integer.valueOf(4),
										          Integer.valueOf(5),
										          Integer.valueOf(6),
										          Integer.valueOf(7),
										          Integer.valueOf(8),
										          Integer.valueOf(9) });
		orderLabel.setLabelFor(orderCombo);
		orderCombo.setSelectedItem(Integer.valueOf(4));
		
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout(8, 8));
		p.add(orderLabel, BorderLayout.WEST);
		p.add(orderCombo, BorderLayout.CENTER);
		p.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		cp.add(p);
		
		useAlpha = new JCheckBox(rb.getString(PatchAnimBundle.USEALPHA));
		p = new JPanel();
		p.setLayout(new BorderLayout(8, 8));
		p.add(useAlpha, BorderLayout.CENTER);
		p.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		cp.add(p);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createHorizontalGlue());
		ok = new JButton(rb.getString(PatchAnimBundle.OK));
		p.add(ok);
		p.add(Box.createHorizontalStrut(10));
		cancel = new JButton(rb.getString(PatchAnimBundle.CANCEL));
		p.add(cancel);
		p.add(Box.createHorizontalGlue());
		p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		cp.add(p);
		
		Utils.sizeUniformly(Utils.Sizing.Width, orderCombo, useAlpha);
		Utils.sizeUniformly(Utils.Sizing.Width, ok, cancel);
		pack();
	}
	
	private void initListeners() {
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				isOk = true;
				dispose();
			}
		});
		
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				isOk = false;
				dispose();
			}
		});
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				isOk = false;
				dispose();
			}
		});
	}
	
	public boolean isOk() {
		return isOk;
	}
	
	public int getOrder() {
		if (isOk)
			return ((Integer)orderCombo.getSelectedItem()).intValue();
		return 4;
	}
	
	public boolean useAlpha() {
		if (isOk)
			return useAlpha.isSelected();
		return false;
	}
}
