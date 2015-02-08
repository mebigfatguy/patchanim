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

import java.io.File;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

import com.mebigfatguy.patchanim.main.PatchAnimBundle;

public class PatchAnimFileFilter extends FileFilter {
	@Override
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		return (f.getPath().endsWith("paf"));
	}

	@Override
	public String getDescription() {
		ResourceBundle rb = PatchAnimBundle.getBundle();
		return rb.getString(PatchAnimBundle.FILEFILTER);
	}
}
