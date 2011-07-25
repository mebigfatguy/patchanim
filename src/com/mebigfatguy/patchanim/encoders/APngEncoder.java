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

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.mebigfatguy.patchanim.ExportType;

/**
 * generates apng files by simply relying on the built in png encoder of ImageIO
 * and sewing together multiple chunks from the underlying pngs.
 */
public class APngEncoder {
	private static final byte[] HEADER = new byte[] { -119, 80, 78, 71, 13, 10, 26, 10 };
	private static final int IHDR = 0x49484452;
	private static final int IDAT = 0x49444154;
	private static final int IEND = 0x49454E44;
	private static final int acTL = 0x6163544C;
	private static final int fcTL = 0x6663544C;
	private static final int fdAT = 0x66644154;

	private DataOutputStream out = null;
	private boolean started = false;
	private boolean closeStream = false;
	private boolean headerWritten = false;
	private boolean repeatInfinite = false;
	private int delay = 100;
	private int frameCount = 1;
	private int seqNum = 0;
	private boolean processedFirstFrame = false;
	
	public void setDelay(int ms) {
		delay = ms;
	}
	
	public void setRepeat(boolean infinite) {
		repeatInfinite = infinite;
	}
	
	public void setNumFrames(int frames) {
		frameCount = frames;
	}
	
	public boolean start(OutputStream os) {
		if (os == null) 
			return false;
		boolean ok = true;
		closeStream = false;
		out = new DataOutputStream(os);
		try {
			out.write(HEADER);
		} catch (IOException e) {
			ok = false;
		}
		return started = ok;
	}
	
	public boolean start(String file) {
		boolean ok = true;
		try {
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			ok = start(out);
			closeStream = true;
		} catch (IOException e) {
			ok = false;
		}
		return started = ok;
	}
	
	public boolean finish() {
		if (!started) 
			return false;
		try {
			Chunk iendChunk = new Chunk(0, IEND);
			iendChunk.calcCRC();
			iendChunk.write(out);
			if (closeStream)
				out.close();
		} catch (IOException ioe) {
		} finally {
			out = null;
			started = false;
		}
		
		return true;
	}
	
	public boolean addFrame(BufferedImage im) {
		if ((im == null) || !started)
			return false;
		try {
			PngStream pStrm;
			boolean sawIDAT = false;
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream(im.getHeight() * im.getWidth()); //a conservative estimate
				ImageIO.write(im, ExportType.Pngs.getExtension(), baos);
				pStrm = new PngStream(baos.toByteArray());
				baos.reset();
			}
			Chunk chunk = pStrm.readNextChunk();
			while (chunk != null) {
				switch (chunk.type) {
					case IHDR:
						if (!headerWritten) {
							chunk.write(out);
							headerWritten = true;
							Chunk acTLChunk = new Chunk(8, acTL);

							acTLChunk.injectInt(0, frameCount);
							acTLChunk.injectInt(4, repeatInfinite ? 0 : 1);
							acTLChunk.calcCRC();
							acTLChunk.write(out);
						}
					break;
					
					case IDAT:
						if (!sawIDAT) {
							Chunk fcTLChunk = new Chunk(26, fcTL);
							fcTLChunk.injectInt(0, seqNum++);
							fcTLChunk.injectInt(4, im.getWidth());
							fcTLChunk.injectInt(8, im.getHeight());
							fcTLChunk.injectInt(12, 0);
							fcTLChunk.injectInt(16, 0);
							fcTLChunk.injectShort(20, delay);
							fcTLChunk.injectShort(22, 1000);
							fcTLChunk.injectShort(24, 0);
							fcTLChunk.calcCRC();
							fcTLChunk.write(out);
							sawIDAT = true;
						}
						if (!processedFirstFrame) {
							chunk.write(out);
						} else {
							Chunk fdATChunk = new Chunk(chunk.length + 4, fdAT);
							fdATChunk.injectInt(0, seqNum++);
							System.arraycopy(chunk.data, 0, fdATChunk.data, 4, chunk.length);
							fdATChunk.calcCRC();
							fdATChunk.write(out);
						}
					break;
				}
				chunk = pStrm.readNextChunk();
			}
			processedFirstFrame = true;

			return true;
		} catch (IOException ioe) {
			return false;
		}
	}
}
