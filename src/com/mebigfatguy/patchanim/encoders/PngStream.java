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
package com.mebigfatguy.patchanim.encoders;

public class PngStream {
	private static final int HEADERLENGTH = 8;
	public int pos;
	public byte[] data;
	
	public PngStream(byte[] pngData) {
		pos = HEADERLENGTH;
		data = pngData;
	}
	
	public Chunk readNextChunk() {
		if (pos >= data.length)
			return null;

		Chunk c = new Chunk(readNextInt(), readNextInt());

		System.arraycopy(data, pos, c.data, 0, c.length);
		pos += c.length;
		c.crc = readNextInt();
		return c;
	}
	
	private int readNextInt() {
		int val = 0;
		for (int i = 0; i < 4; i++) {
			val <<= 8;
			val |= (0x00FF & data[pos++]);
		}
		return val;
	}

}
