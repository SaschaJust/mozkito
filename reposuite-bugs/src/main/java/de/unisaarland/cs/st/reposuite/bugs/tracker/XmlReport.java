/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker;

import org.jdom.Document;

import de.unisaarland.cs.st.reposuite.utils.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class XmlReport extends RawReport {
	
	private static final long serialVersionUID = 6524458006854786132L;
	private final Document    document;
	
	/**
	 * @param rawReport
	 * @param document
	 */
	public XmlReport(final RawReport rawReport, final Document document) {
		super(rawReport.getId(), rawReport);
		Condition.notNull(document);
		this.document = document;
	}
	
	/**
	 * @return the document
	 */
	public Document getDocument() {
		return this.document;
	}
	
}
