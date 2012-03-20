package de.unisaarland.cs.st.moskito.bugs.tracker;

import java.util.Set;

public interface OverviewParser {
	
	Set<? extends Long> getBugIds();
	
	boolean parse(String content);
	
}
