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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class Chunk {
	
	public int length;
	public int type;
	public byte[] data;
	public int crc;
	
	public Chunk(int len, int chunkType) {
		length = len;
		type = chunkType;
		data = new byte[length];
		crc = 0;
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(length);
		out.writeInt(type);
		out.write(data);
		out.writeInt(crc);
	}
	
	public void injectInt(int offset, int value) {
		data[offset++] = (byte)(value >> 24 & 0x00FF);
		data[offset++] = (byte)(value >> 16 & 0x00FF);
		data[offset++] = (byte)(value >> 8 & 0x00FF);
		data[offset]   = (byte)(value & 0x00FF);
	}
	
	public void injectShort(int offset, int value) {
		data[offset++] = (byte)(value >> 8 & 0x00FF);
		data[offset]   = (byte)(value & 0x00FF);
	}
	
	public void injectByte(int offset, int value) {
		data[offset]   = (byte)(value & 0x00FF);
	}
	
	public void calcCRC() {
		CRC32 crc32 = new CRC32();
		byte[] typeBytes = new byte[4];
		typeBytes[0] = (byte)(type >> 24 & 0x00FF);
		typeBytes[1] = (byte)(type >> 16 & 0x00FF);
		typeBytes[2] = (byte)(type >> 8 & 0x00FF);
		typeBytes[3] = (byte)(type & 0x00FF);
		crc32.update(typeBytes);
		crc32.update(data);
		crc = (int)crc32.getValue();
	}
}