package de.unisaarland.cs.st.reposuite.settings;

public class MissingRequiredArgumentException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MissingRequiredArgumentException(RepoSuiteArgument arg) {
		super("Required argument " + arg.getName() + " was not set.");
	}
}
