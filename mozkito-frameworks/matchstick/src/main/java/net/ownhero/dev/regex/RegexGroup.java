/**
 * 
 */
package net.ownhero.dev.regex;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RegexGroup {
	
	private final int    index;
	private final String match;
	private final String name;
	private final String pattern;
	private final int    start;
	private final int    end;
	
	private final String text;
	
	/**
	 * @param match
	 * @param index
	 * @param name
	 */
	public RegexGroup(final String pattern, final String text, final String match, final int index, final String name,
	        final int start, final int end) {
		this.pattern = pattern;
		this.text = text;
		this.match = match;
		this.index = index;
		this.name = name;
		this.end = end;
		this.start = start;
	}
	
	/**
	 * @return
	 */
	public int end() {
		return this.end;
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
		if (!(obj instanceof RegexGroup)) {
			return false;
		}
		RegexGroup other = (RegexGroup) obj;
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
	 * @return the index
	 */
	public int getIndex() {
		return this.index;
	}
	
	/**
	 * @return the match
	 */
	public String getMatch() {
		return this.match;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return this.pattern;
	}
	
	/**
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
		result = prime * result + this.index;
		result = prime * result + ((this.pattern == null)
		                                                 ? 0
		                                                 : this.pattern.hashCode());
		result = prime * result + ((this.text == null)
		                                              ? 0
		                                              : this.text.hashCode());
		return result;
	}
	
	/**
	 * @return
	 */
	public int start() {
		return this.start;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RegexGroup [match=" + this.match + ", name=" + this.name + ", index=" + this.index + "]";
	}
	
}
