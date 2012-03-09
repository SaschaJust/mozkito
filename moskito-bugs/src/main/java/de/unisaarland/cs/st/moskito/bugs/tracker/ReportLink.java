package de.unisaarland.cs.st.moskito.bugs.tracker;

import java.net.URI;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

public class ReportLink {
	
	private final String bugId;
	private final URI    uri;
	
	public ReportLink(final URI uri, @NotNull final String bugId) {
		this.uri = uri;
		this.bugId = bugId;
	}
	
	public String getBugId() {
		return this.bugId;
	}
	
	public URI getUri() {
		return this.uri;
	}
	
}
