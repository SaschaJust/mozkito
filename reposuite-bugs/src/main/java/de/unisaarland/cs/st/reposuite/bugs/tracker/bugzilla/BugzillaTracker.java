/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.bugzilla;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.List;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class BugzillaTracker extends Tracker {
	
	
	@Override
	public boolean checkRAW(final RawReport rawReport) {
		if (!super.checkRAW(rawReport)) {
			return false;
		}
		if (rawReport.getContent().contains("<bug error=\"NotFound\">")) {
			return false;
		}
		Regex regex = new Regex("<head>\\s*<title>Format Not Found</title>");
		if (regex.matches(rawReport.getContent())) {
			return false;
		}
		return false;
	}
	
	@Override
	public boolean checkXML(final XmlReport xmlReport) {
		if (!super.checkXML(xmlReport)) {
			return false;
		}
		if (!xmlReport.getDocument().getRootElement().getName().equals("bugzilla")) {
			return false;
		}
		@SuppressWarnings ("unchecked") List<Element> bugs = xmlReport.getDocument().getRootElement()
		.getChildren("bug");
		if (bugs.size() != 1) {
			return false;
		}
		return true;
	}
	
	@Override
	public XmlReport createDocument(final RawReport rawReport) {
		Condition.notNull(rawReport);
		
		BufferedReader reader = new BufferedReader(new StringReader(rawReport.getContent()));
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(reader);
			reader.close();
			return new XmlReport(rawReport, document);
		} catch (TransformerFactoryConfigurationError e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (JDOMException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	@Override
	public Report parse(final XmlReport rawReport) {
		Condition.notNull(rawReport);
		
		Report bugReport = new Report();
		Element itemElement = rawReport.getDocument().getRootElement().getChild("bug");
		BugzillaXMLParser.handleRoot(bugReport, itemElement, this.personManager);
		bugReport.setLastFetch(rawReport.getFetchTime());
		bugReport.setHash(rawReport.getMd5());
		
		String uriString = rawReport.getUri().toString().replace("show_bug.cgi", "show_activity.cgi");
		if (uriString.equals(rawReport.getUri().toString())) {
			if (Logger.logWarn()) {
				Logger.warn("Could not fetch bugzilla report history: could not create neccessary url.");
			}
		} else {
			try {
				URI historyUri = new URI(uriString);
				BugzillaXMLParser.handleHistory(historyUri, bugReport, this.personManager);
			} catch (Exception e) {
				if (Logger.logError()) {
					if (bugReport.getId() == -1) {
						Logger.error("Could not fetch bug history for bugReport. Used uri =`" + uriString + "`.");
					} else {
						Logger.error("Could not fetch bug history for bugReport `" + bugReport.getId() + "`. Used uri =`"
								+ uriString + "`.");
					}
					Logger.error(e.getMessage(), e);
				}
			}
		}
		
		return bugReport;
	}
	
}
