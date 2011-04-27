package de.unisaarland.cs.st.reposuite.bugs.tracker.bugzilla;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Severity;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.AttachmentEntry;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.FetchException;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.utils.DateTimeUtils;
import de.unisaarland.cs.st.reposuite.utils.IOUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.RawContent;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

public class BugzillaXMLParser {
	
	protected static Namespace namespace    = Namespace.getNamespace("http://www.w3.org/1999/xhtml");
	
	protected static Regex     siblingRegex = new Regex("bug\\s+({sibling}\\d+)");
	protected static Regex     dateRegex    = new Regex("yyyy-MM-dd HH:mm:ss Z");
	
	protected static List<AttachmentEntry> extractAttachments(final Element rootElement,
	                                                          final BugzillaTracker tracker) {
		List<AttachmentEntry> result = new LinkedList<AttachmentEntry>();
		
		@SuppressWarnings ("unchecked")
		List<Element> attachElems = rootElement.getChildren("attachment");
		
		for (Element element : attachElems) {
			
			Element attachIdElem = element.getChild("attachid", element.getNamespace());
			if (attachIdElem == null) {
				continue;
			}
			String attachId = attachIdElem.getText();
			// https://bugs.eclipse.org/bugs/attachment.cgi?id=82463
			// String attachURL =
			AttachmentEntry attachmentEntry = new AttachmentEntry(attachId);
			
			Element attacherElem = element.getChild("attacher", element.getNamespace());
			if (attacherElem == null) {
				continue;
			}
			attachmentEntry.setAuthor(new Person(attacherElem.getText(), null, null));
			
			Element descElem = element.getChild("desc", element.getNamespace());
			if (descElem == null) {
				continue;
			}
			attachmentEntry.setDescription(descElem.getText());
			
			Element filenameElem = element.getChild("filename", element.getNamespace());
			if (filenameElem == null) {
				continue;
			}
			attachmentEntry.setFilename(filenameElem.getText());
			
			Element dateElem = element.getChild("date", element.getNamespace());
			if (dateElem == null) {
				continue;
			}
			attachmentEntry.setTimestamp(DateTimeUtils.parseDate(dateElem.getText()));
			
			String uri = tracker.getUri().toString();
			if (!uri.endsWith("/")) {
				uri += "/";
			}
			uri += "attachment.cgi?id=" + attachId;
			
			try {
				attachmentEntry.setLink(new URL(uri));
			} catch (MalformedURLException e1) {
				
			}
			
			Element deltaTSElem = element.getChild("delta_ts", element.getNamespace());
			if (deltaTSElem == null) {
				continue;
			}
			attachmentEntry.setDeltaTS(DateTimeUtils.parseDate(deltaTSElem.getText()));
			
			Element typeElem = element.getChild("type", element.getNamespace());
			if (typeElem == null) {
				continue;
			}
			attachmentEntry.setMime(typeElem.getText());
			
			Element sizeElem = element.getChild("size", element.getNamespace());
			if (sizeElem == null) {
				continue;
			}
			
			try {
				long size = new Long(sizeElem.getText());
				attachmentEntry.setSize(size);
			} catch (NumberFormatException e) {
				
			}
			
			result.add(attachmentEntry);
		}
		
		return result;
	}
	
	/**
	 * @param string
	 * @return
	 */
	protected static Priority getPriority(final String string) {
		String priorityString = string.toUpperCase();
		if (priorityString.equals("P1")) {
			return Priority.VERY_HIGH;
		} else if (priorityString.equals("P2")) {
			return Priority.HIGH;
		} else if (priorityString.equals("P3")) {
			return Priority.NORMAL;
		} else if (priorityString.equals("P4")) {
			return Priority.LOW;
		} else if (priorityString.equals("P5")) {
			return Priority.VERY_LOW;
		} else {
			return Priority.UNKNOWN;
		}
	}
	
	/**
	 * @param string
	 * @return
	 */
	protected static Resolution getResolution(final String string) {
		String resString = string.toUpperCase();
		if (resString.equals("FIXED")) {
			return Resolution.RESOLVED;
		} else if (resString.equals("INVALID")) {
			return Resolution.INVALID;
		} else if (resString.equals("WONTFIX")) {
			return Resolution.WONT_FIX;
		} else if (resString.equals("LATER")) {
			return Resolution.UNRESOLVED;
		} else if (resString.equals("REMIND")) {
			return Resolution.UNRESOLVED;
		} else if (resString.equals("DUPLICATE")) {
			return Resolution.DUPLICATE;
		} else if (resString.equals("WORKSFORME")) {
			return Resolution.WORKS_FOR_ME;
		} else if (resString.equals("DUPLICATE")) {
			return Resolution.DUPLICATE;
		} else if (resString.equals("NOT_ECLIPSE")) {
			return Resolution.INVALID;
		} else {
			return Resolution.UNKNOWN;
		}
	}
	
