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
package com.mebigfatguy.patchanim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mebigfatguy.patchanim.surface.CombinedPatch;

/**
 * represents the document that can be saved to file.
 */
public class PatchAnimDocument implements Serializable {
	
	private static final long serialVersionUID = -7412254429829665944L;
	
	private boolean dirty;
	private final int order;
	private final boolean useAlpha;
	private List<CombinedPatch> patches;
	private int width;
	private int height;
	private AnimationType animationType;
	private OutOfBoundsColor outOfBoundsColor;
	private int tweenCount;
	private TweenStyle tweenStyle;
	
	/**
	 * constructs a new document for the New menu item
	 * 
	 * @param patchOrder the order of the patch (number of control points)
	 * @param alpha whether to use alpha blending or not
	 */
	public PatchAnimDocument(int patchOrder, boolean alpha) {
		order = patchOrder;
		useAlpha = alpha;
		patches = new ArrayList<CombinedPatch>();
		CombinedPatch patch = new CombinedPatch(order, true);
		patches.add(patch);
		width = 200;
		height = 200;
		animationType = AnimationType.Wave;
		outOfBoundsColor = OutOfBoundsColor.Clip;
		tweenCount = 10;
		tweenStyle = TweenStyle.Linear;
	}

	/**
	 * determines if the document has been modified since it was last saved
	 * @return whether the document has been modified.
	 */
	public boolean isDirty() {
		return dirty;
	}
	
	/**
	 * marks whether or not the document has been modified.
	 * @param isDirty whether or not the document has been modified
	 */
	public void setDirty(boolean isDirty) {
		dirty = isDirty;
	}
	
	/**
	 * returns the order of the bezier patch used for this document
	 * @return the order of the patch
	 */
	public int getOrder() {
		return order;
	}
	
	/**
	 * returns whether to use the alpha channel
	 * @return the alpha channel option flag
	 */
	public boolean useAlpha() {
		return useAlpha;
	}
	
	/**
	 * retrieves a list of all the bezier patches
	 * @return the list of all the bezier patches
	 */
	public List<CombinedPatch> getPatches() {
		return patches;
	}

	/**
	 * sets the list of bezier patches to use for color blends
	 * @param patches the list of bezier patches
	 */
	public void setPatches(List<CombinedPatch> patches) {
		this.patches = patches;
	}

	/**
	 * returns the preferred width in pixels of the generated animation file
	 * @return the width in pixels
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * sets the preferred width in pixels of generated animation files
	 * @param width the width in pixels
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * gets the height in pixels of the generated animation file
	 * @return the height in pixels
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * sets the height in pixels of the gnerated animation files
	 * @param height the height in pixels
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * gets the preferred animation type to use for the animation files
	 * @return the preferred animation type
	 */
	public AnimationType getAnimationType() {
		return animationType;
	}

	/**
	 * sets the preferred animation type to use for animation files
	 * @param animationType the preferred animation type
	 */
	public void setAnimationType(AnimationType animationType) {
		this.animationType = animationType;
	}

	/**
	 * gets the algorithm to use when a color is beyond the 0 - 255 range
	 * @return the algorithm choice to use for out of bounds color
	 */
	public OutOfBoundsColor getOutOfBoundsColor() {
		return outOfBoundsColor;
	}

	/**
	 * sets the algorithm to use when a color is beyond the 0 - 255 range
	 * @param outOfBoundsColor the algorithm choice to use for out of bounds colors
	 */
	public void setOutOfBoundsColor(OutOfBoundsColor outOfBoundsColor) {
		this.outOfBoundsColor = outOfBoundsColor;
	}

	/**
	 * gets the preferred number of in-between frames to generate when creating animations
	 * @return the number of in-between frames
	 */
	public int getTweenCount() {
		return tweenCount;
	}

	/**
	 * sets the preferred number of in-between frames to generate when creating animations
	 * @param tweenCount the number of in-between frames
	 */
	public void setTweenCount(int tweenCount) {
		this.tweenCount = tweenCount;
	}
	
	/**
	 * gets the algorithm used to tween between one patch and another 
	 * @return the tween algorithm
	 */
	public TweenStyle getTweenStyle() {
		return tweenStyle;
	}
	
	/**
	 * sets the algorithm used to tween between one patch and another 
	 * @param tweenStyle the tween algorithm
	 */
	public void setTweenStyle(TweenStyle tweenStyle) {
		this.tweenStyle = tweenStyle;
	}
}
