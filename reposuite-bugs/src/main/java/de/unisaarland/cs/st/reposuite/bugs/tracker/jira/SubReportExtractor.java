package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

public class SubReportExtractor extends DefaultHandler {
	
	private final StringBuffer content = new StringBuffer();
	private final StringBuffer titleContent = new StringBuffer();
	private boolean            inItem  = false;
	private Long               id;
	private final Long         expectedId;
	private String             reportString = "";
	
	SubReportExtractor(final Long expectedId) {
		this.expectedId = expectedId;
	}
	
	@Override
	public void characters(final char[] ch, final int start, final int length) {
		if (this.content != null) {
			this.content.append(ch, start, length);
		}
		if (this.titleContent != null) {
			this.titleContent.append(ch, start, length);
		}
	}
	
	@Override
	public void endElement(final String uri, final String localName, final String qName) {
		if (localName.equals("item")) {
			this.inItem = false;
			if (this.id.equals(this.expectedId)) {
				this.reportString = this.content.toString();
			} else {
				this.content.setLength(0);
			}
		} else if (localName.equals("title") && this.inItem) {
			List<RegexGroup> groups = JiraIDExtractor.idRegex.find(this.content.toString().trim());
			if ((groups.size() < 1) && (groups.get(0).getText() == null)) {
				if (Logger.logError()) {
					Logger.error("Error while parsing Jira overview XMl. Unknown title format found!");
				}
			} else {
				this.id = new Long(groups.get(0).getMatch());
			}
		}
		this.titleContent.setLength(0);
	}
	
	public String getReportString() {
		return this.reportString;
	}
	
	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
	throws SAXException {
		if (localName.equals("item")) {
			this.inItem = true;
		}
	}
	
}
