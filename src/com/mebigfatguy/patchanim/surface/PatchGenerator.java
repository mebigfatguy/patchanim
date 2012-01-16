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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

import com.mebigfatguy.patchanim.OutOfBoundsColor;
import com.mebigfatguy.patchanim.PatchColor;

public class PatchGenerator {
	
	private PatchGenerator() {
	}
	
	public static void recalcCombinedImage(CombinedPatch patch, BufferedImage image, OutOfBoundsColor oob) {
		if (patch == null)
			return;
		
		int numComponents = image.getColorModel().getNumComponents();
		boolean useAlpha = numComponents == 4;
		
		WritableRaster wr = image.getRaster();
		DataBuffer db = wr.getDataBuffer();
		int pixel = 0;

		PatchCoords[] coords = new PatchCoords[4];
		
		coords[0] = patch.getPatch(PatchColor.Red);
		coords[1] = patch.getPatch(PatchColor.Green);
		coords[2] = patch.getPatch(PatchColor.Blue);
		coords[3] = patch.getPatch(PatchColor.Alpha);
		int order = coords[0].getOrder();
		
		double u;
		double v;
		double[] uCoeffs = new double[order];
		double[] vCoeffs = new double[order];
		double[] value = new double[4];
		int[] iValue = new int[4];
		
		int sampleSizeX = image.getWidth();
		int sampleSizeY = image.getHeight();
		
		for (int iv = 0; iv < sampleSizeY; iv++) {
			v = (double)iv / (double)sampleSizeY;
			buildCoefficients(v, vCoeffs);
			
			for (int iu = 0; iu < sampleSizeX; iu++) {
				u = (double)iu / (double)sampleSizeX;
				buildCoefficients(u, uCoeffs);
			
				value[0] = value[1] = value[2] = value[3] = 0.0;
				
				for (int j = 0; j < order; j++) {
					for (int i = 0; i < order; i++) {
						double coeff = uCoeffs[i] * vCoeffs[j];
						for (int k = 0; k < numComponents; k++) {
							value[k] += coords[k].getCoordinate(i, j).getColor() * coeff;
						}
					}
				}
				
				for (int k = 0; k < numComponents; k++) {
					iValue[k] = adjustColor(value[k], oob);
				}
				
				if (useAlpha)
					db.setElem(pixel++, iValue[3]);
				db.setElem(pixel++, iValue[2]);
				db.setElem(pixel++, iValue[1]);
				db.setElem(pixel++, iValue[0]);
			}
		}
	}
	
	public static void recalcIndexedImage(PatchColor color, CombinedPatch patch, BufferedImage image, OutOfBoundsColor oob) {
		if (patch == null)
			return;
		
		WritableRaster wr = image.getRaster();
		DataBuffer db = wr.getDataBuffer();
		int pixel = 0;
		
		PatchCoords coords = patch.getPatch(color);
		if (coords.getCoordinate(0, 0) == null)
			return;
		
		int order = coords.getOrder();
		
		double u;
		double v;
		double[] uCoeffs = new double[order];
		double[] vCoeffs = new double[order];
		
		int sampleSizeX = image.getWidth();
		int sampleSizeY = image.getHeight();

		for (int iv = 0; iv < sampleSizeY; iv++) {
			v = (double)iv / (double)sampleSizeY;
			buildCoefficients(v, vCoeffs);
			
			for (int iu = 0; iu < sampleSizeX; iu++) {
				u = (double)iu / (double)sampleSizeX;
				buildCoefficients(u, uCoeffs);
			
				double value = 0.0;
				for (int j = 0; j < order; j++) {
					for (int i = 0; i < order; i++) {
						value += coords.getCoordinate(i, j).getColor() * uCoeffs[i] * vCoeffs[j];
					}
				}

				db.setElem(pixel++, adjustColor(value, oob));
			}
		}
	}
	
	private static int adjustColor(double color, OutOfBoundsColor oob) {
		int value = (int)color;
		if ((value & 0xFFFFFF00) == 0)
			return value;
		
		switch (oob) {
			case Clip:
				if (value < 0)
					value = 0;
				else
					value = 255;
			break;
			
			case Cycle:
				value = value & 0x00FF;
			break;
			
			case Wave:
				int period = value / 256;
				if ((period & 0x01)  != 0) {
					if (value > 0)
						value = 255 - (value+1) & 0x00FF;
					else
						value = value & 0x00FF;
				}
				else {
					if (value > 0)
						value = value & 0x00FF;
					else
						value = 256 - value & 0x00FF;
				}
			break;
		}
		
		return value;
	}

	public static BufferedImage buildImage(Color color, boolean useAlpha, int sampleSizeX, int sampleSizeY) {
		BufferedImage image = null;
		
		if (color == null) {
			image = new BufferedImage(sampleSizeX, sampleSizeY, useAlpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);
		} else {
			byte[] r = new byte[256];
			byte[] g = new byte[256];
			byte[] b = new byte[256];
			
			for (int i = 0; i < 256; i++) {
				r[i] = (byte)((i * color.getRed()) / 255);
				g[i] = (byte)((i * color.getGreen()) / 255);
				b[i] = (byte)((i * color.getBlue()) / 255);
			}
			IndexColorModel icm = new IndexColorModel(8, 256, r, g, b);
			
			image = new BufferedImage(sampleSizeX, sampleSizeY, BufferedImage.TYPE_BYTE_INDEXED, icm);
		}
		
		Graphics graphics = image.getGraphics();
		graphics.setColor(color);
		graphics.fillRect(0, 0, sampleSizeX, sampleSizeY);
		return image;
	}
	
	public static void buildCoefficients(double t, double[] coeffs) {
		double tt = 1.0;
		for (int i = 0; i < coeffs.length; i++) {
			coeffs[i] = tt;
			tt *= t;
		}
		
		double oneMinusT = 1.0 - t;
		double oneMinusTT = 1.0;
		for (int i = coeffs.length - 1; i >= 0; i--) {
			coeffs[i] *= oneMinusTT;
			oneMinusTT *= oneMinusT;
		}
		
		for (int i = 0; i < coeffs.length; i++) {
			coeffs[i] *= NChooseI.nChooseI(coeffs.length-1, i);
		}
	}

	static class NChooseI {
		private static double[] factorial = { 1.0, 1.0, 2.0, 6.0, 24.0, 120.0, 720.0, 5040.0, 40320.0, 362880.0 };
		
		public static double nChooseI(int n, int i) {
			return factorial[n]/(factorial[i] * factorial[n-i]);
		}
	}
}
