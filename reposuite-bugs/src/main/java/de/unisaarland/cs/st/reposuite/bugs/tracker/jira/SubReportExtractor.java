package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import java.util.List;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.jdom.Element;

import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

public class SubReportExtractor {
	
	/**
	 * @param element
	 * @param idToSearch
	 * @return
	 */
	@NoneNull
	public static Element extract(final Element element, final Long idToSearch) {
		if (element.getName().equals("rss")) {
			return extract(element.getChild("channel", element.getNamespace()), idToSearch);
		} else if (element.getName().equals("channel")) {
			@SuppressWarnings ("unchecked")
			List<Element> items = element.getChildren("item", element.getNamespace());
			for (Element item : items) {
				Element match = extract(item, idToSearch);
				if (match != null) {
					return (Element) match.clone();
				}
			}
		} else if (element.getName().equals("item")) {
			if (element.getChild("title", element.getNamespace()) != null) {
				String title = element.getChild("title", element.getNamespace()).getText().trim();
				List<RegexGroup> groups = JiraIDExtractor.idRegex.find(title);
				if ((groups.size() == 2) && (groups.get(1).getMatch().equals(idToSearch.toString()))) {
					return element;
				}
			}
		}
		return null;
	}
	
}
