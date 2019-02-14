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

/**
 * denotes how to render colors when they are outside the range of 0 - 255
 * <ul>
 * 	<li><b>Clip</b> Clip high values to 255, and low values to 0</li>
 *  <li><b>Cycle</b> Uses the remainder of the color with 255</li>
 *  <li><b>Roll</b> Inverses the value at 255 and 0</li>
 * </ul>
 */
public enum OutOfBoundsColor {
	Clip,
	Cycle,
	Wave;
	
	private static final String OOB = "oob.";
	/**
	 * returns the localized value of the enum
	 * @return the localized display value
	 */
	@Override
	public String toString() {
		ResourceBundle rb = PatchAnimBundle.getBundle();
		return rb.getString(PatchAnimBundle.ROOT + OOB + name().toLowerCase(Locale.ENGLISH));
	}
}
