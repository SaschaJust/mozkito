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
package de.unisaarland.cs.st.reposuite.bugs.tracker.sourceforge;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class SourceforgeSummaryParser extends DefaultHandler {
	
	private static enum SummaryParserMode {
		DEFAULT, TABLE, TR, TD
	}
	
	private StringBuffer      content = null;
	private SummaryParserMode currentMode;
	
	private final HashSet<Long>     ids;            ;
	
	public SourceforgeSummaryParser() {
		this.content = new StringBuffer();
		this.currentMode = SummaryParserMode.DEFAULT;
		this.ids = new HashSet<Long>();
	}
	
	@Override
	public void characters(final char[] ch, final int start, final int length) {
		if (this.content != null) {
			this.content.append(ch, start, length);
		}
	}
	
	@Override
	public void endElement(final String uri, final String localName, final String qName) {
		
		if (localName.equals("tfoot")) {
			this.currentMode = SummaryParserMode.TABLE;
		} else if ((this.currentMode == SummaryParserMode.TD) && (localName.equals("td"))) {
			String idString = this.content.toString().trim();
			this.ids.add(new Long(idString));
			this.currentMode = SummaryParserMode.TABLE;
		}
	}
	
	public Set<Long> getIDs() {
		return this.ids;
	}
	
	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
	throws SAXException {
		
		if ((this.currentMode == SummaryParserMode.TABLE) && localName.equals("tr")) {
			this.currentMode = SummaryParserMode.TR;
		} else if ((this.currentMode == SummaryParserMode.TR) && localName.equals("td")) {
			this.currentMode = SummaryParserMode.TD;
			this.content.setLength(0);
		}
		
	}
	
}
