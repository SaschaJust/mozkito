/**
 * 
 */
package net.ownhero.dev.andama.observer;

/**
 * The Class LaunchCommand.
 *
 * @author just
 */
public class LaunchCommand extends Command {
	
	/**
	 * The Class ModuleArgument.
	 */
	class ModuleArgument extends Argument {
		
		/* (non-Javadoc)
		 * @see net.ownhero.dev.andama.observer.Argument#defaultValue()
		 */
		@Override
		public String defaultValue() {
			return null;
		}
		
		/* (non-Javadoc)
		 * @see net.ownhero.dev.andama.observer.Argument#helpString()
		 */
		@Override
		public String helpString() {
			// TODO Auto-generated method stub
			return null;
		}
		
		/* (non-Javadoc)
		 * @see net.ownhero.dev.andama.observer.Argument#isRequired()
		 */
		@Override
		public boolean isRequired() {
			return true;
		}
		
		/* (non-Javadoc)
		 * @see net.ownhero.dev.andama.observer.Argument#value()
		 */
		@Override
		public String value() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	/**
	 * Instantiates a new launch command.
	 *
	 * @param args the args
	 */
	public LaunchCommand(final String args) {
		// TODO this could be done automatically by the command registry
		// (register all subclasses extending arguments to the given command)
		getValidArguments().add(ModuleArgument.class);
	}
	
	/* (non-Javadoc)
	 * @see net.ownhero.dev.andama.observer.Command#execute()
	 */
	@Override
	public boolean execute() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
