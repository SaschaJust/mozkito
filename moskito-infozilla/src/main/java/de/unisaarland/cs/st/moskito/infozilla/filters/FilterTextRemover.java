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
/**
 * 
 * FilterTextRemover.java
 * 
 * @author Nicolas Bettenburg � 2009-2010, all rights reserved.
 ******************************************************************** 
 *         This file is part of infoZilla. * * InfoZilla is non-free software: you may not redistribute it * and/or
 *         modify it without the permission of the original author. * * InfoZilla is distributed in the hope that it
 *         will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS
 *         FOR A PARTICULAR PURPOSE. *
 ******************************************************************** 
 * 
 */

package de.unisaarland.cs.st.moskito.infozilla.filters;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>FilterTextRemover</code> class is used to cut out the structural element from the complete input text.
 * Given a list of (possibly overlapping) cut points, the class calculates the longest consecutive parts and marks them
 * for deletion through a character-wise bit mask.
 * 
 * @author Nicolas Bettenburg.
 * 
 */
public class FilterTextRemover {
	
	private class CutPoint {
		
		private int start;
		private int end;
		
		public CutPoint(final int start, final int end) {
			super();
			this.setStart(start);
			this.setEnd(end);
		}
		
		/**
		 * @return the end
		 */
		@SuppressWarnings ("unused")
		public int getEnd() {
			return this.end;
		}
		
		/**
		 * @return the start
		 */
		@SuppressWarnings ("unused")
		public int getStart() {
			return this.start;
		}
		
		/**
		 * @param end
		 *            the end to set
		 */
		public void setEnd(final int end) {
			this.end = end;
		}
		
		/**
		 * @param start
		 *            the start to set
		 */
		public void setStart(final int start) {
			this.start = start;
		}
	}
	
	private String               originalText = "";
	private final boolean[]      deletionMask;
	private final List<CutPoint> cutPoints;
	
	public FilterTextRemover(final String originalText) {
		// Set the Text
		this.originalText = originalText;
		
		// Create a deletion mask of appropriate size
		this.deletionMask = new boolean[originalText.length()];
		
		// Initialize the Deletion Mask with false (=do not delete)
		for (int i = 0; i < this.deletionMask.length; i++) {
			this.deletionMask[i] = false;
		}
		
		this.cutPoints = new ArrayList<CutPoint>();
	}
	
	public String doDelete() {
		StringBuilder myStringBuilder = new StringBuilder();
		
		for (int i = 0; i < this.originalText.length(); i++) {
			if (!this.deletionMask[i]) {
				myStringBuilder.append(this.originalText.charAt(i));
			}
		}
		return myStringBuilder.toString();
	}
	
	public String getText() {
		return this.originalText;
	}
	
	public void markForDeletion(final int start,
	                            final int end) {
		this.cutPoints.add(new CutPoint(start, end));
		if ((start >= 0) && (end <= this.deletionMask.length)) {
			for (int i = start; i < end; i++) {
				this.deletionMask[i] = true;
			}
		} else {
			System.err.println("Warning! Trying to Delete out of Bounds: " + start + " until " + end
			        + " but bounds are 0:" + this.deletionMask.length);
			System.err.println("Will not mark for deletion!");
		}
	}
	
}
