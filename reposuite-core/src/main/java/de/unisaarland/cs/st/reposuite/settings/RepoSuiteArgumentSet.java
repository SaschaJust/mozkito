package de.unisaarland.cs.st.reposuite.settings;

import java.util.Collection;
import java.util.HashMap;

public abstract class RepoSuiteArgumentSet {
	
	protected HashMap<String, RepoSuiteArgument> arguments;
	
	public RepoSuiteArgumentSet() {
		arguments = new HashMap<String, RepoSuiteArgument>();
	}
	
	/**
	 * Call this method to add an argument to the set of arguments. But be aware
	 * that you have to set all arguments before adding it to the MinerSettings!
	 * 
	 * @param argument
	 *            MinerArgument to be added
	 * @throws DuplicateArgumentException
	 *             If the argument given to be added was added before.
	 */
	public void addArgument(RepoSuiteArgument argument) throws DuplicateArgumentException {
		if (arguments.containsKey(argument.getName())) {
			throw new DuplicateArgumentException(argument);
		}
		arguments.put(argument.getName(), argument);
	}
	
	public Collection<RepoSuiteArgument> getArguments() {
		return arguments.values();
	}
	
	public abstract Object getValue();
	
}
