package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla;

import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.kisa.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser;

public class BugzillaOverviewParser implements OverviewParser {
	
	private final Set<Long> bugIds = new HashSet<Long>();
	
	public BugzillaOverviewParser() {
		
	}
	
	@Override
	public Set<? extends Long> getBugIds() {
		// PRECONDITIONS
		
		try {
			return this.bugIds;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public boolean parse(final String content) {
		// PRECONDITIONS
		
		try {
			final Document document = Jsoup.parse(content);
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
								final Long id = Long.parseLong(td.text().trim());
								this.bugIds.add(id);
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
