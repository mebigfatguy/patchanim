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

import java.util.List;

import javax.swing.AbstractListModel;

import com.mebigfatguy.patchanim.PatchAnimDocument;
import com.mebigfatguy.patchanim.surface.CombinedPatch;

public class PatchListModel extends AbstractListModel {

	private static final long serialVersionUID = -4980398874234658317L;
	
	private PatchAnimDocument doc;
	
	public PatchListModel() {
		doc = null;
	}
	
	public PatchListModel(PatchAnimDocument document) {
		doc = document;
	}
	
	public void add(int pos, CombinedPatch patch) {
		if (doc == null)
			return;

		List<CombinedPatch> patches = doc.getPatches();
		patches.add(pos, patch);
		doc.setDirty(true);
		fireIntervalAdded(this, pos, pos);
	}
	
	public void remove(int pos) {
		if (doc == null)
			return;

		List<CombinedPatch> patches = doc.getPatches();
		patches.remove(pos);
		doc.setDirty(true);
		fireIntervalRemoved(this, pos, pos);	
	}
	
	public Object getElementAt(int index) {
		if (doc == null)
			return null;

		List<CombinedPatch> patches = doc.getPatches();
		return patches.get(index);
	}

	public int getSize() {
		if (doc == null)
			return 0;
		
		List<CombinedPatch> patches = doc.getPatches();
		return patches.size();
	}

}
