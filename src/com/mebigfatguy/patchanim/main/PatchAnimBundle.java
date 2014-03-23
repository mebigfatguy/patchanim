/*
 * patchanim - A bezier surface patch color blend animation builder
 * Copyright (C) 2008 Dave Brosius
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
package com.mebigfatguy.patchanim.main;

import java.util.ResourceBundle;

public class PatchAnimBundle {
	
	public static final String ROOT = "patchanim.";
	public static final String TITLE = "patchanim.title";
	public static final String NAMEDTITLE = "patchanim.namedtitle";
	public static final String UNTITLED = "patchanim.untitled";
	public static final String FILE = "patchanim.file";
	public static final String NEW = "patchanim.new";
	public static final String OPEN = "patchanim.open";
	public static final String SAVE = "patchanim.save";
	public static final String SAVEAS = "patchanim.saveas";
	public static final String FILEFILTER = "patchanim.patchanimfilter";
	public static final String EXPORT = "patchanim.export";
	public static final String JPGSERIES = "patchanim.jpgs";
	public static final String JPGSERIESFILTER = "patchanim.filter.jpgs";
	public static final String PNGSERIES = "patchanim.pngs";
	public static final String PNGSERIESFILTER = "patchanim.filter.pngs";
	public static final String GIFSERIES = "patchanim.gifs";
	public static final String GIFSERIESFILTER = "patchanim.filter.gifs";
	public static final String ANIMATEDGIF = "patchanim.animatedgif";
	public static final String ANIMATEDGIFFILTER = "patchanim.filter.animatedgif";
	public static final String ANIMATEDPNG = "patchanim.apng";
	public static final String ANIMATEDPNGFILTER = "patchanim.filter.apng";
	public static final String ANIMATEDMNG = "patchanim.mng";
	public static final String ANIMATEDMNGFILTER = "patchanim.filter.mng";
	public static final String EXPORTINGFILE = "patchanim.exportfile";
	public static final String QUIT = "patchanim.quit";
	public static final String CONTROLS = "patchanim.control";
	public static final String NEWDOCUMENT = "patchanim.newdocument";
	public static final String SETORDER = "patchanim.setorder";
	public static final String USEALPHA = "patchanim.usealpha";
	public static final String OK = "patchanim.ok";
	public static final String CANCEL = "patchanim.cancel";
	public static final String PATCHES = "patchanim.patches";
	public static final String WIDTH = "patchanim.width";
	public static final String WIDTH_TT = "patchanim.tooltip.width";
	public static final String HEIGHT = "patchanim.height";
	public static final String HEIGHT_TT = "patchanim.tooltip.height";
	public static final String ANIMATION = "patchanim.animation";
	public static final String ANIMATION_TT = "patchanim.tooltip.animation";
	public static final String TYPENONE = "patchanim.type.none";
	public static final String TYPECYCLE = "patchanim.type.cycle";
	public static final String TYPEWAVE = "patchanim.type.wave";
	public static final String OUTOFBOUNDSCOLOR = "patchanim.outofboundscolor";
	public static final String OUTOFBOUNDSCOLOR_TT = "patchanim.tooltip.outofboundscolor";	
	public static final String OOBCLIP = "patchanim.oob.clip";
	public static final String OOBROLL = "patchanim.oob.cycle";
	public static final String OOBWAVE = "patchanim.oob.wave";
	public static final String TWEENFRAMES = "patchanim.tween";
	public static final String TWEENFRAMES_TT = "patchanim.tooltip.tween";
	public static final String TWEENSTYLE = "patchanim.tweenstyle";
	public static final String TWEENSTYLE_TT = "patchanim.tooltip.tweenstyle";
	public static final String TEST = "patchanim.test";
	public static final String TEST_TT = "patchanim.tooltip.test";
	public static final String STOP = "patchanim.stop";
	public static final String DEFAULTPATCHNAME = "patchanim.defaultpatchname";
	public static final String ENTERNEWPATCHNAME = "patchanim.enternewpatchname";
	public static final String ADD = "patchanim.add";
	public static final String REMOVE = "patchanim.remove";
	public static final String RENAME = "patchanim.rename";
	public static final String CLONE = "patchanim.clone";
	public static final String COLOR = "patchanim.color";
	public static final String SETALLPOINTS = "patchanim.setallpoints";
	public static final String SETBORDERPOINTS = "patchanim.setborderpoints";
	public static final String BLACK = "patchanim.black";
	public static final String FULLCOLOR="patchanim.fullcolor";
	public static final String VALUE="patchanim.value";
	public static final String LIGHTENPATCH = "patchanim.lightenpatch";
	public static final String DARKENPATCH = "patchanim.darkenpatch";
	public static final String LINEARGRADIENT = "patchanim.lineargradient";
	public static final String LEFTTORIGHT = "patchanim.lefttoright";
	public static final String TOPTOBOTTOM = "patchanim.toptobottom";
	public static final String RIGHTTOLEFT = "patchanim.righttoleft";
	public static final String BOTTOMTOTOP = "patchanim.bottomtotop";
	public static final String RADIALGRADIENT = "patchanim.radialgradient";
	public static final String SHAPEGRADIENT = "patchanim.shapegradient";
	public static final String OUTWARD = "patchanim.outward";
	public static final String INWARD = "patchanim.inward";
	public static final String SHIFT = "patchanim.shift";
	public static final String LEFT = "patchanim.left";
	public static final String DOWN = "patchanim.down";
	public static final String RIGHT = "patchanim.right";
	public static final String UP = "patchanim.up";
	public static final String INVERT = "patchanim.invert";
	public static final String COPYPATCHFROM = "patchanim.copypatchfrom";
	public static final String REDPATCH = "patchanim.redpatch";
	public static final String GREENPATCH = "patchanim.greenpatch";
	public static final String BLUEPATCH = "patchanim.bluepatch";
	public static final String ALPHAPATCH = "patchanim.alphapatch";
	public static final String ASKSAVE = "patchanim.asksave";
	public static final String LOADFAILED = "patchanim.err.loadfailed";	
	public static final String SAVEFAILED = "patchanim.err.savefailed";
	public static final String EXPORTFAILED = "patchanim.err.exportfailed";
	
	private static ResourceBundle rb = ResourceBundle.getBundle("com/mebigfatguy/patchanim/resources");
	
	private PatchAnimBundle()
	{
	}
	
	public static ResourceBundle getBundle() {
		return rb;
	}
}
