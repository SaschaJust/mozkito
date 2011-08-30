package net.ownhero.dev.andama.observer;

import java.util.HashSet;
import java.util.Set;

public abstract class Command {
	
	private final Set<Class<? extends Argument>> validArguments = new HashSet<Class<? extends Argument>>();
	
	public Set<Class<? extends Argument>> getValidArguments() {
		return validArguments;
	}
	
	public Command(Argument... arguments) {
		// TODO check if arguments ar valid
	}
	
	/**
	 * @return
	 */
	public String getToken() {
		return this.getClass().getSimpleName()
		        .substring(0, this.getClass().getSimpleName().length() - Command.class.getSimpleName().length())
		        .toLowerCase();
	}
	
	public abstract boolean execute();
	
}
