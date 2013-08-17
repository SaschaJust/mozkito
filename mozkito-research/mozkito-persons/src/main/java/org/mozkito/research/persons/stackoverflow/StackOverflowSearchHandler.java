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
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import org.mozkito.utilities.datastructures.RawContent;
import org.mozkito.utilities.io.IOUtils;
import org.mozkito.utilities.io.exceptions.FetchException;
import org.mozkito.utilities.io.exceptions.UnsupportedProtocolException;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class StackOverflowSearchHandler extends DefaultHandler {
	
	private boolean             inDetails     = false;
	private boolean             foundUsername = false;
	private String              username;
	private String              path;
	private String              url;
	private Map<String, String> meta;
	
	/**
     * 
     */
	public StackOverflowSearchHandler(@NotNull final String username) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			this.username = username;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(final char[] ch,
	                       final int start,
	                       final int length) throws SAXException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			if (this.foundUsername) {
				final String username = new String(ch, start, length);
				System.err.println(username);
				if (this.username.equalsIgnoreCase(username)) {
					this.url = "http://www.stackoverflow.com" + this.path;
					collectMetaData(this.url);
				} else {
					this.foundUsername = false;
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @param url2
	 */
	private void collectMetaData(final String url) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			try {
				final RawContent rawContent = IOUtils.fetch(new URI(url));
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
					final StackOverflowUserHandler handler = new StackOverflowUserHandler(this.username);
					parser.setContentHandler(handler);
					final InputSource inputSource = new InputSource(br);
					parser.parse(inputSource);
					
					this.meta = handler.getMeta();
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
			} catch (UnsupportedProtocolException | FetchException | URISyntaxException e) {
				// TODO Auto-generated catch block
				
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(final String uri,
	                       final String localName,
	                       final String qName) throws SAXException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			switch (localName) {
				case "div":
					this.inDetails = false;
					break;
				default:
					break;
			}
			
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return
	 */
	public Map<String, String> getMeta() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.meta;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
	 *      org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(final String uri,
	                         final String localName,
	                         final String qName,
	                         final Attributes attributes) throws SAXException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			
			switch (localName) {
				case "div":
					final String classAttrib = attributes.getValue("class");
					if ((classAttrib != null) && "user-details".equals(classAttrib)) {
						this.inDetails = true;
					}
					break;
				case "a":
					if (this.inDetails) {
						this.path = attributes.getValue("href");
						this.foundUsername = true;
					}
				default:
					break;
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
}
