package net.ownhero.dev.andama.settings;


public class LoggerOutputFileArgument extends OutputFileArgument {
	
	LoggerOutputFileArgument(AndamaSettings settings, String name, String description, String defaultValue,
			boolean isRequired, boolean overwrite) {
		super(settings, name, description, defaultValue, isRequired, overwrite);
	}
	
	@Override
	public boolean init() {
		setCachedValue(null);
		return true;
	}
	
}
