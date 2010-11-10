/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.joda.time.DateTime;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.reposuite.bugs.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

/**
 * The Class JiraTracker.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class JiraTracker extends Tracker {
	
	private File overalXML;
	
	private static Regex doesNotExistRegex = new Regex(
	"<title>Issue\\s+Does\\s+Not\\s+Exist\\s+-\\s+jira.codehaus.org\\s+</title>");
	
	private static Regex errorRegex = new Regex(
	"<title>\\s+Oops\\s+-\\s+an\\s+error\\s+has\\s+occurred\\s+</title>");
	
	private static Regex digitRegex        = new Regex("^({bugid}\\d+).*");
	
	
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#checkRAW(java.lang
	 * .String)
	 */
	@Override
	public boolean checkRAW(final RawReport rawReport) {
		if(!super.checkRAW(rawReport)){
			return false;
		}
		if (doesNotExistRegex.matches(rawReport.getContent())) {
			return false;
		}
		if (errorRegex.matches(rawReport.getContent())) {
			return false;
		}
		return true;
	}
	
	@Override
	public XmlReport createDocument(final RawReport rawReport) {
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
	
	
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#fetchSource(java.
	 * net.URI)
	 */
	@Override
	public RawReport fetchSource(final URI uri) throws UnsupportedProtocolException {
		if (this.overalXML == null) {
			// fetch source from net
			return super.fetchSource(uri);
		} else {
			// fetch source from local file
			Long idToFetch = this.reverseURI(uri);
			if (idToFetch == null) {
				return super.fetchSource(uri);
			}
			XMLReader parser;
			try {
				parser = XMLReaderFactory.createXMLReader();
				SubReportExtractor handler = new SubReportExtractor(idToFetch);
				parser.setContentHandler(handler);
				InputSource inputSource = new InputSource(new FileInputStream(this.overalXML));
				parser.parse(inputSource);
				String result = handler.getReportString();
				if (result == null) {
					return super.fetchSource(uri);
				}
				try {
					MessageDigest md = MessageDigest.getInstance("MD5");
					return new RawReport(idToFetch, md.digest(result.getBytes()), new DateTime(), "xhtml", result);
				} catch (NoSuchAlgorithmException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				}
				return null;
				
			} catch (SAXException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return super.fetchSource(uri);
			} catch (FileNotFoundException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return super.fetchSource(uri);
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return super.fetchSource(uri);
			}
		}
	}
	
	@Override
	public Report parse(final XmlReport rawReport) {
		Condition.notNull(rawReport);
		Report bugReport = new Report();
		Element itemElement = rawReport.getDocument().getRootElement().getChild("channel").getChild("item");
		JiraXMLHandler.handleRoot(bugReport, itemElement, this.personManager);
		bugReport.setLastFetch(rawReport.getFetchTime());
		return bugReport;
	}
	
	private Long reverseURI(final URI uri) {
		int index = this.fetchURI.toString().indexOf(Tracker.bugIdPlaceholder);
		String subURI = uri.toString().substring(index, uri.toString().length());
		List<RegexGroup> groups = digitRegex.find(subURI);
		if (groups.size() < 1) {
			return null;
		}
		try {
			return new Long(groups.get(0).getMatch());
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/*
	 * The given uri can either point to an overall XML or an pattern string
	 * that contains one or multiple {@link
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#bugIdPlaceholder}
	 * that will be replaced by a bug id while fetching bug reports. If the
	 * string contains no such place holder, the uri will be considered to point
	 * to an overall XML file
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#setup(java.net.URI,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#setup(java.net.URI,
	 * java.net.URI, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public void setup(final URI fetchURI, final URI overviewURI, final String pattern, final String username,
			final String password, final Long startAt, final Long stopAt) throws InvalidParameterException {
		super.setup(fetchURI, overviewURI, pattern, username, password, startAt, stopAt);
		
		Condition.notNull(stopAt, "stopAt cannot be null");
		Condition.check(stopAt.longValue() >= 0l, "stopAt value must be larger or equal than zero");
		
		if (startAt == null) {
			this.startAt = 1l;
		}
		
		if (overviewURI != null) {
			if (Logger.logDebug()) {
				Logger.debug("Reading overview XML to extract possible report IDs ... ");
			}
			try {
				RawReport rawReport = this.fetchSource(overviewURI);
				if (rawReport == null) {
					if (Logger.logError()) {
						Logger.error("Could not fetch overview URL.");
					}
					return;
				}
				if (!rawReport.getFormat().equals("XHTML")) {
					if (Logger.logError()) {
						Logger.error("Expected overall Jira bug file in XML format. Got format: "
								+ rawReport.getFormat());
					}
					return;
				}
				this.overalXML = FileUtils.createRandomFile();
				FileOutputStream writer = new FileOutputStream(this.overalXML);
				writer.write(rawReport.getContent().getBytes());
				writer.flush();
				writer.close();
				
				// Parse all bug report IDs
				XMLReader parser = XMLReaderFactory.createXMLReader();
				JiraIDExtractor handler = new JiraIDExtractor();
				parser.setContentHandler(handler);
				InputSource inputSource = new InputSource(new FileInputStream(this.overalXML));
				parser.parse(inputSource);
				for (Long id : handler.getIds()) {
					addBugId(id);
				}
			} catch (Exception e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				if (Logger.logDebug()) {
					Logger.debug("failed!");
				}
				return;
			}
		} else {
			for (long i = this.startAt; i <= this.stopAt; ++i) {
				addBugId(i);
			}
		}
		this.initialized = true;
		if (Logger.logDebug()) {
			Logger.debug("done");
		}
	}
	
}
