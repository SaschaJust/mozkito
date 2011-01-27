package org.se2010.emine.events;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class EMineEventBus implements IEMineEventBus
{
	private final Map<Class<? extends IEMineEvent>, List<IEMineEventListener>> listenerMap;
	
	private static final EMineEventBus instance = new EMineEventBus();
	
	private EMineEventBus() 
	{	
		this.listenerMap = new ConcurrentHashMap<Class<? extends IEMineEvent>, List<IEMineEventListener>>();
	}
	
	public static IEMineEventBus getInstance()
	{
		return EMineEventBus.instance;				
	}
	
	public static void clear()
	{
		EMineEventBus.instance.listenerMap.clear();
	}
	
	@Override
	public void registerEventListener(final Class<? extends IEMineEvent> eventType,
									  final IEMineEventListener 		 listener) 
	{
		if(eventType == null)
		{
			throw new NullPointerException("Event type must not be null!");
		}
		
		if(listener == null)
		{
			throw new NullPointerException("Event listener must not be null!");
		}
		
		List<IEMineEventListener> listenerList;
		synchronized (this.listenerMap) 
		{
			listenerList = this.listenerMap.get(eventType);

			if(listenerList == null)
			{
				listenerList = Collections.synchronizedList(new ArrayList<IEMineEventListener>());
				this.listenerMap.put(eventType, listenerList);
			}	
		}
		
		synchronized (listenerList) 
		{
			if(listenerList.contains(listener))
			{
				// TODO: introduce specialization of RuntimeExcepion?
				throw new RuntimeException("Listener " + listener + 
										   " is already registered for event type" + eventType.getName());
			}
			
			listenerList.add(listener);
		}
	}
	

	@Override
	public void unregisterEventListener(final IEMineEventListener listener) 
	{
		if(listener == null)
		{
			throw new NullPointerException("Event listener must not be null!");
		}

		// a IEMineEventListener can be installed for different event types. Therefore, the entire map need to be browsed.
		for(final Map.Entry<Class<? extends IEMineEvent>, List<IEMineEventListener>> entrySet : this.listenerMap.entrySet())
		{
			final List<IEMineEventListener> listeners = entrySet.getValue();
			listeners.remove(listener);
		}
	}

	@Override
	public void fireEvent(final IEMineEvent event) 
	{
		if(event == null)
		{
			throw new NullPointerException("Event must not be null!");
		}
		
		final List<IEMineEventListener> listeners = this.listenerMap.get(event.getClass());
		if(listeners == null)
		{
			// TODO: shall we introduce a logging mechanism and output a warning here?
			return;
		}
		
		synchronized(listeners)
		{
			// TODO: thread pool management
			for(final IEMineEventListener listener : listeners)
			{
				final Runnable run = new Runnable() 
				{
					@Override
					public void run() 
					{
						listener.onEvent(event);
					}
				};
				new Thread(run).start();
			}			
		}
	}
}
