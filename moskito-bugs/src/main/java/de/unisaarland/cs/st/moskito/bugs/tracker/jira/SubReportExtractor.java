/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.jira;

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
	public static Element extract(final Element element,
	                              final Long idToSearch) {
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
