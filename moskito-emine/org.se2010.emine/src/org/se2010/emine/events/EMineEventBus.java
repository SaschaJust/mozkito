package org.se2010.emine.events;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of {@link IEMineEventBus}. {@link EMineEventBus} is designed
 * according to Singleton design pattern as there must be only one {@link IEMineEventBus}
 * instance at runtime and it must be accessible from everywhere. 
 * 
 * @TODO Change Singleton to Factory pattern for providing and maintaining {@link IEMineEventBus} instance
 * 
 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
 * @version  1.0 02/2011
 */
public final class EMineEventBus implements IEMineEventBus
{
	private final Map<Class<? extends IEMineEvent>, List<IEMineEventListener>> listenerMap;
	
	private static final EMineEventBus instance = new EMineEventBus();

	private final Lock lock;

	/**
	 * Private constructor
	 */
	private EMineEventBus() 
	{	
		this.listenerMap = new ConcurrentHashMap<Class<? extends IEMineEvent>, List<IEMineEventListener>>();
		this.lock        = new ReentrantLock();
	}
	
	/**
	 * Provides {@link IEMineEventBus} instance (according to Singleton pattern)
	 * 
	 * @return {@link IEMineEventBus} instance
	 */
	public static IEMineEventBus getInstance()
	{
		return EMineEventBus.instance;				
	}
	
	/**
	 * Removes all registered listeners.
	 */
	public static void clear()
	{
		EMineEventBus.instance.listenerMap.clear();
	}

	/**
	 * {@inheritDoc}
	 */
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

		this.lock.lock();
		try
		{
			listenerList = this.listenerMap.get(eventType);

			if(listenerList == null)
			{
				listenerList = Collections.synchronizedList(new ArrayList<IEMineEventListener>());
				this.listenerMap.put(eventType, listenerList);
			}	
		}
		finally
		{
			this.lock.unlock();
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
	
	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fireEvent(final IEMineEvent event) 
	{
		if(event == null)
		{
			throw new NullPointerException("Event must not be null!");
		}
		
		this.lock.lock();
		final List<IEMineEventListener> listeners;
		try
		{
			listeners = this.listenerMap.get(event.getClass());
			if(listeners == null)
			{
				// TODO: shall we introduce a logging mechanism and output a warning here?
				return;
			}
		}
		finally
		{
			this.lock.unlock();
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