	protected static Severity getSeverity(final String string) {
		String serverityString = string.toLowerCase();
		if (serverityString.equals("blocker")) {
			return Severity.BLOCKER;
		} else if (serverityString.equals("critical")) {
			return Severity.CRITICAL;
		} else if (serverityString.equals("major")) {
			return Severity.MAJOR;
		} else if (serverityString.equals("normal")) {
			return Severity.NORMAL;
		} else if (serverityString.equals("minor")) {
			return Severity.MINOR;
		} else if (serverityString.equals("trivial")) {
			return Severity.TRIVIAL;
		} else if (serverityString.equals("enhancement")) {
			return Severity.ENHANCEMENT;
		} else {
			if (Logger.logWarn()) {
				Logger.warn("Bugzilla severity `" + serverityString + "` could not be mapped. Ignoring it.");
			}
			return null;
		}
	}
	
	/**
	 * @param string
	 * @return
	 */
	protected static Status getStatus(final String string) {
		String statusString = string.toUpperCase();
		if (statusString.equals("UNCONFIRMED")) {
			return Status.UNCONFIRMED;
		} else if (statusString.equals("NEW")) {
			return Status.NEW;
		} else if (statusString.equals("ASSIGNED")) {
			return Status.ASSIGNED;
		} else if (statusString.equals("REOPENED")) {
			return Status.REOPENED;
		} else if (statusString.equals("RESOLVED")) {
			return Status.CLOSED;
		} else if (statusString.equals("VERIFIED")) {
			return Status.VERIFIED;
		} else if (statusString.equals("CLOSED")) {
			return Status.CLOSED;
		} else {
			return Status.UNKNOWN;
		}
	}
	
