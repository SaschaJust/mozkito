package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Priority;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Status;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Type;
import de.unisaarland.cs.st.reposuite.exceptions.FetchException;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonManager;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.IOUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.RawContent;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

public class JiraXMLParser {
	
	protected static Regex             idRegex        = new Regex("^[^-]+-({bugid}\\d+)");
	protected static DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("E, dd MMM yyyy HH:mm:ss Z");
	protected static DateTimeFormatter dateTimeHistoryFormat = DateTimeFormat.forPattern("dd/MMM/yy hh:mm a");
	protected static Namespace         namespace      = Namespace.getNamespace("http://www.w3.org/1999/xhtml");
	
	protected static Element getElement(final Element root, final Namespace namespace, final String tag, final String attribute, final String value){
		@SuppressWarnings ("unchecked") List<Element> children = root.getChildren(tag, namespace);
		for (Element child : children) {
			if ((child.getAttributeValue(attribute) != null) && (child.getAttributeValue(attribute).equals(value))) {
				return child;
			}
		}
		throw new NoSuchElementException("Could not find <" + tag + "> tag with attribute `" + attribute + "` set to `"
				+ value + "` in namespace `" + namespace + "` for parent `" + root.toString() + "`");
	}
	
	protected static Priority getPriority(final String prioString) {
		if (prioString.equals("Blocker")) {
			return Priority.VERY_HIGH;
		} else if (prioString.equals("Critical")) {
			return Priority.HIGH;
		} else if (prioString.equals("Major")) {
			return Priority.NORMAL;
		} else if (prioString.equals("Minor")) {
			return Priority.LOW;
		} else if (prioString.equals("Trivial")) {
			return Priority.VERY_LOW;
		} else {
			return Priority.UNKNOWN;
		}
	}
	
	protected static Resolution getResolution(final String resString) {
		if (resString.equals("Won't Fix")) {
			return Resolution.WONT_FIX;
		} else if (resString.equals("Duplicate")) {
			return Resolution.DUPLICATE;
		} else if (resString.equals("Incomplete")) {
			return Resolution.UNRESOLVED;
		} else if (resString.equals("Cannot Reproduce")) {
			return Resolution.WORKS_FOR_ME;
		} else if (resString.equals("Not A Bug")) {
			return Resolution.INVALID;
		} else if (resString.equals("Unresolved")) {
			return Resolution.UNRESOLVED;
		} else if (resString.equals("Fixed")) {
			return Resolution.RESOLVED;
		} else {
			return Resolution.UNKNOWN;
		}
	}
	
	protected static Status getStatus(final String statusString) {
		if (statusString.equals("Open")) {
			return Status.VERIFIED;
		} else if (statusString.equals("In Progress")) {
			return Status.IN_PROGRESS;
		} else if (statusString.equals("Reopened")) {
			return Status.REOPENED;
		} else if (statusString.equals("Resolved")) {
			return Status.CLOSED;
		} else if (statusString.equals("Closed")) {
			return Status.CLOSED;
		} else if (statusString.equals("Iteration")) {
			return Status.IN_PROGRESS;
		} else if (statusString.equals("Submitted")) {
			return Status.NEW;
		} else if (statusString.equals("Analysis")) {
			return Status.NEW;
		} else if (statusString.equals("Patch Pending")) {
			return Status.IN_PROGRESS;
		} else if (statusString.equals("With Customer")) {
			return Status.IN_PROGRESS;
		} else {
			return Status.UNKNOWN;
		}
	}
	
	private static void handleComments(final List<Element> comments, final Report report,
			final PersonManager personManager) {
		for (Element comment : comments) {
			Person author = personManager.getPerson(new Person(comment.getAttributeValue("author"), null, null));
			DateTime commentDate = dateTimeFormat.parseDateTime(comment.getAttributeValue("created"));
			String commentText = comment.getText();
			if ((report.getResolutionTimestamp() != null) && (report.getResolutionTimestamp().isEqual(commentDate))) {
				report.setResolver(author);
			}
			report.addComment(new Comment(report, author, commentDate, commentText));
		}
	}
	
