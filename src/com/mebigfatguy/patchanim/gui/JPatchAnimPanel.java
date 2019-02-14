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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.mebigfatguy.patchanim.PatchColor;

public class JPatchAnimPanel extends JPanel {
	
	private static final long serialVersionUID = 3030850111559417377L;

	public JPatchAnimPanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout(4, 4));
		JPatchControlPanel ctrl = new JPatchControlPanel();
		p.add(ctrl, BorderLayout.WEST);
		JPatchSamplePanel sample = new JPatchSamplePanel(PatchColor.Combined);
		JPanel q = new JPanel();
		q.setLayout(new BoxLayout(q, BoxLayout.X_AXIS));
		q.add(Box.createHorizontalGlue());
		q.add(sample);	
		q.add(Box.createHorizontalGlue());
		q.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		p.add(q, BorderLayout.CENTER);
		add(p);
		
		
		p = new JPanel();
		p.setLayout(new BorderLayout(4, 4));
		JPanel listPanel = new JPatchListPanel();
		p.add(listPanel, BorderLayout.WEST);
		
		Utils.sizeUniformly(Utils.Sizing.Width, ctrl, listPanel );
		
		SourcePatchesPanel sourcesPanel = new SourcePatchesPanel();
		
		p.add(sourcesPanel, BorderLayout.CENTER);

		p.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		add(p);
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
	}
}
