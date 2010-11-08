/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.sourceforge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.List;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.BugReport;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SourceforgeTracker extends Tracker {
	
	private static Regex submittedRegex = new Regex(
	                                            "({fullname}[^(]+)\\(\\s*({username}[^\\s]+)\\s*\\)\\s+-\\s+({timestamp}\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2})");
	private static Regex subjectRegex   = new Regex("({subject}.*)\\s+-\\s+ID:\\s+({bugid}\\d+)$");
	
	public static DateTime parseDate(final String s) {
		DateTime d = null;
		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
		d = dtf.parseDateTime(s);
		return d;
	}
	
	@Override
	public boolean checkRAW(final String rawReport) {
		boolean retValue = true;
		// TODO check length
		retValue &= rawReport.length() > 1000;
		
		// TODO check for md5
		// TODO check for timestamp
		return true;
	}
	
	@Override
	public boolean checkXML(final Document xmlReport) {
		return true;
	}
	
	@Override
	public Document createDocument(final String rawReport) {
		BufferedReader reader = new BufferedReader(new StringReader(rawReport));
		try {
			SAXBuilder saxBuilder = new SAXBuilder("org.ccil.cowan.tagsoup.Parser");
			Document document = saxBuilder.build(reader);
			reader.close();
			return document;
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	};
	
	@SuppressWarnings ("unchecked")
	private void handleDivElement(final BugReport bugReport, Element e, final Element n) {
		if (e.getName().equals("label")) {
			
			String fieldName = e.getValue().replaceFirst(":.*", "");
			String fieldValue = n.getValue();
			
			if (Logger.logDebug()) {
				Logger.debug("Found field `" + fieldName + "` with value: `" + fieldValue + "`");
			}
			
			if (fieldName.equals("Category")) {
				bugReport.setCategory(fieldValue);
			} else if (fieldName.equals("Details")) {
				bugReport.setDescription(fieldValue);
			} else if (fieldName.equals("Submitted")) {
				List<RegexGroup> find = submittedRegex.find(fieldValue);
				System.err.println(JavaUtils.collectionToString(find));
				bugReport.setSubmitter(this.personManager.getPerson(new Person(find.get(1).getMatch().trim(), find
				        .get(0).getMatch().trim(), null)));
				bugReport.setCreatingTimestamp(parseDate(find.get(2).getMatch().trim()));
			} else if (fieldName.equals("Status")) {
				// TODO status
			} else if (fieldName.equals("Resolution")) {
				// TODO resolution
			} else if (fieldName.equals("Assigned")) {
				bugReport.setAssignedTo(this.personManager.getPerson(new Person(null, fieldValue, null)));
			} else if (fieldName.equals("Group")) {
				// TODO group
			} else if (fieldName.equals("Details")) {
				bugReport.setDescription(fieldValue);
			} else if (fieldName.equals("Priority")) {
				// TODO priority
			} else {
				if (Logger.logDebug()) {
					Logger.debug("Unhandled field `" + fieldName + "` with value: `" + fieldValue + "`");
				}
			}
		} else if ((e.getAttributeValue("class") != null) && e.getAttributeValue("class").matches("p[1-9] box")) {
			// Ok, this is the exception rule for the summary
			if (Logger.logDebug()) {
				Logger.debug("Found field: subject, value: " + n.getValue());
			}
			List<RegexGroup> find = subjectRegex.find(n.getValue());
			bugReport.setSubject(find.get(0).getMatch());
			bugReport.setId(Long.parseLong(find.get(1).getMatch()));
		} else if ((e.getAttributeValue("id") != null) && e.getAttributeValue("id").equals("comment_table_container")) {
			// Comments are not properly formatted. Hacking it.
			e = (Element) (e.getChildren() != null ? e.getChildren().get(0) : null);
			if (e != null) {
				try {
					Element e1, e2;
					
					// Pray to god, that they won't change the layout
					e = (Element) e.getChildren().get(2); // tbody
					
					// All childs of tbody should be [TR]
					for (Object commentObject : e.getChildren()) {
						Element el = (Element) ((Element) commentObject).getChildren().get(0); // td
						el = (Element) el.getChildren().get(0); // div
						e1 = (Element) el.getChildren().get(0); // div
						e1 = (Element) e1.getChildren().get(1); // p
						e2 = (Element) el.getChildren().get(1); // div
						e2 = (Element) e2.getChildren().get(0); // p
						List<Element> children = e1.getChildren();
						Element el3 = children.get(1); // a
						String senderName = "";
						if (el3.getAttribute("title") != null) {
							senderName = FileUtils.lineSeparator + "Name: " + el3.getAttributeValue("title");
						}
						
						if (Logger.logDebug()) {
							Logger.debug("Found comment from " + senderName);
						}
						// StructuralElement c =
						// StructuralElementParser.get(Comment.class, span);
						// this.bugReport.addComment((Comment) c);
						// Logger.getLogger(this.getClass()).debug("Adding '" +
						// c.getHandle() + "': " + c);
					}
				} catch (IndexOutOfBoundsException exc) {
					// Skip!
				}
			}
			
		} else if ((e.getAttributeValue("id") != null) && e.getAttributeValue("id").equals("commentbar")) {
			// e = (Element) (e.getChildren() != null ? e.getChildren().get(0) :
			// null);
			String s = e.getText().trim();
			String[] sa = s.split(" +");
			if (sa.length > 3) {
				// this.bugReport.setCommentCount(Integer.parseInt(sa[2]));
			}
		} else {
			// System.out.println(e.getName() + "=>" + e.getValue());
		}
		
		// Logger.getLogger(this.getClass()).trace(" " + e.getName() + ":" +
		// e.getText());
		List<Element> el = e.getChildren();
		for (int i = 0; i < el.size(); i++) {
			if (i + 1 < el.size()) {
				handleDivElement(bugReport, el.get(i), el.get(i + 1));
			} else {
				handleDivElement(bugReport, el.get(i), null);
			}
			
		}
	}
	
	@SuppressWarnings ("unchecked")
	private void hangle(final BugReport bugReport, final Element e, final Element n) {
		if (((e.getAttributeValue("class") != null) && (e.getAttributeValue("class").startsWith("yui-u") || e
		        .getAttributeValue("class").startsWith("yui-g")))
		        || ((e.getAttributeValue("id") != null) && (e.getAttributeValue("id").equals("comment_table_container") || e
		                .getAttributeValue("id").equals("commentbar")))) {
			handleDivElement(bugReport, e, n);
		} else {
			List<Element> el = e.getChildren();
			for (int i = 0; i < el.size(); i++) {
				if (i + 1 < el.size()) {
					hangle(bugReport, el.get(i), el.get(i + 1));
				} else {
					hangle(bugReport, el.get(i), null);
				}
				
			}
		}
	}
	
	@Override
	public BugReport parse(final Document document) {
		// System.err.println(document);
		// Content content = document.getContent(1);
		// Element element = content.getDocument().getRootElement();
		Element element = document.getRootElement();
		BugReport bugReport = new BugReport();
		hangle(bugReport, element, null);
		// System.err.println(bugReport);
		
		return bugReport;
	}
	
	@Override
	public void setup(final URI fetchURI, final URI overviewURI, final String pattern, final String username,
	        final String password, final Long startAt, final Long stopAt) throws InvalidParameterException {
		super.setup(fetchURI, overviewURI, pattern, username, password, startAt, stopAt);
		
		if (overviewURI != null) {
			if (Logger.logWarn()) {
				Logger.warn(getHandle() + "does not support overviewURIs.");
			}
		}
		if (startAt == null) {
			this.startAt = 1l;
		}
		if (stopAt == null) {
			throw new InvalidParameterException("stopAt must not be null");
		}
		
		for (long i = this.startAt; i <= this.stopAt; ++i) {
			addSuspect(i);
		}
		
		this.initialized = true;
	}
	
}
