package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.persistence.model.Person;

/**
 * The Class BugzillaHistoryParser.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class BugzillaHistoryParser {
	
	/** The namespace. */
	protected static Namespace              namespace = Namespace.getNamespace("http://www.w3.org/1999/xhtml");
	
	/** The history uri. */
	private final URI                       historyUri;
	
	/** The report id. */
	private final long                      reportId;
	
	/** The resolver. */
	private Person                          resolver;
	
	/** The resolution timestamp. */
	private DateTime                        resolutionTimestamp;
	
	/** The history. */
	private final SortedSet<HistoryElement> history   = new TreeSet<HistoryElement>();
	
	/** The parsed. */
	private boolean                         parsed    = false;
	
	/**
	 * Instantiates a new bugzilla history parser.
	 * 
	 * @param historyUri
	 *            the history uri
	 * @param reportId
	 *            the report id
	 */
	public BugzillaHistoryParser(final URI historyUri, final long reportId) {
		this.historyUri = historyUri;
		this.reportId = reportId;
		
	}
	
	/**
	 * Gets the history.
	 * 
	 * @return the history
	 */
	public SortedSet<HistoryElement> getHistory() {
		return this.history;
	}
	
	/**
	 * Gets the resolution timestamp.
	 * 
	 * @return the resolution timestamp
	 */
	public DateTime getResolutionTimestamp() {
		return this.resolutionTimestamp;
	}
	
	/**
	 * Gets the resolver.
	 * 
	 * @return the resolver
	 */
	public Person getResolver() {
		return this.resolver;
	}
	
	public boolean hasParsed() {
		return this.parsed;
	}
	
	/**
	 * Parses the.
	 * 
	 * @throws UnsupportedProtocolException
	 *             the unsupported protocol exception
	 * @throws FetchException
	 *             the fetch exception
	 * @throws JDOMException
	 *             the jDOM exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SecurityException
	 *             the security exception
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 */
	@NoneNull
	public void parse() throws UnsupportedProtocolException,
	                   FetchException,
	                   JDOMException,
	                   IOException,
	                   SecurityException,
	                   NoSuchFieldException {
		
		if (this.parsed) {
			return;
		}
		
		final RawContent rawContent = IOUtils.fetch(this.historyUri);
		final BufferedReader reader = new BufferedReader(new StringReader(rawContent.getContent()));
		final SAXBuilder saxBuilder = new SAXBuilder("org.ccil.cowan.tagsoup.Parser");
		final Document document = saxBuilder.build(reader);
		reader.close();
		
		final Element rootElement = document.getRootElement();
		if (!rootElement.getName().equals("html")) {
			if (Logger.logError()) {
				Logger.error("Error while parsing bugzilla report history. Root element expectedto have `<html>` tag as root element. Got <"
				        + rootElement.getName() + ">.");
			}
			return;
		}
		
		final Element body = rootElement.getChild("body", namespace);
		if (body == null) {
			if (Logger.logError()) {
				Logger.error("Error while parsing bugzilla report history. No <body> tag found.");
			}
			return;
		}
		@SuppressWarnings ("unchecked")
		final List<Element> bodyChildren = body.getChildren();
		for (final Element bodyChild : bodyChildren) {
			if (bodyChild.getName().equals("div") && (bodyChild.getAttribute("id") != null)
			        && (bodyChild.getAttributeValue("id").equals("bugzilla-body"))) {
				final Element table = bodyChild.getChild("table", namespace);
				if (table == null) {
					if (Logger.logError()) {
						Logger.error("Error while parsing bugzilla report history. No <table> tag found.");
					}
					return;
				}
				
				@SuppressWarnings ("unchecked")
				final List<Element> trs = new ArrayList<Element>(table.getChildren("tr", namespace));
				if (trs.size() > 0) {
					trs.remove(0);
				}
				
				int rowspan = 0;
				HistoryElement hElement = null;
				Person historyAuthor = null;
				DateTime dateTime = null;
				for (final Element tr : trs) {
					int whatIndex = 2;
					@SuppressWarnings ("unchecked")
					final List<Element> tds = tr.getChildren("td", namespace);
					if ((tds.size() < 5) && (rowspan < 1)) {
						if (Logger.logError()) {
							Logger.error("Error while parsing bugzilla report history. Expected at least 5 table columns, found :"
							        + tds.size());
						}
						return;
					} else if (tds.size() < 3) {
						if (Logger.logError()) {
							Logger.error("Error while parsing bugzilla report history. Expected at least 3 table columns, found :"
							        + tds.size());
						}
						return;
					}
					if (rowspan == 0) {
						final String username = tds.get(0).getText().trim();
						final String rowspanString = tds.get(0).getAttributeValue("rowspan");
						if (rowspanString != null) {
							rowspan = Integer.valueOf(rowspanString).intValue() - 1;
						}
						historyAuthor = new Person(username, null, null);
						dateTime = DateTimeUtils.parseDate(tds.get(1).getText().trim());
						if (hElement != null) {
							this.history.add(hElement);
						}
						hElement = new HistoryElement(this.reportId, historyAuthor, dateTime);
						
					} else {
						--rowspan;
						whatIndex -= 2;
					}
					
					final String what = tds.get(whatIndex).getText().trim().toLowerCase();
					final String removed = tds.get(++whatIndex).getText().trim();
					final String added = tds.get(++whatIndex).getText().trim();
					
					String field = null;
					if (what.equals("priority")) {
						field = "priority";
						hElement.addChangedValue(field, BugzillaParser.getPriority(removed),
						                         BugzillaParser.getPriority(added));
						continue;
					} else if (what.equals("summary")) {
						field = ("summary");
						hElement.addChangedValue(field, removed, added);
						continue;
					} else if (what.equals("resolution")) {
						field = ("resolution");
						hElement.addChangedValue(field, BugzillaParser.getResolution(removed),
						                         BugzillaParser.getResolution(added));
						// set report resolution date and resolver
						if (BugzillaParser.getResolution(added).equals(Resolution.RESOLVED)) {
							this.resolver = historyAuthor;
							this.resolutionTimestamp = dateTime;
						}
						continue;
					} else if (what.equals("assignee")) {
						field = ("assignedTo");
						final Person oldValue = new Person(removed, null, null);
						final Person newValue = new Person(added, null, null);
						hElement.addChangedValue(field, oldValue, newValue);
						continue;
					} else if (what.equals("target milestone")) {
						
					} else if (what.equals("cc")) {
						
					} else if (what.equals("component")) {
						field = ("component");
						hElement.addChangedValue(field, removed, added);
						continue;
					} else if (what.equals("summary")) {
						field = ("summary");
						hElement.addChangedValue(field, removed, added);
						continue;
					} else if (what.equals("severity")) {
						field = ("severity");
						hElement.addChangedValue(field, BugzillaParser.getSeverity(removed),
						                         BugzillaParser.getSeverity(added));
						continue;
					} else if (what.equals("blocks")) {
						// TODO how shall I do that?
					} else if (what.equals("depends on")) {
						// TODO how shall I do that?
					} else if (what.equals("status")) {
						field = ("status");
						hElement.addChangedValue(field, BugzillaParser.getStatus(removed),
						                         BugzillaParser.getStatus(added));
						continue;
					} else if (what.equals("product")) {
						field = ("product");
						hElement.addChangedValue(field, removed, added);
						continue;
					} else if (what.equals("category")) {
						field = ("category");
						hElement.addChangedValue(field, removed, added);
						continue;
					}
				}
				if (hElement != null) {
					this.history.add(hElement);
				}
			}
		}
		this.parsed = true;
	}
}
