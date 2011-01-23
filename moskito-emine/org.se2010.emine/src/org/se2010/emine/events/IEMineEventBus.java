package org.se2010.emine.events;

public interface IEMineEventBus 
{
	public void registerEventListener(Class<? extends IEMineEvent> eventType, IEMineEventListener listener);
	public void unregisterEventListener(IEMineEventListener listener);
	public void fireEvent(IEMineEvent event);
}
