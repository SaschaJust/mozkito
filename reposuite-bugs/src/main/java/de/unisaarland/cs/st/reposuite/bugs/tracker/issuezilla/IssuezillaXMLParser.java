/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.bugs.tracker.issuezilla;

import java.net.URI;
import java.util.List;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.jdom.Element;
import org.jdom.Namespace;
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
import de.unisaarland.cs.st.reposuite.persistence.model.Person;

public class IssuezillaXMLParser {
	
	protected static Namespace namespace    = Namespace.getNamespace("http://www.w3.org/1999/xhtml");
	
	protected static Regex     siblingRegex = new Regex(
	"\\*\\*\\* This issue has been marked as a duplicate of\\s+({sibling}\\d+) \\*\\*\\*");
	protected static Regex     dateRegex    = new Regex("yyyy-MM-dd HH:mm:ss Z");
	
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
	
	private static Type getType(final String t) {
		if (t.equals("DEFECT")) {
			return Type.BUG;
		} else if (t.equals("ENHANCEMENT")) {
			return Type.RFE;
		} else if (t.equals("FEATURE")) {
			return Type.RFE;
		} else if (t.equals("TASK")) {
			return Type.TASK;
		} else if (t.equals("PATCH")) {
			return Type.OTHER;
		}
		return Type.UNKNOWN;
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
			String authorUsername = who.getTextTrim();
			author = new Person(authorUsername, null, null);
		}
		
