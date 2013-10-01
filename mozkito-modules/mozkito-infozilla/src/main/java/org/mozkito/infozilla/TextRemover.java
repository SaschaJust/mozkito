/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.infozilla;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import jregex.REFlags;
import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTime;

import org.mozkito.persons.model.Person;
import org.mozkito.utilities.io.FileUtils;

/**
 * The Class TextRemover.
 */
public class TextRemover {
	
	/** The text. */
	private final String            text;
	
	/** The regions. */
	private final SortedSet<Region> regions = new TreeSet<>();
	
	/** The author. */
	private final Person            author;
	
	/** The timestamp. */
	private final DateTime          timestamp;
	
	/**
	 * Instantiates a new text remover.
	 * 
	 * @param text
	 *            the text
	 * @param author
	 *            the author
	 * @param timestamp
	 *            the timestamp
	 */
	public TextRemover(final String text, final Person author, final DateTime timestamp) {
		PRECONDITIONS: {
			if (text == null) {
				throw new NullPointerException();
			}
			if (author == null) {
				throw new NullPointerException();
			}
			if (timestamp == null) {
				throw new NullPointerException();
			}
		}
		
		this.text = text;
		this.author = author;
		this.timestamp = timestamp;
	}
	
	/**
	 * Block.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return true, if successful
	 */
	public boolean block(final int from,
	                     final int to) {
		PRECONDITIONS: {
			assert this.text != null;
			
			if (from < 0) {
				throw new IllegalArgumentException();
			}
			if (to <= 0) {
				throw new IllegalArgumentException();
			}
			if (to >= this.text.length()) {
				;
			}
		}
		
		Region newRegion = null;
		
		try {
			newRegion = new Region(from, to);
			final List<Region> replaceRegions = new LinkedList<>();
			
			REGIONS: for (final Region region : this.regions) {
				if (newRegion.overlaps(region)) {
					replaceRegions.add(region);
				}
			}
			
			for (final Region region : replaceRegions) {
				newRegion = newRegion.merge(region);
				this.regions.remove(region);
			}
			
			return this.regions.add(newRegion);
		} finally {
			assert newRegion != null;
			assert newRegion.getFrom() <= from;
			assert newRegion.getTo() >= to;
		}
	}
	
	/**
	 * Gets the author.
	 * 
	 * @return the author
	 */
	public Person getAuthor() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.author;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	public String getText() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.text;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the timestamp.
	 * 
	 * @return the timestamp
	 */
	public DateTime getTimestamp() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.timestamp;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Prepare.
	 * 
	 * @return the string
	 */
	public String prepare() {
		SANITY: {
			assert this.text != null;
			assert this.regions != null;
		}
		
		if (!this.regions.isEmpty()) {
			final StringBuilder builder = new StringBuilder();
			int position = 0;
			Region current = null;
			
			for (final Region region : this.regions) {
				assert region.getFrom() >= 0;
				assert region.getTo() > 0;
				assert region.getFrom() < this.text.length();
				assert region.getTo() <= this.text.length();
				
				builder.append(this.text.substring(position, region.getFrom()));
				
				for (int i = region.getFrom(); i < (region.getTo() - FileUtils.lineSeparator.length()); ++i) {
					builder.append(' ');
				}
				
				builder.append(FileUtils.lineSeparator);
				position = region.getTo();
				current = region;
			}
			
			if (current != null) {
				builder.append(this.text.substring(current.getTo()));
			}
			
			return builder.toString();
		} else {
			return this.text;
		}
	}
	
	/**
	 * Strip.
	 * 
	 * @return the string
	 */
	public String strip() {
		SANITY: {
			assert this.text != null;
			assert this.regions != null;
		}
		
		final Regex regex = new Regex("\\n\\s*\\n", REFlags.DOTALL | REFlags.MULTILINE);
		
		if (!this.regions.isEmpty()) {
			final StringBuilder builder = new StringBuilder();
			int position = 0;
			Region current = null;
			
			for (final Region region : this.regions) {
				assert region.getFrom() >= 0;
				assert region.getTo() > 0;
				assert region.getFrom() < this.text.length();
				assert region.getTo() <= this.text.length();
				
				builder.append(this.text.substring(position, region.getFrom()));
				position = region.getTo();
				current = region;
			}
			
			if (current != null) {
				builder.append(this.text.substring(current.getTo()));
			}
			final String string = regex.removeAll(builder.toString());
			
			return string;
		} else {
			return this.text;
		}
	}
}
