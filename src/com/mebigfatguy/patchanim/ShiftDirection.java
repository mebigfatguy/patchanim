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
package com.mebigfatguy.patchanim;

import java.util.Locale;
import java.util.ResourceBundle;

import com.mebigfatguy.patchanim.main.PatchAnimBundle;

public enum ShiftDirection {
	Left,
	Down,
	Right,
	Up;
	
	/**
	 * returns the localized value of the type
	 */
	@Override
	public String toString() {
		ResourceBundle rb = PatchAnimBundle.getBundle();
		return rb.getString(PatchAnimBundle.ROOT + name().toLowerCase(Locale.ENGLISH));
	}

}
