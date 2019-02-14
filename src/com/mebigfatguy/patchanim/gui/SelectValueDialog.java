/*
 * patchanim - A bezier surface patch color blend animation builder
 * Copyright (C) 2008-2019 Dave Brosius
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
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.mebigfatguy.patchanim.main.PatchAnimBundle;

public class SelectValueDialog extends JDialog {
    
    private static final long serialVersionUID = -1189257623926359615L;
    private JTextField colorField;
    private JButton cancelButton;
    private JButton okButton;
    private boolean ok;

    public SelectValueDialog(Component parentComponent) {
        initComponents();
        initListeners();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        pack();
        setLocationRelativeTo(parentComponent);
    }
    
    public Double getValue() {
        ok = false;
        setVisible(true);
        
        if (!ok)
            return null;
        
        return Double.valueOf(colorField.getText());
    }
    
    private void initComponents() {
        final ResourceBundle rb = PatchAnimBundle.getBundle();
        setTitle(rb.getString(PatchAnimBundle.SELECTVALUE));
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout(4, 4));
        
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.setLayout(new BorderLayout(10, 10));
        p.add(new JLabel(rb.getString(PatchAnimBundle.VALUE)), BorderLayout.WEST);
        
        colorField = new JTextField(20);
        colorField.setDocument(new DoubleDocument());
        colorField.setText("128");
        p.add(colorField, BorderLayout.CENTER);
        
        cp.add(p, BorderLayout.CENTER);
        
        p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        
        p.add(Box.createHorizontalGlue());
        cancelButton = new JButton(rb.getString(PatchAnimBundle.CANCEL));
        p.add(cancelButton);
        p.add(Box.createHorizontalStrut(10));
        okButton = new JButton(rb.getString(PatchAnimBundle.OK));
        p.add(okButton);
        p.add(Box.createHorizontalStrut(10));
        
        cp.add(p, BorderLayout.SOUTH);
    }
    
    private void initListeners() {
        getRootPane().setDefaultButton(okButton);
        
        okButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent ae) {
                dispose();
                ok = true;
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent ae) {
                dispose();
                ok = false;
            }
        });
        
        colorField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                modified();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                modified();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                modified();
            }

            private void modified() {
                okButton.setEnabled(colorField.getText().length() > 0);
            }
        });
    }
}
