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
