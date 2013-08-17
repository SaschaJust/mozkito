/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.research.persons.stackoverflow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import net.ownhero.dev.kisa.Logger;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.mozkito.persons.model.Person;
import org.mozkito.utilities.datastructures.RawContent;
import org.mozkito.utilities.io.IOUtils;
import org.mozkito.utilities.io.exceptions.FetchException;
import org.mozkito.utilities.io.exceptions.UnsupportedProtocolException;

/**
 * The Class StackOverflowParser.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class StackOverflowParser {
	
	/** The Constant BASE_URL. */
	private static final String BASE_URL = "http://stackoverflow.com/users/filter?search=";
	
	/**
	 * Gets the meta data.
	 * 
	 * @param person
	 *            the person
	 */
	public static void getMetaData(final Person person) {
		try {
			
			for (final String username : person.getUsernames()) {
				final RawContent rawContent = IOUtils.fetch(new URI(BASE_URL + username));
				final String html = rawContent.getContent();
				
				final SAXBuilder saxBuilder = new SAXBuilder(new XMLReaderSAX2Factory(false,
				                                                                      "org.ccil.cowan.tagsoup.Parser"));
				try {
					final Document document = saxBuilder.build(new StringReader(html));
					final XMLOutputter outp = new XMLOutputter();
					outp.setFormat(Format.getPrettyFormat());
					final String xml = outp.outputString(document);
					
					final BufferedReader br = new BufferedReader(new StringReader(xml));
					final XMLReader parser = XMLReaderFactory.createXMLReader();
					parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
					final StackOverflowSearchHandler handler = new StackOverflowSearchHandler(username);
					parser.setContentHandler(handler);
					final InputSource inputSource = new InputSource(br);
					parser.parse(inputSource);
					
					handler.getMeta();
					System.err.println(handler.getMeta());
				} catch (final JDOMException e) {
					if (Logger.logError()) {
						Logger.error(e, "Could not convert overview to XHTML!");
					}
				} catch (final SAXException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					
				}
			}
		} catch (UnsupportedProtocolException | FetchException | URISyntaxException e) {
			// TODO Auto-generated catch block
			
		}
	}
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	@SuppressWarnings ("deprecation")
	public static void main(final String[] args) {
		getMetaData(new Person("balusc", null, null));
	}
}
