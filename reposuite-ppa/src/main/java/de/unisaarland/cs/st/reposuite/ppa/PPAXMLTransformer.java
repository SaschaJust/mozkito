package de.unisaarland.cs.st.reposuite.ppa;

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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The Class PPAXMLSink stores computed JavaChanegOperations into an XML file
 * (or prints the XML to stdout).
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAXMLTransformer extends RepoSuiteSinkThread<JavaChangeOperation> {
	
	public static String ROOT_ELEMENT_NAME = "javaChangeOperations";
	
	public static List<JavaChangeOperation> readOperations(final File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			org.jdom.Document document = saxBuilder.build(reader);
			reader.close();
			return readOperations(document.getRootElement());
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
	
	public static List<JavaChangeOperation> readOperations(final org.jdom.Element element) {
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
			JavaChangeOperation operation = JavaChangeOperation.fromXMLRepresentation(child);
			if (operation != null) {
				result.add(operation);
			}
		}
		return result;
	}
	
	/** The document. */
	private final Document             document;
	
	/** The operations element. */
	private final Element              operationsElement;
	
	/** The out stream. */
	private final OutputStream         outStream;
	
	/** The transaction elements. */
	private final Map<String, Element> transactionElements = new HashMap<String, Element>();
	
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
	public PPAXMLTransformer(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final OutputStream outStream) throws ParserConfigurationException {
		super(threadGroup, PPAXMLTransformer.class.getSimpleName(), settings);
		operationsElement = new Element(ROOT_ELEMENT_NAME);
		document = new org.jdom.Document(operationsElement);
		this.outStream = outStream;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (!checkConnections()) {
			return;
		}
		
		if (!checkNotShutdown()) {
			return;
		}
		
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
		}
		
		synchronized (JavaElementCache.classDefs) {
			JavaElementCache.classDefs.notifyAll();
		}
		
		JavaChangeOperation currentOperation;
		
		try {
			while (!isShutdown() && ((currentOperation = read()) != null)) {
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
			
			if (Logger.logInfo()) {
				Logger.info("PPAXMLSink done. Terminating... ");
			}
			finish();
		} catch (InterruptedException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
	
}
