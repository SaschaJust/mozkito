package de.unisaarland.cs.st.reposuite.ppa;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class PPAXMLSink extends RepoSuiteSinkThread<JavaChangeOperation> {
	
	private final Document             document;
	private final Element              operationsElement;
	private final OutputStream         outStream;
	private final Map<String, Element> transactionElements = new HashMap<String, Element>();
	
	public PPAXMLSink(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
			final OutputStream outStream) throws ParserConfigurationException {
		super(threadGroup, PPAXMLSink.class.getSimpleName(), settings);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser = factory.newDocumentBuilder();
		this.document = parser.newDocument();
		this.operationsElement = this.document.createElement("javaChangeOperations");
		this.document.appendChild(this.operationsElement);
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
		
		JavaChangeOperation currentOperation;
		
		try {
			while (!isShutdown() && ((currentOperation = read()) != null)) {
				String transactionId = currentOperation.getRevision().getTransaction().getId();
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + currentOperation);
				}
				
				if (!this.transactionElements.containsKey(transactionId)) {
					Element transactionElement = this.document.createElement("transaction");
					transactionElement.setAttribute("id", transactionId);
					this.operationsElement.appendChild(transactionElement);
					this.transactionElements.put(transactionId, transactionElement);
				}
				Element transactionElement = this.transactionElements.get(transactionId);
				
				Element operationElement = currentOperation.getXMLRepresentation(this.document);
				transactionElement.appendChild(operationElement);
			}
			
			try {
				// Use a Transformer for output
				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				
				DOMSource source = new DOMSource(this.document);
				StreamResult result = new StreamResult(this.outStream);
				transformer.transform(source, result);
				this.outStream.close();
			} catch (FileNotFoundException e) {
				throw new UnrecoverableError(e.getMessage(), e);
			} catch (TransformerConfigurationException e) {
				throw new UnrecoverableError(e.getMessage(), e);
			} catch (TransformerException e) {
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
