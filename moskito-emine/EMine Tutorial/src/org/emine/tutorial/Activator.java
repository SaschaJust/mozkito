package org.emine.tutorial;

import org.se2010.emine.IActivator;
import org.se2010.emine.artifacts.ConfigurationArtifact;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.EditorEvent;
import org.se2010.emine.events.IEMineEventBus;
import org.se2010.emine.events.IEMineEventListener;
import org.se2010.emine.events.ModificationEvent;

public final class Activator implements IActivator 
{
	private final IEMineEventListener listener;

	public Activator()
	{
		this.listener = new DebugListener();
	}
	
	@Override
	public void start() 
	{
		final IEMineEventBus bus = EMineEventBus.getInstance();
		
		bus.registerEventListener(EditorEvent.EditorOpenedEvent.class, 		 this.listener); 
		bus.registerEventListener(EditorEvent.EditorClosedEvent.class, 		 this.listener); 
		bus.registerEventListener(EditorEvent.EditorActivatedEvent.class,    this.listener); 
		bus.registerEventListener(EditorEvent.EditorDeactivatedEvent.class,  this.listener); 
		bus.registerEventListener(ModificationEvent.ClassAddedEvent.class,   this.listener); 
		bus.registerEventListener(ModificationEvent.ClassRemovedEvent.class, this.listener); 
		bus.registerEventListener(ModificationEvent.ClassChangedEvent.class, this.listener);
		bus.registerEventListener(ConfigurationArtifact.class, 				 this.listener);
	}

	@Override
	public void stop() 
	{
		EMineEventBus.getInstance().unregisterEventListener(this.listener);
	}

}
