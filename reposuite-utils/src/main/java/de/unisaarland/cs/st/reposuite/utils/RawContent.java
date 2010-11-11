/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils;

import java.net.URI;
import java.util.Arrays;

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
	 *            not null, 2 &lt; <code>format.length</code> &lt; 6
	 * @param document
	 *            will be null in most cases
	 * @param content
	 *            not null, 0 &lt; <code>content.length</code>
	 */
	public RawContent(final URI uri, final byte[] md5, final DateTime fetchTime, final String format,
			final String content) {
		super();
		Condition.notNull(uri);
		Condition.notNull(md5);
		Condition.equals(md5.length, 16);
		Condition.notNull(fetchTime);
		Condition.notNull(format);
		Condition.greater(format.length(), 2);
		Condition.notNull(content);
		Condition.greater(content.length(), 0);
		
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
		} else {
			return this.fetchTime.compareTo(arg0.getFetchTime());
		}
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
		StringBuilder builder = new StringBuilder();
		builder.append("RawReport [md5=");
		builder.append(Arrays.toString(this.md5));
		builder.append(", fetchTime=");
		builder.append(this.fetchTime);
		builder.append(", format=");
		builder.append(this.format);
		builder.append(", content=");
		builder.append(StringEscapeUtils.escapeJava(StringEscapeUtils.unescapeHtml(this.content.length() > 10 ? this.content
				.substring(0, 10) : this.content)));
		builder.append("]");
		return builder.toString();
	}
	
}
