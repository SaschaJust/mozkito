package net.ownhero.dev.hiari.settings;

import java.util.Collection;
import java.util.Properties;

import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.registerable.ArgumentProvider;

public interface ISettings {
	
	boolean addArgumentMapping(final String name,
	                           final ArgumentSet<?> argument);
	
	void addArgumentProvider(final ArgumentProvider provider);
	
	void addToolInformation(final String tool,
	                        final String information);
	
	Collection<ArgumentSet<?>> getArguments();
	
	String getHelpString();
	
	Properties getProperties();
	
	IArgument<?> getSetting(final String name);
	
	String getToolInformation();
	
	boolean hasSetting(final String name);
	
	void parse() throws SettingsParseError;
	
	void parseArguments(final Collection<IArgument<?>> arguments) throws SettingsParseError;
}
