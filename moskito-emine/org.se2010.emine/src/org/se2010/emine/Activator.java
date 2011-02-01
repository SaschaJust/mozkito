 package org.se2010.emine;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.EclipseEventHandler;
import org.se2010.emine.events.EclipseEventHandler;
import org.se2010.emine.events.EditorEvent;
import org.se2010.emine.events.IEMineEvent;
import org.se2010.emine.events.IEMineEventListener;
import org.se2010.emine.events.ModificationEvent;
import org.se2010.emine.events.reposuite.RepoSuiteEvent;
import org.se2010.emine.events.reposuite.RepoSuiteListener;
import org.se2010.emine.listeners.Controller;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.se2010.emine"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	//private EclipseEventHandler eHandler = null;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception 
	{
		super.start(context);
		plugin = this;
		
	 EclipseEventHandler.init();
		
		final IEMineEventListener listener = new IEMineEventListener() 
		{
			
			public void onEvent(final IEMineEvent event) 
			{
				System.out.println("==== TEST LISTENER ====> " + event);
			}
		};

		EMineEventBus.getInstance().registerEventListener(EditorEvent.EditorOpenedEvent.class, listener); 
		EMineEventBus.getInstance().registerEventListener(EditorEvent.EditorClosedEvent.class, listener); 
		EMineEventBus.getInstance().registerEventListener(EditorEvent.EditorActivatedEvent.class, listener); 
		EMineEventBus.getInstance().registerEventListener(EditorEvent.EditorDeactivatedEvent.class, listener); 
		EMineEventBus.getInstance().registerEventListener(ModificationEvent.ClassAddedEvent.class, listener); 
		EMineEventBus.getInstance().registerEventListener(ModificationEvent.ClassRemovedEvent.class, listener); 
		EMineEventBus.getInstance().registerEventListener(ModificationEvent.ClassChangedEvent.class, listener);
		
		new RepoSuiteListener();
	//	new Controller();
	}
	
	public static void getEclipseEventHandler(){
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
