package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;


public class JiraIDExtractor extends DefaultHandler {
	
	private boolean inItem = false;
	private final StringBuffer content = new StringBuffer();
	private final List<Long>   ids     = new LinkedList<Long>();
	protected static Regex     idRegex = new Regex("^\\[[^\\]]+-({bugid}\\d+)\\]");
	
	@Override
	public void characters(final char[] ch, final int start, final int length) {
		if (this.content != null) {
			this.content.append(ch, start, length);
		}
	}
	
	@Override
	public void endElement(final String uri, final String localName, final String qName) {
		if (localName.equals("item")) {
			this.inItem = false;
		} else if (localName.equals("title") && this.inItem) {
			List<RegexGroup> groups = idRegex.find(this.content.toString().trim());
			if ((groups.size() < 1) && (groups.get(0).getText() == null)) {
				if (Logger.logError()) {
					Logger.error("Error while parsing Jira overview XMl. Unknown title format found!");
				}
			}else{
				this.ids.add(new Long(groups.get(0).getMatch()));
			}
		}
		this.content.setLength(0);
	}
	
	public List<Long> getIds() {
		return this.ids;
	}
	
	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
		if (localName.equals("item")) {
			this.inItem = true;
		}
	}
}
