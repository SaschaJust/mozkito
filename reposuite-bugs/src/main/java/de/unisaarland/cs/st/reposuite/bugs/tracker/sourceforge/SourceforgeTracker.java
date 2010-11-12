/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.sourceforge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Priority;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Status;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.DateTimeUtils;
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
	                                            "({fullname}[^(]+)\\(\\s+({username}[^\\s]+)\\s+\\)\\s+-\\s+({timestamp}\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}.*)");
	private final Regex  subjectRegex   = new Regex("({subject}.*)\\s+-\\s+ID:\\s+({bugid}\\d+)$");
	
	@Override
	public boolean checkRAW(final RawReport rawReport) {
		boolean retValue = super.checkRAW(rawReport);
		
		retValue &= rawReport.getContent().length() > 1000;
		
		return retValue;
	}
	
	@Override
	public boolean checkXML(final XmlReport xmlReport) {
		boolean retValue = super.checkXML(xmlReport);
		
		return retValue;
	}
	
	@Override
	public XmlReport createDocument(final RawReport rawReport) {
		BufferedReader reader = new BufferedReader(new StringReader(rawReport.getContent()));
		try {
			SAXBuilder saxBuilder = new SAXBuilder("org.ccil.cowan.tagsoup.Parser");
			Document document = saxBuilder.build(reader);
			reader.close();
			return new XmlReport(rawReport, document);
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
	}
	
	public void getIdsFromURI(final URI uri) {
		if (uri.getScheme().equals("file")) {
			// FIXME this will fail on ?+*
			Regex regex = new Regex(".*" + this.pattern.replace(bugIdPlaceholder, "({bugid}\\d+)"));
			File baseDir = new File(uri.getPath());
			
			if (baseDir.exists() && baseDir.isDirectory() && baseDir.canExecute() && baseDir.canRead()) {
				Collection<File> files = FileUtils.listFiles(baseDir, null, true);
				
				for (File file : files) {
					if (regex.find(file.getAbsolutePath()) != null) {
						addBugId(Long.parseLong(regex.getGroup("bugid")));
					}
				}
			} else if (baseDir.exists() && baseDir.isFile() && baseDir.canRead()) {
				if (regex.find(baseDir.getAbsolutePath()) != null) {
					addBugId(Long.parseLong(regex.getGroup("bugid")));
				}
			} else {
				
				if (Logger.logError()) {
					Logger.error("Overview URI not valid: " + uri);
				}
			}
		} else {
			
		}
	};
	
	@SuppressWarnings ("unchecked")
	private void handleDivElement(final Report bugReport, Element e, final Element n) {
		if (e.getName().equals("label")) {
			
			String fieldName = e.getValue().replaceFirst(":.*", "");
			String fieldValue = n.getValue();
			
			if (Logger.logTrace()) {
				Logger.trace("Found field `" + fieldName + "` with value: `" + fieldValue + "`");
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
				bugReport.setCreationTimestamp(DateTimeUtils.parseDate(find.get(2).getMatch().trim()));
			} else if (fieldName.equals("Status")) {
				// from: CLOSED, DELETED, OPEN, PENDING
				// to: UNKNOWN, UNCONFIRMED, NEW, ASSIGNED, IN_PROGRESS,
				// REOPENED, RESOLVED, VERIFIED, CLOSED
				if (fieldValue.equals("CLOSED")) {
					bugReport.setStatus(Status.CLOSED);
				} else if (fieldValue.equals("DELETED")) {
					bugReport.setStatus(Status.CLOSED);
				} else if (fieldValue.equals("OPEN")) {
					bugReport.setStatus(Status.NEW);
				} else if (fieldValue.equals("PENDING")) {
					bugReport.setStatus(Status.IN_PROGRESS);
				}
			} else if (fieldName.equals("Resolution")) {
				// from: ACCEPTED, DUPLICATE, FIXED, INVALID, LATER, NONE,
				// OUT_OF_DATE, POSTPONED, REJECTED, REMIND, WONT_FIX,
				// WORKS_FOR_ME;
				// to: UNKNOWN, UNRESOLVED, DUPLICATE, RESOLVED, INVALID,
				// WONT_FIX, WORKS_FOR_ME;
				if (fieldValue.equals("ACCEPTED")) {
					bugReport.setStatus(Status.CLOSED);
				} else if (fieldValue.equals("DUPLICATE")) {
					bugReport.setResolution(Resolution.DUPLICATE);
				} else if (fieldValue.equals("FIXED")) {
					bugReport.setResolution(Resolution.RESOLVED);
				} else if (fieldValue.equals("INVALID")) {
					bugReport.setResolution(Resolution.INVALID);
				} else if (fieldValue.equals("LATER")) {
					bugReport.setResolution(Resolution.UNRESOLVED);
				} else if (fieldValue.equals("NONE")) {
					bugReport.setResolution(Resolution.UNRESOLVED);
				} else if (fieldValue.equals("OUT_OF_DATE")) {
					bugReport.setResolution(Resolution.UNKNOWN);
				} else if (fieldValue.equals("POSTPONED")) {
					bugReport.setResolution(Resolution.UNRESOLVED);
				} else if (fieldValue.equals("REJECTED")) {
					bugReport.setResolution(Resolution.INVALID);
				} else if (fieldValue.equals("REMIND")) {
					bugReport.setResolution(Resolution.UNRESOLVED);
				} else if (fieldValue.equals("WONT_FIX")) {
					bugReport.setResolution(Resolution.WONT_FIX);
				} else if (fieldValue.equals("WORKS_FOR_ME")) {
					bugReport.setResolution(Resolution.WORKS_FOR_ME);
				} else {
					bugReport.setResolution(Resolution.UNKNOWN);
				}
			} else if (fieldName.equals("Assigned")) {
				bugReport.setAssignedTo(this.personManager.getPerson(new Person(null, fieldValue, null)));
			} else if (fieldName.equals("Group")) {
				// bugReport.set
			} else if (fieldName.equals("Details")) {
				bugReport.setDescription(fieldValue);
			} else if (fieldName.equals("Priority")) {
				// 1..9;
				// UNKNOWN, VERY_LOW, LOW, NORMAL, HIGH, VERY_HIGH;
				int priority = Integer.parseInt(fieldValue);
				switch (priority) {
					case 1:
					case 2:
						bugReport.setPriority(Priority.VERY_LOW);
						break;
					case 3:
					case 4:
						bugReport.setPriority(Priority.LOW);
						break;
					case 5:
						bugReport.setPriority(Priority.NORMAL);
						break;
					case 6:
					case 7:
						bugReport.setPriority(Priority.HIGH);
						break;
					case 8:
					case 9:
						bugReport.setPriority(Priority.VERY_HIGH);
						break;
					default:
						bugReport.setPriority(Priority.UNKNOWN);
						break;
				}
			} else {
				if (Logger.logDebug()) {
					Logger.debug("Unhandled field `" + fieldName + "` with value: `" + fieldValue + "`");
				}
			}
		} else if ((e.getAttributeValue("class") != null) && e.getAttributeValue("class").matches("p[1-9] box")) {
			// Ok, this is the exception rule for the summary
			if (Logger.logTrace()) {
				Logger.trace("Found field: subject, value: " + n.getValue());
			}
			List<RegexGroup> find = this.subjectRegex.find(n.getValue());
			bugReport.setSubject(find.get(0).getMatch());
			bugReport.setId(Long.parseLong(find.get(1).getMatch()));
		} else if ((e.getAttributeValue("id") != null) && e.getAttributeValue("id").equals("comment_table_container")) {
			// Comments are not properly formatted. Hacking it.
			e = (Element) (e.getChildren() != null ? e.getChildren().get(0) : null);
			if (e != null) {
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
					// List<Element> children = e1.getChildren();
					// Element el3 = children.get(1); // a
					// if (el3.getAttribute("title") != null) {
					// FileUtils.lineSeparator + "Name: " +
					// el3.getAttributeValue("title");
					// }
					// String span = e1.getValue() + senderName +
					// FileUtils.lineSeparator + e2.getValue();
					System.err.println(e1.getValue());
					System.err
					        .println("================================================================================");
					System.err.println(e2.getValue());
					Element commenter = e1.getChild("a", e1.getNamespace());
					
					String commenterUsername = commenter.getContent(0) != null ? commenter.getContent(0).getValue()
					        .trim() : null;
					String commenterFullname = ((((commenter.getAttributes() != null) && (commenter.getAttributes()
					        .size() > 2))) ? "" : null);
					Person commentAuthor = this.personManager.getPerson(new Person(commenterFullname,
					        commenterUsername, null));
					String datetime = e1.getContent(0).getValue().trim();
					datetime = datetime.substring(datetime.indexOf(" ") + 1, datetime.length());
					DateTime commentTimestamp = DateTimeUtils.parseDate(datetime);
					String commentBody = e2.getValue().trim();
					Comment comment = new Comment(bugReport, commentAuthor, commentTimestamp, commentBody);
					
					if (Logger.logDebug()) {
						Logger.debug("Found comment: " + comment);
					}
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
	private void hangle(final Report bugReport, final Element e, final Element n) {
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
	public Report parse(final XmlReport xmlReport) {
		// System.err.println(document);
		// Content content = document.getContent(1);
		// Element element = content.getDocument().getRootElement();
		Element element = xmlReport.getDocument().getRootElement();
		Report bugReport = new Report();
		hangle(bugReport, element, null);
		// System.err.println(bugReport);
		
		return bugReport;
	}
	
	@Override
	public void setup(final URI fetchURI, final URI overviewURI, final String pattern, final String username,
	        final String password, final Long startAt, final Long stopAt, final String cacheDir)
	        throws InvalidParameterException {
		super.setup(fetchURI, overviewURI, pattern, username, password, startAt, stopAt, cacheDir);
		
		if (overviewURI != null) {
			getIdsFromURI(overviewURI);
		} else {
			if (startAt == null) {
				this.startAt = 1l;
			}
			if (stopAt == null) {
				throw new InvalidParameterException("stopAt must not be null");
			}
			
			for (long i = this.startAt; i <= this.stopAt; ++i) {
				addBugId(i);
			}
		}
		
		this.initialized = true;
	}
	
}
