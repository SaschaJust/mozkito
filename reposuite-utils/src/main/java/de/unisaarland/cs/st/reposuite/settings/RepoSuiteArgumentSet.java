package de.unisaarland.cs.st.reposuite.settings;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public abstract class RepoSuiteArgumentSet {
	
	private HashMap<String, RepoSuiteArgument> arguments;
	
	/**
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 */
	public RepoSuiteArgumentSet() {
		arguments = new HashMap<String, RepoSuiteArgument>();
	}
	
	/**
	 * Call this method to add an argument to the set of arguments. But be aware
	 * that you have to set all arguments before adding it to the MinerSettings!
	 * 
	 * @param argument
	 *            MinerArgument to be added
	 * @return <code>true</code> if the argument could be added.
	 *         <code>false</code> otherwise.
	 */
	public boolean addArgument(RepoSuiteArgument argument) {
		if (arguments.containsKey(argument.getName())) {
			return false;
		}
		arguments.put(argument.getName(), argument);
		return true;
	}
	
	/**
	 * Return the arguments held within the set.
	 * 
	 * @return
	 */
	public Map<String, RepoSuiteArgument> getArguments() {
		return arguments;
	}
	
	public abstract Object getValue();
	
}
