/**
 * 
 */
package net.ownhero.dev.regex;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class Group.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class Group {
	
	/** The index. */
	private final int    index;
	
	/** The match. */
	private final String match;
	
	/** The name. */
	private final String name;
	
	/** The pattern. */
	private final String pattern;
	
	/** The start. */
	private final int    start;
	
	/** The end. */
	private final int    end;
	
	/** The text. */
	private final String text;
	
	/**
	 * Instantiates a new group.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param text
	 *            the text
	 * @param match
	 *            the match
	 * @param index
	 *            the index
	 * @param name
	 *            the name
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public Group(@NotNull final String pattern, @NotNull final String text, @NotNull final String match,
	        @NotNegative final int index, @NotNull final String name, @NotNegative final int start,
	        @NotNegative final int end) {
		// PRECONDITIONS
		
		try {
			this.pattern = pattern;
			this.text = text;
			this.match = match;
			this.index = index;
			this.name = name;
			this.end = end;
			this.start = start;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.pattern, "Field '%s' in '%s'.", "pattern", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.text, "Field '%s' in '%s'.", "text", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			//			Condition.notNull(this.match, "Field '%s' in '%s'.", "match", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			CompareCondition.notNegative(this.index, "Field '%s' in '%s'.", "index", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			CompareCondition.notNegative(this.start, "Field '%s' in '%s'.", "start", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			CompareCondition.notNegative(this.end, "Field '%s' in '%s'.", "end", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * End.
	 * 
	 * @return the int
	 */
	public int end() {
		// PRECONDITIONS
		CompareCondition.notNegative(this.end, "Field '%s' in '%s'.", "end", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.end;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Group)) {
			return false;
		}
		final Group other = (Group) obj;
		if (this.index != other.index) {
			return false;
		}
		if (this.pattern == null) {
			if (other.pattern != null) {
				return false;
			}
		} else if (!this.pattern.equals(other.pattern)) {
			return false;
		}
		if (this.text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!this.text.equals(other.text)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	public final String getHandle() {
		return getClass().getSimpleName();
	}
	
	/**
	 * Gets the index.
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return this.index;
	}
	
	/**
	 * Gets the match.
	 * 
	 * @return the match
	 */
	public String getMatch() {
		return this.match;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets the pattern.
	 * 
	 * @return the pattern
	 */
	public String getPattern() {
		return this.pattern;
	}
	
	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	public String getText() {
		return this.text;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.index;
		result = (prime * result) + ((this.pattern == null)
		                                                   ? 0
		                                                   : this.pattern.hashCode());
		result = (prime * result) + ((this.text == null)
		                                                ? 0
		                                                : this.text.hashCode());
		return result;
	}
	
	/**
	 * Start.
	 * 
	 * @return the int
	 */
	public int start() {
		// PRECONDITIONS
		CompareCondition.notNegative(this.start, "Field '%s' in '%s'.", "start", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.start;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Group [index=");
		builder.append(this.index);
		builder.append(", match=");
		builder.append(this.match);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", start=");
		builder.append(this.start);
		builder.append(", end=");
		builder.append(this.end);
		builder.append("]");
		return builder.toString();
	}
	
}
