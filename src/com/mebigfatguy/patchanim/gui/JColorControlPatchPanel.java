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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.mebigfatguy.patchanim.PatchColor;
import com.mebigfatguy.patchanim.gui.events.ActivePatchChangedEvent;
import com.mebigfatguy.patchanim.gui.events.ActivePatchChangedListener;
import com.mebigfatguy.patchanim.main.PatchAnimBundle;
import com.mebigfatguy.patchanim.surface.CombinedPatch;
import com.mebigfatguy.patchanim.surface.Coordinate;
import com.mebigfatguy.patchanim.surface.PatchCoords;

public class JColorControlPatchPanel extends JPanel implements PatchDecorator {
	
	private static final long serialVersionUID = -2524694507912574529L;
	private static final double MINCLICKDISTANCESQ = 12.0;
	private static final double MINDRAGDISTANCESQ = 3.0;
	
	private PatchCoords coords;
	private final PatchColor color;
	private JPatchSamplePanel sample;
	private JLabel colorLabel;
	private JTextField colorField;
	private transient final ValueDocumentListener docListener = new ValueDocumentListener();
	private int selectedXPt;
	private int selectedYPt;
	
	public JColorControlPatchPanel(PatchColor c) {
		color = c;
		selectedXPt = 0;
		selectedYPt = 0;
		initComponents();
		initListeners();
	}
	
	public void useAlpha(boolean useAlpha) {
	    sample.useAlpha(useAlpha);
	}
	
	@Override
    public Point getSelectedCoordinate() {
        return new Point(selectedXPt, selectedYPt);
    }

    private void initComponents() {
		ResourceBundle rb = PatchAnimBundle.getBundle();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		sample = new JPatchSamplePanel(color);
		sample.setDecorator(this);
			
		colorLabel = new JLabel(rb.getString(PatchAnimBundle.COLOR));
		colorField = new JTextField(new DoubleDocument(), "", 4);
		colorLabel.setLabelFor(colorField);
		
		add(Box.createVerticalGlue());
		JPanel p = new JPanel();
		{
			p.setLayout(new BorderLayout(4, 4));
			p.add(sample, BorderLayout.CENTER);
			
			JPanel south = new JPanel();
			south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
			south.add(Box.createHorizontalGlue());
			south.add(colorLabel);
			south.add(Box.createHorizontalStrut(5));
			south.add(colorField);
			south.add(Box.createHorizontalGlue());
			p.add(south, BorderLayout.SOUTH);
		}
		add(p);
		add(Box.createVerticalGlue());
		
		setMaximumSize(getPreferredSize());
	}
	
	private void initListeners() {
		final PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		mediator.addActivePatchChangedListener(new ActivePatchChangedListener() {
			@Override
			public void activePatchChanged(ActivePatchChangedEvent apce) {
				if ((color != PatchColor.Alpha) || mediator.getDocument().useAlpha()) {
					CombinedPatch currentPatch = apce.getActivePatch();
					coords = currentPatch.getPatch(color);
					if (coords != null) {
						setColorField(false);
						sample.recalcImage(color, currentPatch);
					}
				}
			}
		});
		colorField.getDocument().addDocumentListener(docListener);
	}
	
