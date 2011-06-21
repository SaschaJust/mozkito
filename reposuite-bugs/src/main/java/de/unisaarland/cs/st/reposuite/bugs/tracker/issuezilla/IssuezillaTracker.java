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
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.issuezilla;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.kisa.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;


/**
 * The Class IssuezillaTracker.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class IssuezillaTracker extends Tracker {
	
	
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#checkRAW(de.unisaarland
	 * .cs.st.reposuite.bugs.tracker.RawReport)
	 */
	@Override
	public boolean checkRAW(final RawReport rawReport) {
		return !rawReport.getContent().contains("<issue status_code=\"404\" status_message=\"NotFound\">");
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#createDocument(de
	 * .unisaarland.cs.st.reposuite.bugs.tracker.RawReport)
	 */
	@Override
	public XmlReport createDocument(final RawReport rawReport) {
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
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#parse(de.unisaarland
	 * .cs.st.reposuite.bugs.tracker.XmlReport)
	 */
	@Override
	public Report parse(final XmlReport rawReport) {
		Report bugReport = new Report(rawReport.getId());
		
		Element itemElement = rawReport.getDocument().getRootElement().getChild("issue");
		
		IssuezillaXMLParser.handleRoot(bugReport, itemElement, this, rawReport.getUri());
		bugReport.setLastFetch(rawReport.getFetchTime());
		bugReport.setHash(rawReport.getMd5());
		
		return bugReport;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#setup(java.net.URI,
	 * java.net.URI, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Long, java.lang.Long, java.lang.String)
	 */
	@Override
	public void setup(final URI fetchURI,
	                  final URI overviewURI,
	                  final String pattern,
	                  final String username,
	                  final String password,
	                  final Long startAt,
	                  final Long stopAt,
	                  final String cacheDirPath) throws InvalidParameterException {
		
		super.setup(fetchURI, overviewURI, pattern, username, password, startAt, stopAt, cacheDirPath);
		// TODO authentication not supported. For that we would have to use
		// the API but the examples don;t even work. -.-
		
		// TODO just iterating over all possible IDs. This is not clever but so
		// far I did not found a clever solution
		for (long i = startAt; i <= stopAt; ++i) {
			super.addBugId(i);
		}
	}
}
