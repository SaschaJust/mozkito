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
