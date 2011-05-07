package net.ownhero.dev.andama.settings;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public abstract class AndamaArgumentSet<T> {
	
	private final Map<String, AndamaArgument<?>> arguments;
	
	/**
	 * @see de.unisaarland.cs.st.reposuite.settings.AndamaArgument
	 */
	public AndamaArgumentSet() {
		this.arguments = new HashMap<String, AndamaArgument<?>>();
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
	public boolean addArgument(final AndamaArgument<?> argument) {
		if (this.arguments.containsKey(argument.getName())) {
			return false;
		}
		
		this.arguments.put(argument.getName(), argument);
		
		return true;
	}
	
	/**
	 * Return the arguments held within the set.
	 * 
	 * @return
	 */
	public Map<String, AndamaArgument<?>> getArguments() {
		return this.arguments;
	}
	
	/**
	 * @return
	 */
	public abstract T getValue();
	
}
