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
package org.mozkito.codeanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElementFactory;
import org.mozkito.persistence.ModelStorage;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class PPAXMLSink stores computed JavaChanegOperations into an XML file (or prints the XML to stdout).
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PPAXMLTransformer extends Sink<JavaChangeOperation> {
	
	/** The root element name. */
	public static String ROOT_ELEMENT_NAME = "javaChangeOperations";
	
	/**
	 * Read operations.
	 * 
	 * @param element
	 *            the element
	 * @param transactionStorage
	 *            the transaction storage
	 * @return the list
	 */
	public static List<JavaChangeOperation> readOperations(final Element element,
	                                                       final ModelStorage<String, ChangeSet> transactionStorage) {
		final List<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		if (!element.getName().equals(PPAXMLTransformer.ROOT_ELEMENT_NAME)) {
			if (Logger.logError()) {
				Logger.error("RootElement for JavaChangeOperations must have be <"
				        + PPAXMLTransformer.ROOT_ELEMENT_NAME + "> but was <" + element.getName() + ">");
			}
			return result;
		}
		
		final List<Element> children = element.getChildren();
		
		final JavaElementFactory elementFactory = new JavaElementFactory();
		
		for (final Element child : children) {
			final JavaChangeOperation operation = JavaChangeOperation.fromXMLRepresentation(child, transactionStorage,
			                                                                                elementFactory);
			if (operation != null) {
				result.add(operation);
			}
		}
		return result;
	}
	
	/**
	 * Read operations.
	 * 
	 * @param file
	 *            the file
	 * @param transactionStorage
	 *            the transaction storage
	 * @return the list
	 */
	public static List<JavaChangeOperation> readOperations(final File file,
	                                                       final ModelStorage<String, ChangeSet> transactionStorage) {
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(file));
			final SAXBuilder saxBuilder = new SAXBuilder(
			                                             new XMLReaderSAX2Factory(false,
			                                                                      "org.apache.xerces.parsers.SAXParser"));
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			final Document document = saxBuilder.build(reader);
			reader.close();
			return readOperations(document.getRootElement(), transactionStorage);
		} catch (final JDOMException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		}
		return new ArrayList<JavaChangeOperation>(0);
	}
	
	/**
	 * Instantiates a new pPAXML sink.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param outStream
	 *            the out stream
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 */
	public PPAXMLTransformer(final Group threadGroup, final Settings settings, final OutputStream outStream)
	        throws ParserConfigurationException {
		super(threadGroup, settings, false);
		final Element operationsElement = new Element(PPAXMLTransformer.ROOT_ELEMENT_NAME);
		final Document document = new Document(operationsElement);
		final Map<String, Element> transactionElements = new HashMap<String, Element>();
		
		new PostExecutionHook<JavaChangeOperation, JavaChangeOperation>(this) {
			
			@Override
			public void postExecution() {
				try {
					// Use a Transformer for output
					final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
					outputter.output(document, outStream);
					outStream.close();
				} catch (final FileNotFoundException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				} catch (final IOException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				}
			}
		};
		
		new ProcessHook<JavaChangeOperation, JavaChangeOperation>(this) {
			
			@Override
			public void process() {
				final JavaChangeOperation currentOperation = getInputData();
				final String changeSetId = currentOperation.getRevision().getChangeSet().getId();
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + currentOperation);
				}
				
				if (!transactionElements.containsKey(changeSetId)) {
					final Element transactionElement = new Element("transaction");
					transactionElement.setAttribute("id", changeSetId);
					operationsElement.addContent(transactionElement);
					transactionElements.put(changeSetId, transactionElement);
				}
				final Element transactionElement = transactionElements.get(changeSetId);
				
				final Element operationElement = currentOperation.getXMLRepresentation();
				transactionElement.addContent(operationElement);
			}
		};
	}
	
}
