/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.RawContent;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RawReport extends RawContent {
	
	private static final long serialVersionUID = -4448381593266361762L;
	private final long        id;
	
	/**
	 * @param id
	 *            not null, &gt;0
	 * @param rawContent
	 *            not null
	 */
	public RawReport(final long id, final RawContent rawContent) {
		super(rawContent.getUri(), rawContent.getMd5(), rawContent.getFetchTime(), rawContent.getFormat(), rawContent
		        .getContent());
		Condition.greater(id, 0l);
		Condition.notNull(rawContent);
		
		this.id = id;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final RawContent arg0) {
		if (arg0 == null) {
			return 1;
		} else if (arg0 instanceof RawReport) {
			if (this.id > ((RawReport) arg0).id) {
				return 1;
			} else if (this.id < ((RawReport) arg0).id) {
				return -1;
			} else {
				return 1;
			}
		} else {
			return super.compareTo(arg0);
		}
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RawReport [id=");
		builder.append(this.id);
		builder.append(", fetchTime=");
		builder.append(getFetchTime());
		builder.append(", format=");
		builder.append(getFormat());
		builder.append(", md5=");
		try {
			builder.append(JavaUtils.byteArrayToHexString(getMd5()));
		} catch (UnsupportedEncodingException e) {
			builder.append(Arrays.toString(getMd5()));
		}
		builder.append(", uri=");
		builder.append(getUri());
		builder.append("]");
		return builder.toString();
	}
	
}
