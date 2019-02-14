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

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mebigfatguy.patchanim.AnimationType;
import com.mebigfatguy.patchanim.OutOfBoundsColor;
import com.mebigfatguy.patchanim.PatchAnimDocument;
import com.mebigfatguy.patchanim.TweenStyle;
import com.mebigfatguy.patchanim.gui.events.PatchCompletionEvent;
import com.mebigfatguy.patchanim.gui.events.PatchCompletionListener;

public class PatchAnimator {

	private final Set<PatchCompletionListener> pcListeners = new HashSet<PatchCompletionListener>();
	private final PatchAnimDocument document;
	
	public PatchAnimator(PatchAnimDocument paDocument) {
		document = paDocument;
	}
	
	public void addPatchCompletionListener(PatchCompletionListener listener) {
		synchronized(pcListeners) {
			pcListeners.add(listener);
		}
	}
	
	public void removePatchCompletionListener(PatchCompletionListener listener) {
		synchronized(pcListeners) {
			pcListeners.remove(listener);
		}
	}
	
	public void animatePatches() throws InterruptedException {
		BufferedImage image = PatchGenerator.buildImage(null, document.useAlpha(), document.getWidth(), document.getHeight());		
		List<CombinedPatch> patches = document.getPatches();
		int lastPatch = patches.size() - 1;
		int tweenCount = document.getTweenCount();
		OutOfBoundsColor oob = document.getOutOfBoundsColor();
		AnimationType atype = document.getAnimationType();
		TweenStyle tweenStyle = document.getTweenStyle();
		
		if (lastPatch == 0) {
			PatchGenerator.recalcCombinedImage(patches.get(0), image, oob);
			firePatchCompleted(image);
		} else {
			for(int p = 0; p < lastPatch; p++) {
				CombinedPatch startPatch = patches.get(p);
				CombinedPatch endPatch = patches.get(p+1);
				for (int t = 0; t < tweenCount + 1; t++) {
					CombinedPatch tweenPatch = CombinedPatch.tween(startPatch, endPatch, tweenStyle, (double)t / (double)(tweenCount + 1));
					PatchGenerator.recalcCombinedImage(tweenPatch, image, oob);
					firePatchCompleted(image);
				}
			}
			
			switch (atype) {
				case None: {
					CombinedPatch patch = patches.get(lastPatch);
					PatchGenerator.recalcCombinedImage(patch, image, oob);
					firePatchCompleted(image);
					if (atype == AnimationType.None)
						return;
				}
				break;
				
				case Cycle: {
					CombinedPatch startPatch = patches.get(lastPatch);
					CombinedPatch endPatch = patches.get(0);
					for (int t = 0; t < tweenCount + 1; t++) {
						CombinedPatch tweenPatch = CombinedPatch.tween(startPatch, endPatch, tweenStyle, (double)t / (double)(tweenCount + 1));
						PatchGenerator.recalcCombinedImage(tweenPatch, image, oob);
						firePatchCompleted(image);
					}
				}
				break;
				
				case Wave: {
					for (int p = lastPatch; p > 0; p--) {
						CombinedPatch startPatch = patches.get(p-1);
						CombinedPatch endPatch = patches.get(p);
						for (int t = tweenCount + 1; t > 0; t--) {
							CombinedPatch tweenPatch = CombinedPatch.tween(startPatch, endPatch, tweenStyle, (double)t / (double)(tweenCount + 1));
							PatchGenerator.recalcCombinedImage(tweenPatch, image, oob);
							firePatchCompleted(image);
						}
					}
				}
				break;
			}
		}
	}
	
	private void firePatchCompleted(BufferedImage image) throws InterruptedException {
		PatchCompletionEvent pce = new PatchCompletionEvent(this, image);
		Set<PatchCompletionListener> listeners;
		synchronized(pcListeners) {
			listeners = new HashSet<PatchCompletionListener>(pcListeners);
		}
		
		for (PatchCompletionListener listener : listeners) {
			listener.patchCompleted(pce);
		}
	}
}
