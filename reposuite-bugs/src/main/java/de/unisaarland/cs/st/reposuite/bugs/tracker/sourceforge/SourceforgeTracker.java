/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.sourceforge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.joda.time.DateTime;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.History;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Priority;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Severity;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Status;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Type;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.DateTimeUtils;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SourceforgeTracker extends Tracker {
	
	private static Regex              submittedRegex = new Regex(
	"({fullname}[^(]+)\\(\\s+({username}[^\\s]+)\\s+\\)\\s+-\\s+({timestamp}\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}.*)");
	
	protected static Regex            groupIdRegex   = new Regex("group_id=({group_id}\\d+)");
	protected static Regex            atIdRegex      = new Regex("atid=({atid}\\d+)");
	protected static Regex            offsetRegex    = new Regex("offset=({offset}\\d+)");
	protected static Regex            limitRegex     = new Regex("limit=({limit}\\d+)");
	
	private static Map<String, Field> fieldMap       = new HashMap<String, Field>() {
		
		private static final long serialVersionUID = 1L;
		
		{
			try {
				put("close_date",
						Report.class
						.getDeclaredField("resolutionTimestamp"));
				put("resolution_id",
						Report.class.getDeclaredField("resolution"));
				put("status_id",
						Report.class.getDeclaredField("status"));
			} catch (Exception e) {
				if (Logger.logError()) {
					Logger.error(
							"No such field in "
							+ Report.class.getSimpleName()
							+ ": " + e.getMessage(), e);
				}
			}
		}
	};
	
	private static Priority buildPriority(final String value) {
		// 1..9;
		// UNKNOWN, VERY_LOW, LOW, NORMAL, HIGH, VERY_HIGH;
		int priority = Integer.parseInt(value);
		switch (priority) {
			case 1:
			case 2:
				return Priority.VERY_LOW;
			case 3:
			case 4:
				return Priority.LOW;
			case 5:
				return Priority.NORMAL;
			case 6:
			case 7:
				return Priority.HIGH;
			case 8:
			case 9:
				return Priority.VERY_HIGH;
			default:
				return Priority.UNKNOWN;
		}
	}
	
	private static Resolution buildResolution(final String value) {
		// from: ACCEPTED, DUPLICATE, FIXED, INVALID, LATER, NONE,
		// OUT_OF_DATE, POSTPONED, REJECTED, REMIND, WONT_FIX,
		// WORKS_FOR_ME;
		// to: UNKNOWN, UNRESOLVED, DUPLICATE, RESOLVED, INVALID,
		// WONT_FIX, WORKS_FOR_ME;
		if (value.equalsIgnoreCase("ACCEPTED")) {
			return Resolution.UNRESOLVED;
		} else if (value.equalsIgnoreCase("DUPLICATE")) {
			return Resolution.DUPLICATE;
		} else if (value.equalsIgnoreCase("FIXED")) {
			return Resolution.RESOLVED;
		} else if (value.equalsIgnoreCase("INVALID")) {
			return Resolution.INVALID;
		} else if (value.equalsIgnoreCase("LATER")) {
			return Resolution.UNRESOLVED;
		} else if (value.equalsIgnoreCase("NONE")) {
			return Resolution.UNRESOLVED;
		} else if (value.equalsIgnoreCase("OUT_OF_DATE")) {
			return Resolution.UNKNOWN;
		} else if (value.equalsIgnoreCase("POSTPONED")) {
			return Resolution.UNRESOLVED;
		} else if (value.equalsIgnoreCase("REJECTED")) {
			return Resolution.INVALID;
		} else if (value.equalsIgnoreCase("REMIND")) {
			return Resolution.UNRESOLVED;
		} else if (value.equalsIgnoreCase("WONT_FIX")) {
			return Resolution.WONT_FIX;
		} else if (value.equalsIgnoreCase("WORKS_FOR_ME")) {
			return Resolution.WORKS_FOR_ME;
		} else {
			return Resolution.UNKNOWN;
		}
	}
	
	private static Severity buildSeverity(final String value) {
		return Severity.UNKNOWN;
	}
	
	private static Status buildStatus(final String value) {
		// from: CLOSED, DELETED, OPEN, PENDING
		// to: UNKNOWN, UNCONFIRMED, NEW, ASSIGNED, IN_PROGRESS,
		// REOPENED, RESOLVED, VERIFIED, CLOSED
		if (value.equalsIgnoreCase("CLOSED")) {
			return Status.CLOSED;
		} else if (value.equalsIgnoreCase("DELETED")) {
			return Status.CLOSED;
		} else if (value.equalsIgnoreCase("OPEN")) {
			return Status.NEW;
		} else if (value.equalsIgnoreCase("PENDING")) {
			return Status.IN_PROGRESS;
		} else {
			return Status.UNKNOWN;
		}
	}
	
	private static Type buildType(final String value) {
		if (value.equalsIgnoreCase("BUG")) {
			return Type.BUG;
		} else if (value.equalsIgnoreCase("RFE")) {
			return Type.RFE;
		} else if (value.equalsIgnoreCase("TASK")) {
			return Type.TASK;
		} else if (value.equalsIgnoreCase("TEST")) {
			return Type.TEST;
		} else {
			return Type.OTHER;
		}
	}
	
	private final Regex subjectRegex = new Regex("({subject}.*)\\s+-\\s+ID:\\s+({bugid}\\d+)$");
	
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
			if (Logger.logError()) {
				Logger.error("Cannot create XML document!", e);
			}
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error("Cannot create XML document!", e);
			}
		} catch (JDOMException e) {
			if (Logger.logError()) {
				Logger.error("Cannot create XML document!", e);
			}
		}
		
		return null;
	}
	
	protected Set<Long> getIdsFromHTTPUri(final URI uri) throws SAXException, IOException {
		groupIdRegex.find(uri.toString());
		String groupId = groupIdRegex.getGroup("group_id");
		if (groupId == null) {
			if (Logger.logError()) {
				Logger.error("Could not extract group_id from uri: " + uri.toString());
			}
		}
		atIdRegex.find(uri.toString());
		String atId = atIdRegex.getGroup("atid");
		if (atId == null) {
			if (Logger.logError()) {
				Logger.error("Could not extract atid from uri: " + uri.toString());
			}
		}
		
		String baseUriString = uri.toString();
		
		limitRegex.find(uri.toString());
		String limit = limitRegex.getGroup("limit");
		if (limit == null) {
			baseUriString += "&limit=100";
		} else {
			baseUriString = limitRegex.replaceAll(uri.toString(), "limit=100");
		}
		
		offsetRegex.find(uri.toString());
		String offsetString = offsetRegex.getGroup("offset");
		if (offsetString != null) {
			baseUriString = offsetRegex.replaceAll(uri.toString(), "");
		}
		baseUriString += "&offset=";
		Set<Long> ids = new HashSet<Long>();
		
		int offset = 0;
		boolean running = true;
		while (running) {
			String nextUri = baseUriString + offset;
			offset += 100;
			URL url = new URL(nextUri);
			SourceforgeSummaryParser parseHandler = new SourceforgeSummaryParser();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
			
			StringBuilder htmlSB = new StringBuilder();
			// write file to disk
			String line = "";
			while ((line = br.readLine()) != null) {
				htmlSB.append(line);
				htmlSB.append(FileUtils.lineSeparator);
			}
			br.close();
			
			String html = htmlSB.toString();
			
			if ((html.contains("There was an error processing your request ..."))
					|| (html.contains("No results were found to match your current search criteria."))) {
				running = false;
				break;
			}
			
			SAXBuilder saxBuilder = new SAXBuilder("org.ccil.cowan.tagsoup.Parser");
			try {
				Document document = saxBuilder.build(new StringReader(html));
				XMLOutputter outp = new XMLOutputter();
				outp.setFormat(Format.getPrettyFormat());
				String xml = outp.outputString(document);
				
				br = new BufferedReader(new StringReader(xml));
				XMLReader parser = XMLReaderFactory.createXMLReader();
				parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
				parser.setContentHandler(parseHandler);
				InputSource inputSource = new InputSource(br);
				parser.parse(inputSource);
				
				Set<Long> idSet = parseHandler.getIDs();
				if (idSet.size() < 1) {
					running = false;
				} else {
					ids.addAll(idSet);
				}
			} catch (JDOMException e) {
				if (Logger.logError()) {
					Logger.error("Could not convert overview to XHTML!", e);
				}
				return null;
			}
			
		}
		return ids;
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
		} else if ((uri.getScheme().equals("http")) || (uri.getScheme().equals("https"))) {
			try {
				Set<Long> idsFromHTTPUri = getIdsFromHTTPUri(uri);
				for (Long id : idsFromHTTPUri) {
					addBugId(id);
				}
			} catch (SAXException e) {
				if (Logger.logError()) {
					Logger.error("Could not fetch all bug report IDs from sourceforge!", e);
				}
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error("Could not fetch all bug report IDs from sourceforge!", e);
				}
			}
			
		} else {
			
		}
	};
	
	@SuppressWarnings("unchecked")
	private void handleDivElement(final Report bugReport, Element e, final Element n) {
		if (e.getName().equals("label")) {
			
			String fieldName = e.getValue().replaceFirst(":.*", "");
			String fieldValue = n.getValue();
			
			if (Logger.logTrace()) {
				Logger.trace("Found field `" + fieldName + "` with value: `" + fieldValue + "`");
			}
			
			if (fieldName.equalsIgnoreCase("Category")) {
				bugReport.setCategory(fieldValue);
			} else if (fieldName.equalsIgnoreCase("Details")) {
				bugReport.setDescription(fieldValue);
			} else if (fieldName.equalsIgnoreCase("Submitted")) {
				List<RegexGroup> find = submittedRegex.find(fieldValue);
				bugReport.setSubmitter(new Person(find.get(2).getMatch().trim(), find.get(1).getMatch().trim(), null));
				bugReport.setCreationTimestamp(DateTimeUtils.parseDate(find.get(3).getMatch().trim()));
			} else if (fieldName.equals("Status")) {
				bugReport.setStatus(buildStatus(fieldValue));
			} else if (fieldName.equals("Resolution")) {
				bugReport.setResolution(buildResolution(fieldValue));
			} else if (fieldName.equalsIgnoreCase("Assigned")) {
				if (!(fieldValue.contains("Nobody") || (fieldValue.contains("Anonym")))) {
					bugReport.setAssignedTo(new Person(null, fieldValue, null));
				}
			} else if (fieldName.equalsIgnoreCase("Group")) {
				bugReport.setComponent(fieldValue);
			} else if (fieldName.equalsIgnoreCase("Details")) {
				bugReport.setDescription(fieldValue);
			} else if (fieldName.equalsIgnoreCase("Priority")) {
				bugReport.setPriority(buildPriority(fieldValue));
			} else {
				if (Logger.logWarn()) {
					Logger.warn("Unhandled field `" + fieldName + "` with value: `" + fieldValue + "`");
				}
				
			}
		} else if ((e.getAttributeValue("class") != null) && e.getAttributeValue("class").matches("p[1-9] box")) {
			// Ok, this is the exception rule for the summary
			if (Logger.logTrace()) {
				Logger.trace("Found field: subject, value: " + n.getValue());
			}
			List<RegexGroup> find = this.subjectRegex.find(n.getValue());
			bugReport.setSubject(find.get(1).getMatch());
			bugReport.setId(Long.parseLong(find.get(2).getMatch()));
		} else if ((e.getAttributeValue("id") != null) && e.getAttributeValue("id").equals("comment_table_container")) {
			// Comments are not properly formatted. Hacking it.
			e = (Element) (e.getChildren() != null ? e.getChildren().get(0) : null);
			if (e != null) {
				Element e1, e2;
				
				// Pray to god, that they won't change the layout
				e = (Element) e.getChildren().get(2); // tbody
				
				// All childs of tbody should be [TR]
				for (Object commentObject : e.getChildren()) {
					
					//insert correct comment id
					String comment_id = ((Element) commentObject).getAttributeValue("id");
					comment_id = comment_id.replace("artifact_comment_", "");
					
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
					// System.err.println(e1.getValue());
					// System.err
					// .println("================================================================================");
					// System.err.println(e2.getValue());
					Element commenter = e1.getChild("a", e1.getNamespace());
					//					System.err.println(">>>" + bugReport.getId());
					String commenterUsername;
					String commenterFullname;
					
					Person commentAuthor = null;
					if (commenter == null) {
						commenterUsername = "nobody";
						commenterFullname = "Nobody/Anonymous";
					} else {
						if (commenter.getContent(0) != null) {
							commenterFullname = commenter.getContent(0).getValue().trim();
						} else {
							commenterFullname = null;
						}
						if ((commenter.getAttributes() != null) && (commenter.getAttributes().size() > 2)) {
							commenterUsername = ((Attribute) commenter.getAttributes().get(2)).getValue();
						} else {
							commenterUsername = null;
						}
						commentAuthor = new Person(commenterFullname, commenterUsername, null);
					}
					
					
					String datetime = e1.getContent(0).getValue().trim();
					datetime = datetime.substring(datetime.indexOf(" ") + 1, datetime.length());
					DateTime commentTimestamp = DateTimeUtils.parseDate(datetime);
					String commentBody = e2.getText().trim();
					Comment comment = new Comment(new Integer(comment_id), commentAuthor, commentTimestamp,
							commentBody);
					if ((bugReport.getLastUpdateTimestamp() == null)
							|| (commentTimestamp.isAfter(bugReport.getLastUpdateTimestamp()))) {
						bugReport.setLastUpdateTimestamp(commentTimestamp);
					}
					bugReport.addComment(comment);
					if (Logger.logDebug()) {
						Logger.debug("Found comment: " + comment);
					}
				}
			}
		} else if ((e.getAttributeValue("id") != null) && e.getAttributeValue("id").equals("changebar")) {
			int i = e.getParentElement().indexOf(e);
			for (; i < e.getParentElement().getContent().size(); ++i) {
				if (e.getParentElement().getContent().get(i) instanceof Element) {
					if (((Element) e.getParentElement().getContent().get(i)).getName().equals("div")) {
						break;
					}
				}
			}
			Element tabular = (Element) ((Element) e.getParentElement().getContent().get(i)).getContent().get(1);
			Element body = tabular.getChild("tbody", tabular.getNamespace());
			if (body != null) {
				List<Element> tableRows = body.getChildren("tr", body.getNamespace());
				for (Element tableRow : tableRows) {
					Element fieldElement = ((Element) tableRow.getChildren().get(0));
					Element oldValueElement = ((Element) tableRow.getChildren().get(1));
					Element datetimeElement = ((Element) tableRow.getChildren().get(2));
					Element authorElement = (Element) ((Element) tableRow.getChildren().get(3)).getContent().get(0);
					
					Field field = null;
					field = fieldMap.get(fieldElement.getValue().toLowerCase().trim());
					if (field == null) {
						
						if (Logger.logWarn()) {
							Logger.warn("Field not found: " + fieldElement.getValue().toLowerCase().trim());
						}
						return;
					}
					History history = bugReport.getHistory().get(field.getName());
					
					Object newValue = null;
					if (history.isEmpty()) {
						// take actual value
						Method method = null;
						try {
							method = Report.class.getMethod("get" + Character.toUpperCase(field.getName().charAt(0))
									+ field.getName().substring(1), new Class<?>[0]);
						} catch (SecurityException e1) {
							if (Logger.logError()) {
								Logger.error("Failed parsing element!", e1);
							}
						} catch (NoSuchMethodException e1) {
							if (Logger.logError()) {
								Logger.error("Failed parsing element!", e1);
							}
						}
						try {
							newValue = method.invoke(bugReport, new Object[0]);
						} catch (IllegalArgumentException e1) {
							if (Logger.logError()) {
								Logger.error("Failed parsing element!", e1);
							}
						} catch (IllegalAccessException e1) {
							if (Logger.logError()) {
								Logger.error("Failed parsing element!", e1);
							}
						} catch (InvocationTargetException e1) {
							if (Logger.logError()) {
								Logger.error("Failed parsing element!", e1);
							}
						}
					} else {
						// take this
						newValue = history.last().getNewValue(field.getName());
					}
					
					Object oldValue = null;
					
					if (field.getName().equalsIgnoreCase("PRIORITY")) {
						oldValue = buildPriority(oldValueElement.getValue());
					} else if (field.getName().equalsIgnoreCase("RESOLUTION")) {
						oldValue = buildResolution(oldValueElement.getValue());
					} else if (field.getName().equalsIgnoreCase("SEVERITY")) {
						oldValue = buildSeverity(oldValueElement.getValue());
					} else if (field.getName().equalsIgnoreCase("STATUS")) {
						oldValue = buildStatus(oldValueElement.getValue());
					} else if (field.getName().equalsIgnoreCase("TYPE")) {
						oldValue = buildType(oldValueElement.getValue());
					} else {
						oldValue = oldValueElement.getValue();
					}
					
					String authorFullname = authorElement != null ? authorElement.getAttributeValue("title") : null;
					String authorUsername = authorElement != null ? authorElement.getValue() : null;
					
					ArrayList<Object> list = new ArrayList<Object>(2);
					list.add(oldValue);
					list.add(newValue);
					Map<String, Tuple<?, ?>> map = new HashMap<String, Tuple<?, ?>>();
					
					if (authorFullname != null) {
						authorFullname = authorFullname.trim();
					}
					if (authorUsername != null) {
						authorUsername = authorUsername.trim();
					}
					bugReport.addHistoryElement(new HistoryElement(new Person(authorUsername, authorFullname, null),
							DateTimeUtils.parseDate(datetimeElement.getValue()), map));
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
	
	@SuppressWarnings("unchecked")
	private void hangle(final Report bugReport, final Element e, final Element n) {
		if (((e.getAttributeValue("class") != null) && (e.getAttributeValue("class").startsWith("yui-u") || e
				.getAttributeValue("class").startsWith("yui-g")))
				|| ((e.getAttributeValue("id") != null) && (e.getAttributeValue("id").equals("comment_table_container")
						|| e.getAttributeValue("id").equals("commentbar") || e.getAttributeValue("id").equals(
						"changebar")))) {
			
			//		if ((e.getAttribute("class") != null) && (e.getAttributeValue("class").startsWith("yui-g"))) {
			//details and header
			this.handleDivElement(bugReport, e, n);
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
		bugReport.setLastFetch(xmlReport.getFetchTime());
		bugReport.setHash(xmlReport.getMd5());
		hangle(bugReport, element, null);
		//		SortedSet<Comment> comments = bugReport.getComments();
		//		int i = comments.size();
		//		for (Comment comment : comments) {
		//			comment.setId(i--);
		//		}
		bugReport.setType(Type.BUG);
		
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
