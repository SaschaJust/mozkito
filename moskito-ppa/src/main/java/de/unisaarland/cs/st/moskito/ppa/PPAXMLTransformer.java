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
package de.unisaarland.cs.st.moskito.ppa;

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
import net.ownhero.dev.andama.settings.Settings;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

/**
 * The Class PPAXMLSink stores computed JavaChanegOperations into an XML file (or prints the XML to stdout).
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAXMLTransformer extends Sink<JavaChangeOperation> {
	
	public static String ROOT_ELEMENT_NAME = "javaChangeOperations";
	
	public static List<JavaChangeOperation> readOperations(final File file,
	                                                       PersistenceUtil persistenceUtil) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			org.jdom.Document document = saxBuilder.build(reader);
			reader.close();
			return readOperations(document.getRootElement(), persistenceUtil);
		} catch (JDOMException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return new ArrayList<JavaChangeOperation>(0);
	}
	
	public static List<JavaChangeOperation> readOperations(final org.jdom.Element element,
	                                                       PersistenceUtil persistenceUtil) {
		List<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		if (!element.getName().equals(ROOT_ELEMENT_NAME)) {
			if (Logger.logError()) {
				Logger.error("RootElement for JavaChangeOperations must have be <" + ROOT_ELEMENT_NAME + "> but was <"
				        + element.getName() + ">");
			}
			return result;
		}
		
		@SuppressWarnings ("unchecked")
		List<org.jdom.Element> children = element.getChildren();
		
		for (org.jdom.Element child : children) {
			JavaChangeOperation operation = JavaChangeOperation.fromXMLRepresentation(child, persistenceUtil);
			if (operation != null) {
				result.add(operation);
			}
		}
		return result;
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
		final Element operationsElement = new Element(ROOT_ELEMENT_NAME);
		final Document document = new org.jdom.Document(operationsElement);
		final Map<String, Element> transactionElements = new HashMap<String, Element>();
		
		new PostExecutionHook<JavaChangeOperation, JavaChangeOperation>(this) {
			
			@Override
			public void postExecution() {
				try {
					// Use a Transformer for output
					XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
					outputter.output(document, outStream);
					outStream.close();
				} catch (FileNotFoundException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				} catch (IOException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				}
			}
		};
		
		new ProcessHook<JavaChangeOperation, JavaChangeOperation>(this) {
			
			@Override
			public void process() {
				JavaChangeOperation currentOperation = getInputData();
				String transactionId = currentOperation.getRevision().getTransaction().getId();
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + currentOperation);
				}
				
				if (!transactionElements.containsKey(transactionId)) {
					Element transactionElement = new Element("transaction");
					transactionElement.setAttribute("id", transactionId);
					operationsElement.addContent(transactionElement);
					transactionElements.put(transactionId, transactionElement);
				}
				Element transactionElement = transactionElements.get(transactionId);
				
				Element operationElement = currentOperation.getXMLRepresentation();
				transactionElement.addContent(operationElement);
			}
		};
	}
	
}
