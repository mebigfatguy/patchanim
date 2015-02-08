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

import java.util.HashSet;
import java.util.Set;

import com.mebigfatguy.patchanim.PatchAnimDocument;
import com.mebigfatguy.patchanim.gui.events.ActivePatchChangedEvent;
import com.mebigfatguy.patchanim.gui.events.ActivePatchChangedListener;
import com.mebigfatguy.patchanim.gui.events.DocumentChangedEvent;
import com.mebigfatguy.patchanim.gui.events.DocumentChangedListener;
import com.mebigfatguy.patchanim.gui.events.SettingsChangedEvent;
import com.mebigfatguy.patchanim.gui.events.SettingsChangedListener;
import com.mebigfatguy.patchanim.surface.CombinedPatch;

public class PatchPanelMediator {
	private static final PatchPanelMediator mediator = new PatchPanelMediator();

	private PatchAnimDocument document;
	private CombinedPatch activePatch;
	private Set<DocumentChangedListener> dclisteners = new HashSet<DocumentChangedListener>();
	private Set<ActivePatchChangedListener> apclisteners = new HashSet<ActivePatchChangedListener>();
	private Set<SettingsChangedListener> sclisteners = new HashSet<SettingsChangedListener>();
	private boolean windowFocused = true;
	
	private PatchPanelMediator() {
	}
	
	public static PatchPanelMediator getMediator() {
		return mediator;
	}
	public void addDocumentChangedListener(DocumentChangedListener dcl) {
		synchronized(dclisteners) {
			dclisteners.add(dcl);
		}
	}
	
	public void setDocument(PatchAnimDocument doc) {
		document = doc;
		DocumentChangedEvent dce = new DocumentChangedEvent(this, doc);
		synchronized(dclisteners) {
			for (DocumentChangedListener dcl : dclisteners) {
				dcl.documentChanged(dce);
			}
		}
	}
	
	public PatchAnimDocument getDocument() {
		return document;
	}
	
	public void addActivePatchChangedListener(ActivePatchChangedListener apcl) {
		synchronized(apclisteners) {
			apclisteners.add(apcl);
		}
	}
	
	public void addSettingsChangedListener(SettingsChangedListener scl) {
		synchronized(sclisteners) {
			sclisteners.add(scl);
		}
	}
	
	public void fireSettingsChanged() {
		synchronized(sclisteners) {
			SettingsChangedEvent sce = new SettingsChangedEvent(this, document);
			for (SettingsChangedListener scl : sclisteners) {
				scl.settingsChanged(sce);
			}
		}
	}
	
	public CombinedPatch getActivePatch() {
		return activePatch;
	}
	
	public void setNewActivePatch(CombinedPatch patch) {
		activePatch = patch;
		ActivePatchChangedEvent apce = new ActivePatchChangedEvent(this, patch);
		synchronized(apclisteners) {
			for (ActivePatchChangedListener apcl : apclisteners) {
				apcl.activePatchChanged(apce);
			}
		}
	}
	
	public void setFocused(boolean focused) {
		windowFocused = focused;
		synchronized(apclisteners) {
			ActivePatchChangedEvent apce = new ActivePatchChangedEvent(this, activePatch);
			for (ActivePatchChangedListener apcl : apclisteners) {
				apcl.activePatchChanged(apce);
			}
		}
	}
	
	public boolean isFocused() {
		return windowFocused;
	}
	
	

}
