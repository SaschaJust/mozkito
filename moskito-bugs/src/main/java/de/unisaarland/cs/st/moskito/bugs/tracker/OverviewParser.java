package de.unisaarland.cs.st.moskito.bugs.tracker;

import java.net.URI;
import java.util.Set;

public interface OverviewParser {
	
	Set<? extends URI> getBugURIs();
	
	boolean parseOverview();
	
}
