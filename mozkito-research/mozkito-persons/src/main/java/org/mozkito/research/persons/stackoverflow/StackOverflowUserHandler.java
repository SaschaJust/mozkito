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

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The Class StackOverflowUserHandler.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class StackOverflowUserHandler extends DefaultHandler {
	
	/** The in data. */
	private boolean                   inData     = false;
	
	/** The table data. */
	private int                       tableData  = -1;
	
	/** The key. */
	private String                    key        = null;
	
	/** The value. */
	private String                    value      = null;
	
	/** The meta. */
	private final Map<String, String> meta       = new HashMap<String, String>();
	
	/** The is td. */
	private boolean                   isTD       = false;
	
	/** The extract url. */
	private boolean                   extractURL = false;
	
	/**
	 * Instantiates a new stack overflow user handler.
	 * 
	 * @param username
	 *            the username
	 */
	public StackOverflowUserHandler(final String username) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
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
			if (this.isTD && (this.tableData >= 0)) {
				if (this.tableData == 1) {
					this.key = new String(ch, start, length);
					switch (this.key) {
						case "website":
							this.extractURL = true;
							this.key = null;
							break;
						case "location":
						case "age":
							break;
						default:
							this.key = null;
					}
				} else if (this.key != null) {
					this.value = new String(ch, start, length);
					this.meta.put(this.key, this.value);
					this.tableData = -1;
					this.key = null;
					this.value = null;
				}
				this.isTD = false;
				--this.tableData;
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the meta.
	 * 
	 * @return the meta
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
			if (!this.inData) {
				if ("div".equals(localName)) {
					final String classAttrib = attributes.getValue("class");
					if ((classAttrib != null) && classAttrib.equals("data")) {
						;
					}
					this.inData = true;
				}
			} else {
				if ("td".equals(localName)) {
					this.isTD = true;
					if (this.tableData < 0) {
						this.tableData = 1;
					}
					
				} else if ("a".equals(localName)) {
					if (this.extractURL) {
						this.extractURL = false;
						this.meta.put("website", attributes.getValue("href"));
					}
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
