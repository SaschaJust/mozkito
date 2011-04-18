package de.unisaarland.cs.st.reposuite.infozilla.model;

import java.net.URL;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.TextElement;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;

public class Link {
	
	private enum Kind {
		WEB, REPOSITORY, TRACKER;
	}
	
	private URL           url;
	
	private final Kind    kind  = Kind.WEB;
	
	private String        stringRepresentation;
	
	private final boolean valid = false;
	
	private Person        postedBy;
	
	private DateTime      postedOn;
	
	private TextElement   postedIn;
	
	/**
	 * @return the kind
	 */
	public Kind getKind() {
		return this.kind;
	}
	
	/**
	 * @return the postedBy
	 */
	public Person getPostedBy() {
		return this.postedBy;
	}
	
	/**
	 * @return the postedIn
	 */
	public TextElement getPostedIn() {
		return this.postedIn;
	}
	
	/**
	 * @return the postedOn
	 */
	public DateTime getPostedOn() {
		return this.postedOn;
	}
	
	/**
	 * @return the stringRepresentation
	 */
	public String getStringRepresentation() {
		return this.stringRepresentation;
	}
	
	/**
	 * @return the url
	 */
	public URL getUrl() {
		return this.url;
	}
	
	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return this.valid;
	}
	
	/**
	 * @param postedBy the postedBy to set
	 */
	public void setPostedBy(final Person postedBy) {
		this.postedBy = postedBy;
	}
	
	/**
	 * @param postedIn the postedIn to set
	 */
	public void setPostedIn(final TextElement postedIn) {
		this.postedIn = postedIn;
	}
	
	/**
	 * @param postedOn the postedOn to set
	 */
	public void setPostedOn(final DateTime postedOn) {
		this.postedOn = postedOn;
	}
	
	/**
	 * @param stringRepresentation the stringRepresentation to set
	 */
	public void setStringRepresentation(final String stringRepresentation) {
		this.stringRepresentation = stringRepresentation;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(final URL url) {
		this.url = url;
	}
}
