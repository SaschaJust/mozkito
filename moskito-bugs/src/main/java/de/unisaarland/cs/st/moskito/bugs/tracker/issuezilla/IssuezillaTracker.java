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
/**
 * 
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.issuezilla;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.kisa.Logger;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.RawReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;

/**
 * The Class IssuezillaTracker.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class IssuezillaTracker extends Tracker {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#checkRAW(de.unisaarland
	 * .cs.st.reposuite.bugs.tracker.RawReport)
	 */
	@Override
	public boolean checkRAW(final RawReport rawReport) {
		return !rawReport.getContent().contains("<issue status_code=\"404\" status_message=\"NotFound\">");
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#createDocument(de
	 * .unisaarland.cs.st.reposuite.bugs.tracker.RawReport)
	 */
	@Override
	public XmlReport createDocument(final RawReport rawReport) {
		final BufferedReader reader = new BufferedReader(new StringReader(rawReport.getContent()));
		try {
			final SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			final Document document = saxBuilder.build(reader);
			reader.close();
			return new XmlReport(rawReport, document);
		} catch (final TransformerFactoryConfigurationError e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final JDOMException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	// /*
	// * (non-Javadoc)
	// * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#parse(de.unisaarland
	// * .cs.st.reposuite.bugs.tracker.XmlReport)
	// */
	// @Override
	// public Report parse(final XmlReport rawReport) {
	// Report bugReport = new Report(rawReport.getId());
	//
	// Element itemElement = rawReport.getDocument().getRootElement().getChild("issue");
	//
	// IssuezillaXMLParser.handleRoot(bugReport, itemElement, this, rawReport.getUri());
	// bugReport.setLastFetch(rawReport.getFetchTime());
	// bugReport.setHash(rawReport.getMd5());
	//
	// return bugReport;
	// }
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser(final XmlReport xmlReport) {
		// PRECONDITIONS
		
		try {
			return new IssuezillaParser();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#setup(java.net.URI, java.net.URI, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.Long, java.lang.Long, java.lang.String)
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
