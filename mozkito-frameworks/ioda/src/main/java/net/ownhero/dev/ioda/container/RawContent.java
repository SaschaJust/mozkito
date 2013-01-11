/*******************************************************************************
 * Copyright 2013 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.ioda.container;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.ioda.interfaces.Storable;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.Size;

import org.joda.time.DateTime;

/**
 * The Class RawContent.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class RawContent implements Comparable<RawContent>, Storable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8090298340304255338L;
	
	/** The md5. */
	private final byte[]      md5;
	
	/** The fetch time. */
	private final DateTime    fetchTime;
	
	/** The format. */
	private final String      format;
	
	/** The content. */
	private final String      content;
	
	/** The uri. */
	private final URI         uri;
	
	/** The filename. */
	private String            filename;
	
	/** The cached. */
	private boolean           cached;
	
	/**
	 * Instantiates a new raw content.
	 * 
	 * @param uri
	 *            the uri
	 * @param md5
	 *            not null
	 * @param fetchTime
	 *            not null
	 * @param format
	 *            not null
	 * @param content
	 *            not null, 0 &lt; <code>content.length</code>
	 */
	@NoneNull
	public RawContent(final URI uri, @Size (size = 16, value = "A MD5 hashsum has to be of size 16.") final byte[] md5,
	        final DateTime fetchTime, final String format, final String content) {
		super();
		
		this.uri = uri;
		this.md5 = md5;
		this.fetchTime = fetchTime;
		this.format = format;
		this.content = content;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.ioda.interfaces.Storable#cached()
	 */
	@Override
	public boolean cached() {
		return this.cached;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final RawContent arg0) {
		if (arg0 == null) {
			return 1;
		}
		return this.fetchTime.compareTo(arg0.getFetchTime());
	}
	
	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	public String getContent() {
		return this.content;
	}
	
	/**
	 * Gets the fetch time.
	 * 
	 * @return the fetchTime
	 */
	public DateTime getFetchTime() {
		return this.fetchTime;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.ioda.interfaces.Storable#getFilename()
	 */
	@Override
	public String getFilename() {
		return this.filename;
	}
	
	/**
	 * Gets the format.
	 * 
	 * @return the format
	 */
	public String getFormat() {
		return this.format;
	}
	
	/**
	 * Gets the md5.
	 * 
	 * @return the md5
	 */
	public byte[] getMd5() {
		return this.md5;
	}
	
	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public long getSize() {
		return this.content.length();
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
	 * @see net.ownhero.dev.ioda.interfaces.Storable#setCached(java.lang.String)
	 */
	@Override
	public void setCached(final String filename) {
		this.filename = filename;
		this.cached = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RawReport [md5=");
		try {
			builder.append(JavaUtils.byteArrayToHexString(getMd5()));
		} catch (final UnsupportedEncodingException e) {
			builder.append(Arrays.toString(getMd5()));
		}
		builder.append(", fetchTime=");
		builder.append(this.fetchTime);
		builder.append(", format=");
		builder.append(this.format);
		// builder.append(", content=");
		// builder.append(StringEscapeUtils.escapeJava(StringEscapeUtils.unescapeHtml(this.content.length() > 10
		// ? this.content.substring(0,
		// 10)
		// : this.content)));
		builder.append("]");
		return builder.toString();
	}
	
}
