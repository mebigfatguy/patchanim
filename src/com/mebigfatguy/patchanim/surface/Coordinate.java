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
package com.mebigfatguy.patchanim.surface;

import java.io.Serializable;

public class Coordinate implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 5211344767856486552L;
	
	private double x, y, color;
	
	public Coordinate(double xPos, double yPos, double colorVal) {
		x = xPos;
		y = yPos;
		color = colorVal;
	}
	
	@Override
	public Coordinate clone() {
		try {
			return (Coordinate) super.clone();
		} catch (CloneNotSupportedException cnse) {
			return new Coordinate(0.0, 0.0, 0.0);
		}
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double xPos) {
		x = xPos;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double yPos) {
		y = yPos;
	}
	
	public double getColor() {
		return color;
	}
	
	public void setColor(double colorVal) {
		color = colorVal;
	}
	
	
	@Override
	public String toString() {
		return "x=" + x + " y=" + y + " color=" + color;		
	}
}