	private void setColorField(final boolean fireEvents) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Coordinate coord = coords.getCoordinate(selectedXPt, selectedYPt);
				double newColor = coord.getColor();
				double oldColor;
				try {
					oldColor = Double.parseDouble(colorField.getText());
				} catch (NumberFormatException nfe) {
					oldColor = 0.0;
				}
				if (newColor != oldColor) {
					if (!fireEvents)
						colorField.getDocument().removeDocumentListener(docListener);
					colorField.setText(String.valueOf(coord.getColor()));
					if (!fireEvents)
						colorField.getDocument().addDocumentListener(docListener);
				}
			}
		});
	}
	
	@Override
	public void drawDecoration(Graphics2D g, Rectangle bounds) {
		if (coords == null)
			return;
		
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		if (!mediator.isFocused())
			return;
			
		g.setColor(Color.yellow);
		int order = coords.getOrder();
		for (int u = 0; u < order; u++) {
			for (int v = 0; v < order; v++) {
				Coordinate c = coords.getCoordinate(u, v);
				if ((selectedXPt == u) && (selectedYPt == v)) {
					g.fillOval((int)(((c.getX() * (bounds.width - 5)) / 100.0) + bounds.x), 
							   (int)(((c.getY() * (bounds.height - 5)) / 100.0) + bounds.y), 5, 5);
				} else {
					g.drawOval((int)(((c.getX() * (bounds.width - 5)) / 100.0) + bounds.x), 
							   (int)(((c.getY() * (bounds.height - 5)) / 100.0) + bounds.y), 5, 5);
				}
			}
		}
	}
	
	@Override
	public boolean press(Point p, Rectangle bounds) {

		if (setSelectedControlPt(p, bounds)) {
			setColorField(false);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					colorField.requestFocusInWindow();
					colorField.setSelectionStart(0);
					colorField.setSelectionEnd(Integer.MAX_VALUE);
					invalidate();
					revalidate();
					repaint();
				}
			});
			return true;
		}
		return false;
	}
	
	@Override
	public boolean drag(Point p, Rectangle bounds) {
		double inputX = ((p.x - bounds.x) * 100.0) / bounds.width;
		double inputY = ((p.y - bounds.y) * 100.0) / bounds.height;
		inputX = Math.min(100.0, Math.max(inputX, 0.0));
		inputY = Math.min(100.0, Math.max(inputY, 0.0));
		
		Coordinate c= coords.getCoordinate(selectedXPt, selectedYPt);
		double oldX = c.getX();
		double oldY = c.getY();
		
		double xDeltaSq = inputX - oldX;
		xDeltaSq *= xDeltaSq;
		double yDeltaSq = inputY - oldY;
		yDeltaSq *= yDeltaSq;
		
		if ((xDeltaSq + yDeltaSq) > MINDRAGDISTANCESQ) {
			c.setX(inputX);
			c.setY(inputY);
			return true;
		}
		
		return false;
	}
	
	private boolean setSelectedControlPt(Point p, Rectangle bounds) {
		double minDistanceSq = Double.MAX_VALUE;
		double inputX = ((p.x - bounds.x) * 100.0) / bounds.width;
		double inputY = ((p.y - bounds.y) * 100.0) / bounds.height;
		int minU = 0;
		int minV = 0;
		
		int order = coords.getOrder();
		for (int u = 0; u < order; u++) {
			for (int v = 0; v < order; v++) {
				Coordinate c = coords.getCoordinate(u, v);
				double xSq = c.getX() - inputX;
				xSq *= xSq;
				double ySq = c.getY() - inputY;
				ySq *= ySq;
				
				double distSq = xSq + ySq;
				if (distSq < minDistanceSq) {
					minDistanceSq = distSq;
					minU = u;
					minV = v;
				}
			}
		}
		
		if (minDistanceSq < MINCLICKDISTANCESQ) {
			selectedXPt = minU;
			selectedYPt = minV;
			return true;
		}
		
		return false;
	}
	
	class ValueDocumentListener implements DocumentListener
	{
		@Override
		public void changedUpdate(DocumentEvent de) {
			processChange();
		}
	
		@Override
		public void insertUpdate(DocumentEvent de) {
			processChange();
		}
	
		@Override
		public void removeUpdate(DocumentEvent de) {
			processChange();
		}
		
		private void processChange() {
			double value;

			try {
				value = Double.parseDouble(colorField.getText());
			} catch (NumberFormatException nfe) {
				value = 0.0;
			}
			Coordinate coord = coords.getCoordinate(selectedXPt, selectedYPt);
			coord.setColor(value);
			coords.setCoordinate(selectedXPt, selectedYPt, coord);
			PatchPanelMediator mediator = PatchPanelMediator.getMediator();
			mediator.setNewActivePatch(mediator.getActivePatch());
		}	
	}
}
