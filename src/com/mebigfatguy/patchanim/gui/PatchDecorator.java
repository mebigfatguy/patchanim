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

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public interface PatchDecorator {
	void drawDecoration(Graphics g, Rectangle bounds);
	
	boolean press(Point p, Rectangle bounds);
	
	boolean drag(Point p, Rectangle bounds);
	
	Point getSelectedCoordinate();
}
