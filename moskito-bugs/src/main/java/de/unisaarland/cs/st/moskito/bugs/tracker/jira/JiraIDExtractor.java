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
package de.unisaarland.cs.st.moskito.bugs.tracker.jira;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class JiraIDExtractor extends DefaultHandler {
	
	private boolean            inItem  = false;
	private final StringBuffer content = new StringBuffer();
	private final List<Long>   ids     = new LinkedList<Long>();
	protected static Regex     idRegex = new Regex("^\\[[^\\]]+-({bugid}\\d+)\\]");
	
	@Override
	public void characters(final char[] ch,
	                       final int start,
	                       final int length) {
		if (this.content != null) {
			this.content.append(ch, start, length);
		}
	}
	
	@Override
	public void endElement(final String uri,
	                       final String localName,
	                       final String qName) {
		if (localName.equals("item")) {
			this.inItem = false;
		} else if (localName.equals("title") && this.inItem) {
			List<RegexGroup> groups = idRegex.find(this.content.toString().trim());
			if ((groups.size() < 2) && (groups.get(1).getText() == null)) {
				if (Logger.logError()) {
					Logger.error("Error while parsing Jira overview XMl. Unknown title format found!");
				}
			} else {
				this.ids.add(new Long(groups.get(1).getMatch()));
			}
		}
		this.content.setLength(0);
	}
	
	public List<Long> getIds() {
		return this.ids;
	}
	
	@Override
	public void startElement(final String uri,
	                         final String localName,
	                         final String qName,
	                         final Attributes attributes) throws SAXException {
		if (localName.equals("item")) {
			this.inItem = true;
		}
	}
}
