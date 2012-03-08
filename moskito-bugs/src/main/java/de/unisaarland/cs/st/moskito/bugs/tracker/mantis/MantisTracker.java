/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package de.unisaarland.cs.st.moskito.bugs.tracker.mantis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser;
import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.RawReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MantisTracker extends Tracker {
	
	// URL = https://issues.openbravo.com/print_bug_page.php?bug_id=19779
	
	/**
	 * 
	 */
	public MantisTracker() {
		
	}
	
	@Override
	public boolean checkRAW(final RawReport rawReport) {
		if (!super.checkRAW(rawReport)) {
			return false;
		}
		Regex regex = new Regex("Issue\\s+\\d+\\s+not\\s+found.");
		List<List<RegexGroup>> findAll = regex.findAll(rawReport.getContent());
		if (findAll != null) {
			if (Logger.logInfo()) {
				Logger.info("Ignoring report " + rawReport.getUri().toASCIIString()
				        + ". checkRaw() failed: issue seems not to exist.");
			}
			return false;
		}
		regex = new Regex("Access Denied.");
		findAll = regex.findAll(rawReport.getContent());
		if (findAll != null) {
			if (Logger.logInfo()) {
				Logger.info("Ignoring report " + rawReport.getUri().toASCIIString()
				        + ". checkRaw() failed: issue requires special permission.");
			}
			return false;
		}
		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#createDocument(de.unisaarland.cs.st.moskito.bugs.tracker.RawReport
	 * )
	 */
	@Override
	public XmlReport createDocument(final RawReport rawReport) {
		final BufferedReader reader = new BufferedReader(new StringReader(rawReport.getContent()));
		
		try {
			final SAXBuilder saxBuilder = new SAXBuilder("org.ccil.cowan.tagsoup.Parser");
			final Document document = saxBuilder.build(reader);
			reader.close();
			
			return new XmlReport(rawReport, document);
		} catch (final TransformerFactoryConfigurationError e) {
			if (Logger.logError()) {
				Logger.error("Cannot create XML document!", e);
			}
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error("Cannot create XML document!", e);
			}
		} catch (final JDOMException e) {
			if (Logger.logError()) {
				Logger.error("Cannot create XML document!", e);
			}
		}
		return null;
	}
	
	@Override
	public URI getLinkFromId(final Long bugId) {
		// PRECONDITIONS
		
		try {
			try {
				return new URI(Tracker.bugIdRegex.replaceAll(this.fetchURI.toString() + this.pattern, bugId + ""));
			} catch (final URISyntaxException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return null;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public OverviewParser getOverviewParser(final RawContent overviewContent) {
		// PRECONDITIONS
		
		try {
			if (Logger.logError()) {
				Logger.error("Overview parsing not supported yet.");
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser(final XmlReport xmlReport) {
		// PRECONDITIONS
		
		try {
			return new MantisParser();
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
