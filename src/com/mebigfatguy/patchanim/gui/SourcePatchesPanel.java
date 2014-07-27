/*
 * patchanim - A bezier surface patch color blend animation builder
 * Copyright (C) 2008-2014 Dave Brosius
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

import java.awt.Container;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.mebigfatguy.patchanim.PatchColor;
import com.mebigfatguy.patchanim.gui.events.DocumentChangedEvent;
import com.mebigfatguy.patchanim.gui.events.DocumentChangedListener;

public class SourcePatchesPanel extends JPanel {
	
	private static final long serialVersionUID = -6986592027015551508L;
	
	JColorControlPatchPanel redPatch;
	JColorControlPatchPanel greenPatch;
	JColorControlPatchPanel bluePatch;
	JColorControlPatchPanel alphaPatch;

	
	public SourcePatchesPanel() {
		redPatch = new JColorControlPatchPanel(PatchColor.Red);
		greenPatch = new JColorControlPatchPanel(PatchColor.Green);
		bluePatch = new JColorControlPatchPanel(PatchColor.Blue);
		alphaPatch = new JColorControlPatchPanel(PatchColor.Alpha);

		rebuild(false);
		
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		mediator.addDocumentChangedListener(new DocumentChangedListener() {
			@Override
			public void documentChanged(DocumentChangedEvent dce) {
			    boolean useAlpha = dce.getDocument().useAlpha();
				rebuild(useAlpha);
				redPatch.useAlpha(useAlpha);
				greenPatch.useAlpha(useAlpha);
				bluePatch.useAlpha(useAlpha);
			}
		});
	}
	
	public final void rebuild(final boolean useAlpha) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Container c = SourcePatchesPanel.this.getParent();
				if (c != null)
					c.invalidate();
				removeAll();
				setLayout(new BoxLayout(SourcePatchesPanel.this, BoxLayout.X_AXIS));
				add(Box.createHorizontalGlue());
				add(redPatch);
				add(Box.createHorizontalStrut(10));
				add(greenPatch);
				add(Box.createHorizontalStrut(10));
				add(bluePatch);
				if (useAlpha) {
					add(Box.createHorizontalStrut(10));
					add(alphaPatch);
				}
				add(Box.createHorizontalGlue());
				invalidate();
				revalidate();
				if (c != null) {
					c.invalidate();	
					c.repaint();
				}
				
			}
		});
	}
}
