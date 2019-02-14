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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mebigfatguy.patchanim.gui.events.DocumentChangedEvent;
import com.mebigfatguy.patchanim.gui.events.DocumentChangedListener;
import com.mebigfatguy.patchanim.main.PatchAnimBundle;
import com.mebigfatguy.patchanim.surface.CombinedPatch;

public class JPatchListPanel extends JPanel {
	private static final long serialVersionUID = -5365174588964238457L;
	private static final String UPBUTTON = "/com/mebigfatguy/patchanim/up.gif";
	private static final String DOWNBUTTON = "/com/mebigfatguy/patchanim/down.gif";
	
	private JList<CombinedPatch> patchList;
	private PatchListModel patchListModel;
	private JButton addButton;
	private JButton removeButton;
	private JButton upButton;
	private JButton downButton;
	
	public JPatchListPanel() {
		initComponents();
		initListeners();
	}
	
	private void initComponents() {
		ResourceBundle rb = PatchAnimBundle.getBundle();
		setLayout(new BorderLayout(4, 4));
		
		patchListModel = new PatchListModel();
		patchList = new JList<>(patchListModel);
		patchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
		patchList.setPreferredSize(new Dimension(200, 300));
		add(new JScrollPane(patchList), BorderLayout.CENTER);
		
		JPanel modCtrls = new JPanel();
		modCtrls.setLayout(new BoxLayout(modCtrls, BoxLayout.X_AXIS));
		modCtrls.add(Box.createHorizontalGlue());
		
		addButton = new JButton(rb.getString(PatchAnimBundle.ADD));
		removeButton = new JButton(rb.getString(PatchAnimBundle.REMOVE));
		removeButton.setEnabled(false);
		Utils.sizeUniformly(Utils.Sizing.Both, addButton, removeButton);
		modCtrls.add(addButton);
		modCtrls.add(Box.createHorizontalStrut(10));
		modCtrls.add(removeButton);
		modCtrls.add(Box.createHorizontalGlue());
		add(modCtrls, BorderLayout.SOUTH);
		
		JPanel moveCtrls = new JPanel();
		Class<?> panelClass = getClass();
		upButton = new JButton(new ImageIcon(panelClass.getResource(UPBUTTON)));
		downButton = new JButton(new ImageIcon(panelClass.getResource(DOWNBUTTON)));
		upButton.setEnabled(false);
		downButton.setEnabled(false);
		Utils.sizeUniformly(Utils.Sizing.Both, new JComponent[] {upButton, downButton});
		moveCtrls.setLayout(new BoxLayout(moveCtrls, BoxLayout.Y_AXIS));
		moveCtrls.add(Box.createVerticalGlue());
		moveCtrls.add(upButton);
		moveCtrls.add(Box.createVerticalStrut(10));
		moveCtrls.add(downButton);
		moveCtrls.add(Box.createVerticalGlue());
		add(moveCtrls, BorderLayout.EAST);
		
		setBorder(BorderFactory.createTitledBorder(rb.getString(PatchAnimBundle.PATCHES)));
	}
	
