/*
 * patchanim - A bezier surface patch color blend animation builder
 * Copyright (C) 2008-2010 Dave Brosius
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
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.mebigfatguy.patchanim.AnimationType;
import com.mebigfatguy.patchanim.PatchAnimDocument;
import com.mebigfatguy.patchanim.gui.events.PatchCompletionEvent;
import com.mebigfatguy.patchanim.gui.events.PatchCompletionListener;
import com.mebigfatguy.patchanim.main.PatchAnimBundle;
import com.mebigfatguy.patchanim.surface.PatchAnimator;

public class JTestFrame extends JFrame implements PatchCompletionListener {
	
	private static final long serialVersionUID = -7975149184522257748L;
	
	private transient Thread animThread = null;
	private TestPanel testPanel = null;
	private final PatchAnimDocument document;
	private long lastRedraw;
	
	public JTestFrame() {
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		document = mediator.getDocument();

		initComponents();
		initListeners();
	}
	
	public synchronized void beginAnimation() {
		if (animThread != null)
			return;
		
		animThread = new Thread(new Runnable() {
			public void run() {
				AnimationType type = document.getAnimationType();
				do {
					try {
						PatchAnimator animator = new PatchAnimator(document);
						animator.addPatchCompletionListener(JTestFrame.this);
						animator.animatePatches();
						animator.removePatchCompletionListener(JTestFrame.this);
					} catch (InterruptedException ie) {
						break;
					} catch (Exception e) {
						//Could get OutOfBoundsException if user deletes patches mid animation
					}
				} while (type != AnimationType.None);
			}
		});
		animThread.start();
		
	}
	
	public synchronized void endAnimation() {
		if (animThread == null)
			return;
		
		try {
			animThread.interrupt();
			animThread.join();
		} catch (InterruptedException ie) {
			//OK
		} finally {
			animThread = null;
		}
	}
	
	public void patchCompleted(PatchCompletionEvent pce) throws InterruptedException {
		long now = System.currentTimeMillis();
		long sleepTime = 100 - (now - lastRedraw);
		if (sleepTime > 0)
			Thread.sleep(sleepTime);
		testPanel.redraw(pce.getImage());
		lastRedraw = now;
	}
	
	private void initComponents() {
		ResourceBundle rb = PatchAnimBundle.getBundle();
		testPanel = new TestPanel();
		setLayout(new BorderLayout(4, 4));
		add(testPanel, BorderLayout.CENTER);
		pack();
		setTitle(rb.getString(PatchAnimBundle.TEST));
	}
	
	private void initListeners() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				endAnimation();
				dispose();
			}
		});
	}
	
	class TestPanel extends JPanel {
		private static final long serialVersionUID = -6464417075282170562L;
		private BufferedImage image = null;
		private int width;
		private int height;

		public TestPanel() {
			width = document.getWidth();
			height = document.getHeight();
			
			if (width == 0)
				width = 100;
			if (height == 0)
				height = 100;

			Dimension d = new Dimension(width, height);
			setMinimumSize(d);
			setMaximumSize(d);
			setPreferredSize(d);
			setSize(width, height);
		}
		
		public void redraw(BufferedImage redrawImage) throws InterruptedException {
			try {
				image = redrawImage;
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						invalidate();
						revalidate();
						repaint();
					}
				});
			} catch (InvocationTargetException ite) {
				InterruptedException ie = new InterruptedException("Error rendering test");
				ie.initCause(ite);
				throw ie;
			}
		}
		
		@Override
		public void paintComponent(Graphics g) {
			if (image != null)
				g.drawImage(image, 0, 0, width, height, 0, 0, width, height, null);
		}
	}
}
