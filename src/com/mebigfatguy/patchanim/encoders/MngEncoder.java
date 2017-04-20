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
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import com.mebigfatguy.patchanim.ExportType;

/**
 * generates mng files by simply relying on the built in png encoder of ImageIO and sewing together multiple chunks from the underlying pngs.
 */
public class MngEncoder {
    private static final byte[] HEADER = new byte[] { -118, 77, 78, 71, 13, 10, 26, 10 };
    private static final int IHDR = 0x49484452;
    private static final int IDAT = 0x49444154;
    private static final int IEND = 0x49454E44;
    private static final int MHDR = 0x4D484452;
    private static final int DEFI = 0x44454649;
    private static final int SAVE = 0x53415645;
    private static final int TERM = 0x5445524D;
    private static final int SEEK = 0x5345454B;
    private static final int LOOP = 0x4E4F4F50;
    private static final int SHOW = 0x53484F59;
    private static final int ENDL = 0x454E444E;
    private static final int MEND = 0x4D454E44;

    private DataOutputStream out = null;
    private boolean started = false;
    private boolean closeStream = false;
    private boolean headerWritten = false;
    private boolean repeatInfinite = false;
    private int delay = 100;
    private int frameCount = 1;
    private int seqNum = 0;

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
        if (os == null) {
            return false;
        }
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
            out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(Paths.get(file))));
            ok = start(out);
            closeStream = true;
        } catch (IOException e) {
            ok = false;
        }
        return started = ok;
    }

    public boolean finish() {
        if (!started) {
            return false;
        }
        try {
            Chunk saveChunk = new Chunk(0, SAVE);
            saveChunk.calcCRC();
            saveChunk.write(out);

            Chunk termChunk = new Chunk(10, TERM);
            termChunk.injectShort(0, 0);
            termChunk.injectInt(2, 0);
            termChunk.injectInt(6, repeatInfinite ? 0x7FFFFFFF : 0);

            termChunk.calcCRC();
            termChunk.write(out);

            Chunk seekChunk = new Chunk(0, SEEK);
            seekChunk.calcCRC();
            seekChunk.write(out);

            if (repeatInfinite) {
                Chunk loopChunk = new Chunk(6, LOOP);
                loopChunk.injectByte(0, 0);
                loopChunk.injectInt(1, 0x7FFFFFFF);
                loopChunk.injectByte(5, 6);
                loopChunk.calcCRC();
                loopChunk.write(out);
            }

            Chunk showChunk = new Chunk(0, SHOW);
            showChunk.calcCRC();
            showChunk.write(out);

            if (repeatInfinite) {
                Chunk endlChunk = new Chunk(1, ENDL);
                endlChunk.injectByte(0, 0);
                endlChunk.calcCRC();
                endlChunk.write(out);
            }

            Chunk iendChunk = new Chunk(0, MEND);
            iendChunk.calcCRC();
            iendChunk.write(out);
            if (closeStream) {
                out.close();
            }
        } catch (IOException ioe) {
        } finally {
            out = null;
            started = false;
        }

        return true;
    }

    public boolean addFrame(BufferedImage im) {
        if ((im == null) || !started) {
            return false;
        }
        try {
            PngStream pStrm;
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(im.getHeight() * im.getWidth()); // a conservative estimate
                ImageIO.write(im, ExportType.Pngs.getExtension(), baos);
                pStrm = new PngStream(baos.toByteArray());
                baos.reset();
            }
            Chunk chunk = pStrm.readNextChunk();
            while (chunk != null) {
                switch (chunk.type) {
                    case IHDR:
                        if (!headerWritten) {
                            Chunk mhdrChunk = new Chunk(28, MHDR);
                            mhdrChunk.injectInt(0, im.getWidth());
                            mhdrChunk.injectInt(4, im.getHeight());
                            mhdrChunk.injectInt(8, delay);
                            mhdrChunk.injectInt(12, 0);
                            mhdrChunk.injectInt(16, frameCount);
                            mhdrChunk.injectInt(20, frameCount);
                            mhdrChunk.injectInt(24, 583);
                            mhdrChunk.calcCRC();
                            mhdrChunk.write(out);
                            headerWritten = true;
                        }
                        Chunk defiChunk = new Chunk(4, DEFI);
                        defiChunk.injectInt(0, (seqNum++) << 16);
                        defiChunk.calcCRC();
                        defiChunk.write(out);
                        chunk.write(out);
                    break;

                    case IDAT:
                        chunk.write(out);
                    break;

                    case IEND:
                        chunk.write(out);
                    break;
                }
                chunk = pStrm.readNextChunk();
            }
            return true;
        } catch (IOException ioe) {
            return false;
        }
    }
}
