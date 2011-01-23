package org.se2010.emine.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class EMineEventBus implements IEMineEventBus
{
	private final HashMap<Class<? extends IEMineEvent>, ArrayList<IEMineEventListener>> listenerMap;
	
	private static EMineEventBus instance;
	
	private EMineEventBus() 
	{	
		this.listenerMap = new HashMap<Class<? extends IEMineEvent>, ArrayList<IEMineEventListener>>();
	}
	
	public static IEMineEventBus getInstance()
	{
		if(EMineEventBus.instance == null)
		{
		   EMineEventBus.instance = new EMineEventBus();
		}
		
		return EMineEventBus.instance;
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
		
		
		ArrayList<IEMineEventListener> listenerList = this.listenerMap.get(eventType);
		
		if(listenerList == null)
		{
			listenerList = new ArrayList<IEMineEventListener>();
			this.listenerMap.put(eventType, listenerList);
		}
		
		listenerList.add(listener);
	}
	

	@Override
	public void unregisterEventListener(final IEMineEventListener listener) 
	{
		if(listener == null)
		{
			throw new NullPointerException("Event listener must not be null!");
		}
		
		// a IEMineEventListener can be installed for different event types. Therefore, the entire map need to be browsed.
		for(final Map.Entry<Class<? extends IEMineEvent>, ArrayList<IEMineEventListener>> entrySet : this.listenerMap.entrySet())
		{
			final ArrayList<IEMineEventListener> listeners = entrySet.getValue();
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
		
		final Class<? extends IEMineEvent> eventType= event.getType();
		
		if(eventType == null)
		{
			//TODO: introduce an own RuntimeException?
			throw new RuntimeException("Event type retrieved from event " + event + " is null!");
		}

		
		final ArrayList<IEMineEventListener> listeners = this.listenerMap.get(eventType);

		// TODO: shall we introduce a logging mechanism and output a warning here?
		if(listeners != null)
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
