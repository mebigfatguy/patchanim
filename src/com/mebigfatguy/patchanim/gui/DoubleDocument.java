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
package com.mebigfatguy.patchanim.gui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * a text component document that enforces that only double values are entered
 */
public class DoubleDocument extends PlainDocument
{
	private static final long serialVersionUID = 2390424803444456428L;
	
	private static final Pattern DOUBLEPATTERN = Pattern.compile("-?[0-9]*(\\.[0-9]*)?");
    
	/**
	 * check input for only double valued characters
	 * 
	 * @param offs the offset where insert occurs
	 * @param str the inserted text
	 * @param a the attributes for the inserted text
	 */
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        String start = getText(0, offs);
        String end = getText(offs, this.getLength() - offs);
        String newIntegerString = (start + str + end).trim();
        
        Matcher m = DOUBLEPATTERN.matcher(newIntegerString);
        if (m.matches()) {
            super.insertString(offs, str, a);
        }
    }
}