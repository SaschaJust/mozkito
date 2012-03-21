package de.unisaarland.cs.st.moskito.bugs.tracker.mantis;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;

public class MantisOverviewParser implements OverviewParser {
	
	private final String          trackerUri;
	private final Set<ReportLink> reportLinks = new HashSet<ReportLink>();
	
	private final Regex           pageRegex   = new Regex("\\?page_number=({page_number}\\d+)");
	private final Regex           reportRegex = new Regex("view.php\\?id=({bugid}\\d+)");
	
	public MantisOverviewParser(final String trackerUri) {
		this.trackerUri = trackerUri;
	}
	
	protected int determineNumPages(final URI uri) {
		// PRECONDITIONS
		
		try {
			final RawContent firstPage = IOUtils.fetch(uri);
			final Document document = Jsoup.parse(firstPage.getContent());
			final Element buglistTable = document.getElementById("buglist");
			if (buglistTable == null) {
				throw new UnrecoverableError(
				                             "Could not find <table id=\"buglist\"> in overview HTML page. Maybe CSS changed.");
			}
			final Elements trs = buglistTable.getElementsByTag("tr");
			if ((trs == null) || (trs.isEmpty())) {
				if (Logger.logWarn()) {
					Logger.warn("Found <table id=\"buglist\"> table with no table rows.");
				}
				return 0;
			}
			final Elements tds = trs.get(0).getElementsByTag("td");
			if ((tds == null) || (tds.isEmpty()) || (tds.size() < 2)) {
				if (Logger.logWarn()) {
					Logger.warn("Found <table id=\"buglist\"> table with no or too less table columns in first row.");
				}
				return 0;
			}
			final Elements aTags = tds.get(1).getElementsByTag("a");
			if ((aTags == null) || (aTags.isEmpty())) {
				if (Logger.logWarn()) {
					Logger.warn("Could not find any page links.");
				}
				return 0;
			}
			
			final Element lastATag = aTags.get(aTags.size() - 1);
			final String href = lastATag.attr("href");
			final List<RegexGroup> regexGroups = this.pageRegex.find(href);
			for (final RegexGroup regexGroup : regexGroups) {
				if ((regexGroup.getName() != null) && (regexGroup.getName().equals("page_number"))) {
					return Integer.valueOf(regexGroup.getMatch());
				}
			}
			return 0;
			
		} catch (final UnsupportedProtocolException e) {
			throw new UnrecoverableError(e);
		} catch (final FetchException e) {
			throw new UnrecoverableError(e);
		} catch (final NumberFormatException e) {
			throw new UnrecoverableError(e);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public ReportLink getLinkFromId(final String bugId) {
		// PRECONDITIONS
		
		try {
			try {
				final StringBuilder sb = new StringBuilder();
				sb.append(this.trackerUri);
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
			return this.reportLinks;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	protected List<ReportLink> handlePage(final URI pageUri) {
		// PRECONDITIONS
		
		final List<ReportLink> result = new LinkedList<ReportLink>();
		try {
			
			final RawContent firstPage = IOUtils.fetch(pageUri);
			final Document document = Jsoup.parse(firstPage.getContent());
			final Element buglistTable = document.getElementById("buglist");
			if (buglistTable == null) {
				throw new UnrecoverableError(
				                             "Could not find <table id=\"buglist\"> in overview HTML page. Maybe CSS changed.");
			}
			final Elements trs = buglistTable.getElementsByTag("tr");
			if ((trs == null) || (trs.isEmpty()) || (trs.size() < 4)) {
				if (Logger.logWarn()) {
					Logger.warn("Found <table id=\"buglist\"> table with less than 4 table rows.");
				}
				return result;
			}
			for (int i = 3; i < trs.size(); ++i) {
				final Element row = trs.get(i);
				final Elements aTags = row.getElementsByTag("a");
				if ((aTags == null) || (aTags.isEmpty())) {
					continue;
				}
				for (final Element aTag : aTags) {
					final String href = aTag.attr("href").trim();
					final List<RegexGroup> find = this.reportRegex.find(href);
					if ((find == null) || (find.isEmpty())) {
						continue;
					}
					for (final RegexGroup regexGroup : find) {
						if ((regexGroup.getName() != null) && (regexGroup.getName().equals("bugid"))) {
							result.add(new ReportLink(new URI(this.trackerUri + href), regexGroup.getMatch()));
							break;
						}
					}
					break;
				}
			}
			return result;
		} catch (final UnsupportedProtocolException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return result;
		} catch (final FetchException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return result;
		} catch (final URISyntaxException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public boolean parseOverview() {
		// PRECONDITIONS
		
		try {
			final String firstPage = "view_all_bug_page.php";
			
			// determine the number of pages
			StringBuilder sb = new StringBuilder();
			sb.append(this.trackerUri);
			sb.append("/");
			sb.append(firstPage);
			final int numPages = determineNumPages(new URI(sb.toString()));
			
			// handle each page
			for (int page = 1; page <= numPages; ++page) {
				sb = new StringBuilder();
				sb.append(this.trackerUri);
				sb.append("/");
				sb.append(firstPage);
				sb.append("?page_number=");
				sb.append(page);
				
				final URI pageUri = new URI(sb.toString());
				this.reportLinks.addAll(handlePage(pageUri));
			}
			
			return true;
		} catch (final URISyntaxException e) {
			throw new UnrecoverableError(e);
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
