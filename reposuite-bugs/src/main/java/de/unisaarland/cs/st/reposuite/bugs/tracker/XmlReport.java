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
	
	private final Document document;
	
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
