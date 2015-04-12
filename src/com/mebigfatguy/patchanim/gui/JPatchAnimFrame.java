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
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import com.mebigfatguy.patchanim.ExportType;
import com.mebigfatguy.patchanim.PatchAnimDocument;
import com.mebigfatguy.patchanim.gui.events.ActivePatchChangedEvent;
import com.mebigfatguy.patchanim.gui.events.ActivePatchChangedListener;
import com.mebigfatguy.patchanim.gui.events.SettingsChangedEvent;
import com.mebigfatguy.patchanim.gui.events.SettingsChangedListener;
import com.mebigfatguy.patchanim.io.PatchAnimIO;
import com.mebigfatguy.patchanim.io.PatchExporter;
import com.mebigfatguy.patchanim.main.PatchAnimBundle;

public class JPatchAnimFrame extends JFrame {
	
	private static final long serialVersionUID = -4610407923936772733L;
	private static final String ICON_URL = "/com/mebigfatguy/patchanim/gui/patchanimicon.jpg";
	
	private JMenuItem newItem;
	private JMenuItem openItem;
	private JMenuItem saveItem;
	private JMenuItem saveAsItem;
	private JMenu exportMenu;
	private JMenuItem exportJpgsItem;
	private JMenuItem exportPngsItem;
	private JMenuItem exportGifsItem;
	private JMenuItem exportAnimatedGifItem;
	private JMenuItem exportAnimatedPngItem;
	private JMenuItem exportAnimatedMngItem;
	private JMenuItem quitItem;
	private PatchAnimDocument document;
	private File documentLocation;
	
	public JPatchAnimFrame() {
		initComponents();
		initMenus();
		initListeners();
	}
	
	private void initComponents() {
		ResourceBundle rb = PatchAnimBundle.getBundle();
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout(4, 4));
		
		JPatchAnimPanel patchPanel = new JPatchAnimPanel();
		document = new PatchAnimDocument(4, false);
		documentLocation = null;
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		mediator.setDocument(document);
		
		cp.add(patchPanel, BorderLayout.CENTER);
		
		String title = MessageFormat.format(rb.getString(PatchAnimBundle.NAMEDTITLE), rb.getString(PatchAnimBundle.UNTITLED));
		setTitle(title);
		
