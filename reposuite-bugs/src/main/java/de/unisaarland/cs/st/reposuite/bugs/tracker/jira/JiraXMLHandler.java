package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import java.util.List;

import org.jdom.Element;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Priority;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Status;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Type;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonManager;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

public class JiraXMLHandler {
	
	protected static Regex idRegex = new Regex("^[^-]+-({bugid}\\d+)");
	protected static DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("E, dd MMM yyyy HH:mm:ss Z");
	
	// TODO subTasks
	
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
				if ((groups == null) || (groups.size() != 1)) {
					if (Logger.logError()) {
						Logger.error("Error while parsing Jira report " + element.getText()
								+ ". Cannot determine report id. Abort!");
					}
					return;
				}
				report.setId(new Long(groups.get(0).getMatch()).longValue());
			} else if (element.getName().equals("summary")) {
				report.setSummary(element.getText());
			} else if (element.getName().equals("type")) {
				String typeString = element.getText();
				try{
					Type type = Type.valueOf(typeString.toUpperCase());
					report.setType(type);
				}catch(IllegalArgumentException e){
					if (typeString.equals("Improvement") || typeString.equals("New Feature")) {
						report.setType(Type.RFE);
					}else if(typeString.equals("Sub-Task")){
						// FIXME get the type of the parent report
						report.setType(Type.OTHER);
					}else{
						report.setType(Type.OTHER);
					}
				}
			} else if (element.getName().equals("priority")) {
				String prioString = element.getText();
				if (prioString.equals("Blocker")) {
					report.setPriority(Priority.VERY_HIGH);
				} else if (prioString.equals("Critical")) {
					report.setPriority(Priority.HIGH);
				} else if (prioString.equals("Major")) {
					report.setPriority(Priority.NORMAL);
				} else if (prioString.equals("Minor")) {
					report.setPriority(Priority.LOW);
				} else if (prioString.equals("Trivial")) {
					report.setPriority(Priority.VERY_LOW);
				} else {
					report.setPriority(Priority.UNKNOWN);
				}
			} else if (element.getName().equals("status")) {
				String statusString = element.getText();
				if (statusString.equals("Open")) {
					report.setStatus(Status.VERIFIED);
				} else if (statusString.equals("In Progress")) {
					report.setStatus(Status.IN_PROGRESS);
				} else if (statusString.equals("Reopened")) {
					report.setStatus(Status.REOPENED);
				} else if (statusString.equals("Resolved")) {
					report.setStatus(Status.RESOLVED);
				} else if (statusString.equals("Closed")) {
					report.setStatus(Status.CLOSED);
				} else if (statusString.equals("Iteration")) {
					report.setStatus(Status.IN_PROGRESS);
				} else if (statusString.equals("Submitted")) {
					report.setStatus(Status.NEW);
				} else if (statusString.equals("Analysis")) {
					report.setStatus(Status.NEW);
				} else if (statusString.equals("Patch Pending")) {
					report.setStatus(Status.IN_PROGRESS);
				} else if (statusString.equals("With Customer")) {
					report.setStatus(Status.IN_PROGRESS);
				}else {
					report.setStatus(Status.UNKNOWN);
				}
			} else if (element.getName().equals("resolution")) {
				String resString = element.getText();
				if (resString.equals("Won't Fix")) {
					report.setResolution(Resolution.WONT_FIX);
				}else if(resString.equals("Duplicate")){
					report.setResolution(Resolution.DUPLICATE);
				}else if(resString.equals("Incomplete")){
					report.setResolution(Resolution.UNRESOLVED);
				}else if(resString.equals("Cannot Reproduce")){
					report.setResolution(Resolution.WORKS_FOR_ME);
				}else if(resString.equals("Not A Bug")){
					report.setResolution(Resolution.INVALID);
				}else if(resString.equals("Unresolved")){
					report.setResolution(Resolution.UNRESOLVED);
				}else if(resString.equals("Fixed")){
					report.setResolution(Resolution.RESOLVED);
				}else {
					report.setResolution(Resolution.UNKNOWN);
				}
				
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
			} else if (element.getName().equals("subtask")) {
				// TODO I could not find an example yet
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