		Element bug_when = rootElement.getChild("issue_when");
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
	 * @param uri
	 */
	@NoneNull
	public static void handleRoot(final Report report,
	                              final Element rootElement,
	                              final IssuezillaTracker tracker,
	                              final URI uri) {
		Condition.check(rootElement.getName().equals("issue"), "The root element must be 'issue'.");
		
		@SuppressWarnings ({ "unchecked" })
		List<Element> elements = rootElement.getChildren();
		for (Element element : elements) {
			if (element.getName().equals("issue_id")) {
				boolean skip = false;
				try {
					Long id = new Long(element.getText());
					if (!id.equals(report.getId())) {
						if (Logger.logError()) {
							Logger.error("Attempting to parse bug " + id.toString() + " for bug report "
							             + report.getId());
						}
						skip = true;
					}
				} catch (NumberFormatException e) {
					if (Logger.logWarn()) {
						Logger.warn("Found issuezilla bug with unparsable id: " + element.getText());
					}
					skip = true;
				}
				if (skip) {
					if (Logger.logDebug()) {
						Logger.debug("Skipping bug id parsing. Already known.");
					}
					return;
				}
			} else if (element.getName().equals("issue_status")) {
				String statusString = element.getText().trim();
				report.setStatus(getStatus(statusString));
			} else if (element.getName().equals("priority")) {
				String priorityString = element.getText().trim();
				report.setPriority(getPriority(priorityString));
			} else if (element.getName().equals("resolution")) {
				String resString = element.getText().trim();
				report.setResolution(getResolution(resString));
			} else if (element.getName().equals("component")) {
				report.setComponent(element.getText().trim());
			} else if (element.getName().equals("version")) {
				report.setVersion(element.getText().trim());
			} else if (element.getName().equals("rep_platform")) {
				// TODO shall we add a field to Report
			} else if (element.getName().equals("assigned_to")) {
				String username = element.getTextTrim();
				report.setAssignedTo(new Person(username, null, null));
			} else if (element.getName().equals("delta_ts")) {
				DateTime modificationTime = DateTimeUtils.parseDate(element.getText().trim());
				if (modificationTime == null) {
					if (Logger.logWarn()) {
						Logger.warn("Issuezila modification time `" + element.getText()
						            + "` cannot be interpreted as timestamp. Ignoring.");
					}
				} else {
					report.setLastUpdateTimestamp(modificationTime);
				}
			} else if (element.getName().equals("subcomponent")) {
				report.setProduct(element.getText().trim());
			} else if (element.getName().equals("reporter")) {
				String username = element.getText().trim();
				report.setSubmitter(new Person(username, null, null));
			} else if (element.getName().equals("target_milestone")) {
				// TODO shall we add a field in Report?
			} else if (element.getName().equals("issue_type")) {
				report.setType(getType(element.getText().trim()));
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
			} else if (element.getName().equals("qa_contact")) {
				// TODO shall we add a field in Report?
			} else if (element.getName().equals("status_whiteboard")) {
				// TODO shall we add a field in Report?
			} else if (element.getName().equals("issue_file_loc")) {
				// TODO shall we add a field in Report?
			} else if (element.getName().equals("votes")) {
				// TODO shall we add a field in Report?
			} else if (element.getName().equals("op_sys")) {
				// TODO shall we add a field to Report
			} else if (element.getName().equals("short_desc")) {
				report.setSubject(element.getText().trim());
			} else if (element.getName().equals("keywords")) {
				// TODO shall we add a field to Report
			} else if (element.getName().equals("long_desc")) {
				handleLongDesc(report, element);
			} else if ((element.getName().equals("blocks")) || (element.getName().equals("dependson"))
					|| (element.getName().equals("has_duplicates")) || (element.getName().equals("is_duplicates"))) {
				Element linkElem = element.getChild("issue_id", element.getNamespace());
				try {
					report.addSibling(new Long(linkElem.getText()));
				} catch (NumberFormatException e) {
					if (Logger.logWarn()) {
						Logger.warn("Found issue link to malformatted issue id: " + linkElem.getText());
					}
				}
			} else if (element.getName().equals("activity")) {
				/* @formatter:off
				 * <activity>
				 * 	<user>bobtarling</user>
				 * 	<when>2009-03-30 02:55:40</when>
				 * 	<field_name>subcomponent</field_name>
				 * 	<field_desc>Subcomponent</field_desc>
				 * 	<oldvalue>Other</oldvalue>
				 * 	<newvalue>Persistence</newvalue>
				 * </activity>
				 */
				
				Element userElem = element.getChild("user",element.getNamespace());
				Person author = null;
				if(userElem != null){
					author = new Person(userElem.getTextTrim(),null,null);
				}else{
					author = new Person("<unknown>",null,null);
				}
				
				Element whenElem = element.getChild("when",element.getNamespace());
				DateTime when = new DateTime();
				if(whenElem != null){
					when = DateTimeUtils.parseDate(whenElem.getTextTrim());
				}
				
				HistoryElement historyElement = null;
				
				//combine multiple activities with same time stamp into one history element
				if(report.getHistory().getElements().size() > 0){
					HistoryElement lastHistoryElement = report.getHistory().getElements().last();
					if((lastHistoryElement.getTimestamp().isEqual(when)) && (lastHistoryElement.getAuthor().equals(author))){
						//use the same history element again
						historyElement = lastHistoryElement;
					}
				}
				if(historyElement == null){
					historyElement = new HistoryElement(report.getId(), author, when);
				}
				
				
				
				Element fieldElem = element.getChild("field_name",element.getNamespace());
				if(fieldElem != null){
					String fieldName = fieldElem.getTextTrim();
					Element oldValueElem = element.getChild("oldvalue",element.getNamespace());
					String oldValue = "";
					if(oldValueElem != null){
						oldValue = oldValueElem.getTextTrim();
					}
					
					Element newValueElem = element.getChild("newvalue",element.getNamespace());
					String newValue = "";
					if(newValueElem != null){
						newValue = newValueElem.getTextTrim();
					}
					
					if(fieldName.equals("component")){
						historyElement.addChangedValue("component", oldValue, newValue);
					}else if(fieldName.equals("subcomponent")){
						historyElement.addChangedValue("product", oldValue, newValue);
					}else if(fieldName.equals("issue_status")){
						historyElement.addChangedValue("status", getStatus(oldValue), getStatus(newValue));
					}else if(fieldName.equals("priority")){
						historyElement.addChangedValue("priority", getPriority(oldValue), getPriority(newValue));
					}else if(fieldName.equals("resolution")){
						Resolution newResolution = getResolution(newValue);
						historyElement.addChangedValue("resolution", getResolution(oldValue), newResolution);
						//mark last resolve as resolve date
						if(newResolution.equals(Resolution.RESOLVED)){
							report.setResolutionTimestamp(when);
							report.setResolver(author);
						}
					}else if(fieldName.equals("version")){
						historyElement.addChangedValue("version", oldValue, newValue);
					}else if(fieldName.equals("rep_platform")){
						//TODO no such field in report
					}else if(fieldName.equals("assigned_to")){
						Person oldP = new Person(oldValue, null, null);
						Person newP = new Person(newValue, null, null);
						historyElement.addChangedValue("assignedTo", oldP, newP);
					}else if(fieldName.equals("target_milestone")){
						//TODO no such field in report
					}else if(fieldName.equals("issue_type")){
						historyElement.addChangedValue("type", getType(oldValue), getType(newValue));
					}else if(fieldName.equals("qa_contact")){
						//TODO no such field in report
					}else if(fieldName.equals("status_whiteboard")){
						//TODO no such field in report
					}else if(fieldName.equals("issue_file_loc")){
						//TODO no such field in report
					}else if(fieldName.equals("votes")){
						//TODO no such field in report
					}else if(fieldName.equals("op_sys")){
						//TODO no such field in report
					}else if(fieldName.equals("short_desc")){
						historyElement.addChangedValue("subject", oldValue, newValue);
					}
					report.addHistoryElement(historyElement);
				}
			} else if (element.getName().equals("attachment")) {
				
				Element idElem = element.getChild("attachid", element.getNamespace());
				AttachmentEntry attachment = new AttachmentEntry(idElem.getTextTrim());
				
				Element mimeElem = element.getChild("mimetype", element.getNamespace());
				if (mimeElem != null) {
					attachment.setMime(mimeElem.getTextTrim());
				}
				
				Element dateElem = element.getChild("date", element.getNamespace());
				if (dateElem != null) {
					attachment.setTimestamp(DateTimeUtils.parseDate(dateElem.getTextTrim()));
				}
				
				Element descElem = element.getChild("desc", element.getNamespace());
				if (descElem != null) {
					attachment.setDescription(descElem.getTextTrim());
				}
				
				Element filenameElem = element.getChild("filename", element.getNamespace());
				if (filenameElem != null) {
					attachment.setFilename(filenameElem.getTextTrim());
				}
				
				Element submitterElem = element.getChild("submitting_username", element.getNamespace());
				if (submitterElem != null) {
					attachment.setAuthor(new Person(submitterElem.getTextTrim(), null, null));
				}
				
				StringBuilder attachmentLinkBuilder = new StringBuilder();
				attachmentLinkBuilder.append(uri.getScheme());
				attachmentLinkBuilder.append("://");
				attachmentLinkBuilder.append(uri.getAuthority());
				attachmentLinkBuilder.append("/nonav/issues/showattachment.cgi/");
				attachmentLinkBuilder.append(attachment.getId());
				attachmentLinkBuilder.append("/");
				attachment.setLink(attachmentLinkBuilder.toString());
				report.addAttachmentEntry(attachment);
			}
		}
	}
}
