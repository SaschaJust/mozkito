/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package de.unisaarland.cs.st.mozkito.issues.tracker;

import java.net.URI;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

/**
 * The Class ReportLink.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ReportLink {
	
	/** The bug id. */
	private final String bugId;
	
	/** The uri. */
	private final URI    uri;
	
	/**
	 * Instantiates a new report link.
	 * 
	 * @param uri
	 *            the uri
	 * @param bugId
	 *            the bug id
	 */
	public ReportLink(final URI uri, @NotNull final String bugId) {
		this.uri = uri;
		this.bugId = bugId;
	}
	
	/**
	 * Gets the bug id.
	 * 
	 * @return the bug id
	 */
	public String getBugId() {
		return this.bugId;
	}
	
	/**
	 * Gets the uri.
	 * 
	 * @return the uri
	 */
	public URI getUri() {
		return this.uri;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[ uri=");
		sb.append(this.uri != null
		                          ? this.uri.toASCIIString()
		                          : "null");
		sb.append(", bugId=");
		sb.append(this.bugId);
		sb.append("]");
		return sb.toString();
	}
}
