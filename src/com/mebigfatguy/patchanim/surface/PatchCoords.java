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
import java.util.Arrays;
import java.util.Random;

import com.mebigfatguy.patchanim.TweenStyle;

public class PatchCoords implements Serializable, Cloneable {
	private static final long serialVersionUID = -4052789167154764908L;
	
	private final int order;
	private Coordinate[][] coords;
	
	public static PatchCoords buildRandomPatch(int patchOrder) {
		Coordinate[][] coords = new Coordinate[patchOrder][patchOrder];
		Random r = new Random();
		for (int u = 0; u < patchOrder; u++) {
			for (int v = 0; v < patchOrder; v++) {
				coords[u][v] = new Coordinate((u * 100.0) / (patchOrder - 1), (v * 100.0) / (patchOrder - 1), r.nextInt(400) - 50);
			}
		}
		return new PatchCoords(patchOrder, coords);
	}
	
	public static PatchCoords buildFullColorPatch(int patchOrder) {
		Coordinate[][] coords = new Coordinate[patchOrder][patchOrder];
		for (int u = 0; u < patchOrder; u++) {
			for (int v = 0; v < patchOrder; v++) {
				coords[u][v] = new Coordinate((u * 100.0) / (patchOrder - 1), (v * 100.0) / (patchOrder - 1), 255);
			}
		}
		return new PatchCoords(patchOrder, coords);
	}
	
	public PatchCoords(int patchOrder) {
		order = patchOrder;
		coords = new Coordinate[patchOrder][patchOrder];
	}
	
	public PatchCoords(int patchOrder, Coordinate[][] coordinates) {
		order = patchOrder;
		coords = coordinates;
	}
	
	@Override
	public PatchCoords clone() {
		try {
			PatchCoords clonedCoords = (PatchCoords)super.clone();
			clonedCoords.coords = new Coordinate[order][order];
			for (int u = 0; u < order; u++) {
				for (int v = 0; v < order; v++) {
					clonedCoords.coords[u][v] = coords[u][v].clone();
				}
			}
			return clonedCoords;
		} catch (CloneNotSupportedException cnse) {
			return buildRandomPatch(order);
		}
	}
	
	public int getOrder() {
		return order;
	}
	
	public static PatchCoords tween(PatchCoords startCoords, PatchCoords endCoords, TweenStyle tweenStyle, double frac) {
		
		PatchCoords tweenCoords = new PatchCoords(startCoords.getOrder());
		double tweenFrac = tweenStyle.transform(frac);
		for (int x = 0; x < tweenCoords.order; x++) {
			for (int y = 0; y < tweenCoords.order; y++) {
				Coordinate startC = startCoords.getCoordinate(x,y);
				Coordinate endC = endCoords.getCoordinate(x,y);
				double startColor = startC.getColor();
				int tweenColor = (int)(startColor + (endC.getColor() - startColor) * tweenFrac);
				Coordinate tweenC = new Coordinate(startC.getX(), startC.getY(), tweenColor);
				tweenCoords.setCoordinate(x, y, tweenC);
			}
		}
		
		return tweenCoords;
	}
	
	public Coordinate getCoordinate(int i, int j) {
		return coords[i][j];
	}

	public void setCoordinate(int i, int j, Coordinate coordinate) {
		coords[i][j] = coordinate;
	}
	
	public void resetSpatialCoordinates() {
		for (int u = 0; u < order; u++) {
			for (int v = 0; v < order; v++) {
				coords[u][v].setX((u * 100.0) / (order - 1));
				coords[u][v].setY((v * 100.0) / (order - 1));
			}
		}

	}

	@Override
	public String toString() {
		return Arrays.toString(coords);
	}
}
