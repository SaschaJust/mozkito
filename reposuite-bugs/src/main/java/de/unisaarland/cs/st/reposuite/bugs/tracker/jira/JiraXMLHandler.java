package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import java.util.List;

import org.jdom.Element;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

public class JiraXMLHandler {
	
	protected static Regex idRegex = new Regex("^[^-]+-({bugid}\\d+)");
	
	public static void handleRoot(final Report report, final Element element) {
		Condition.equals(element.getName(), "item");
		@SuppressWarnings ("unchecked")
		List<Element> children = element.getChildren();
		for (Element child : children) {
			if (child.getName().equals("title")) {
				report.setSubject(child.getText());
			} else if (child.getName().equals("description")) {
				report.setDescription(child.getText());
			} else if (child.getName().equals("key")) {
				List<RegexGroup> groups = idRegex.find(child.getText());
				if ((groups == null) || (groups.size() != 1)) {
					if (Logger.logError()) {
						Logger.error("Error while parsing Jira report " + child.getText()
								+ ". Cannot determine report id. Abort!");
					}
					return;
				}
				report.setId(new Long(groups.get(0).getMatch()).longValue());
			} else if (child.getName().equals("summary")) {
				// bugReport.setS
			} else if (child.getName().equals("type")) {
				
			} else if (child.getName().equals("priority")) {
				
			} else if (child.getName().equals("status")) {
				
			} else if (child.getName().equals("resolution")) {
				
			} else if (child.getName().equals("assignee")) {
				
			} else if (child.getName().equals("reporter")) {
				
			} else if (child.getName().equals("created")) {
				
			} else if (child.getName().equals("updated")) {
				
			} else if (child.getName().equals("comments")) {
				
			} else if (child.getName().equals("subtask")) {
				
			} else if (child.getName().equals("description")) {
				
			}
		}
	}
	
}
