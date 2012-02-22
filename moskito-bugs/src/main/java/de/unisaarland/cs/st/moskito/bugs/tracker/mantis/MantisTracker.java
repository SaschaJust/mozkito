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

import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.RawReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MantisTracker extends Tracker {
	
	/**
	 * 
	 */
	public MantisTracker() {
		// TODO Auto-generated constructor stub
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
		
		throw new UnrecoverableError();
	}
	
	//
	// /*
	// * (non-Javadoc)
	// * @see
	// de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#parse(de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport)
	// */
	// @Override
	// public Report parse(final XmlReport xmlReport) {
	// xmlReport.getDocument().getRootElement();
	// final Report bugReport = new Report(xmlReport.getId());
	// bugReport.setLastFetch(xmlReport.getFetchTime());
	// bugReport.setHash(xmlReport.getMd5());
	//
	// // parse
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
			return new MantisParser();
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
