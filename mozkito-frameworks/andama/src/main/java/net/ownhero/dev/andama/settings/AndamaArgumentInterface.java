package net.ownhero.dev.andama.settings;

import java.util.Set;

public interface AndamaArgumentInterface<T> extends Comparable<AndamaArgumentInterface<T>> {
	
	/**
	 * @return
	 */
	Set<AndamaArgumentInterface<?>> getDependees();
	
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
	AndamaSettings getSettings();
	
	/**
	 * @return
	 */
	T getValue();
	
	/**
	 * @return
	 */
	boolean required();
	
}
