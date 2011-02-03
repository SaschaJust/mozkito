package de.unisaarland.cs.st.reposuite.ppa.internal.visitors;

import java.io.IOException;

import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The Class XMLChangeOperationVisitor.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class XMLChangeOperationVisitor implements ChangeOperationVisitor {
	
	/** The document. */
	private final Document document = new DocumentImpl();
	
	/** The operations element. */
	private final Element  operationsElement;
	
	/** The transaction element. */
	private Element        transactionElement = null;
	
	/** The xml writer. */
	private final XMLWriter xmlWriter;
	
	/**
	 * Instantiates a new xML change operation visitor.
	 * 
	 * @param xmlWriter
	 *            the xml writer
	 */
	public XMLChangeOperationVisitor(final XMLWriter xmlWriter) {
		this.operationsElement = this.document.createElement("javaChangeOperations");
		this.document.appendChild(this.operationsElement);
		this.xmlWriter = xmlWriter;
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
		try {
			this.xmlWriter.write(this.document);
			this.xmlWriter.close();
		} catch (IOException e) {
			if (Logger.logError()) {
				throw new UnrecoverableError(e.getMessage());
			}
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
