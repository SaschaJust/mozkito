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

package org.mozkito.utilities.text;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * The Class Correction.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Correction {
	
	/**
	 * The Class Replacement.
	 */
	public static class Replacement {
		
		/** The original. */
		private String original;
		
		/** The corrected. */
		private String corrected;
		
		/** The start. */
		private int    start;
		
		/** The end. */
		private int    end;
		
		/** The rule. */
		private Rule   rule;
		
		/**
		 * Gets the corrected.
		 * 
		 * @return the corrected
		 */
		public final String getCorrected() {
			// PRECONDITIONS
			
			try {
				return this.corrected;
			} finally {
				// POSTCONDITIONS
				Condition.notNull(this.corrected, "Field '%s' in '%s'.", "corrected", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		/**
		 * Gets the end.
		 * 
		 * @return the end
		 */
		public final int getEnd() {
			// PRECONDITIONS
			
			try {
				return this.end;
			} finally {
				// POSTCONDITIONS
				CompareCondition.notNegative(this.end, "Field '%s' in '%s'.", "end", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$        	
			}
		}
		
		/**
		 * Gets the original.
		 * 
		 * @return the original
		 */
		public final String getOriginal() {
			// PRECONDITIONS
			
			try {
				return this.original;
			} finally {
				// POSTCONDITIONS
				Condition.notNull(this.original, "Field '%s' in '%s'.", "original", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		/**
		 * Gets the rule.
		 * 
		 * @return the rule
		 */
		public final Rule getRule() {
			// PRECONDITIONS
			
			try {
				return this.rule;
			} finally {
				// POSTCONDITIONS
				Condition.notNull(this.rule, "Field '%s' in '%s'.", "rule", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		/**
		 * Gets the start.
		 * 
		 * @return the start
		 */
		public final int getStart() {
			// PRECONDITIONS
			
			try {
				return this.start;
			} finally {
				// POSTCONDITIONS
				CompareCondition.notNegative(this.start, "Field '%s' in '%s'.", "start", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * The Class ReplacementIterator.
	 */
	public static class ReplacementIterator implements Iterator<Replacement> {
		
		/** The iterator. */
		private final Iterator<Replacement> iterator;
		
		/**
		 * Instantiates a new replacement iterator.
		 * 
		 * @param iterator
		 *            the iterator
		 */
		public ReplacementIterator(final Iterator<Replacement> iterator) {
			this.iterator = iterator;
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			// PRECONDITIONS
			
			try {
				return this.iterator.hasNext();
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Replacement next() {
			// PRECONDITIONS
			
			try {
				return this.iterator.next();
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			// PRECONDITIONS
			
			try {
				throw new UnsupportedOperationException("Deleting replacements in a correction is not supported.");
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/**
	 * The Class Rule.
	 */
	public static class Rule {
		// stub
	}
	
	/**
	 * Gets the data elements.
	 * 
	 * @param root
	 *            the root
	 * @param name
	 *            the name
	 * @param elements
	 *            the elements
	 */
	private static final void getDataElements(final Element root,
	                                          final String name,
	                                          final List<Element> elements) {
		boolean added = false;
		if ("div".equals(root.getName())) {
			final Attribute attribute = root.getAttribute("class");
			if ((attribute != null) && attribute.getValue().contains(name)) {
				final Element pre = root.getChild("pre");
				assert pre != null;
				for (final Element span : pre.getChildren()) {
					final List<Element> entryList = span.getChildren();
					if (entryList.size() >= 7) {
						final Element entry = new Element("entry");
						final String word = entryList.get(2).getValue();
						final Element wordElement = new Element("word", entry.getNamespace());
						wordElement.setText(word);
						entry.addContent(wordElement);
						final String find = entryList.get(4).getValue();
						final Element findElement = new Element("find", entry.getNamespace());
						findElement.setText(find);
						entry.addContent(findElement);
						final String replace = entryList.get(6).getValue();
						final Element replaceElement = new Element("replace", entry.getNamespace());
						replaceElement.setText(replace);
						entry.addContent(replaceElement);
						elements.add(entry);
					}
				}
				added = true;
			}
		}
		
		if (!added) {
			for (final Element element : root.getChildren()) {
				getDataElements(element, name, elements);
			}
		}
	}
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		retf("Sclupture");
	}
	
	/**
	 * Main2.
	 * 
	 * @param args
	 *            the args
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JDOMException
	 *             the jDOM exception
	 */
	public static void main2(final String[] args) throws IOException, JDOMException {
		final HttpClient client = new DefaultHttpClient();
		final HttpGet method = new HttpGet("http://en.wikipedia.org/wiki/Wikipedia:AWB/Typos");
		
		final ResponseHandler<String> responseHandler = new BasicResponseHandler();
		final String responseBody = client.execute(method, responseHandler);
		
		method.releaseConnection();
		
		final SAXBuilder saxBuilder = new SAXBuilder(new XMLReaderSAX2Factory(false,
		                                                                      "org.apache.xerces.parsers.SAXParser"));
		saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		final ByteArrayInputStream stream = new ByteArrayInputStream(responseBody.getBytes());
		final InputStreamReader reader = new InputStreamReader(stream);
		final Document document = saxBuilder.build(reader);
		
		final Element rootElement = document.getRootElement();
		final Element body = rootElement.getChild("body", rootElement.getNamespace());
		
		System.err.println(document);
		
		final List<Element> elements = new LinkedList<>();
		getDataElements(body, "source-xml", elements);
		
		System.err.println(elements.size());
		
		final Document target = new Document();
		final Element entries = new Element("entries");
		
		for (final Element element : elements) {
			entries.addContent(element.clone());
		}
		target.addContent(entries);
		
		reader.close();
		
		FileWriter w = null;
		final File file = new File("retf.xml");
		w = new FileWriter(file);
		
		final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		outputter.output(target, w);
		w.close();
	}
	
	/**
	 * Retf.
	 * 
	 * @param text
	 *            the text
	 * @return the correction
	 */
	public static Correction retf(final String text) {
		final InputStream stream = Correction.class.getResourceAsStream("/retf.xml");
		assert stream != null;
		
		final SAXBuilder saxBuilder = new SAXBuilder(new XMLReaderSAX2Factory(false,
		                                                                      "org.apache.xerces.parsers.SAXParser"));
		saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		final InputStreamReader reader = new InputStreamReader(stream);
		Document document;
		try {
			document = saxBuilder.build(reader);
			final Element entries = document.getRootElement();
			for (final Element entry : entries.getChildren()) {
				final Regex regex = new Regex(entry.getChildText("find"));
				if (regex.find(text) != null) {
					final String string = regex.replaceAll(text, entry.getChildText("replace"));
					System.err.println(string);
				}
			}
		} catch (JDOMException | IOException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			
		}
		
		return null;
	}
	
	/** The original text. */
	private String                  originalText;
	
	/** The corrected text. */
	private String                  correctedText;
	
	/** The replacements. */
	private final List<Replacement> replacements = new LinkedList<>();
	
	/**
	 * Instantiates a new correction.
	 */
	private Correction() {
	}
	
	/**
	 * Gets the corrected text.
	 * 
	 * @return the correctedText
	 */
	public final String getCorrectedText() {
		// PRECONDITIONS
		
		try {
			return this.correctedText;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.correctedText, "Field '%s' in '%s'.", "correctedText", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the number of corrections.
	 * 
	 * @return the number of corrections
	 */
	public int getNumberOfCorrections() {
		return this.replacements.size();
	}
	
	/**
	 * Gets the original text.
	 * 
	 * @return the originalText
	 */
	public final String getOriginalText() {
		// PRECONDITIONS
		
		try {
			return this.originalText;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.originalText, "Field '%s' in '%s'.", "originalText", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Sets the corrected text.
	 * 
	 * @param correctedText
	 *            the correctedText to set
	 */
	public final void setCorrectedText(final String correctedText) {
		// PRECONDITIONS
		Condition.notNull(correctedText, "Argument '%s' in '%s'.", "correctedText", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.correctedText = correctedText;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.correctedText, correctedText,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Sets the original text.
	 * 
	 * @param originalText
	 *            the originalText to set
	 */
	public final void setOriginalText(final String originalText) {
		// PRECONDITIONS
		Condition.notNull(originalText, "Argument '%s' in '%s'.", "originalText", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.originalText = originalText;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.originalText, originalText,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
}