	@SuppressWarnings ("unchecked")
	public static void handleHistory(final URI historyUri, final Report report, final PersonManager personManager)
	throws UnsupportedProtocolException, JDOMException, IOException, SecurityException,
	NoSuchFieldException {
		Condition.notNull(historyUri);
		Condition.notNull(report);
		Condition.notNull(personManager);
		
		try{
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
			
			
			Element nextElem = getElement(body, namespace, "div", "id", "main-content");
			nextElem = getElement(nextElem, namespace, "div", "class", "active-area");
			nextElem = getElement(nextElem, namespace, "div", "id", "primary");
			nextElem = getElement(nextElem, namespace, "div", "class", "content");
			nextElem = getElement(nextElem, namespace, "div", "id", "activitymodule");
			nextElem = getElement(nextElem, namespace, "div", "class", "mod-content");
			nextElem = getElement(nextElem, namespace, "div", "id", "issue_actions_container");
			
			List<Element> changeHistoryItems = nextElem.getChildren("div", namespace);
			for (Element changeHistoryItem : changeHistoryItems) {
				if (changeHistoryItem.getAttributeValue("class").equals("issue-data-block")) {
					List<Element> actionContainers = changeHistoryItem.getChildren("div", namespace);
					for (Element actionContainer : actionContainers) {
						Person author = null;
						DateTime timestamp = null;
						String oldValue = null;
						String newValue = null;
						
						
						Element actionDetails = getElement(actionContainer, namespace, "div", "class", "action-details");
						Element actionDetailsA = getElement(actionDetails, namespace, "a", "class",
						"user-hover user-avatar");
						String email = actionDetailsA.getAttributeValue("rel");
						String fullname = actionDetailsA.getText();
						author = personManager.getPerson(new Person(null, fullname, email));
						
						Element date = getElement(actionDetails, namespace, "span", "class", "date");
						String dateString = date.getText();
						if (dateString != null) {
							timestamp = dateTimeHistoryFormat.parseDateTime(dateString);
						}
						
						Element actionBody = getElement(actionContainer, namespace, "div", "class", "action-body");
						Element table = actionBody.getChild("table", namespace);
						if (table == null) {
							if (Logger.logError()) {
								Logger.error("Error while parsing jira history. HTML structure unknown: could not find table in actionBody");
							}
							return;
						}
						
						Element tbody = table.getChild("tbody", namespace);
						if (tbody == null) {
							if (Logger.logError()) {
								Logger.error("Error while parsing jira history. HTML structure unknown: could not find tbody in actionBody");
							}
							return;
						}
						
						List<Element> trs = tbody.getChildren("tr", namespace);
						HistoryElement hElement = new HistoryElement(author, report, null, null, null, timestamp);
						
						for (Element tr : trs) {
							if (tr == null) {
								if (Logger.logError()) {
									Logger.error("Error while parsing jira history. HTML structure unknown: could not find tr in actionBody");
								}
								return;
							}
							String fieldString = getElement(tr, namespace, "td", "class", "activity-name").getText().trim();
							oldValue = getElement(tr, namespace, "td", "class", "activity-old-val").getText().trim();
							newValue = getElement(tr, namespace, "td", "class", "activity-new-val").getText().trim();
							
							
							if(fieldString.equals("Status")){
								hElement.addChangedValue(Report.class.getDeclaredField("status"), getStatus(oldValue),
										getStatus(newValue));
							}else if(fieldString.equals("Resolution")){
								hElement.addChangedValue(Report.class.getDeclaredField("resolution"),
										getResolution(oldValue), getResolution(newValue));
							} else if (fieldString.equals("Priority")) {
								hElement.addChangedValue(Report.class.getDeclaredField("priority"),
										getPriority(oldValue), getPriority(newValue));
							}
						}
						if (hElement.getChangedValues().keySet().size() > 0) {
							report.addHistoryElement(hElement);
						}
					}
				}
			}
			
		} catch (NoSuchElementException e) {
			if (Logger.logError()) {
				Logger.error("Error while parsing jira history. HTML structure unknown: " + e.getMessage(), e);
			}
			return;
		} catch (FetchException e){
			if (Logger.logError()) {
				Logger.error("Error while fetching jira history. URL not found: " + e.getMessage(), e);
			}
			return;
		}
		
	}
	
