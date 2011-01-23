package org.se2010.emine.events;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EMineEventBusTest 
{
	private IEMineEventBus eventBus;

	@Before
	public void setUp() throws Exception 
	{
		this.eventBus = EMineEventBus.getInstance();
	}

	@After
	public void tearDown() throws Exception 
	{
		final Field field = this.eventBus.getClass().getDeclaredField("instance");
		field.setAccessible(true);
		field.set(this.eventBus, null);

		this.eventBus = null;
	}
	
	@Test
	public void testGetInstance() throws Exception
	{
		assertFalse(this.eventBus == null);
		
		final IEMineEventBus bus = EMineEventBus.getInstance();
		assertTrue(this.eventBus == bus);
	}
	
	@Test(expected=NullPointerException.class)
	public void testRegisterListenerWithNullType()
	{
		final IEMineEventListener listener = new TestListener();
		this.eventBus.registerEventListener(null, listener);
	}
	
	@Test(expected=NullPointerException.class)
	public void testRegisterListenerWithNullListener()
	{
		this.eventBus.registerEventListener(IEMineEvent.class, null);
	}
	
	
	@SuppressWarnings({"unchecked" })
	private HashMap<Class<? extends IEMineEvent>, ArrayList<IEMineEventListener>> getListenerMap()
	throws Exception
	{
		final Field field = this.eventBus.getClass().getDeclaredField("listenerMap");
		field.setAccessible(true);
		
		return (HashMap<Class<? extends IEMineEvent>, 
		    	         ArrayList<IEMineEventListener>>) field.get(this.eventBus);

	}
	
	@Test
	public void testRegisterListener() throws Exception
	{
		final IEMineEventListener listener1 = new TestListener();
		final IEMineEvent		  event1    = new TestEvent1();
		
		final IEMineEventListener listener2 = new TestListener();
		final IEMineEvent		  event2    = new TestEvent2();
		
		this.eventBus.registerEventListener(event1.getClass(), listener1);
		this.eventBus.registerEventListener(event2.getClass(), listener2);
		
		
		final HashMap<Class<? extends IEMineEvent>, 
		              ArrayList<IEMineEventListener>> listenerMap;
		listenerMap = this.getListenerMap();
		
		final ArrayList<IEMineEventListener> queue1 = listenerMap.get(event1.getClass());
		assertFalse(queue1 == null);
		assertTrue(queue1.size() == 1);
		assertTrue(queue1.get(0) == listener1);
		
		final ArrayList<IEMineEventListener> queue2 = listenerMap.get(event2.getClass());
		assertFalse(queue2 == null);
		assertTrue(queue2.size() == 1);
		assertTrue(queue2.get(0) == listener2);
	}
	
	@Test(expected=RuntimeException.class)
	public void testRegisterListenerWithDuplicateListenerForSameEventType() throws Exception
	{
		final IEMineEventListener listener1 = new TestListener();
		final IEMineEvent		  event1    = new TestEvent1();
		
		this.eventBus.registerEventListener(event1.getClass(), listener1);
		this.eventBus.registerEventListener(event1.getClass(), listener1);
	}
	
	@Test
	public void testRegisterListenerWithDuplicateListenerForDiffEventType() throws Exception
	{
		final IEMineEventListener listener1 = new TestListener();
		final IEMineEvent		  event1    = new TestEvent1();
		final IEMineEvent		  event2    = new TestEvent2();
		
		this.eventBus.registerEventListener(event1.getClass(), listener1);
		this.eventBus.registerEventListener(event2.getClass(), listener1);
	}	
	
	@Test(expected=NullPointerException.class)
	public void testUnregisterListenerWithNull()
	{
		this.eventBus.unregisterEventListener(null);
	}
	
	@Test
	public void testUnregisterListenerSimpleCase() throws Exception
	{
		final IEMineEventListener listener1 = new TestListener();
		final IEMineEvent		  event1    = new TestEvent1();
		
		this.eventBus.registerEventListener(event1.getClass(), listener1);
		this.eventBus.unregisterEventListener(listener1);
		
		final HashMap<Class<? extends IEMineEvent>, 
					  ArrayList<IEMineEventListener>> listenerMap;		
		
		listenerMap = this.getListenerMap();
		
		final ArrayList<IEMineEventListener> listeners = listenerMap.get(event1.getClass());
		assertFalse(listeners == null);
		assertTrue(listeners.size() == 0);
	}
	
	@Test
	public void testUnregisterListenerComplexCase() throws Exception
	{
		final IEMineEventListener listener1 = new TestListener();
		final IEMineEvent		  event1    = new TestEvent1();
		final IEMineEvent		  event2    = new TestEvent2();
		
		this.eventBus.registerEventListener(event1.getClass(), listener1);
		this.eventBus.registerEventListener(event2.getClass(), listener1);		
		
		this.eventBus.unregisterEventListener(listener1);
		
		final HashMap<Class<? extends IEMineEvent>, 
					  ArrayList<IEMineEventListener>> listenerMap;		
		
		listenerMap = this.getListenerMap();
		
		ArrayList<IEMineEventListener> listeners = listenerMap.get(event1.getClass());
		assertFalse(listeners == null);
		assertTrue(listeners.size() == 0);
		
		listeners = listenerMap.get(event2.getClass());
		assertFalse(listeners == null);
		assertTrue(listeners.size() == 0);
	}	
	
	
	@Test(expected=NullPointerException.class)
	public void testFireEventWithNull() throws Exception
	{
		this.eventBus.fireEvent(null);
	}
	
	@Test
	public void testFireEvent() throws Exception
	{
		final int numListeners = 100;
		
		final ArrayList<TestListener> listeners1  = new ArrayList<TestListener>(numListeners);
		final ArrayList<TestListener> listeners2  = new ArrayList<TestListener>(numListeners);
		final ArrayList<TestListener> listeners12 = new ArrayList<TestListener>(numListeners);
		
		for(int i = 0; i < numListeners; i++)
		{
			listeners1 .add(new TestListener());
			listeners2 .add(new TestListener());
			listeners12.add(new TestListener());
		}
		
		final IEMineEvent event1 = new TestEvent1();
		final IEMineEvent event2 = new TestEvent2();
		
		for(final TestListener listener : listeners1)
		{
			this.eventBus.registerEventListener(event1.getClass(), listener);
		}
		
		for(final TestListener listener : listeners2)
		{
			this.eventBus.registerEventListener(event2.getClass(), listener);
		}
		
		for(final TestListener listener : listeners12)
		{
			this.eventBus.registerEventListener(event1.getClass(), listener);
			this.eventBus.registerEventListener(event2.getClass(), listener);
		}
		
		
		this.eventBus.fireEvent(event1);
		
		// Sleep for 2 seconds because fireEvent() notifies all listeners asynchronously
		Thread.sleep(2000);
		
		
		for(int i = 0; i < numListeners; i++)
		{
			assertTrue(listeners1 .get(i).event != null);
			assertTrue(listeners12.get(i).event != null);
			assertTrue(listeners2.get(i).event  == null);
		}
	}	
	
	
	
	
	
	//----------------------------------------------------------------------
	//-- Classes for test purposes
	//----------------------------------------------------------------------
	
	private final class TestEvent1 implements IEMineEvent { }
	private final class TestEvent2 implements IEMineEvent { }
	
	private final class TestListener implements IEMineEventListener
	{
		public IEMineEvent event;
		
		@Override
		public void onEvent(final IEMineEvent event) 
		{
			this.event = event;
		}
	}
}