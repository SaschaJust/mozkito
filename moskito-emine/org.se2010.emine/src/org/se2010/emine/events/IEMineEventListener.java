package org.se2010.emine.events;

public interface IEMineEventListener 
{
	// should be synchronized!
	public void onEvent(IEMineEvent event);
}
