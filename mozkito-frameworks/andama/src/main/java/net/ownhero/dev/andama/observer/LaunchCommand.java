/**
 * 
 */
package net.ownhero.dev.andama.observer;

/**
 * @author just
 * 
 */
public class LaunchCommand extends Command {
	
	/**
	 * 
	 */
	public LaunchCommand(String args) {
		getValidArguments().add(ModuleArgument.class);
	}
	
	@Override
	public boolean execute() {
		// TODO Auto-generated method stub
		return false;
	}
	
	class ModuleArgument extends Argument {
		
		@Override
		public boolean isRequired() {
			return true;
		}
		
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
		public String value() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
}
