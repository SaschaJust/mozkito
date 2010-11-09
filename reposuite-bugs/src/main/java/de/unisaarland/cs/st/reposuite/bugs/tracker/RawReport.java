/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker;

import java.util.Arrays;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.utils.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RawReport {
	
	private final long     id;
	private final byte[]   md5;
	private final DateTime fetchTime;
	private final String   format;
	private final String   content;
	
	/**
	 * @param id
	 *            not null, &gt;0
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
	public RawReport(final long id, final byte[] md5, final DateTime fetchTime, final String format,
	        final String content) {
		super();
		
		Condition.greater(id, 0l);
		Condition.notNull(md5);
		Condition.equals(md5.length, 32);
		Condition.notNull(fetchTime);
		Condition.notNull(format);
		Condition.greater(format.length(), 2);
		Condition.less(format.length(), 6);
		Condition.notNull(content);
		Condition.greater(content.length(), 0);
		
		this.id = id;
		this.md5 = md5;
		this.fetchTime = fetchTime;
		this.format = format;
		this.content = content;
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
	
	/**
	 * @return the format
	 */
	public String getFormat() {
		return this.format;
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}
	
	/**
	 * @return the md5
	 */
	public byte[] getMd5() {
		return this.md5;
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
		builder.append(this.content.length() > 10 ? this.content.substring(0, 10) : this.content);
		builder.append("]");
		return builder.toString();
	}
	
}
