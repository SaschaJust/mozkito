package net.ownhero.dev.andama.settings;

import java.util.Set;

import net.ownhero.dev.andama.settings.dependencies.Requirement;

public interface AndamaArgumentInterface<T> extends Comparable<AndamaArgumentInterface<T>> {
	
	/**
	 * @return
	 */
	Set<AndamaArgumentInterface<?>> getDependencies();
	
	/**
	 * @return The description of the argument (as printed in help string).
	 */
	String getDescription();
	
	/**
	 * @return the simple class name
	 */
	String getHandle();
	
	/**
	 * @return The name of the argument (as printed in help string).
	 */
	String getName();
	
	/**
	 * @return
	 */
	Requirement getRequirements();
	
	/**
	 * @return
	 */
	AndamaSettings getSettings();
	
	/**
	 * @return
	 */
	T getValue();
	
	/**
	 * @return
	 */
	boolean required();
	
	/**
	 * @param indentation
	 * @return
	 */
	String toString(int indentation);
}
