package net.ownhero.dev.andama.utils;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public enum LogLevel {
	OFF, ERROR, WARN, INFO, DEBUG, TRACE;
	
	/**
	 * @return the simple class name
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
