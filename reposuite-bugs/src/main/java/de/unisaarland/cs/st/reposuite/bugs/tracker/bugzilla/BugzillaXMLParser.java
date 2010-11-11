package de.unisaarland.cs.st.reposuite.bugs.tracker.bugzilla;

import java.util.List;

import org.jdom.Element;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonManager;

public class BugzillaXMLParser {
	
	public static void handleRoot(final Report bugReport, final Element rootElement, final PersonManager personManager) {
		@SuppressWarnings ({ "unchecked" }) List<Element> elements = rootElement.getChildren();
		for (Element element : elements) {
			if (element.getName().equals("bug_id")) {
				
			} else if (element.getName().equals("creation_ts")) {
				
			}
		}
	}
	
}
