/**
 * 
 */
package net.ownhero.dev.andama.settings.dependencies;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.settings.AndamaArgumentInterface;
import net.ownhero.dev.andama.settings.ListArgument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Contains extends Requirement {
	
	private final ListArgument               argument;
	private final AndamaArgumentInterface<?> depender;
	
	public Contains(final ListArgument argument, final AndamaArgumentInterface<?> depender) {
		this.argument = argument;
		this.depender = depender;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#check()
	 */
	@Override
	public boolean check() {
		if (this.argument != null) {
			return this.argument.getValue().contains(this.depender.getName());
		} else {
			return false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
	 */
	@Override
	public Set<AndamaArgumentInterface<?>> getDependencies() {
		HashSet<AndamaArgumentInterface<?>> dependencies = new HashSet<AndamaArgumentInterface<?>>();
		dependencies.add(this.argument);
		
		return dependencies;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getFailureCause()
	 */
	@Override
	public List<Requirement> getMissingRequirements() {
		return check()
		              ? null
		              : new LinkedList<Requirement>() {
			              
			              private static final long serialVersionUID = 1L;
			              
			              {
				              add(Contains.this);
			              }
		              };
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "";
	};
	
}
