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

import org.se2010.emine.artifacts.ConfigurationArtifact;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.EditorEvent;
import org.se2010.emine.events.IEMineEvent;
import org.se2010.emine.events.IEMineEventBus;
import org.se2010.emine.events.IEMineEventListener;
import org.se2010.emine.events.ModificationEvent;

public class DebugListener implements IEMineEventListener
{
	public DebugListener() {}

	public void registerOnEventBus()
	{
		final IEMineEventBus bus = EMineEventBus.getInstance();
		
		bus.registerEventListener(EditorEvent.EditorOpenedEvent.class, this); 
		bus.registerEventListener(EditorEvent.EditorClosedEvent.class, this); 
		bus.registerEventListener(EditorEvent.EditorActivatedEvent.class, this); 
		bus.registerEventListener(EditorEvent.EditorDeactivatedEvent.class, this); 
		bus.registerEventListener(ModificationEvent.ClassAddedEvent.class, this); 
		bus.registerEventListener(ModificationEvent.ClassRemovedEvent.class, this); 
		bus.registerEventListener(ModificationEvent.ClassChangedEvent.class, this);
		bus.registerEventListener(ConfigurationArtifact.class, this);
	}
	
	@Override
	public void onEvent(final IEMineEvent event) 
	{
		System.out.println("==== TEST LISTENER ====> " + event);		
	}
}
