package de.unisaarland.cs.st.reposuite.bugs.tracker.google;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.gdata.data.Person;
import com.google.gdata.data.projecthosting.BlockedOn;
import com.google.gdata.data.projecthosting.Blocking;
import com.google.gdata.data.projecthosting.Cc;
import com.google.gdata.data.projecthosting.ClosedDate;
import com.google.gdata.data.projecthosting.IssuesEntry;
import com.google.gdata.data.projecthosting.Label;

import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.utils.RawContent;
import de.unisaarland.cs.st.reposuite.utils.Storable;

/**
 * The Class GoogleRawContent.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GoogleRawContent extends XmlReport implements Storable {
	
	/**
	 * 
	 */
	private static final long       serialVersionUID = 1L;
	
	private final List<GooglePerson> authors          = new LinkedList<GooglePerson>();
	private final Set<GooglePerson> contributors     = new HashSet<GooglePerson>();
	private final Set<GooglePerson> ccs              = new HashSet<GooglePerson>();
	private final Set<Integer>      blockedOn        = new HashSet<Integer>();
	private final Set<Integer>      blocking         = new HashSet<Integer>();
	private DateTime                closeDate;
	private final DateTime          editDate;
	private final DateTime          updateDate;
	private final GooglePerson      owner;
	private String                  type;
	private final String            state;
	private final String            status;
	private String                  priority;
	private final String            summary;
	private final String            title;
	private String                  category;
	private String                  version;
	
	private final DateTime          creationDate;
	
	/**
	 * Instantiates a new google raw content.
	 * 
	 * @param id
	 *            the id
	 * @param fetchTime
	 *            the fetch time
	 * @param entry
	 *            the entry
	 * @param md5
	 *            the md5
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	public GoogleRawContent(final Long id, final DateTime fetchTime, final IssuesEntry entry, final byte[] md5)
	throws URISyntaxException {
		super(new RawReport(id, new RawContent(new URI(id.toString()), md5, fetchTime, "GooleIssues", "")),
				new Document());
		for (Person p : entry.getAuthors()) {
			this.authors.add(new GooglePerson(p.getEmail(), p.getName(), p.getNameLang()));
		}
		for (BlockedOn b : entry.getBlockedOns()) {
			this.blockedOn.add(b.getId().getValue());
		}
		for (Blocking b : entry.getBlockings()) {
			this.blocking.add(b.getId().getValue());
		}
		for (Cc cc : entry.getCcs()) {
			this.ccs.add(new GooglePerson(null, cc.getUsername().getValue(), null));
		}
		com.google.gdata.data.DateTime googleClosedDate = entry.getClosedDate().getValue();
		
		for (Person p : entry.getContributors()) {
			this.contributors.add(new GooglePerson(p.getEmail(), p.getName(), p.getNameLang()));
		}
		
		com.google.gdata.data.DateTime published = entry.getPublished();
		this.creationDate = new DateTime(published.getValue(), DateTimeZone.forOffsetHours(published.getTzShift()));
		
		ClosedDate closedDate = entry.getClosedDate();
		if ((closedDate != null) && (closedDate.getValue() != null)) {
			this.closeDate = new DateTime(googleClosedDate.getValue(), DateTimeZone.forOffsetHours(googleClosedDate
					.getTzShift()));
		} else {
			this.closeDate = null;
		}
		
		com.google.gdata.data.DateTime edited = entry.getEdited();
		if(edited != null){
			this.editDate = new DateTime(edited.getValue(), DateTimeZone.forOffsetHours(edited.getTzShift()));
		}else{
			this.editDate = null;
		}
		
		com.google.gdata.data.DateTime googleUpdated = entry.getUpdated();
		if (googleUpdated != null) {
			this.updateDate = new DateTime(googleUpdated.getValue(),
					DateTimeZone.forOffsetHours(googleUpdated.getTzShift()));
		} else {
			this.updateDate = null;
		}
		
		for (Label l : entry.getLabels()) {
			String value = l.getValue().toLowerCase();
			if (value.startsWith("type-")) {
				this.type = value.replace("type-", "").trim();
			} else if (value.startsWith("priority-")) {
				this.priority = value.replace("priority-", "").trim();
			} else if (value.startsWith("category-")) {
				this.category = value.replace("category-", "").trim();
			} else if (value.startsWith("milestone-")) {
				this.version = value.replace("milestone-", "").trim();
			}
		}
		
		this.owner = new GooglePerson(null, entry.getOwner().getUsername().getValue(), null);
		this.state = entry.getState().getValue().toString();
		this.status = entry.getStatus().getValue();
		this.summary = entry.getSummary().getPlainText();
		this.title = entry.getTitle().getPlainText();
		
	}
	
	/**
	 * Gets the authors.
	 * 
	 * @return the authors
	 */
	public List<GooglePerson> getAuthors() {
		return this.authors;
	}
	
	/**
	 * Gets the blocked on.
	 * 
	 * @return the blocked on
	 */
	public Set<Integer> getBlockedOn() {
		return this.blockedOn;
	}
	
	/**
	 * Gets the blocking.
	 * 
	 * @return the blocking
	 */
	public Set<Integer> getBlocking() {
		return this.blocking;
	}
	
	/**
	 * Gets the categories.
	 * 
	 * @return the categories
	 */
	public String getCategory() {
		return this.category;
	}
	
	/**
	 * Gets the ccs.
	 * 
	 * @return the ccs
	 */
	public Set<GooglePerson> getCcs() {
		return this.ccs;
	}
	
	/**
	 * Gets the close date.
	 * 
	 * @return the close date - might be null if not set
	 */
	public DateTime getCloseDate() {
		return this.closeDate;
	}
	
	/**
	 * Gets the contributors.
	 * 
	 * @return the contributors
	 */
	public Set<GooglePerson> getContributors() {
		return this.contributors;
	}
	
	/**
	 * Gets the creation date.
	 * 
	 * @return the creation date
	 */
	public DateTime getCreationDate() {
		return this.creationDate;
	}
	
	/**
	 * Gets the edits the date.
	 * 
	 * @return the edits the date
	 */
	public DateTime getEditDate() {
		return this.editDate;
	}
	
	/**
	 * Gets the owner.
	 * 
	 * @return the owner
	 */
	public GooglePerson getOwner() {
		return this.owner;
	}
	
	/**
	 * Gets the priority.
	 * 
	 * @return the priority (Critical, High, Medium, Low)
	 */
	public String getPriority() {
		return this.priority;
	}
	
	/**
	 * Gets the state.
	 * 
	 * @return the state (CLOSED or OPEN)
	 */
	public String getState() {
		return this.state;
	}
	
	/**
	 * Gets the status.
	 * 
	 * @return the status (Started, Accepted, FixedNotReleased, NeedsInfo, New,
	 *         PatchesWelcome, ReviewPending, AssumedStale, Duplicate, Fixed,
	 *         Invalid, KnownQuirk, NotPlanned, etc.)
	 */
	public String getStatus() {
		return this.status;
	}
	
	/**
	 * Gets the summary.
	 * 
	 * @return the summary
	 */
	public String getSummary() {
		return this.summary;
	}
	
	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type (Defect, Enhancement, Task, Docs, ---, Feature,
	 *         Optimization, etc.)
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * Gets the update date.
	 * 
	 * @return the update date
	 */
	public DateTime getUpdateDate() {
		return this.updateDate;
	}
	
	/**
	 * Gets the version.
	 * 
	 * @return the version
	 */
	public String getVersion(){
		return this.version;
	}
}
