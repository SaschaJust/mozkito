/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.mozkito.infozilla.model.itemization;

import java.util.ArrayList;
import java.util.List;

import de.unisaarland.cs.st.mozkito.infozilla.model.Inlineable;

/**
 * This class encapsulates an Itemization as created by FilterEnumerations filter.
 * 
 * @author Nicolas Bettenburg
 * 
 */
public class Itemization implements Inlineable {
	
	/** The enumeration_items. */
	private List<String> enumeration_items;
	
	/** The start. */
	private int          start;
	
	/** The end. */
	private int          end;
	
	/** The enum start. */
	private int          enumStart;
	
	/** The enum end. */
	private int          enumEnd;
	
	/**
	 * Instantiates a new itemization.
	 */
	public Itemization() {
		this.enumeration_items = new ArrayList<String>();
	}
	
	/**
	 * Instantiates a new itemization.
	 * 
	 * @param items
	 *            the items
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public Itemization(final List<String> items, final int start, final int end) {
		this.start = start;
		this.end = end;
		this.enumeration_items = items;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.infozilla.model.Inlineable#getEndPosition()
	 */
	@Override
	public int getEndPosition() {
		return this.end;
	}
	
	/**
	 * Gets the enum end.
	 * 
	 * @return the enum end
	 */
	public int getEnumEnd() {
		return this.enumEnd;
	}
	
	/**
	 * Gets the enumeration_items.
	 * 
	 * @return the enumeration_items
	 */
	public List<String> getEnumeration_items() {
		return this.enumeration_items;
	}
	
	/**
	 * Gets the enum start.
	 * 
	 * @return the enum start
	 */
	public int getEnumStart() {
		return this.enumStart;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.infozilla.model.Inlineable#getStartPosition()
	 */
	@Override
	public int getStartPosition() {
		return this.start;
	}
	
	/**
	 * Sets the end.
	 * 
	 * @param end
	 *            the new end
	 */
	public void setEnd(final int end) {
		this.end = end;
	}
	
	/**
	 * Sets the enum end.
	 * 
	 * @param enumEnd
	 *            the new enum end
	 */
	public void setEnumEnd(final int enumEnd) {
		this.enumEnd = enumEnd;
	}
	
	/**
	 * Sets the enumeration_items.
	 * 
	 * @param enumeration_items
	 *            the new enumeration_items
	 */
	public void setEnumeration_items(final List<String> enumeration_items) {
		this.enumeration_items = enumeration_items;
	}
	
	/**
	 * Sets the enum start.
	 * 
	 * @param enumStart
	 *            the new enum start
	 */
	public void setEnumStart(final int enumStart) {
		this.enumStart = enumStart;
	}
	
	/**
	 * Sets the start.
	 * 
	 * @param start
	 *            the new start
	 */
	public void setStart(final int start) {
		this.start = start;
	}
	
}
