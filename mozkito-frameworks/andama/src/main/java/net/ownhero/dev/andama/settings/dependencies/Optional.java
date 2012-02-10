/**
 * 
 */
package net.ownhero.dev.andama.settings.dependencies;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.settings.AndamaArgumentInterface;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Optional extends Requirement {
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#check()
	 */
	@Override
	public boolean check() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
	 */
	@Override
	public Set<AndamaArgumentInterface<?>> getDependencies() {
		return new HashSet<AndamaArgumentInterface<?>>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#getFailureCause()
	 */
	@Override
	public List<Requirement> getMissingRequirements() {
		return null;
	}
	
}
