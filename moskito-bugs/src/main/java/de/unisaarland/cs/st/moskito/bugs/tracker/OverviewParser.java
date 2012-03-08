package de.unisaarland.cs.st.moskito.bugs.tracker;

import java.net.URI;
import java.util.Set;

import net.ownhero.dev.ioda.container.RawContent;

public interface OverviewParser {
	
	Set<? extends URI> getBugURIs();
	
	boolean parseOverview(RawContent content);
	
}
