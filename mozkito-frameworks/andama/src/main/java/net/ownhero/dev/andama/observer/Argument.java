package net.ownhero.dev.andama.observer;

public abstract class Argument {
	
	public abstract boolean isRequired();
	
	public abstract String defaultValue();
	
	public abstract String helpString();
	
	public abstract String value();
}
