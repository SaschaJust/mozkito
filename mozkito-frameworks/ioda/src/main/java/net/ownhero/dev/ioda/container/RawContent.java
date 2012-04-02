/**
 * 
 */
package net.ownhero.dev.ioda.container;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.ioda.interfaces.Storable;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.Size;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RawContent implements Comparable<RawContent>, Storable {
	
	private static final long serialVersionUID = -8090298340304255338L;
	private final byte[]      md5;
	private final DateTime    fetchTime;
	private final String      format;
	private final String      content;
	private final URI         uri;
	private String            filename;
	private boolean           cached;
	
	/**
	 * @param md5
	 *            not null
	 * @param fetchTime
	 *            not null
	 * @param format
	 *            not null
	 * @param document
	 *            will be null in most cases
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
	
	@Override
	public boolean cached() {
		return this.cached;
	}
	
	@Override
	public int compareTo(final RawContent arg0) {
		if (arg0 == null) {
			return 1;
		}
		return this.fetchTime.compareTo(arg0.getFetchTime());
	}
	
	/**
	 * @return the content
	 */
	public String getContent() {
		return this.content;
	}
	
	/**
	 * @return the fetchTime
	 */
	public DateTime getFetchTime() {
		return this.fetchTime;
	}
	
	@Override
	public String getFilename() {
		return this.filename;
	}
	
	/**
	 * @return the format
	 */
	public String getFormat() {
		return this.format;
	}
	
	/**
	 * @return the md5
	 */
	public byte[] getMd5() {
		return this.md5;
	}
	
	public long getSize() {
		return this.content.length();
	}
	
	/**
	 * @return the uri
	 */
	public URI getUri() {
		return this.uri;
	}
	
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
		builder.append(", content=");
		builder.append(StringEscapeUtils.escapeJava(StringEscapeUtils.unescapeHtml(this.content.length() > 10
		                                                                                                     ? this.content.substring(0,
		                                                                                                                              10)
		                                                                                                     : this.content)));
		builder.append("]");
		return builder.toString();
	}
	
}
