package net.ownhero.dev.andama.settings;

import java.util.Set;

import net.ownhero.dev.andama.exceptions.SettingsParseError;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.ioda.Tuple;

public interface IArgument<T> extends Comparable<IArgument<?>> {
	
	/**
	 * @return
	 */
	Set<IArgument<?>> getDependencies();
	
	/**
	 * @return The description of the argument (as printed in help string).
	 */
	String getDescription();
	
	/**
	 * @return the simple class name
	 */
	String getHandle();
	
	String getHelpString();
	
	/**
	 * @param indentation
	 * @return
	 */
	String getHelpString(int indentation);
	
	/**
	 * @return
	 */
	Tuple<Integer, Integer> getKeyValueSpan();
	
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
	Settings getSettings();
	
	/**
	 * @return
	 */
	T getValue();
	
	/**
	 * @return
	 */
	boolean isInitialized();
	
	/**
	 * @throws SettingsParseError
	 * 
	 */
	void parse() throws SettingsParseError;
	
	/**
	 * @return
	 */
	boolean required();
	
	/**
	 * 
	 * @param keyWidth
	 * @param valueWidth
	 * @return
	 */
	String toString(int keyWidth,
	                int valueWidth);
}
