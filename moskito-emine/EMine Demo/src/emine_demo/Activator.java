package emine_demo;

import org.se2010.emine.IActivator;
import org.se2010.emine.events.EMineEventBus;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements IActivator
{
	private final DummyListener repoSuiteListener;
	private final DebugListener     debugListener;
	
	
	/**
	 * The constructor
	 */
	public Activator() 
	{
		this.repoSuiteListener = new DummyListener();
		this.debugListener     = new DebugListener();
	}

	@Override
	public void start() 
	{
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> START Extension");
		
		this.repoSuiteListener.registerOnEventBus();
		this.debugListener.registerOnEventBus();
	}

	@Override
	public void stop() 
	{
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> STOP Extension");
		
		EMineEventBus.getInstance().unregisterEventListener(this.repoSuiteListener);
		EMineEventBus.getInstance().unregisterEventListener(this.debugListener);
	}
}