	@SuppressWarnings ("unchecked")
	private static void handleIssueLinks(final List<Element> elements, final Report report,
			final PersonManager personManager) {
		Condition.notNull(elements);
		Condition.notNull(report);
		Condition.notNull(personManager);
		
		for(Element issueLinkType : elements){
			if(issueLinkType.getName().equals("issuelinktype")){
				List<Element> links = issueLinkType.getChildren();
				for(Element link : links){
					if(link.getName().equals("inwardlinks") || link.getName().equals("outwardlinks")){
						List<Element> issueLinks = link.getChildren("issuelink");
						for(Element issueLink : issueLinks){
							Element issueKey = issueLink.getChild("issuekey");
							if(issueKey != null){
								List<RegexGroup> groups= idRegex.find(issueKey.getText());
								if ((groups == null) || (groups.size() != 2)) {
									if (Logger.logError()) {
										Logger.error("Error while parsing Jira report " + issueKey.getText()
												+ ". Cannot determine report id. Abort!");
									}
									return;
								}
								report.addSibling(new Long(groups.get(1).getMatch()).longValue());
							}
						}
					}
				}
			}
		}
		
	}
	
	@SuppressWarnings ("unchecked")
	public static void handleRoot(final Report report, final Element root, final PersonManager personManager) {
		Condition.notNull(report);
		Condition.notNull(root);
		Condition.notNull(personManager);
		Condition.equals(root.getName(), "item");
		
		List<Element> children = root.getChildren();
		for (Element element : children) {
			if (element.getName().equals("title")) {
				report.setSubject(element.getText());
			} else if (element.getName().equals("description")) {
				report.setDescription(element.getText());
			} else if (element.getName().equals("key")) {
				List<RegexGroup> groups = idRegex.find(element.getText());
				if ((groups == null) || (groups.size() != 2)) {
					if (Logger.logError()) {
						Logger.error("Error while parsing Jira report " + element.getText()
								+ ". Cannot determine report id. Abort!");
					}
					return;
				}
				report.setId(new Long(groups.get(1).getMatch()).longValue());
			} else if (element.getName().equals("summary")) {
				report.setSummary(element.getText());
			} else if (element.getName().equals("type")) {
				String typeString = element.getText();
				try {
					Type type = Type.valueOf(typeString.toUpperCase());
					report.setType(type);
				} catch (IllegalArgumentException e) {
					if (typeString.equals("Improvement") || typeString.equals("New Feature")) {
						report.setType(Type.RFE);
					} else if (typeString.equals("Sub-Task")) {
						// FIXME get the type of the parent report
						report.setType(Type.OTHER);
					} else {
						report.setType(Type.OTHER);
					}
				}
			} else if (element.getName().equals("priority")) {
				String prioString = element.getText();
				report.setPriority(getPriority(prioString));
			} else if (element.getName().equals("status")) {
				String statusString = element.getText();
				report.setStatus(getStatus(statusString));
			} else if (element.getName().equals("resolution")) {
				String resString = element.getText();
				report.setResolution(getResolution(resString));
			} else if (element.getName().equals("assignee")) {
				String username = element.getAttributeValue("username");
				if ((username != null) && (!username.equals("-1"))) {
					String name = element.getText();
					if (name.equals(username)) {
						report.setAssignedTo(personManager.getPerson(new Person(username, null, null)));
					} else {
						report.setAssignedTo(personManager.getPerson(new Person(username, name, null)));
					}
				}
			} else if (element.getName().equals("reporter")) {
				String username = element.getAttributeValue("username");
				if ((username != null) && (!username.equals("-1"))) {
					String name = element.getText();
					if (name.equals(username)) {
						report.setSubmitter(personManager.getPerson(new Person(username, null, null)));
					} else {
						report.setSubmitter(personManager.getPerson(new Person(username, name, null)));
					}
				}
			} else if (element.getName().equals("created")) {
				DateTime dateTime = dateTimeFormat.parseDateTime(element.getText());
				if (dateTime != null) {
					report.setCreationTimestamp(dateTime);
				}
			} else if (element.getName().equals("updated")) {
				DateTime dateTime = dateTimeFormat.parseDateTime(element.getText());
				if (dateTime != null) {
					report.setLastUpdateTimestamp(dateTime);
				}
			} else if (element.getName().equals("comments")) {
				handleComments(element.getChildren("comment"), report, personManager);
			} else if (element.getName().equals("issuelinks")) {
				handleIssueLinks(element.getChildren(), report, personManager);
			} else if (element.getName().equals("resolved")) {
				DateTime dateTime = dateTimeFormat.parseDateTime(element.getText());
				if (dateTime != null) {
					report.setResolutionTimestamp(dateTime);
				}
			} else if (element.getName().equals("version")) {
				report.setVersion(element.getText());
			} else if (element.getName().equals("component")) {
				report.setComponent(element.getText());
			}
		}
	}
	
}
