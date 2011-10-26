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
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.joda.time.DateTime;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;

/**
 * The Class JiraTracker.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class JiraTracker extends Tracker {
	
	protected static String getHistoryURL(final URI uri) {
		String xmlUrl = uri.toString();
		int index = xmlUrl.lastIndexOf("/");
		String suffix = xmlUrl.substring(index, xmlUrl.length());
		String historyUrl = xmlUrl.replace("si/jira.issueviews:issue-xml/", "browse/");
		return historyUrl.replace(suffix,
		        "?page=com.atlassian.jira.plugin.system.issuetabpanels:changehistory-tabpanel#issue-tabs");
	}
	
	private File         overalXML;
	
	private static Regex doesNotExistRegex = new Regex(
	                                               "<title>Issue\\s+Does\\s+Not\\s+Exist\\s+-\\s+jira.codehaus.org\\s+</title>");
	
	private static Regex errorRegex        = new Regex(
	                                               "<title>\\s+Oops\\s+-\\s+an\\s+error\\s+has\\s+occurred\\s+</title>");
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#checkRAW(java.lang
	 * .String)
	 */
	@Override
	@NoneNull
	public boolean checkRAW(final RawReport rawReport) {
		if (!super.checkRAW(rawReport)) {
			return false;
		}
		if (doesNotExistRegex.matches(rawReport.getContent())) {
			return false;
		}
		if (errorRegex.matches(rawReport.getContent())) {
			return false;
		}
		if (!(rawReport.getUri().toString().endsWith(".xml") || (rawReport.getUri().toString().endsWith(".xhtml")))) {
			if (Logger.logWarn()) {
				Logger.warn("The Jira rawRepo uri is not an xml file.");
			}
			return false;
		}
		return true;
	}
	
	@Override
	public boolean checkXML(final XmlReport xmlReport) {
		if (!super.checkXML(xmlReport)) {
			return false;
		}
		
		Element rootElement = xmlReport.getDocument().getRootElement();
		
		if (rootElement == null) {
			if (Logger.logError()) {
				Logger.error("Root element is <null>");
			}
			return false;
		}
		
		if (!rootElement.getName().equals("rss")) {
			if (Logger.logError()) {
				Logger.error("Name of root element is not `rss`");
			}
			return false;
		}
		
		@SuppressWarnings("rawtypes") List children = rootElement.getChildren("channel", rootElement.getNamespace());
		
		if (children == null) {
			if (Logger.logError()) {
				Logger.error("No `channel` children.");
			}
			return false;
		}
		
		if (children.size() != 1) {
			if (Logger.logError()) {
				Logger.error("No `channel` children.");
			}
			return false;
		}
		
		@SuppressWarnings("unchecked") List<Element> items = rootElement.getChildren("channel",
		        rootElement.getNamespace());
		if (items.get(0).getChildren("item", rootElement.getNamespace()).size() != 1) {
			if (Logger.logError()) {
				Logger.error("No `item` children.");
			}
			return false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#createDocument(de
	 * .unisaarland.cs.st.reposuite.bugs.tracker.RawReport)
	 */
	@Override
	@NoneNull
	public XmlReport createDocument(final RawReport rawReport) {
		
		BufferedReader reader = new BufferedReader(new StringReader(rawReport.getContent()));
		try {
			SAXBuilder saxBuilder = new SAXBuilder("org.ccil.cowan.tagsoup.Parser");
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
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#fetchSource(java.
	 * net.URI)
	 */
	@Override
	@NoneNull
	public RawReport fetchSource(final URI uri) throws FetchException, UnsupportedProtocolException {
		Condition.check(!uri.toString().contains(Tracker.bugIdPlaceholder), "URI must contain bugIdPlaceHolder");
		Condition.check(!uri.toString().endsWith(FileUtils.fileSeparator), "file should not end with "
		        + FileUtils.fileSeparator);
		if (overalXML == null) {
			// fetch source from net
			
			if (Logger.logInfo()) {
				Logger.info("Fetching report `" + uri.toString() + "` from net ... ");
			}
			RawReport source = super.fetchSource(uri);
			
			if (Logger.logInfo()) {
				Logger.info("done");
			}
			return source;
		} else {
			// fetch source from local file
			// FIXME this fails because elements do not start with RSS tag
			if (Logger.logInfo()) {
				Logger.info("Fetching report `" + uri.toString() + "` from local overview xml file ... ");
			}
			
			Long idToFetch = reverseURI(uri);
			if (idToFetch == null) {
				return super.fetchSource(uri);
			}
			try {
				SAXBuilder parser = new SAXBuilder("org.ccil.cowan.tagsoup.Parser");
				Document document = parser.build(overalXML);
				Element element = SubReportExtractor.extract(document.getRootElement(), idToFetch);
				Element rss = new Element("rss", element.getNamespace());
				rss.setAttribute("version", "0.92");
				Element channel = new Element("channel", element.getNamespace());
				channel.addContent(element);
				rss.addContent(channel);
				document = new Document(rss);
				MessageDigest md = MessageDigest.getInstance("MD5");
				XMLOutputter outputter = new XMLOutputter();
				StringWriter sw = new StringWriter();
				outputter.output(document, sw);
				
				if (Logger.logInfo()) {
					Logger.info("done");
				}
				
				return new RawReport(idToFetch, new RawContent(uri, md.digest(sw.getBuffer().toString().getBytes()),
				        new DateTime(), "xhtml", sw.getBuffer().toString()));
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				if (Logger.logInfo()) {
					Logger.info("failed");
				}
				return null;
			} catch (JDOMException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				if (Logger.logInfo()) {
					Logger.info("failed");
				}
				return null;
			} catch (NoSuchAlgorithmException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				if (Logger.logInfo()) {
					Logger.info("failed");
				}
				return null;
			}
		}
	}
	
	@NoneNull
	protected Element getRootElement(final XmlReport rawReport) {
		Element itemElement = rawReport.getDocument().getRootElement();
		itemElement = itemElement.getChild("channel", itemElement.getNamespace());
		return itemElement.getChild("item", itemElement.getNamespace());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#parse(de.unisaarland
	 * .cs.st.reposuite.bugs.tracker.XmlReport)
	 */
	@Override
	@NoneNull
	public Report parse(final XmlReport rawReport) {
		
		if (Logger.logInfo()) {
			Logger.info("Parsing report with id `" + rawReport.getId() + "` ... ");
		}
		
		Report bugReport = new Report(rawReport.getId());
		Element itemElement = getRootElement(rawReport);
		JiraXMLParser.handleRoot(bugReport, itemElement, this);
		bugReport.setLastFetch(rawReport.getFetchTime());
		bugReport.setHash(rawReport.getMd5());
		
		// parse history
		String historyUrl = getHistoryURL(rawReport.getUri());
		if (historyUrl.equals(rawReport.getUri().toString())) {
			if (Logger.logWarn()) {
				Logger.warn("Could not fetch jira report history: could not create neccessary url.");
			}
		} else {
			try {
				URI historyUri = new URI(historyUrl);
				JiraXMLParser.handleHistory(historyUri, bugReport);
			} catch (Exception e) {
				if (Logger.logError()) {
					if (bugReport.getId() == -1) {
						Logger.error("Could not fetch bug history for bugReport. Used uri =`" + historyUrl + "`.");
					} else {
						Logger.error("Could not fetch bug history for bugReport `" + bugReport.getId()
						        + "`. Used uri =`" + historyUrl + "`.");
					}
					Logger.error(e.getMessage(), e);
				}
			}
		}
		if (Logger.logInfo()) {
			Logger.info("done");
		}
		
		return bugReport;
	}
	
	/*
	 * The given uri can either point to an overall XML or an pattern string
	 * that contains one or multiple {@link
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#bugIdPlaceholder}
	 * that will be replaced by a bug id while fetching bug reports. If the
	 * string contains no such place holder, the uri will be considered to point
	 * to an overall XML file
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#setup(java.net.URI,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#setup(java.net.URI,
	 * java.net.URI, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public void setup(final URI fetchURI, final URI overviewURI, final String pattern, final String username,
	        final String password, final Long startAt, final Long stopAt, final String cacheDirPath)
	        throws InvalidParameterException {
		super.setup(fetchURI, overviewURI, pattern, username, password, startAt, stopAt, cacheDirPath);
		
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
				RawContent rawContent = IOUtils.fetch(overviewURI);
				if (rawContent == null) {
					if (Logger.logError()) {
						Logger.error("Could not fetch overview URL.");
					}
					return;
				}
				if (!rawContent.getFormat().toLowerCase().equals("xhtml")) {
					if (Logger.logError()) {
						Logger.error("Expected overall Jira bug file in XML format. Got format: "
						        + rawContent.getFormat());
					}
					return;
				}
				overalXML = FileUtils.createRandomFile(FileShutdownAction.DELETE);
				FileOutputStream writer = new FileOutputStream(overalXML);
				writer.write(rawContent.getContent().getBytes());
				writer.flush();
				writer.close();
				
				// Parse all bug report IDs
				XMLReader parser = XMLReaderFactory.createXMLReader();
				JiraIDExtractor handler = new JiraIDExtractor();
				parser.setContentHandler(handler);
				InputSource inputSource = new InputSource(new FileInputStream(overalXML));
				parser.parse(inputSource);
				for (Long id : handler.getIds()) {
					if ((id <= this.stopAt) && (id >= this.startAt)) {
						addBugId(id);
					}
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
		initialized = true;
		if (Logger.logDebug()) {
			Logger.debug("done");
		}
	}
	
}
