package de.unisaarland.cs.st.moskito.bugs.tracker.mantis;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;

public class MantisOverviewParser implements OverviewParser {
	
	private final String overviewURI;
	
	public MantisOverviewParser(final String overviewURI) {
		this.overviewURI = overviewURI;
		// TODO implement this class
		// https://issues.openbravo.com/view_all_bug_page.php?page_number=2
	}
	
	public ReportLink getLinkFromId(final String bugId) {
		// PRECONDITIONS
		
		try {
			try {
				final StringBuilder sb = new StringBuilder();
				sb.append(this.overviewURI);
				sb.append("view.php?id=");
				sb.append(bugId);
				return new ReportLink(new URI(sb.toString()), bugId);
			} catch (final URISyntaxException e) {
				throw new UnrecoverableError(e);
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public Set<ReportLink> getReportLinks() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return new HashSet<ReportLink>();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public boolean parseOverview() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
