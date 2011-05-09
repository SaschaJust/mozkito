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

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class BugzillaTracker extends Tracker {
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#checkRAW(de.unisaarland
	 * .cs.st.reposuite.bugs.tracker.RawReport)
	 */
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
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#checkXML(de.unisaarland
	 * .cs.st.reposuite.bugs.tracker.XmlReport)
	 */
	@Override
	public boolean checkXML(final XmlReport xmlReport) {
		if (!super.checkXML(xmlReport)) {
			return false;
		}
		if (!xmlReport.getDocument().getRootElement().getName().equals("bugzilla")) {
			return false;
		}
		@SuppressWarnings ("unchecked")
		List<Element> bugs = xmlReport.getDocument().getRootElement().getChildren("bug");
		if (bugs.size() != 1) {
			return false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#createDocument(de
	 * .unisaarland.cs.st.reposuite.bugs.tracker.RawReport)
	 */
	@Override
	public XmlReport createDocument(@NotNull final RawReport rawReport) {
		BufferedReader reader = new BufferedReader(new StringReader(rawReport.getContent()));
		try {
			SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
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
	
	protected Element getRootElement(@NotNull final XmlReport rawReport) {
		return rawReport.getDocument().getRootElement().getChild("bug");
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#parse(de.unisaarland
	 * .cs.st.reposuite.bugs.tracker.XmlReport)
	 */
	@Override
	public Report parse(@NotNull final XmlReport rawReport) {
		Report bugReport = new Report(rawReport.getId());
		Element itemElement = getRootElement(rawReport);
		BugzillaXMLParser.handleRoot(bugReport, itemElement, this);
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
				BugzillaXMLParser.handleHistory(historyUri, bugReport);
			} catch (Exception e) {
				if (Logger.logError()) {
					if (bugReport.getId() == -1) {
						Logger.error("Could not fetch bug history for bugReport. Used uri =`" + uriString + "`.");
					} else {
						Logger.error("Could not fetch bug history for bugReport `" + bugReport.getId()
						        + "`. Used uri =`" + uriString + "`.");
					}
					Logger.error(e.getMessage(), e);
				}
			}
		}
		
		return bugReport;
	}
	
}
