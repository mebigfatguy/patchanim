/*
 * patchanim - A bezier surface patch color blend animation builder
 * Copyright (C) 2008-2012 Dave Brosius
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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EnumSet;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.mebigfatguy.patchanim.AnimationType;
import com.mebigfatguy.patchanim.OutOfBoundsColor;
import com.mebigfatguy.patchanim.PatchAnimDocument;
import com.mebigfatguy.patchanim.TweenStyle;
import com.mebigfatguy.patchanim.gui.events.DocumentChangedEvent;
import com.mebigfatguy.patchanim.gui.events.DocumentChangedListener;
import com.mebigfatguy.patchanim.main.PatchAnimBundle;

public class JPatchControlPanel extends JPanel {

	private static final long serialVersionUID = -5968231995166721151L;
	
	private PatchAnimDocument document;
	private JTextField widthField;
	private JTextField heightField;
	private JComboBox animationCB;
	private JComboBox outOfBoundsColorCB;
	private JTextField tweenFramesField;
	private JComboBox tweenStyleCB;
	private JButton testButton;
	
	public JPatchControlPanel() {
		initComponents();
		initListeners();
	}

	private void initComponents() {
		ResourceBundle rb = PatchAnimBundle.getBundle();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createTitledBorder(rb.getString(PatchAnimBundle.CONTROLS)));
		
		JLabel widthLabel;
		JLabel heightLabel;
		JLabel animationLabel;
		JLabel outOfBoundsLabel;
		JLabel tweenFramesLabel;
		JLabel tweenStyleLabel;
		
		{
			widthLabel = new JLabel(rb.getString(PatchAnimBundle.WIDTH));
			widthField = new JTextField(new IntegerDocument(), "", 8);
			widthLabel.setLabelFor(widthField);
			widthField.setToolTipText(rb.getString(PatchAnimBundle.WIDTH_TT));
			
			JPanel p = Utils.createFormPanel(widthLabel, widthField);

			Utils.limitPanelHeight(p, widthField);
			add(p);
		}
		add(Box.createVerticalStrut(5));
		{
			heightLabel = new JLabel(rb.getString(PatchAnimBundle.HEIGHT));
			heightField = new JTextField(new IntegerDocument(), "", 8);
			heightLabel.setLabelFor(heightField);
			heightField.setToolTipText(rb.getString(PatchAnimBundle.HEIGHT_TT));
			JPanel p = Utils.createFormPanel(heightLabel, heightField);
			
			Utils.limitPanelHeight(p, heightField);
			add(p);
		}
		add(Box.createVerticalStrut(5));
		{
			animationLabel = new JLabel(rb.getString(PatchAnimBundle.ANIMATION));
			animationCB = new JComboBox(new Object[] { AnimationType.None,
													   AnimationType.Cycle,
													   AnimationType.Wave });
			animationLabel.setLabelFor(animationCB);
			animationCB.setToolTipText(rb.getString(PatchAnimBundle.ANIMATION_TT));
			JPanel p = Utils.createFormPanel(animationLabel, animationCB);

			Utils.limitPanelHeight(p, animationCB);
			add(p);
		}
		add(Box.createVerticalStrut(5));	
		{
			outOfBoundsLabel = new JLabel(rb.getString(PatchAnimBundle.OUTOFBOUNDSCOLOR));
			outOfBoundsColorCB = new JComboBox(new Object[] { OutOfBoundsColor.Clip,
															  OutOfBoundsColor.Cycle,
															  OutOfBoundsColor.Wave });
			outOfBoundsColorCB.setToolTipText(rb.getString(PatchAnimBundle.OUTOFBOUNDSCOLOR_TT));
			outOfBoundsLabel.setLabelFor(outOfBoundsColorCB);
			JPanel p = Utils.createFormPanel(outOfBoundsLabel, outOfBoundsColorCB);
			Utils.limitPanelHeight(p, outOfBoundsColorCB);
			add(p);
		}
		add(Box.createVerticalStrut(5));
		{
			tweenFramesLabel = new JLabel(rb.getString(PatchAnimBundle.TWEENFRAMES));
			tweenFramesField = new JTextField(new IntegerDocument(), "", 8);
			tweenFramesLabel.setLabelFor(tweenFramesField);
			tweenFramesField.setToolTipText(rb.getString(PatchAnimBundle.TWEENFRAMES_TT));
			JPanel p = Utils.createFormPanel(tweenFramesLabel, tweenFramesField);
			Utils.limitPanelHeight(p, tweenFramesField);
			add(p);
		}
		add(Box.createVerticalStrut(5));
		{
			tweenStyleLabel = new JLabel(rb.getString(PatchAnimBundle.TWEENSTYLE));
			EnumSet<TweenStyle> ts = EnumSet.<TweenStyle>allOf(TweenStyle.class);
			tweenStyleCB = new JComboBox(ts.toArray(new TweenStyle[ts.size()]));
			tweenStyleLabel.setLabelFor(tweenStyleCB);
			tweenStyleCB.setToolTipText(rb.getString(PatchAnimBundle.TWEENSTYLE_TT));
			outOfBoundsLabel.setLabelFor(tweenStyleCB);
			JPanel p = Utils.createFormPanel(tweenStyleLabel, tweenStyleCB);
			Utils.limitPanelHeight(p, tweenStyleCB);
			add(p);
		}

		testButton = new JButton(rb.getString(PatchAnimBundle.TEST));
		testButton.setToolTipText(rb.getString(PatchAnimBundle.TEST_TT));
		add(testButton);
		
		Utils.sizeUniformly(Utils.Sizing.Both, widthLabel, heightLabel, animationLabel, outOfBoundsLabel, tweenFramesLabel, tweenStyleLabel);
		Utils.sizeUniformly(Utils.Sizing.Width, new JComponent[] { widthField, heightField, animationCB, outOfBoundsColorCB, tweenFramesField, tweenStyleCB });
		
		add(Box.createVerticalGlue());
		
		Dimension d = getPreferredSize();
		if (d.width < 200)
			d.width = 200;
		setPreferredSize(d);
	}
	
	private void initListeners() {
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		mediator.addDocumentChangedListener(new DocumentChangedListener() {
			public void documentChanged(DocumentChangedEvent dce) {
				document = dce.getDocument();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						widthField.setText(String.valueOf(document.getWidth()));
						heightField.setText(String.valueOf(document.getHeight()));
						animationCB.setSelectedIndex(document.getAnimationType().ordinal());
						outOfBoundsColorCB.setSelectedIndex(document.getOutOfBoundsColor().ordinal());
						tweenFramesField.setText(String.valueOf(document.getTweenCount()));
						tweenStyleCB.setSelectedIndex(document.getTweenStyle().ordinal());
					}
				});
			}
		});
		
		widthField.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent fe) {
				try {
					int oldWidth = document.getWidth();
					int newWidth = Integer.parseInt(widthField.getText());
					if (oldWidth != newWidth) {
						document.setWidth(newWidth);
						PatchPanelMediator ppMediator = PatchPanelMediator.getMediator();
						ppMediator.fireSettingsChanged();
					}
				} catch (NumberFormatException nfe) {
					document.setWidth(100);
					PatchPanelMediator ppMediator = PatchPanelMediator.getMediator();
					ppMediator.fireSettingsChanged();
				}				
			}
			
			public void focusGained(FocusEvent fe) {
				widthField.setSelectionStart(0);
				widthField.setSelectionEnd(Integer.MAX_VALUE);
			}
		});

		heightField.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent arg0) {
				try {
					int oldHeight = document.getHeight();
					int newHeight = Integer.parseInt(heightField.getText());
					if (oldHeight != newHeight) {
						document.setHeight(newHeight);
						PatchPanelMediator ppMediator = PatchPanelMediator.getMediator();
						ppMediator.fireSettingsChanged();
					}
				} catch (NumberFormatException nfe) {
					document.setHeight(100);
					PatchPanelMediator ppMediator = PatchPanelMediator.getMediator();
					ppMediator.fireSettingsChanged();
				}
			}
			
			public void focusGained(FocusEvent fe) {
				heightField.setSelectionStart(0);
				heightField.setSelectionEnd(Integer.MAX_VALUE);
			}
		});
		
		tweenFramesField.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent arg0) {
				try {
					int oldTween = document.getTweenCount();
					int newTween = Integer.parseInt(tweenFramesField.getText());
					if (oldTween != newTween) {
						document.setTweenCount(newTween);
						PatchPanelMediator ppMediator = PatchPanelMediator.getMediator();
						ppMediator.fireSettingsChanged();
					}
				} catch (NumberFormatException nfe) {
					document.setTweenCount(10);
					PatchPanelMediator ppMediator = PatchPanelMediator.getMediator();
					ppMediator.fireSettingsChanged();
				}
			}
		});
		
		animationCB.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					document.setAnimationType((AnimationType)ie.getItem());
					PatchPanelMediator ppMediator = PatchPanelMediator.getMediator();
					ppMediator.fireSettingsChanged();
				}
			}
		});
		
		outOfBoundsColorCB.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					document.setOutOfBoundsColor((OutOfBoundsColor)ie.getItem());
					PatchPanelMediator ppMediator = PatchPanelMediator.getMediator();
					ppMediator.fireSettingsChanged();
				}
			}
		});
		
		tweenFramesField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent fe) {
				tweenFramesField.setSelectionStart(0);
				tweenFramesField.setSelectionEnd(Integer.MAX_VALUE);
			}
		});
		
		tweenStyleCB.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					document.setTweenStyle((TweenStyle)ie.getItem());
					PatchPanelMediator ppMediator = PatchPanelMediator.getMediator();
					ppMediator.fireSettingsChanged();
				}
			}
		});
		
		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JTestFrame tf = new JTestFrame();
				tf.setLocationRelativeTo(JPatchControlPanel.this.getRootPane());
				tf.setVisible(true);
				tf.setAlwaysOnTop(true);
				tf.beginAnimation();
			}
		});
	}
}