	private void initListeners() {
		final PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		mediator.addDocumentChangedListener(new DocumentChangedListener() {
			@Override
			public void documentChanged(final DocumentChangedEvent dce) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						patchListModel = new PatchListModel(dce.getDocument());
						patchList.setModel(patchListModel);
						patchList.setSelectedIndex(0);
						mediator.setNewActivePatch(patchListModel.getElementAt(0));
						enabledMovementCtrls();
						removeButton.setEnabled(patchListModel.getSize() > 1);
						
					}
				});	
			}
		});
		
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				int selIndex = patchList.getSelectedIndex();
				if (selIndex < 0)
					selIndex = patchListModel.getSize();
				else
					selIndex++;
				
				PatchPanelMediator ppMediator = PatchPanelMediator.getMediator();
				CombinedPatch newPatch = new CombinedPatch(ppMediator.getDocument().getOrder(), true);
				patchListModel.add(selIndex, newPatch);
				patchList.setSelectedIndex(selIndex);
				removeButton.setEnabled(patchListModel.getSize() > 1);
				enabledMovementCtrls();
			}
		});
		
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				int selIndex = patchList.getSelectedIndex();
				if (selIndex >= 0) {
					patchListModel.remove(selIndex);
					if (selIndex >= patchListModel.getSize()) {
						selIndex--;
					}
					patchList.setSelectedIndex(selIndex);
					PatchPanelMediator ppMediator = PatchPanelMediator.getMediator();
					ppMediator.setNewActivePatch(patchListModel.getElementAt(selIndex));
					removeButton.setEnabled(patchListModel.getSize() > 1);
					enabledMovementCtrls();
				}
			}
		});
		
		upButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				int selIndex = patchList.getSelectedIndex();
				if (selIndex > 0) {
					CombinedPatch patch = patchListModel.getElementAt(selIndex);
					patchListModel.remove(selIndex);
					selIndex--;
					patchListModel.add(selIndex, patch);
					patchList.setSelectedIndex(selIndex);
					enabledMovementCtrls();
				}
			}
		});
		
		downButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				int selIndex = patchList.getSelectedIndex();
				if ((selIndex >= 0) && (selIndex < (patchListModel.getSize() - 1))) {
					CombinedPatch patch = patchListModel.getElementAt(selIndex);
					patchListModel.remove(selIndex);
					selIndex++;
					patchListModel.add(selIndex, patch);
					patchList.setSelectedIndex(selIndex);
					enabledMovementCtrls();
				}
			}
		});


		patchList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int selIndex = patchList.getSelectedIndex();
					if (selIndex >= 0) {
						CombinedPatch newPatch = patchListModel.getElementAt(selIndex);
						PatchPanelMediator ppMediator = PatchPanelMediator.getMediator();
						ppMediator.setNewActivePatch(newPatch);
						enabledMovementCtrls();
					}
				}
			}
		});
		
		patchList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() == 2) {
					CombinedPatch patch = patchList.getSelectedValue();
					if (patch != null) {
						renamePatch(patch);
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent me) {
				if (me.isPopupTrigger()) {
					showPatchListMenu(me);
				}
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				if (me.isPopupTrigger()) {
					showPatchListMenu(me);
				}
			}
		});
	}
	
	private void renamePatch(CombinedPatch patch) {
		if (patch != null) {
			ResourceBundle rb = PatchAnimBundle.getBundle();
			String newName = JOptionPane.showInputDialog(JPatchListPanel.this, rb.getString(PatchAnimBundle.ENTERNEWPATCHNAME), patch.getName());
			if (newName != null) {
				patch.setName(newName);
				PatchPanelMediator mediator = PatchPanelMediator.getMediator();
				mediator.getDocument().setDirty(true);
				patchList.invalidate();
				patchList.revalidate();
				patchList.repaint();
			}
		}
	}
	
	private void showPatchListMenu(MouseEvent me) {
		ResourceBundle rb = PatchAnimBundle.getBundle();
		JPopupMenu patchMenu = new JPopupMenu();
		JMenuItem renameItem = new JMenuItem(rb.getString(PatchAnimBundle.RENAME));
		renameItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				CombinedPatch patch = patchList.getSelectedValue();
				if (patch != null) {
					renamePatch(patch);
				}
			}
		});
		patchMenu.add(renameItem);
		JMenuItem cloneItem = new JMenuItem(rb.getString(PatchAnimBundle.CLONE));
		cloneItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				int selIndex = patchList.getSelectedIndex();
				CombinedPatch patch = patchListModel.getElementAt(selIndex);
				CombinedPatch newPatch = patch.clone();
				patchListModel.add(selIndex + 1, newPatch);
				patchList.setSelectedIndex(selIndex + 1);
				removeButton.setEnabled(true);
			}
		});
		patchMenu.add(cloneItem);
		patchMenu.show(JPatchListPanel.this, me.getX(), me.getY());
	}
	
	private void enabledMovementCtrls() {
		int selIndex = patchList.getSelectedIndex();
		int lastIndex = patchListModel.getSize() - 1;
		
		if ((lastIndex <= 0) || (selIndex < 0)) {
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		} else {
			upButton.setEnabled(selIndex > 0);
			downButton.setEnabled(selIndex < lastIndex);
		}
	}
}
