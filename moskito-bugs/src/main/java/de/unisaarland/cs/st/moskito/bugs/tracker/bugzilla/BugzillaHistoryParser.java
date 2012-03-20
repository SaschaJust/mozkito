package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla;

import java.util.SortedSet;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.persistence.model.Person;

public interface BugzillaHistoryParser {
	
	SortedSet<HistoryElement> getHistory();
	
	DateTime getResolutionTimestamp();
	
	Person getResolver();
	
	boolean hasParsed();
	
	boolean parse();
	
}
