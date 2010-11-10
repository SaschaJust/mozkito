/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker;

import java.util.Arrays;

import de.unisaarland.cs.st.reposuite.utils.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RawReport extends RawContent {
	
	private final long id;
	
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
		builder.append(Arrays.toString(getMd5()));
		builder.append(", uri=");
		builder.append(getUri());
		builder.append("]");
		return builder.toString();
	}
	
}
