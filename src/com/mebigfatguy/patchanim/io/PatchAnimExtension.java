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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xalan.extensions.ExpressionContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mebigfatguy.patchanim.PatchAnimDocument;
import com.mebigfatguy.patchanim.PatchColor;
import com.mebigfatguy.patchanim.surface.Coordinate;

public class PatchAnimExtension {
	private static final String VERSION_URL = "/com/mebigfatguy/patchanim/io/Version.txt";
	private final PatchAnimDocument paDoc;
	private final Document d;
	
	public PatchAnimExtension(PatchAnimDocument doc) throws ParserConfigurationException {
		paDoc = doc;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		d = db.newDocument();
	}
	
	public String getVersion(ExpressionContext context) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(PatchAnimExtension.class.getResourceAsStream(VERSION_URL)));
			return br.readLine();
		} catch (IOException ioe) {
			return "0.0.0";
		} finally {
			Closer.close(br);
		}
	}
	
	public String getOrder(ExpressionContext context) {
		return String.valueOf(paDoc.getOrder());
	}
	
	public String getWidth(ExpressionContext context) {
		return String.valueOf(paDoc.getWidth());
	}
	
	public String getHeight(ExpressionContext context) {
		return String.valueOf(paDoc.getHeight());
	}
	
	public String getAnimationType(ExpressionContext context) {
		return paDoc.getAnimationType().name();
	}
	
	public String getOutOfBoundsColor(ExpressionContext context) {
		return paDoc.getOutOfBoundsColor().name();
	}

	public String getTweenCount(ExpressionContext context) {
		return String.valueOf(paDoc.getTweenCount());
	}
	
	public String getTweenStyle(ExpressionContext context) {
		return String.valueOf(paDoc.getTweenStyle());
	}

	public String useAlpha(ExpressionContext context) {
		return String.valueOf(paDoc.useAlpha());
	}
	
	public NodeList getPatches(ExpressionContext context) {
		return new NodeList() {

			public int getLength() {
				return paDoc.getPatches().size();
			}

			public Node item(int index) {
				return d.createTextNode(String.valueOf(index));
			}
		};
	}
	
	public String getPatchName(ExpressionContext context, Node patchIndexNode) {
		int patchIndex = Integer.parseInt(patchIndexNode.getNodeValue());
		return paDoc.getPatches().get(patchIndex).getName();
	}
	
	public NodeList getCoordinates(ExpressionContext context) {
		return new NodeList() {

			public int getLength() {
				int order = paDoc.getOrder();
				return order * order;
			}

			public Node item(int index) {
				return d.createTextNode(String.valueOf(index));
			}
		};
	}
	
	public String getX(ExpressionContext context, String color, Node patchIndexNode, Node coordIndexNode) {
		return String.valueOf(getCoordinate(color, patchIndexNode, coordIndexNode).getX());
	}
	
	public String getY(ExpressionContext context, String color, Node patchIndexNode, Node coordIndexNode) {
		return String.valueOf(getCoordinate(color, patchIndexNode, coordIndexNode).getY());
	}
	
	public String getColor(ExpressionContext context, String color, Node patchIndexNode, Node coordIndexNode) {
		return String.valueOf(getCoordinate(color, patchIndexNode, coordIndexNode).getColor());
	}
	
	private Coordinate getCoordinate(String color, Node patchIndexNode, Node coordIndexNode) {
		PatchColor patchColor = Enum.<PatchColor>valueOf(PatchColor.class, color);
		int patchIndex = Integer.parseInt(patchIndexNode.getNodeValue());
		int coordIndex = Integer.parseInt(coordIndexNode.getNodeValue());
		int order = paDoc.getOrder();
		
		return paDoc.getPatches().get(patchIndex).getPatch(patchColor).getCoordinate(coordIndex % order, coordIndex / order);
	}


}