		setIconImage(new ImageIcon(JPatchAnimFrame.class.getResource(ICON_URL)).getImage());
		pack();
	}
	
	private void initMenus() {
		ResourceBundle rb = PatchAnimBundle.getBundle();
		JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu(rb.getString(PatchAnimBundle.FILE));
		newItem = new JMenuItem(rb.getString(PatchAnimBundle.NEW));
		fileMenu.add(newItem);
		openItem = new JMenuItem(rb.getString(PatchAnimBundle.OPEN));
		fileMenu.add(openItem);
		fileMenu.addSeparator();
		saveItem = new JMenuItem(rb.getString(PatchAnimBundle.SAVE));
		saveItem.setEnabled(false);
		fileMenu.add(saveItem);
		saveAsItem = new JMenuItem(rb.getString(PatchAnimBundle.SAVEAS));
		fileMenu.add(saveAsItem);
		fileMenu.addSeparator();
		exportMenu = new JMenu(rb.getString(PatchAnimBundle.EXPORT));
		exportJpgsItem = new JMenuItem(rb.getString(PatchAnimBundle.JPGSERIES));
		exportPngsItem = new JMenuItem(rb.getString(PatchAnimBundle.PNGSERIES));
		exportGifsItem = new JMenuItem(rb.getString(PatchAnimBundle.GIFSERIES));
		exportAnimatedGifItem = new JMenuItem(rb.getString(PatchAnimBundle.ANIMATEDGIF));
		exportAnimatedPngItem = new JMenuItem(rb.getString(PatchAnimBundle.ANIMATEDPNG));
		exportAnimatedMngItem = new JMenuItem(rb.getString(PatchAnimBundle.ANIMATEDMNG));
		exportMenu.add(exportJpgsItem);
		exportMenu.add(exportPngsItem);
		exportMenu.add(exportGifsItem);
		exportMenu.addSeparator();
		exportMenu.add(exportAnimatedGifItem);
		exportMenu.add(exportAnimatedPngItem);
		exportMenu.add(exportAnimatedMngItem);
		fileMenu.add(exportMenu);
		fileMenu.addSeparator();
		quitItem = new JMenuItem(rb.getString(PatchAnimBundle.QUIT));
		fileMenu.add(quitItem);
		
		mb.add(fileMenu);
		
		setJMenuBar(mb);
	}
	
	private void initListeners() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); 
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				try {
					if (document.isDirty()) {
						int choice = askSave();
						if (choice == JOptionPane.CANCEL_OPTION)
							return;
						if (choice == JOptionPane.YES_OPTION) {
							save();
						}
					}
					dispose();
					System.exit(0);
				} catch (IOException ioe) {
					ResourceBundle rb = PatchAnimBundle.getBundle();
					JOptionPane.showMessageDialog(JPatchAnimFrame.this, rb.getString(PatchAnimBundle.SAVEFAILED));
				}
			}
		});
		
		addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowGainedFocus(WindowEvent e) {
				PatchPanelMediator mediator = PatchPanelMediator.getMediator();
				mediator.setFocused(true);
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				PatchPanelMediator mediator = PatchPanelMediator.getMediator();
				mediator.setFocused(false);
			}
		});
		
		newItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					if (document.isDirty()) {
						int choice = askSave();
						if (choice == JOptionPane.CANCEL_OPTION)
							return;
						if (choice == JOptionPane.YES_OPTION) {
							save();
						}
					}
					
					newDocument();
				} catch (IOException ioe) {
					ResourceBundle rb = PatchAnimBundle.getBundle();
					JOptionPane.showMessageDialog(JPatchAnimFrame.this, rb.getString(PatchAnimBundle.SAVEFAILED));
				}
			}
		});
		
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					if (document.isDirty()) {
						int choice = askSave();
						if (choice == JOptionPane.CANCEL_OPTION)
							return;
						if (choice == JOptionPane.YES_OPTION) {
							save();
						}
					}
					load();
					
					ResourceBundle rb = PatchAnimBundle.getBundle();
					String title = MessageFormat.format(rb.getString(PatchAnimBundle.NAMEDTITLE), documentLocation == null ? "" : documentLocation.getName());
					setTitle(title);

				} catch (IOException ioe) {
					ResourceBundle rb = PatchAnimBundle.getBundle();
					JOptionPane.showMessageDialog(JPatchAnimFrame.this, rb.getString(PatchAnimBundle.LOADFAILED));
				}
			}
		});
		
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					if (documentLocation == null) {
						saveAs();
					} else {
						save();
					}
				} catch (IOException ioe) {
					ResourceBundle rb = PatchAnimBundle.getBundle();
					JOptionPane.showMessageDialog(JPatchAnimFrame.this, rb.getString(PatchAnimBundle.SAVEFAILED));
				}
			}
		});
		
		saveAsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					saveAs();
				} catch (IOException ioe) {
					ResourceBundle rb = PatchAnimBundle.getBundle();
					JOptionPane.showMessageDialog(JPatchAnimFrame.this, rb.getString(PatchAnimBundle.SAVEFAILED));
				}
			}
		});
		
		exportJpgsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				export(ExportType.JPegs);
			}
		});
		
		exportPngsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				export(ExportType.Pngs);
			}
		});
		
		exportGifsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				export(ExportType.Gifs);
			}
		});
		
		exportAnimatedGifItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				export(ExportType.AnimatedGif);
			}
		});
		
		exportAnimatedPngItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				export(ExportType.AnimatedPng);
			}
		});
		
		exportAnimatedMngItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				export(ExportType.AnimatedMng);
			}
		});
		
		quitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					if (document.isDirty()) {
						int choice = askSave();
						if (choice == JOptionPane.CANCEL_OPTION)
							return;
						if (choice == JOptionPane.YES_OPTION) {
							save();
						}
					}
					dispose();
					System.exit(0);
				} catch (IOException ioe) {
					ResourceBundle rb = PatchAnimBundle.getBundle();
					JOptionPane.showMessageDialog(JPatchAnimFrame.this, rb.getString(PatchAnimBundle.SAVEFAILED));
				}
			}
		});
		
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		mediator.addActivePatchChangedListener(new ActivePatchChangedListener () {
			@Override
			public void activePatchChanged(ActivePatchChangedEvent apce) {
				document.setDirty(true);
				saveItem.setEnabled(true);
			}
		});
		
		mediator.addSettingsChangedListener(new SettingsChangedListener() {
			@Override
			public void settingsChanged(SettingsChangedEvent sce) {
				document.setDirty(true);
				saveItem.setEnabled(true);
			}
		});
	}
	
	@Override
	public void setVisible(boolean show) {
		newDocument();
		super.setVisible(show);
	}
	
	private void newDocument() {
		ResourceBundle rb = PatchAnimBundle.getBundle();
		NewDocumentDialog newDialog = new NewDocumentDialog();
		newDialog.setModal(true);
		newDialog.setLocationRelativeTo(JPatchAnimFrame.this);
		newDialog.setVisible(true);
		
		int order = newDialog.getOrder();
		boolean useAlpha = newDialog.useAlpha();
		
		document = new PatchAnimDocument(order, useAlpha);
		documentLocation = null;
		saveItem.setEnabled(false);
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		mediator.setDocument(document);
		String title = MessageFormat.format(rb.getString(PatchAnimBundle.NAMEDTITLE), rb.getString(PatchAnimBundle.UNTITLED));
		setTitle(title);
	}
	
	private void load() {
		try {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setFileFilter(new PatchAnimFileFilter());
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			int option = chooser.showOpenDialog(JPatchAnimFrame.this);
			if (option == JFileChooser.APPROVE_OPTION) {
				documentLocation = chooser.getSelectedFile();
				document = PatchAnimIO.loadFile(documentLocation);
			}
		} catch (IOException e) {
			ResourceBundle rb = PatchAnimBundle.getBundle();
			JOptionPane.showMessageDialog(JPatchAnimFrame.this, rb.getString(PatchAnimBundle.LOADFAILED));
			documentLocation = null;
			document = new PatchAnimDocument(4, false);
		} finally {
			PatchPanelMediator mediator = PatchPanelMediator.getMediator();
			mediator.setDocument(document);
			saveItem.setEnabled(false);
		}
	}
	
	private void saveAs() throws IOException {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		File defLocation;
		if (documentLocation == null)
			defLocation = new File(System.getProperty("user.dir"));
		else
			defLocation = documentLocation.getParentFile();

		chooser.setCurrentDirectory(defLocation);
		chooser.setFileFilter(new PatchAnimFileFilter());
		int option = chooser.showSaveDialog(JPatchAnimFrame.this);
		if (option == JFileChooser.APPROVE_OPTION) {
			String path = chooser.getSelectedFile().getPath();
			if (!path.toLowerCase().endsWith(".paf"))
				path += ".paf";
			documentLocation = new File(path);
			PatchAnimIO.saveFile(documentLocation, document);
			documentLocation = chooser.getSelectedFile();
			document.setDirty(false);
			saveItem.setEnabled(false);
		}
	}
	
	private void save() throws IOException {
		PatchAnimIO.saveFile(documentLocation, document);
		document.setDirty(false);
		saveItem.setEnabled(false);
	}
	
	private int askSave() {
		ResourceBundle rb = PatchAnimBundle.getBundle();
		return JOptionPane.showConfirmDialog(JPatchAnimFrame.this, rb.getString(PatchAnimBundle.ASKSAVE), rb.getString(PatchAnimBundle.TITLE), JOptionPane.YES_NO_CANCEL_OPTION);
	}
	
	private File getExportLocation(final ExportType type) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		File defLocation;
		if (documentLocation == null)
			defLocation = new File(System.getProperty("user.dir"));
		else
			defLocation = documentLocation.getParentFile();
		chooser.setCurrentDirectory(defLocation);
		
		if (!type.isMultipleFiles()) {
			chooser.setFileFilter(new FileFilter() {
				private final ResourceBundle rb = PatchAnimBundle.getBundle();

				@Override
				public boolean accept(File f) {
					return (f.isDirectory() || f.getPath().toLowerCase().endsWith("." + type.getExtension()));
				}

				@Override
				public String getDescription() {
					return rb.getString(type.getDescriptionKey());
				}
			});
		}
		
		int option = chooser.showSaveDialog(JPatchAnimFrame.this);
		if (option != JFileChooser.APPROVE_OPTION)
			return null;

		return chooser.getSelectedFile();
	}
	
	private void export(ExportType type) {
			File f = getExportLocation(type);
			if (f != null) {
				final PatchExporter exporter = new PatchExporter(type, f);
				final ExportFrame ed = new ExportFrame();
				ed.setLocationRelativeTo(JPatchAnimFrame.this);
				ed.setVisible(true);
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							exporter.addExportListener(ed);
							exporter.export(document);
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									ed.dispose();								
								}
							});
						} catch (IOException ioe) {
							ResourceBundle rb = PatchAnimBundle.getBundle();
							JOptionPane.showMessageDialog(JPatchAnimFrame.this, rb.getString(PatchAnimBundle.EXPORTFAILED));
						}
					}
				});
				t.start();
			}
	}
}

