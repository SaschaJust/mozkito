/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.infozilla.model.itemization;

import java.util.ArrayList;
import java.util.List;

import de.unisaarland.cs.st.reposuite.infozilla.model.Inlineable;

/**
 * This class encapsulates an Itemization as created by FilterEnumerations filter.
 * @author Nicolas Bettenburg
 *
 */
public class Itemization implements Inlineable {
	
	private List<String> enumeration_items;
	private int          start;
	private int          end;
	private int          enumStart;
	private int          enumEnd;
	
	public Itemization() {
		this.enumeration_items = new ArrayList<String>();
	}
	
	public Itemization(final List<String> items, final int start, final int end) {
		this.start = start;
		this.end = end;
		this.enumeration_items = items;
	}
	
	public int getEndPosition() {
		return this.end;
	}
	
	public int getEnumEnd() {
		return this.enumEnd;
	}
	
	public List<String> getEnumeration_items() {
		return this.enumeration_items;
	}
	
	public int getEnumStart() {
		return this.enumStart;
	}
	
	public int getStartPosition() {
		return this.start;
	}
	
	public void setEnd(final int end) {
		this.end = end;
	}
	
	public void setEnumEnd(final int enumEnd) {
		this.enumEnd = enumEnd;
	}
	
	public void setEnumeration_items(final List<String> enumeration_items) {
		this.enumeration_items = enumeration_items;
	}
	
	public void setEnumStart(final int enumStart) {
		this.enumStart = enumStart;
	}
	
	public void setStart(final int start) {
		this.start = start;
	}
	
}
