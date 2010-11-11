package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import java.util.List;

import org.jdom.Element;

import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

public class SubReportExtractor {
	
	public static Element extract(final Element element, final Long idToSearch) {
		Condition.notNull(element);
		Condition.notNull(idToSearch);

		if (element.getName().equals("rss")) {
			return extract(element.getChild("channel"), idToSearch);
		} else if (element.getName().equals("channel")) {
			@SuppressWarnings ("unchecked") List<Element> items = element.getChildren("item");
			for (Element item : items) {
				Element match = extract(item, idToSearch);
				if (match != null) {
					return (Element) match.clone();
				}
			}
		} else if (element.getName().equals("item")) {
			if (element.getChild("title") != null) {
				String title = element.getChild("title").getText().trim();
				List<RegexGroup> groups = JiraIDExtractor.idRegex.find(title);
				if ((groups.size() == 1) && (groups.get(0).getMatch().equals(idToSearch.toString()))) {
					return element;
				}
			}
		}
		return null;
	}
	
}
