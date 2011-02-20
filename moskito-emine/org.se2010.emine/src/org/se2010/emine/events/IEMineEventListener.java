package org.se2010.emine.events;

/**
 * Interface for components listening on {@link IEMineEventBus} for specific {@link IEMineEvent}s.
 * 
 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
 * @version  1.0 02/2011
 */
public interface IEMineEventListener 
{
	/**
	 * onEvent() is called by the {@link IEMineEventBus} instance
	 * on which the listener is registered when a corresponding
	 * {@link IEMineEvent} is fired via {@link IEMineEventBus#fireEvent(IEMineEvent)}.
	 * 
	 * <b>Note:</b> Every implementation of {@link IEMineEventListener} should
	 * consider thread safety regarding this method as it is usually called
	 * concurrently.
	 * 
	 * @param event  {@link IEMineEvent} instance for which the listener is registered on {@link IEMineEventBus}
	 */
	public void onEvent(IEMineEvent event);
}
