/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import java.util.List;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.regex.RegexGroup;

import org.jdom.Element;

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
