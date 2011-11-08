package org.se2010.emine.events;

/**
 * The eMine event mechanism is inspired by the <a href="http://code.google.com/intl/de-DE/webtoolkit/articles/mvp-architecture.html#events">
 * Google Web Toolkit Event Bus</a> pattern. It is mainly intended for a flexible and bidirectional communication between the eMine plugin and the
 * MSA-core. The idea is that each arbitrary component can trigger an {@link IEMineEvent} over the event bus
 * where other components may have registered one or more {@link IEMineEventListener}s listening for
 * specific kinds of {@link IEMineEvent}s. Each of those listeners shall be notified asynchronously, as there is no
 * need to synchronize with the listeners. 
 * <br/><b>Note:</b> There must be only one {@link IEMineEventBus} at runtime!
 * 
 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
 * @version  1.0 02/2011
 */
public interface IEMineEventBus 
{
	/**
	 * Registers an {@link IEMineEventListener} for a specific event.
	 * 
	 * @param eventType  Class of {@link IEMineEvent} implementation for which the given listener shall be registered
	 * @param listener   {@link IEMineEventListener} implementation
	 */
	public void registerEventListener(Class<? extends IEMineEvent> eventType, IEMineEventListener listener);

	/**
	 * Unregisters given {@link IEMineEventListener} from {@link IEMineEventBus}.
	 * If the given listener is not registered, it does nothing.
	 * 
	 * @param listener  listener to be removed
	 */
	public void unregisterEventListener(IEMineEventListener listener);
	
	/**
	 * Fires the given {@link IEMineEvent} over the {@link IEMineEventBus} 
	 * passing the event asynchronously to the corresponding listeners.
	 * 
	 * @param event {@link IEMineEvent} implementation
	 */
	public void fireEvent(IEMineEvent event);
}