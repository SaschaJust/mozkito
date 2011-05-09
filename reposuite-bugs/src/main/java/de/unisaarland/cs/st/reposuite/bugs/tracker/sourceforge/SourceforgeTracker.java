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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

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
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Severity;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.AttachmentEntry;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.History;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.utils.DateTimeUtils;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SourceforgeTracker extends Tracker {
	
	private static String             submittedPattern   = "({fullname}[^(]+)\\(\\s+({username}[^\\s]+)\\s+\\)\\s+-\\s+({timestamp}\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}.*)";
	
	protected static String           groupIdPattern     = "group_id=({group_id}\\d+)";
	protected static String           atIdPattern        = "atid=({atid}\\d+)";
	protected static String           fileIdPattern      = "file_id=({fileid}\\d+)";
	protected static String           offsetPattern      = "offset=({offset}\\d+)";
	protected static String           limitPattern       = "limit=({limit}\\d+)";
	protected static String           htmlCommentPattern = "(?#special condition to check wether we got a new line after or before the match to remove one of them)(?(?=<!--.*?-->$\\s)(?#used if condition was true)<!--.*?-->\\s|(?#used if condition was false)\\s?<!--.*?-->)";
	
	private static Map<String, Field> fieldMap           = new HashMap<String, Field>() {
		                                                     
		                                                     private static final long serialVersionUID = 1L;
		                                                     
		                                                     {
			                                                     try {
				                                                     put("resolution_id",
				                                                         Report.class.getDeclaredField("resolution"));
				                                                     put("status_id",
				                                                         Report.class.getDeclaredField("status"));
				                                                     put("priority",
				                                                         Report.class.getDeclaredField("priority"));
				                                                     put("close_date",
				                                                         Report.class.getDeclaredField("resolutionTimestamp"));
				                                                     put("category_id",
				                                                         Report.class.getDeclaredField("category"));
				                                                     put("artifact_group_id",
				                                                         Report.class.getDeclaredField("component"));
				                                                     // FIXME
				                                                     // what a
				                                                     // field to
				                                                     // set here
				                                                     // put("assigned_to",
				                                                     // Report.class.getDeclaredField("product"));
				                                                     put("summary",
				                                                         Report.class.getDeclaredField("subject"));
			                                                     } catch (Exception e) {
				                                                     if (Logger.logError()) {
					                                                     Logger.error("No such field in "
					                                                             + Report.class.getSimpleName() + ": "
					                                                             + e.getMessage(), e);
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
	
	private static Element getDeepestChild(final Element e) {
		@SuppressWarnings ("rawtypes")
		List children = e.getChildren();
		if (children.size() > 1) {
			return null;
		} else if (children.size() < 1) {
			return e;
		} else {
			return getDeepestChild((Element) children.get(0));
		}
	}
	
	private HistoryElement lastHistoryElement = null;
	
	private final Regex    subjectRegex       = new Regex("({subject}.*)\\s+-\\s+ID:\\s+({bugid}\\d+)$");
	
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
		Regex groupIdRegex = new Regex(groupIdPattern);
		groupIdRegex.find(uri.toString());
		String groupId = groupIdRegex.getGroup("group_id");
		if (groupId == null) {
			if (Logger.logError()) {
				Logger.error("Could not extract group_id from uri: " + uri.toString());
			}
		}
		Regex atIdRegex = new Regex(atIdPattern);
		atIdRegex.find(uri.toString());
		String atId = atIdRegex.getGroup("atid");
		if (atId == null) {
			if (Logger.logError()) {
				Logger.error("Could not extract atid from uri: " + uri.toString());
			}
		}
		
		String baseUriString = uri.toString();
		
		Regex limitRegex = new Regex(limitPattern);
		limitRegex.find(uri.toString());
		String limit = limitRegex.getGroup("limit");
		if (limit == null) {
			baseUriString += "&limit=100";
		} else {
			baseUriString = limitRegex.replaceAll(uri.toString(), "limit=100");
		}
		
		Regex offsetRegex = new Regex(offsetPattern);
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
	};
	
	public void getIdsFromURI(final URI uri) {
		if (uri.getScheme().equals("file")) {
			// FIXME this will fail on ?+*
			Regex regex = new Regex(".*" + pattern.replace(bugIdPlaceholder, "({bugid}\\d+)"));
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
	}
	
	@SuppressWarnings ("unchecked")
	private void handleDivElement(final Report bugReport,
	                              Element e,
	                              final Element n) {
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
				Regex submittedRegex = new Regex(submittedPattern);
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
			List<RegexGroup> find = subjectRegex.find(n.getValue());
			bugReport.setSubject(find.get(1).getMatch());
			// bugReport.setId(Long.parseLong(find.get(2).getMatch()));
		} else if ((e.getAttributeValue("id") != null) && e.getAttributeValue("id").equals("comment_table_container")) {
			// Comments are not properly formatted. Hacking it.
			e = (Element) (e.getChildren() != null
			                                      ? e.getChildren().get(0)
			                                      : null);
			if (e != null) {
				Element e1, e2;
				
				// Pray to god, that they won't change the layout
				e = (Element) e.getChildren().get(2); // tbody
				
				// All childs of tbody should be [TR]
				for (Object commentObject : e.getChildren()) {
					
					// insert correct comment id
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
					// System.err.println(">>>" + bugReport.getId());
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
					Comment comment = new Comment(new Integer(comment_id), commentAuthor, commentTimestamp, commentBody);
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
		} else if ((e.getAttributeValue("id") != null) && e.getAttributeValue("id").equals("filebar")) {
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
					
					Element filenameElem = getDeepestChild((Element) tableRow.getChildren().get(0));
					Element descriptionElem = getDeepestChild((Element) tableRow.getChildren().get(1));
					Element aElem = getDeepestChild((Element) tableRow.getChildren().get(2));
					if (aElem == null) {
						continue;
					}
					String href = aElem.getAttributeValue("href");
					List<RegexGroup> find = new Regex(fileIdPattern).find(href);
					if ((find == null) || (find.size() < 2)) {
						continue;
					}
					String attachId = find.get(1).getMatch();
					AttachmentEntry attachment = new AttachmentEntry(attachId);
					
					String description = descriptionElem.getText();
					Regex htmlCommentRegex = new Regex(htmlCommentPattern, Pattern.MULTILINE | Pattern.DOTALL);
					description = htmlCommentRegex.removeAll(description).trim();
					attachment.setDescription(description.replaceAll("\"", ""));
					
					String filename = filenameElem.getText();
					filename = htmlCommentRegex.removeAll(filename).trim();
					attachment.setFilename(filename.replaceAll("\"", ""));
					
					if (href.startsWith("/")) {
						href = "http://sourceforge.net" + href;
					} else {
						href = "http://sourceforge.net/" + href;
					}
					try {
						attachment.setLink(new URL(href));
					} catch (MalformedURLException e1) {
						if (Logger.logDebug()) {
							Logger.debug("Could not create link URL when parsing attachemnt.", e);
						}
					}
					bugReport.addAttachmentEntry(attachment);
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
					Element authorElement = ((Element) tableRow.getChildren().get(3)).getChild("a",
					                                                                           tableRow.getNamespace());
					if (authorElement == null) {
						authorElement = getDeepestChild((Element) ((Element) tableRow.getChildren().get(3)).getContent()
						                                                                                   .get(0));
					}
					
					String authorFullname = authorElement != null
					                                             ? authorElement.getAttributeValue("title")
					                                             : null;
					if (authorFullname != null) {
						authorFullname = authorFullname.trim();
					}
					String authorUsername = authorElement != null
					                                             ? authorElement.getValue()
					                                             : null;
					if (authorUsername != null) {
						authorUsername = authorUsername.trim();
					}
					
					Person author = new Person(authorUsername, authorFullname, null);
					
					Field field = null;
					field = fieldMap.get(fieldElement.getValue().toLowerCase().trim());
					if (field == null) {
						if (fieldElement.getValue().toLowerCase().trim().equals("file added")) {
							// search for attachment and set fields
							String[] splitValues = oldValueElement.getValue().trim().split(":");
							if (splitValues.length != 2) {
								if (Logger.logWarn()) {
									Logger.warn("Could not identify attachment file. Failed to extract file id.");
								}
							}
							String attachId = splitValues[0].trim();
							for (AttachmentEntry attachment : bugReport.getAttachmentEntries()) {
								if (attachment.getId().equals(attachId)) {
									attachment.setTimestamp(DateTimeUtils.parseDate(datetimeElement.getValue()));
									attachment.setAuthor(author);
									break;
								}
							}
						} else {
							if (Logger.logWarn()) {
								Logger.warn("Field not found: " + fieldElement.getValue().toLowerCase().trim());
							}
						}
						continue;
					}
					
					DateTime dateTime = DateTimeUtils.parseDate(datetimeElement.getValue());
					
					if (lastHistoryElement == null) {
						lastHistoryElement = new HistoryElement(bugReport.getId(), author,
						                                        DateTimeUtils.parseDate(datetimeElement.getValue()));
					} else {
						if (!lastHistoryElement.getTimestamp().isEqual(dateTime)) {
							if (!bugReport.addHistoryElement(lastHistoryElement)) {
								if (Logger.logWarn()) {
									Logger.warn("Could not add historyElement " + lastHistoryElement.toString());
								}
							}
							lastHistoryElement = new HistoryElement(bugReport.getId(), author,
							                                        DateTimeUtils.parseDate(datetimeElement.getValue()));
						}
					}
					
					// FIXME this method call fails
					// Report historicReport = bugReport.timewarp(dateTime);
					// Object newValue =
					// historicReport.getField(field.getName());
					
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
						// FIXEME the last is wrong since the history gets
						// parsed from newer to older. Anyway the last is
						// the first state
						newValue = history.first().get(field.getName()).getFirst();
					}
					
					Object oldValue = oldValueElement.getValue().trim();
					
					if (field.getName().equalsIgnoreCase("PRIORITY")) {
						
						oldValue = buildPriority(oldValue.toString());
					} else if (field.getName().equalsIgnoreCase("RESOLUTION")) {
						oldValue = buildResolution(oldValue.toString());
					} else if (field.getName().equalsIgnoreCase("SEVERITY")) {
						oldValue = buildSeverity(oldValue.toString());
					} else if (field.getName().equalsIgnoreCase("STATUS")) {
						oldValue = buildStatus(oldValue.toString());
					} else if (field.getName().equalsIgnoreCase("TYPE")) {
						oldValue = buildType(oldValue.toString());
					} else if (field.getName().equalsIgnoreCase("RESOLUTIONTIMESTAMP")) {
						if (oldValue.toString().trim().equals("-")) {
							continue;
						}
						oldValue = DateTimeUtils.parseDate(oldValue.toString());
					}
					
					Tuple<Object, Object> tuple = new Tuple<Object, Object>(oldValue, newValue);
					HashMap<String, Tuple<Object, Object>> valueMap = new HashMap<String, Tuple<Object, Object>>();
					valueMap.put(field.getName(), tuple);
					lastHistoryElement.addChange(valueMap);
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
	private void hangle(final Report bugReport,
	                    final Element e,
	                    final Element n) {
		if (((e.getAttributeValue("class") != null) && (e.getAttributeValue("class").startsWith("yui-u") || e.getAttributeValue("class")
		                                                                                                     .startsWith("yui-g")))
		        || ((e.getAttributeValue("id") != null) && (e.getAttributeValue("id").equals("comment_table_container")
		                || e.getAttributeValue("id").equals("commentbar")
		                || e.getAttributeValue("id").equals("filebar") || e.getAttributeValue("id").equals("changebar")))) {
			
			// if ((e.getAttribute("class") != null) &&
			// (e.getAttributeValue("class").startsWith("yui-g"))) {
			// details and header
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
		Report bugReport = new Report(xmlReport.getId());
		bugReport.setLastFetch(xmlReport.getFetchTime());
		bugReport.setHash(xmlReport.getMd5());
		hangle(bugReport, element, null);
		
		// check if there is a non-added
		if (lastHistoryElement != null) {
			if (!bugReport.addHistoryElement(lastHistoryElement)) {
				if (Logger.logWarn()) {
					Logger.warn("Could not add historyElement " + lastHistoryElement.toString());
				}
			}
		}
		
		bugReport.setType(Type.BUG);
		
		return bugReport;
	}
	
	@Override
	public void setup(final URI fetchURI,
	                  final URI overviewURI,
	                  final String pattern,
	                  final String username,
	                  final String password,
	                  final Long startAt,
	                  final Long stopAt,
	                  final String cacheDir) throws InvalidParameterException {
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
		
		initialized = true;
	}
}
