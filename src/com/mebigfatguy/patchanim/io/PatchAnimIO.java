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
package com.mebigfatguy.patchanim.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.mebigfatguy.patchanim.AnimationType;
import com.mebigfatguy.patchanim.OutOfBoundsColor;
import com.mebigfatguy.patchanim.PatchAnimDocument;
import com.mebigfatguy.patchanim.PatchColor;
import com.mebigfatguy.patchanim.TweenStyle;
import com.mebigfatguy.patchanim.surface.CombinedPatch;
import com.mebigfatguy.patchanim.surface.Coordinate;
import com.mebigfatguy.patchanim.surface.PatchCoords;

public class PatchAnimIO {
    private static final String PATCHANIMDOC_SCHEMA = "/com/mebigfatguy/patchanim/io/PatchAnimDoc.xsd";

    private PatchAnimIO() {

    }

    public static void saveFile(File f, PatchAnimDocument document) throws IOException {
        Path temporary = Files.createTempFile("patchanim", ".paf");
        try (InputStream xslIs = new BufferedInputStream(PatchAnimIO.class.getResourceAsStream("/com/mebigfatguy/patchanim/io/PatchAnimDoc.xsl"))) {

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer(new StreamSource(xslIs));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.newDocument();

            try (OutputStream xmlOs = new BufferedOutputStream(Files.newOutputStream(temporary))) {
                t.setParameter("doc", document);
                t.transform(new DOMSource(d), new StreamResult(xmlOs));
            }
            document.setDirty(false);
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception e) {
            IOException ioe = new IOException("Failed saving document " + f.getPath());
            ioe.initCause(e);
            throw ioe;
        } finally {
            f.delete();
            Files.move(temporary, f.toPath());
        }
    }

    public static PatchAnimDocument loadFile(File f) throws IOException {

        try (InputStream xmlIs = new BufferedInputStream(Files.newInputStream(f.toPath()))) {

            XMLReader reader = XMLReaderFactory.createXMLReader();
            PatchAnimDocContentHandler handler = new PatchAnimDocContentHandler();
            reader.setContentHandler(handler);

            reader.setFeature("http://apache.org/xml/features/validation/schema", true);
            reader.setFeature("http://xml.org/sax/features/validation", true);
            reader.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
                    PatchAnimDocument.class.getResource(PATCHANIMDOC_SCHEMA).toString());

            reader.parse(new InputSource(xmlIs));
            return handler.getDocument();
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception e) {
            IOException ioe = new IOException("Failed loading document " + f.getPath());
            ioe.initCause(e);
            throw ioe;
        }
    }

    static class PatchAnimDocContentHandler extends DefaultHandler {
        private static final String SETTINGS = "Settings";
        private static final String ORDER = "order";
        private static final String ALPHA = "alpha";
        private static final String WIDTH = "width";
        private static final String HEIGHT = "height";
        private static final String ANIMATIONTYPE = "animationType";
        private static final String OUTOFBOUNDSCOLOR = "outOfBoundsColor";
        private static final String TWEENCOUNT = "tweenCount";
        private static final String TWEENSTYLE = "tweenStyle";
        private static final String COMBINEDPATCH = "CombinedPatch";
        private static final String PATCH = "Patch";
        private static final String COLOR = "color";
        private static final String COORDINATE = "Coordinate";
        private static final String X = "x";
        private static final String Y = "y";

        PatchAnimDocument doc = null;

        private CombinedPatch cPatch = null;
        private PatchCoords patchCoords = null;
        private PatchColor patchColor = null;
        private int coordIndex = 0;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) {
            if (SETTINGS.equals(localName)) {
                int order = Integer.parseInt(atts.getValue(ORDER));
                boolean alpha = Boolean.parseBoolean(atts.getValue(ALPHA));
                doc = new PatchAnimDocument(order, alpha);
                doc.getPatches().clear();
                doc.setWidth(Integer.parseInt(atts.getValue(WIDTH)));
                doc.setHeight(Integer.parseInt(atts.getValue(HEIGHT)));
                doc.setAnimationType(Enum.<AnimationType> valueOf(AnimationType.class, atts.getValue(ANIMATIONTYPE)));
                doc.setOutOfBoundsColor(Enum.<OutOfBoundsColor> valueOf(OutOfBoundsColor.class, atts.getValue(OUTOFBOUNDSCOLOR)));
                doc.setTweenCount(Integer.parseInt(atts.getValue(TWEENCOUNT)));
                doc.setTweenStyle(Enum.<TweenStyle> valueOf(TweenStyle.class, atts.getValue(TWEENSTYLE)));
            } else if (COMBINEDPATCH.equals(localName)) {
                cPatch = new CombinedPatch(doc.getOrder(), false);
            } else if (PATCH.equals(localName)) {
                patchCoords = new PatchCoords(doc.getOrder());
                patchColor = Enum.<PatchColor> valueOf(PatchColor.class, atts.getValue(COLOR));
            } else if (COORDINATE.equals(localName)) {
                Coordinate c = new Coordinate(Double.parseDouble(atts.getValue(X)), Double.parseDouble(atts.getValue(Y)),
                        Double.parseDouble(atts.getValue(COLOR)));
                int order = doc.getOrder();
                patchCoords.setCoordinate(coordIndex % order, coordIndex / order, c);
                coordIndex++;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (COMBINEDPATCH.equals(localName)) {
                doc.getPatches().add(cPatch);
                cPatch = null;
            } else if (PATCH.equals(localName)) {
                cPatch.setPatch(patchColor, patchCoords);
                patchColor = null;
                patchCoords = null;
                coordIndex = 0;
            }
        }

        public PatchAnimDocument getDocument() {
            return doc;
        }
    }
}
