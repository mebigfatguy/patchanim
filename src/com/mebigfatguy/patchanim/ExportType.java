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

import com.mebigfatguy.patchanim.main.PatchAnimBundle;

/**
 * denotes the type of file to export to
 * <ul>
 *  <li><b>JPegs</b> a series of jpeg files in a directory</li>
 *  <li><b>Pngs</b> a series of Png files in a directory</li>
 *  <li><b>Gifs</b> a series of Gif files in a directory</li>
 *  <li><b>AnimatedGif</b> an animated gif file</li>
 *  <li><b>AnimatedPng</b> an animated png file</li>
 *  <li><b>AnimatedMng</b> an animated mng file</li>
 * </ul>
 */
public enum ExportType {
	JPegs("jpg", true, PatchAnimBundle.JPGSERIESFILTER),
	Pngs("png", true, PatchAnimBundle.PNGSERIESFILTER),
	Gifs("gif", true, PatchAnimBundle.GIFSERIESFILTER),
	AnimatedGif("gif", false, PatchAnimBundle.ANIMATEDGIFFILTER),
	AnimatedPng("png", false, PatchAnimBundle.ANIMATEDPNGFILTER),
	AnimatedMng("mng", false, PatchAnimBundle.ANIMATEDMNGFILTER);
		
	private String ext;
	private boolean multi;
	private String key;
	
	/**
	 * constructs an ExportType enum
	 * @param extension the file extension used for this type
	 * @param multipleFiles whether or not this file type generates multiple files
	 * @param descriptionKey the resource bundle key for the display value
	 */
	private ExportType(String extension, boolean multipleFiles, String descriptionKey) {
		ext = extension;
		multi = multipleFiles;
		key = descriptionKey;
	}
	
	/**
	 * returns the file extension for this export type
	 * @return the file extension
	 */
	public String getExtension() {
		return ext;
	}
	
	/**
	 * returns whether or not this file type generates multiple files
	 * @return whether the export generates multiple files
	 */
	public boolean isMultipleFiles() {
		return multi;
	}
	
	/**
	 * returns the resource bundle key for the localized value
	 * @return the resource key
	 */
	public String getDescriptionKey() {
		return key;
	}
}
