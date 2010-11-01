package de.unisaarland.cs.st.reposuite.utils;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public enum LogLevel {
	DEBUG, ERROR, INFO, OFF, TRACE, WARN;
	
	/**
	 * @return the simple class name
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
