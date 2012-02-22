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
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.util.Collection;

import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import noNamespace.BugDocument.Bug;
import noNamespace.BugzillaDocument;
import noNamespace.BugzillaDocument.Bugzilla;

import org.apache.xmlbeans.XmlException;
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
public class BugzillaTracker extends Tracker {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#checkRAW(de.unisaarland
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
		final Regex regex = new Regex("<head>\\s*<title>Format Not Found</title>");
		if (regex.matches(rawReport.getContent())) {
			return false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#checkXML(de.unisaarland
	 * .cs.st.reposuite.bugs.tracker.XmlReport)
	 */
	@Override
	public boolean checkXML(final XmlReport xmlReport) {
		
		try {
			final BugzillaDocument bugzillaDocument = BugzillaDocument.Factory.parse(xmlReport.getContent());
			final Bugzilla bugzilla = bugzillaDocument.getBugzilla();
			final Bug[] bugArray = bugzilla.getBugArray();
			return bugArray.length == 1;
		} catch (final XmlException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#createDocument(de
	 * .unisaarland.cs.st.reposuite.bugs.tracker.RawReport)
	 */
	@Override
	public XmlReport createDocument(@NotNull final RawReport rawReport) {
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
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser(final XmlReport xmlReport) {
		// PRECONDITIONS
		
		try {
			
			// check for bugzilla version number
			try {
				final BugzillaDocument bugzillaDocument = BugzillaDocument.Factory.parse(xmlReport.getContent());
				final Bugzilla bugzilla = bugzillaDocument.getBugzilla();
				final String bugzillaVersion = bugzilla.getVersion().getStringValue();
				
				// load all BugzillaParsers
				try {
					final Collection<Class<? extends BugzillaParser>> parserClasses = ClassFinder.getClassesExtendingClass(BugzillaParser.class.getPackage(),
					                                                                                                       BugzillaParser.class,
					                                                                                                       Modifier.ABSTRACT
					                                                                                                               | Modifier.INTERFACE
					                                                                                                               | Modifier.PRIVATE);
					for (final Class<? extends BugzillaParser> parserClass : parserClasses) {
						if (!Modifier.isAbstract(parserClass.getModifiers())) {
							parserClass.newInstance();
						}
					}
				} catch (final Exception e) {
					throw new UnrecoverableError(e);
				}
				
				// get the correct parser and set tracker.
				return BugzillaParser.getParser(bugzillaVersion);
			} catch (final XmlException e) {
				throw new UnrecoverableError(
				                             "Could not extract Bugzilla version. This is necessary to load the correct parser instance.",
				                             e);
				
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
