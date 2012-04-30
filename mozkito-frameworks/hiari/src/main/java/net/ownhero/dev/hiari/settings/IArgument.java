package net.ownhero.dev.hiari.settings;

import java.util.Set;

import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.Tuple;

/**
 * The Interface IArgument.
 * 
 * @param <T>
 *            the generic type
 */
public interface IArgument<T, X extends IOptions<T, ?>> extends Comparable<IArgument<?, ?>> {
	
	/**
	 * Gets the dependencies.
	 * 
	 * @return the dependencies
	 */
	Set<IOptions<?, ?>> getDependencies();
	
	/**
	 * Gets the description.
	 * 
	 * @return The description of the argument (as printed in help string).
	 */
	String getDescription();
	
	/**
	 * Gets the handle.
	 * 
	 * @return the simple class name
	 */
	String getHandle();
	
	/**
	 * Gets the help string.
	 * 
	 * @return the help string
	 */
	String getHelpString();
	
	/**
	 * Gets the help string.
	 * 
	 * @param keyWidth
	 *            the key width
	 * @param indentation
	 *            the indentation
	 * @return the help string
	 */
	String getHelpString(int keyWidth,
	                     int indentation);
	
	/**
	 * Gets the key value span.
	 * 
	 * @return the key value span
	 */
	Tuple<Integer, Integer> getKeyValueSpan();
	
	/**
	 * Gets the name.
	 * 
	 * @return The name of the argument (as printed in help string).
	 */
	String getName();
	
	/**
	 * Gets the options.
	 * 
	 * @return the options
	 */
	X getOptions();
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	ArgumentSet<?, ?> getParent();
	
	/**
	 * Gets the requirements.
	 * 
	 * @return the requirements
	 */
	Requirement getRequirements();
	
	/**
	 * Gets the settings.
	 * 
	 * @return the settings
	 */
	ISettings getSettings();
	
	/**
	 * Gets the tag.
	 * 
	 * @return the tag
	 */
	public String getTag();
	
	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	T getValue();
	
	// /**
	// * Parses the.
	// *
	// * @throws SettingsParseError
	// * the settings parse error
	// */
	// void parse() throws SettingsParseError;
	
	/**
	 * Required.
	 * 
	 * @return true, if successful
	 */
	boolean required();
	
	/**
	 * To string.
	 * 
	 * @param keyWidth
	 *            the key width
	 * @param valueWidth
	 *            the value width
	 * @return the string
	 */
	String toString(int keyWidth,
	                int valueWidth);
	
}
