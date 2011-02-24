package de.unisaarland.cs.st.reposuite.ppa.internal.visitors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

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
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * The Class XMLChangeOperationVisitor.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class XMLChangeOperationVisitor implements ChangeOperationVisitor {
	
	/** The document. */
	private final Document  document;
	
	/** The operations element. */
	private final Element  operationsElement;
	
	/** The transaction element. */
	private Element        transactionElement = null;
	
	private final OutputStream outStream;
	
	
	/**
	 * Instantiates a new xML change operation visitor.
	 * 
	 * @param xmlWriter
	 *            the xml writer
	 * @throws ParserConfigurationException
	 */
	public XMLChangeOperationVisitor(final OutputStream outStream) throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser = factory.newDocumentBuilder();
		this.document = parser.newDocument();
		this.operationsElement = this.document.createElement("javaChangeOperations");
		this.document.appendChild(this.operationsElement);
		this.outStream = outStream;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor
	 * #endVisit()
	 */
	@Override
	public void endVisit() {
		
		try{
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
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation)
	 */
	@Override
	public void visit(final JavaChangeOperation change) {
		//write operation as XML entity to document
		Element operationElement = change.getXMLRepresentation(this.document);
		this.transactionElement.appendChild(operationElement);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction)
	 */
	@Override
	public void visit(final RCSTransaction transaction) {
		this.transactionElement = this.document.createElement("transaction");
		this.transactionElement.setAttribute("id", transaction.getId());
		this.operationsElement.appendChild(this.transactionElement);
		
	}
	
}
