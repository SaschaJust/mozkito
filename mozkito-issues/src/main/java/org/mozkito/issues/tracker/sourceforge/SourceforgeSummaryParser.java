/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package org.mozkito.issues.tracker.sourceforge;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The Class SourceforgeSummaryParser.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class SourceforgeSummaryParser extends DefaultHandler {
	
	/**
	 * The Enum SummaryParserMode.
	 *
	 * @author Kim Herzig <herzig@cs.uni-saarland.de>
	 */
	private static enum SummaryParserMode {
		
		/** The DEFAULT. */
		DEFAULT, 
 /** The TABLE. */
 TABLE, 
 /** The TR. */
 TR, 
 /** The TD. */
 TD
	}
	
	/** The content. */
	private StringBuffer          content = null;
	
	/** The current mode. */
	private SummaryParserMode     currentMode;
	
	/** The ids. */
	private final HashSet<String> ids;            ;
	
	/**
	 * Instantiates a new sourceforge summary parser.
	 */
	public SourceforgeSummaryParser() {
		this.content = new StringBuffer();
		this.currentMode = SummaryParserMode.DEFAULT;
		this.ids = new HashSet<String>();
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(final char[] ch,
	                       final int start,
	                       final int length) {
		if (this.content != null) {
			this.content.append(ch, start, length);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(final String uri,
	                       final String localName,
	                       final String qName) {
		
		if (localName.equals("tfoot")) {
			this.currentMode = SummaryParserMode.TABLE;
		} else if ((this.currentMode == SummaryParserMode.TD) && (localName.equals("td"))) {
			final String idString = this.content.toString().trim();
			this.ids.add(idString);
			this.currentMode = SummaryParserMode.TABLE;
		}
	}
	
	/**
	 * Gets the i ds.
	 *
	 * @return the i ds
	 */
	public Set<String> getIDs() {
		return this.ids;
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(final String uri,
	                         final String localName,
	                         final String qName,
	                         final Attributes attributes) throws SAXException {
		
		if ((this.currentMode == SummaryParserMode.TABLE) && localName.equals("tr")) {
			this.currentMode = SummaryParserMode.TR;
		} else if ((this.currentMode == SummaryParserMode.TR) && localName.equals("td")) {
			this.currentMode = SummaryParserMode.TD;
			this.content.setLength(0);
		}
		
	}
	
}
