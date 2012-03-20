package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla;

import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kisa.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;

public class BugzillaOverviewParser implements OverviewParser {
	
	private final Set<ReportLink> bugURIs = new HashSet<ReportLink>();
	private final BugzillaTracker tracker;
	
	public BugzillaOverviewParser(final BugzillaTracker tracker) {
		this.tracker = tracker;
	}
	
	@Override
	public Set<ReportLink> getReportLinks() {
		// PRECONDITIONS
		
		try {
			return this.bugURIs;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public boolean parseOverview() {
		// PRECONDITIONS
		
		try {
			
			// FIXME replace by Bugzilla overview URI DYNAMIC ARGUMENT
			RawContent content;
			if (this.tracker.getOverviewURI() == null) {
				return false;
			}
			try {
				content = IOUtils.fetch(this.tracker.getOverviewURI());
			} catch (final UnsupportedProtocolException e1) {
				if (Logger.logError()) {
					Logger.error(e1.getMessage(), e1);
				}
				return false;
			} catch (final FetchException e1) {
				if (Logger.logError()) {
					Logger.error(e1.getMessage(), e1);
				}
				return false;
			}
			
			final Document document = Jsoup.parse(content.getContent());
			final Element bugzillabody = document.getElementById("bugzilla-body");
			if (bugzillabody == null) {
				if (Logger.logError()) {
					Logger.error("Could not parse bugzilla overview page: Could not find bugzilla-body <div>.");
				}
				return false;
			}
			final Elements tables = bugzillabody.getElementsByTag("table");
			Element table = null;
			for (final Element e : tables) {
				if (e.attr("class").contains("bz_buglist")) {
					table = e;
					break;
				}
			}
			if (table == null) {
				if (Logger.logError()) {
					Logger.error("Could not parse bugzilla overview page: Could not find bz_Buglist <table>.");
				}
				return false;
			}
			final Elements tbodies = table.getElementsByTag("tbody");
			if ((tbodies == null) || tbodies.isEmpty()) {
				if (Logger.logError()) {
					Logger.error("Could not parse bugzilla overview page: Could not find <tbody>.");
				}
				return false;
			}
			final Elements trs = tbodies.get(0).getElementsByTag("tr");
			for (final Element tr : trs) {
				if (!tr.attr("id").equals("")) {
					for (final Element td : tr.getElementsByTag("td")) {
						if (td.attr("class").contains("bz_id_column")) {
							try {
								final String id = td.text().trim();
								this.bugURIs.add(this.tracker.getLinkFromId(id));
							} catch (final NumberFormatException e) {
								if (Logger.logError()) {
									Logger.error("Could not interprete bug id " + td.text().trim()
									        + " as long. Ignoring bug id.");
								}
							}
						}
					}
				}
			}
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
