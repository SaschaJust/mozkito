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
package org.mozkito.issues.tracker.jira;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Group;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.Regex;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The Class JiraIDExtractor.
 */
public class JiraIDExtractor extends DefaultHandler {
	
	/** The in item. */
	private boolean            inItem  = false;
	
	/** The content. */
	private final StringBuffer content = new StringBuffer();
	
	/** The ids. */
	private final List<String> ids     = new LinkedList<String>();
	
	/** The id regex. */
	protected static Regex     idRegex = new Regex("^\\[({bugid}[^\\]]+-\\d+)\\]");
	
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
		if ("item".equals(localName)) {
			this.inItem = false;
		} else if ("title".equals(localName) && this.inItem) {
			final Match groups = JiraIDExtractor.idRegex.find(this.content.toString().trim());
			final Group group = groups.getGroup("bugid");
			if (group == null) {
				if (Logger.logError()) {
					Logger.error("Error while parsing Jira overview XMl. Unknown title format found!");
				}
			} else {
				this.ids.add(group.getMatch());
			}
		}
		this.content.setLength(0);
	}
	
	/**
	 * Gets the bug report IDs.
	 * 
	 * @return the ids
	 */
	public List<String> getIds() {
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
		if ("item".equals(localName)) {
			this.inItem = true;
		}
	}
}
