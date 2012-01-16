/*
 * patchanim - A bezier surface patch color blend animation builder
 * Copyright (C) 2008-2012 Dave Brosius
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
import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.mebigfatguy.patchanim.PatchColor;
import com.mebigfatguy.patchanim.TweenStyle;
import com.mebigfatguy.patchanim.gui.PatchPanelMediator;
import com.mebigfatguy.patchanim.main.PatchAnimBundle;

public class CombinedPatch implements Serializable, Cloneable {

	private static final long serialVersionUID = 3521025714125245036L;
	private EnumMap<PatchColor, PatchCoords> patches = new EnumMap<PatchColor, PatchCoords>(PatchColor.class);
	private String name;
	
	public CombinedPatch(int order, boolean init) {
		if (init) {
			patches.put(PatchColor.Red, PatchCoords.buildRandomPatch(order));
			patches.put(PatchColor.Green, PatchCoords.buildRandomPatch(order));
			patches.put(PatchColor.Blue, PatchCoords.buildRandomPatch(order));
			patches.put(PatchColor.Alpha, PatchCoords.buildFullColorPatch(order));
		} else {
			patches.put(PatchColor.Red, new PatchCoords(order));
			patches.put(PatchColor.Green, new PatchCoords(order));
			patches.put(PatchColor.Blue, new PatchCoords(order));
			patches.put(PatchColor.Alpha, new PatchCoords(order));
		}
		ResourceBundle rb = PatchAnimBundle.getBundle();
		name = rb.getString(PatchAnimBundle.DEFAULTPATCHNAME);
	}
	
	public CombinedPatch(PatchCoords redPatch, PatchCoords greenPatch, PatchCoords bluePatch, PatchCoords alphaPatch) {
		patches.put(PatchColor.Red, redPatch);
		patches.put(PatchColor.Green, greenPatch);
		patches.put(PatchColor.Blue, bluePatch);
		patches.put(PatchColor.Alpha, alphaPatch);
	}
	
	@Override
	public Object clone() {
		try {
			CombinedPatch clonedPatch = (CombinedPatch)super.clone();
			clonedPatch.patches = new EnumMap<PatchColor, PatchCoords>(PatchColor.class);
			for (Map.Entry<PatchColor, PatchCoords> entry : patches.entrySet()) {
				clonedPatch.patches.put(entry.getKey(), (PatchCoords)entry.getValue().clone());
			}
			return clonedPatch;
		} catch (CloneNotSupportedException cnse) {
			return new CombinedPatch(getPatch(PatchColor.Red).getOrder(), true);
		}
	}
	public static CombinedPatch tween(CombinedPatch startPatch, CombinedPatch endPatch, TweenStyle tweenStyle, double frac) {
		CombinedPatch tweenPatch = new CombinedPatch(startPatch.getPatch(PatchColor.Red).getOrder(), false);
		{
			PatchCoords sRedCoords = startPatch.getPatch(PatchColor.Red);
			PatchCoords eRedCoords = endPatch.getPatch(PatchColor.Red);
			tweenPatch.setPatch(PatchColor.Red, PatchCoords.tween(sRedCoords, eRedCoords, tweenStyle, frac));
		}
		{
			PatchCoords sGreenCoords = startPatch.getPatch(PatchColor.Green);
			PatchCoords eGreenCoords = endPatch.getPatch(PatchColor.Green);
			tweenPatch.setPatch(PatchColor.Green, PatchCoords.tween(sGreenCoords, eGreenCoords, tweenStyle, frac));
		}
		{
			PatchCoords sBlueCoords = startPatch.getPatch(PatchColor.Blue);
			PatchCoords eBlueCoords = endPatch.getPatch(PatchColor.Blue);
			tweenPatch.setPatch(PatchColor.Blue, PatchCoords.tween(sBlueCoords, eBlueCoords, tweenStyle, frac));
		}
		
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		if (mediator.getDocument().useAlpha())
		{
			PatchCoords sAlphaCoords = startPatch.getPatch(PatchColor.Alpha);
			PatchCoords eAlphaCoords = endPatch.getPatch(PatchColor.Alpha);
			tweenPatch.setPatch(PatchColor.Alpha, PatchCoords.tween(sAlphaCoords, eAlphaCoords, tweenStyle, frac));
		}
		return tweenPatch;
	}
	
	public PatchCoords getPatch(PatchColor color) {
		return patches.get(color);
	}
	
	public void setPatch(PatchColor color, PatchCoords coords) {
		patches.put(color, coords);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String patchName) {
		name = patchName;
	}
	@Override
	public String toString() {
		return name;
	}
}