	/**
	 * @param historyUri
	 * @param report
	 * @throws UnsupportedProtocolException
	 * @throws FetchException
	 * @throws JDOMException
	 * @throws IOException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	@NoneNull
	public static void handleHistory(final URI historyUri,
	                                 final Report report) throws UnsupportedProtocolException,
	                                                     FetchException,
	                                                     JDOMException,
	                                                     IOException,
	                                                     SecurityException,
	                                                     NoSuchFieldException {
		RawContent rawContent = IOUtils.fetch(historyUri);
		BufferedReader reader = new BufferedReader(new StringReader(rawContent.getContent()));
		SAXBuilder saxBuilder = new SAXBuilder("org.ccil.cowan.tagsoup.Parser");
		Document document = saxBuilder.build(reader);
		reader.close();
		
		Element rootElement = document.getRootElement();
		if (!rootElement.getName().equals("html")) {
			if (Logger.logError()) {
				Logger.error("Error while parsing bugzilla report history. Root element expectedto have `<html>` tag as root element. Got <"
				        + rootElement.getName() + ">.");
			}
			return;
		}
		
		Element body = rootElement.getChild("body", namespace);
		if (body == null) {
			if (Logger.logError()) {
				Logger.error("Error while parsing bugzilla report history. No <body> tag found.");
			}
			return;
		}
		@SuppressWarnings ("unchecked")
		List<Element> bodyChildren = body.getChildren();
		for (Element bodyChild : bodyChildren) {
			if (bodyChild.getName().equals("div") && (bodyChild.getAttribute("id") != null)
			        && (bodyChild.getAttributeValue("id").equals("bugzilla-body"))) {
				Element table = bodyChild.getChild("table", namespace);
				if (table == null) {
					if (Logger.logError()) {
						Logger.error("Error while parsing bugzilla report history. No <table> tag found.");
					}
					return;
				}
				
				Element tbody = table.getChild("tbody", namespace);
				if (tbody == null) {
					if (Logger.logError()) {
						Logger.error("Error while parsing bugzilla report history. No <tbody> tag found.");
					}
					return;
				}
				
				@SuppressWarnings ("unchecked")
				List<Element> trs = new ArrayList<Element>(tbody.getChildren("tr", namespace));
				if (trs.size() > 0) {
					trs.remove(0);
				}
				
				int rowspan = 0;
				HistoryElement hElement = null;
				Person historyAuthor = null;
				DateTime dateTime = null;
				for (Element tr : trs) {
					int whatIndex = 2;
					@SuppressWarnings ("unchecked")
					List<Element> tds = tr.getChildren("td", namespace);
					if ((tds.size() < 5) && (rowspan < 1)) {
						if (Logger.logError()) {
							Logger.error("Error while parsing bugzilla report history. Expected at least 5 table columns, found :"
							        + tds.size());
						}
						return;
					} else if (tds.size() < 3) {
						if (Logger.logError()) {
							Logger.error("Error while parsing bugzilla report history. Expected at least 3 table columns, found :"
							        + tds.size());
						}
						return;
					}
					if (rowspan == 0) {
						String username = tds.get(0).getText().trim();
						String rowspanString = tds.get(0).getAttributeValue("rowspan");
						if (rowspanString != null) {
							rowspan = Integer.valueOf(rowspanString).intValue() - 1;
						}
						historyAuthor = new Person(username, null, null);
						dateTime = DateTimeUtils.parseDate(tds.get(1).getText().trim());
						if (hElement != null) {
							report.addHistoryElement(hElement);
						}
						hElement = new HistoryElement(report.getId(), historyAuthor, dateTime);
						
					} else {
						--rowspan;
						whatIndex -= 2;
					}
					
					// TODO Add test cases for history parsing
					String what = tds.get(whatIndex).getText().trim().toLowerCase();
					String removed = tds.get(++whatIndex).getText().trim();
					String added = tds.get(++whatIndex).getText().trim();
					
					String field = null;
					if (what.equals("priority")) {
						field = "priority";
						hElement.addChangedValue(field, getPriority(removed), getPriority(added));
						continue;
					} else if (what.equals("summary")) {
						field = ("summary");
						hElement.addChangedValue(field, removed, added);
						continue;
					} else if (what.equals("resolution")) {
						field = ("resolution");
						hElement.addChangedValue(field, getResolution(removed), getResolution(added));
						// set report resolution date and resolver
						if (getResolution(added).equals(Resolution.RESOLVED)) {
							report.setResolver(historyAuthor);
							report.setResolutionTimestamp(dateTime);
						}
						continue;
					} else if (what.equals("assignee")) {
						field = ("assignedTo");
						Person oldValue = new Person(removed, null, null);
						Person newValue = new Person(added, null, null);
						hElement.addChangedValue(field, oldValue, newValue);
						continue;
					} else if (what.equals("target milestone")) {
						
					} else if (what.equals("cc")) {
						
					} else if (what.equals("component")) {
						field = ("component");
						hElement.addChangedValue(field, removed, added);
						continue;
					} else if (what.equals("summary")) {
						field = ("summary");
						hElement.addChangedValue(field, removed, added);
						continue;
					} else if (what.equals("severity")) {
						field = ("severity");
						hElement.addChangedValue(field, getSeverity(removed), getSeverity(added));
						continue;
					} else if (what.equals("blocks")) {
						// TODO how shall I do that?
					} else if (what.equals("depends on")) {
						// TODO how shall I do that?
					} else if (what.equals("status")) {
						field = ("status");
						hElement.addChangedValue(field, getStatus(removed), getStatus(added));
						continue;
					} else if (what.equals("product")) {
						field = ("product");
						hElement.addChangedValue(field, removed, added);
						continue;
					} else if (what.equals("category")) {
						field = ("category");
						hElement.addChangedValue(field, removed, added);
						continue;
					}
				}
				if (hElement != null) {
					report.addHistoryElement(hElement);
				}
			}
		}
	}
	
	@NoneNull
	private static void handleLongDesc(final Report report,
	                                   final Element rootElement) {
		Condition.check(rootElement.getName().equals("long_desc"), "The root element must be 'long_desc'.");
		
		Element who = rootElement.getChild("who");
		Person author = null;
		if (who == null) {
			if (Logger.logWarn()) {
				Logger.warn("Found bugziall comment with no author.");
			}
		} else {
			String authorUsername = who.getText().trim();
			String authorName = who.getAttributeValue("name").trim();
			author = new Person(authorUsername, authorName, null);
		}
		
		Element bug_when = rootElement.getChild("bug_when");
		DateTime timestamp = null;
		if (bug_when == null) {
			if (Logger.logWarn()) {
				Logger.warn("Found bugzilla comment without submit date.");
			}
		} else {
			timestamp = DateTimeUtils.parseDate(bug_when.getText().trim());
		}
		
		Element thetext = rootElement.getChild("thetext");
		String message = "";
		if (thetext == null) {
			if (Logger.logWarn()) {
				Logger.warn("Found bugzilla comment without text.");
			}
		} else {
			message = thetext.getText().trim();
		}
		
		int commentid = report.getComments().size() + 1;
		
		if (!message.equals("")) {
			List<List<RegexGroup>> groupsList = siblingRegex.findAll(message);
			if (groupsList != null) {
				for (List<RegexGroup> groups : groupsList) {
					for (RegexGroup group : groups) {
						if (group.getName().equals("sibling")) {
							try {
								Long sibling = new Long(group.getMatch());
								report.addSibling(sibling);
							} catch (NumberFormatException e) {
								
							}
						}
					}
				}
			}
		}
		
		if (report.getDescription() == null) {
			report.setDescription(message);
		} else {
			report.addComment(new Comment(commentid, author, timestamp, message));
		}
	}
	
	/**
	 * @param report
	 * @param rootElement
	 */
	@NoneNull
	public static void handleRoot(final Report report,
	                              final Element rootElement,
	                              final BugzillaTracker tracker) {
		Condition.check(rootElement.getName().equals("bug"), "The root element must be 'bug'.");
		
		report.setType(Type.BUG);
		
		@SuppressWarnings ({ "unchecked" })
		List<Element> elements = rootElement.getChildren();
		for (Element element : elements) {
			if (element.getName().equals("bug_id")) {
				// try {
				// report.setId(new Long(element.getText()));
				// } catch (NumberFormatException e) {
				// if (Logger.logError()) {
				// Logger.error("Bugzilla bug id `" + element.getText()
				// + "` cannot be interpreted as an long. Abort parsing.");
				// }
				// return;
				// }
				
				if (Logger.logDebug()) {
					Logger.debug("Skipping bug id parsing. Already known.");
				}
			} else if (element.getName().equals("creation_ts")) {
				DateTime creationTime = DateTimeUtils.parseDate(element.getText().trim());
				if (creationTime == null) {
					if (Logger.logWarn()) {
						Logger.warn("Bugzilla creation time `" + element.getText()
						        + "` cannot be interpreted as timestamp. Ignoring.");
					}
				} else {
					report.setCreationTimestamp(creationTime);
				}
			} else if (element.getName().equals("short_desc")) {
				report.setSubject(element.getText().trim());
			} else if (element.getName().equals("delta_ts")) {
				DateTime modificationTime = DateTimeUtils.parseDate(element.getText().trim());
				if (modificationTime == null) {
					if (Logger.logWarn()) {
						Logger.warn("Bugzilla modification time `" + element.getText()
						        + "` cannot be interpreted as timestamp. Ignoring.");
					}
				} else {
					report.setLastUpdateTimestamp(modificationTime);
				}
			} else if (element.getName().equals("classification")) {
				report.setCategory(element.getText().trim());
			} else if (element.getName().equals("product")) {
				report.setProduct(element.getText().trim());
			} else if (element.getName().equals("component")) {
				report.setComponent(element.getText().trim());
			} else if (element.getName().equals("version")) {
				report.setVersion(element.getText().trim());
			} else if (element.getName().equals("rep_platform")) {
				// TODO shall we add a field to Report
			} else if (element.getName().equals("op_sys")) {
				// TODO shall we add a field to Report
			} else if (element.getName().equals("bug_status")) {
				String statusString = element.getText().trim();
				report.setStatus(getStatus(statusString));
			} else if (element.getName().equals("resolution")) {
				String resString = element.getText().trim();
				report.setResolution(getResolution(resString));
			} else if (element.getName().equals("priority")) {
				String priorityString = element.getText().trim();
				report.setPriority(getPriority(priorityString));
			} else if (element.getName().equals("bug_severity")) {
				String serverityString = element.getText().trim().toLowerCase();
				report.setSeverity(getSeverity(serverityString));
			} else if (element.getName().equals("target_milestone")) {
				// TODO shall we add a field in Report?
			} else if (element.getName().equals("reporter")) {
				String username = element.getText().trim();
				String name = element.getAttributeValue("name").trim();
				report.setSubmitter(new Person(username, name, null));
			} else if (element.getName().equals("assigned_to")) {
				String username = element.getText().trim();
				String name = element.getAttributeValue("name").trim();
				report.setAssignedTo(new Person(username, name, null));
			} else if (element.getName().equals("long_desc")) {
				handleLongDesc(report, element);
			} else if (element.getName().equals("blocked")) {
				try {
					report.addSibling(new Long(element.getText()));
				} catch (NumberFormatException e) {
					
				}
			} else if (element.getName().equals("dependson")) {
				try {
					report.addSibling(new Long(element.getText()));
				} catch (NumberFormatException e) {
					
				}
			}
			
			List<AttachmentEntry> attachments = extractAttachments(rootElement, tracker);
			for (AttachmentEntry attachment : attachments) {
				report.addAttachmentEntry(attachment);
			}
		}
	}
}
