/*
 * patchanim - A bezier surface patch color blend gif builder
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
package com.mebigfatguy.patchanim.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.fmsware.gif.AnimatedGifEncoder;
import com.mebigfatguy.patchanim.AnimationType;
import com.mebigfatguy.patchanim.ExportType;
import com.mebigfatguy.patchanim.PatchAnimDocument;
import com.mebigfatguy.patchanim.encoders.APngEncoder;
import com.mebigfatguy.patchanim.encoders.MngEncoder;
import com.mebigfatguy.patchanim.gui.events.ExportEvent;
import com.mebigfatguy.patchanim.gui.events.ExportListener;
import com.mebigfatguy.patchanim.gui.events.PatchCompletionEvent;
import com.mebigfatguy.patchanim.gui.events.PatchCompletionListener;
import com.mebigfatguy.patchanim.main.PatchAnimBundle;
import com.mebigfatguy.patchanim.surface.PatchAnimator;

public class PatchExporter implements PatchCompletionListener {
	private ExportType type;
	private File loc;
	private AnimatedGifEncoder agEncoder;
	private APngEncoder apngEncoder;
	private MngEncoder mngEncoder;
	private int totalImages;
	private int imageIndex;
	private String baseName;
	private Set<ExportListener> elisteners = new HashSet<ExportListener>();
	
	public PatchExporter(ExportType exportType, File location) {
		type = exportType;
		loc = location;
		if (type == ExportType.AnimatedGif)
			agEncoder = new AnimatedGifEncoder();
		else if (type == ExportType.AnimatedPng)
			apngEncoder = new APngEncoder();
		else if (type == ExportType.AnimatedMng)
			mngEncoder = new MngEncoder();
	}
	
	public void addExportListener(ExportListener el) {
		elisteners.add(el);
	}
	
	public void export(PatchAnimDocument document) throws IOException {
		try {
			baseName = loc.getName();
			if (type.isMultipleFiles()) {
				if (!loc.mkdir())
					throw new IOException("Failed to create directory " + loc.getName());
			} else {
				loc = loc.getParentFile();
				String dotExt = "." + type.getExtension();
				if (!baseName.toLowerCase().endsWith(dotExt))
					baseName = baseName + dotExt;
				if (type == ExportType.AnimatedGif)
					agEncoder.start(new File(loc, baseName).getPath());
				else if (type == ExportType.AnimatedPng)
					apngEncoder.start(new File(loc, baseName).getPath());
				else if (type == ExportType.AnimatedMng)
					mngEncoder.start(new File(loc, baseName).getPath());
			}
	
			totalImages = calcImageCount(document);
			imageIndex = 1;
			
			AnimationType atype = document.getAnimationType();
			switch (type) {
				case AnimatedGif:
					agEncoder.setRepeat((atype != AnimationType.None) ? 0 : -1);
				break;
				
				case AnimatedPng:
					apngEncoder.setRepeat(atype != AnimationType.None);
					apngEncoder.setNumFrames(totalImages);
				break;
				
				case AnimatedMng:
					mngEncoder.setRepeat(atype != AnimationType.None);
					mngEncoder.setNumFrames(totalImages);
					mngEncoder.setDelay(100);
			}

			
			PatchAnimator animator = new PatchAnimator(document);
			animator.addPatchCompletionListener(this);
			try {
				animator.animatePatches();
			} catch (InterruptedException ie) {
				ResourceBundle rb = PatchAnimBundle.getBundle();
				JOptionPane.showMessageDialog(null, rb.getString(PatchAnimBundle.EXPORTFAILED));
			}
		} finally {
			if (type == ExportType.AnimatedGif) {
				agEncoder.finish();
			} else if (type == ExportType.AnimatedPng) {
				apngEncoder.finish();
			} else if (type == ExportType.AnimatedMng) {
				mngEncoder.finish();
			}
		}
	}

	public void patchCompleted(PatchCompletionEvent pce) throws InterruptedException {
		try {
			writeSingleFile(pce.getImage(), imageIndex++, loc, baseName, type);
		} catch (IOException ioe) {
			InterruptedException ie = new InterruptedException("Failed saving animation at index " + (imageIndex-1));
			ie.initCause(ioe);
			throw ie;
		}
	}
	
	private void writeSingleFile(BufferedImage image, int index, File dir, String bsName, ExportType exportType) throws IOException {
		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setMinimumIntegerDigits(5);
		nf.setGroupingUsed(false);
		String name = bsName + "_" + nf.format(index) + "." + exportType.getExtension();
		
		File imageFile = new File(dir, name);
		
		if (exportType == ExportType.Gifs) {
			AnimatedGifEncoder encoder = new AnimatedGifEncoder();
			encoder.start(imageFile.getPath());
			encoder.addFrame(image);
			encoder.finish();
		} else if (exportType == ExportType.AnimatedGif) {
			agEncoder.addFrame(image);
			agEncoder.setDelay(100);
		} else if (exportType == ExportType.AnimatedPng) {
			apngEncoder.setDelay(100);
			apngEncoder.addFrame(image);
		} else if (exportType == ExportType.AnimatedMng) {
			mngEncoder.setDelay(100);
			mngEncoder.addFrame(image);
		}
		else
			ImageIO.write(image, exportType.getExtension(), imageFile);
		
		fireExportEvent(index);
	}
	
	private void fireExportEvent(int index) {
		ExportEvent ee = new ExportEvent(this, index, totalImages);
		for (ExportListener el : elisteners) {
			el.imageExported(ee);
		}
	}
	
	private int calcImageCount(PatchAnimDocument document) {
		int numPatches = document.getPatches().size();
		int total = (numPatches - 1) * document.getTweenCount() + (numPatches - 1);
		
		switch (document.getAnimationType()) {
			case None:
				total++;
			break;
			
			case Cycle:
				total += document.getTweenCount() + 1;
			break;
			
			case Wave:
				total = total * 2;
			break;
		}
		
		return total;
	}
}
