package de.unisaarland.cs.st.reposuite.settings;

public class DuplicateArgumentException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DuplicateArgumentException(RepoSuiteArgument argument) {
		super("Argument with name " + argument.getName() + " already exists. Check for duplicates or chose other name.");
	}
	
}
