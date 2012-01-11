/**
 * 
 */
package net.ownhero.dev.andama.observer;

/**
 * @author just
 * 
 */
public class LaunchCommand extends Command {
	
	class ModuleArgument extends Argument {
		
		@Override
		public String defaultValue() {
			return null;
		}
		
		@Override
		public String helpString() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public boolean isRequired() {
			return true;
		}
		
		@Override
		public String value() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	/**
	 * 
	 */
	public LaunchCommand(final String args) {
		// TODO this could be done automatically by the command registry
		// (register all subclasses extending arguments to the given command)
		getValidArguments().add(ModuleArgument.class);
	}
	
	@Override
	public boolean execute() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
