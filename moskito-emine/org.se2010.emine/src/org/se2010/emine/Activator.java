/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package org.se2010.emine;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.se2010.emine.events.EclipseEventHandler;
import org.se2010.emine.views.SyntaxHighlightingListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin 
{
	// The shared instance
	private static Activator plugin;
	
	// The plug-in ID
	public static final String PLUGIN_ID = "org.se2010.emine"; //$NON-NLS-1$
	private static final String EXT_POINT_ID = "org.se2010.emine";
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	private void handleExtensions(final boolean activate) throws CoreException
	{
		final IExtensionRegistry      registry = Platform.getExtensionRegistry();
		final IConfigurationElement[] config = registry.getConfigurationElementsFor(EXT_POINT_ID);
		
		for(final IConfigurationElement configElement : config)
		{
			
			final Object o = configElement.createExecutableExtension("class");
			if(o instanceof IActivator)
			{
				final ISafeRunnable runnable = new ISafeRunnable() 
				{
					@Override
					public void handleException(final Throwable exception) 
					{
						System.err.println("Exception in client: " + exception);
					}

					@Override
					public void run() throws Exception 
					{
						final IActivator activator = (IActivator) o;
						
						if(activate)
						{
							activator.start();
						}
						else
						{
							activator.stop();
						}
					}
				};
				
				SafeRunner.run(runnable);
			}
		}
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
		this.handleExtensions(true);
		
		final SyntaxHighlightingListener highlightingListener = new SyntaxHighlightingListener();
		highlightingListener.registerOnEventBus();
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
		
		this.handleExtensions(false);
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
