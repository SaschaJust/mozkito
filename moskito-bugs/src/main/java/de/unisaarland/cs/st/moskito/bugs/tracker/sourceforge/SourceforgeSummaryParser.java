/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.sourceforge;

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
