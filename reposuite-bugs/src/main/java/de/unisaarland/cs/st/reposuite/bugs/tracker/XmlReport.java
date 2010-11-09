/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker;

import org.jdom.Document;
import org.joda.time.DateTime;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class XmlReport extends RawReport {
	
	private final Document document;
	
	/**
	 * @param id
	 * @param md5
	 * @param fetchTime
	 * @param format
	 * @param document
	 * @param content
	 */
	public XmlReport(final long id, final byte[] md5, final DateTime fetchTime, final String format,
	        final String content, final Document document) {
		super(id, md5, fetchTime, format, content);
		this.document = document;
	}
	
	public XmlReport(final RawReport rawReport, final Document document) {
		super(rawReport.getId(), rawReport.getMd5(), rawReport.getFetchTime(), rawReport.getFormat(), rawReport
		        .getContent());
		this.document = document;
	}
	
	/**
	 * @return the document
	 */
	public Document getDocument() {
		return this.document;
	}
	
}
