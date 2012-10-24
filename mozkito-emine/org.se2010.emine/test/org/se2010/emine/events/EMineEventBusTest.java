/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package org.se2010.emine.events;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import junit.framework.AssertionFailedError;
import junit.framework.TestListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
//		final Field field = this.eventBus.getClass().getDeclaredField("instance");
//		field.setAccessible(true);
//		field.set(this.eventBus, null);

		EMineEventBus.clear();
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
	private Map<Class<? extends IEMineEvent>, List<IEMineEventListener>> getListenerMap()
	throws Exception
	{
		final Field field = this.eventBus.getClass().getDeclaredField("listenerMap");
		field.setAccessible(true);
		
		return (Map<Class<? extends IEMineEvent>, 
		    	List<IEMineEventListener>>) field.get(this.eventBus);

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
		
		
		final Map<Class<? extends IEMineEvent>, 
		              List<IEMineEventListener>> listenerMap;
		listenerMap = this.getListenerMap();
		
		final List<IEMineEventListener> queue1 = listenerMap.get(event1.getClass());
		assertFalse(queue1 == null);
		assertTrue(queue1.size() == 1);
		assertTrue(queue1.toArray()[0] == listener1);
		
		final List<IEMineEventListener> queue2 = listenerMap.get(event2.getClass());
		assertFalse(queue2 == null);
		assertTrue(queue2.size() == 1);
		assertTrue(queue2.toArray()[0] == listener2);
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
		
		final Map<Class<? extends IEMineEvent>, 
					  List<IEMineEventListener>> listenerMap;		
		
		listenerMap = this.getListenerMap();
		
		final List<IEMineEventListener> listeners = listenerMap.get(event1.getClass());
		assertFalse(listeners == null);
		assertTrue (listeners.size() == 0);
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
		
		final Map<Class<? extends IEMineEvent>, 
					  List<IEMineEventListener>> listenerMap;		
		
		listenerMap = this.getListenerMap();
		
		List<IEMineEventListener> listeners = listenerMap.get(event1.getClass());
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
	
	
	@Test
	public void testConcurrentRegistration() throws Exception
	{
		final Runnable run = new Runnable() 
		{
			@Override
			public void run() 
			{
				final IEMineEventListener listener = new TestListener();
				EMineEventBus.getInstance().registerEventListener(TestEvent1.class, listener);
			}
		};
		
		
		for(int i = 0; i < 1000; i++)
		{
			final Thread t1 = new Thread(run);
			final Thread t2 = new Thread(run);
			final Thread t3 = new Thread(run);
			final Thread t4 = new Thread(run);
			final Thread t5 = new Thread(run);

			t1.start();
			t2.start();
			t3.start();
			t4.start();
			t5.start();
			
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			t5.join();
			
			final List<IEMineEventListener> queue = this.getListenerMap().get(TestEvent1.class);
			assertFalse(queue == null);
			assertTrue(queue.size() == 5);
			this.tearDown();
			this.setUp();
		}
	}

	@Test
	public void testConcurrentDeRegistration() throws Exception
	{
		final IEMineEventListener listener1 = new TestListener();
		final IEMineEventListener listener2 = new TestListener();
		final IEMineEventListener listener3 = new TestListener();
		final IEMineEventListener listener4 = new TestListener();
		final IEMineEventListener listener5 = new TestListener();

		
		
		for(int i = 0; i < 1000; i++)
		{
			EMineEventBus.getInstance().registerEventListener(TestEvent1.class, listener1);
			EMineEventBus.getInstance().registerEventListener(TestEvent1.class, listener2);
			EMineEventBus.getInstance().registerEventListener(TestEvent1.class, listener3);
			EMineEventBus.getInstance().registerEventListener(TestEvent1.class, listener4);
			EMineEventBus.getInstance().registerEventListener(TestEvent1.class, listener5);
			EMineEventBus.getInstance().registerEventListener(TestEvent2.class, listener1);
			EMineEventBus.getInstance().registerEventListener(TestEvent2.class, listener2);
			EMineEventBus.getInstance().registerEventListener(TestEvent2.class, listener3);

			final Thread t1 = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					EMineEventBus.getInstance().unregisterEventListener(listener1);
				}
			});
			
			final Thread t2 = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					EMineEventBus.getInstance().unregisterEventListener(listener2);
				}
			});
			
			final Thread t3 = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					EMineEventBus.getInstance().unregisterEventListener(listener3);
				}
			});
			
			final Thread t4 = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					EMineEventBus.getInstance().unregisterEventListener(listener4);
				}
			});
			
			final Thread t5 = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					EMineEventBus.getInstance().unregisterEventListener(listener5);
				}
			});

			t1.start();
			t2.start();
			t3.start();
			t4.start();
			t5.start();
			
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			t5.join();
			
			final List<IEMineEventListener> queue = this.getListenerMap().get(TestEvent1.class);
			assertFalse(queue == null);
			assertTrue(queue.size() == 0);

			final List<IEMineEventListener> queue2 = this.getListenerMap().get(TestEvent2.class);
			assertFalse(queue2 == null);
			assertTrue(queue2.size() == 0);
			
			this.tearDown();
			this.setUp();
		}
	}
	
	
	@Test @Ignore
	public void testConcurrentRegAndDereg() throws Exception
	{
		final IEMineEventListener listener1 = new TestListener();
		
		for(int i = 0; i < 1000; i++)
		{
			final Thread t1 = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					EMineEventBus.getInstance().registerEventListener(TestEvent1.class, listener1);
				}
			});

			final Thread t2 = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					EMineEventBus.getInstance().unregisterEventListener(listener1);
				}
			});
			
			
			t1.start();
			t2.start();
			
			t1.join();
			t2.join();
			
			final List<IEMineEventListener> queue = this.getListenerMap().get(TestEvent1.class);
			assertFalse(queue == null);
			assertTrue(queue.size() == 0);
			
			this.tearDown();
			this.setUp();
		}
	}
	
	
	@Test
	public void testComplexEventFiring() throws Exception
	{
		EMineEventBus.getInstance().registerEventListener(TestEvent1.class, new IEMineEventListener() 
		{
			@Override
			public void onEvent(IEMineEvent event) 
			{
				EMineEventBus.getInstance().fireEvent(new TestEvent2());
			}
		});
		
		EMineEventBus.getInstance().registerEventListener(TestEvent2.class, new IEMineEventListener() 
		{
			@Override
			public void onEvent(IEMineEvent event) 
			{
				EMineEventBus.getInstance().fireEvent(new TestEvent3());
			}
		});
		
		final TestListener listener = new TestListener();
		EMineEventBus.getInstance().registerEventListener(TestEvent3.class, listener);

		EMineEventBus.getInstance().fireEvent(new TestEvent1());
	
		Thread.sleep(1000);
		
		assertFalse(listener.event == null);
		assertTrue(listener.event instanceof TestEvent3);
	}
	
	
	@Test
	public void testConcurrentComplexEventFiring() throws Exception
	{
		EMineEventBus.getInstance().registerEventListener(TestEvent1.class, new IEMineEventListener() 
		{
			@Override
			public void onEvent(IEMineEvent event) 
			{
				new Thread(new Runnable() 
				{
					
					@Override
					public void run() 
					{
						EMineEventBus.getInstance().fireEvent(new TestEvent2());		
					}
				}).start();
				
			}
		});
		
		EMineEventBus.getInstance().registerEventListener(TestEvent2.class, new IEMineEventListener() 
		{
			@Override
			public void onEvent(IEMineEvent event) 
			{
				new Thread(new Runnable() 
				{
					@Override
					public void run() 
					{
						EMineEventBus.getInstance().fireEvent(new TestEvent3());
					}
				}).start();
			}
		});
		
		final TestListener listener = new TestListener();
		EMineEventBus.getInstance().registerEventListener(TestEvent3.class, listener);

		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				EMineEventBus.getInstance().fireEvent(new TestEvent1());
			}
		}).start();
	
		Thread.sleep(1000);
		
		assertFalse(listener.event == null);
		assertTrue(listener.event instanceof TestEvent3);
	}
	
	
	@Test
	public void testEventFiringWhileOtherOperation() throws Exception
	{
		for(int i = 0; i < 100; i++)
		{
			final List<IEMineEventListener> listenersEv1 = Collections.synchronizedList(new ArrayList<IEMineEventListener>(10000)); 
			final List<IEMineEventListener> listenersEv2 = Collections.synchronizedList(new ArrayList<IEMineEventListener>(10000)); 
	
			for(int j = 0; j < 100; j++)
			{
				listenersEv1.add(new TestListener());
				listenersEv2.add(new TestListener());
			}
			
			final Thread t1 = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					for(final IEMineEventListener l : listenersEv1)
					{
						EMineEventBus.getInstance().registerEventListener(TestEvent1.class, l);
					}
				}
			});
			
			
			final Thread t2 = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					for(final IEMineEventListener l : listenersEv2)
					{
						EMineEventBus.getInstance().registerEventListener(TestEvent2.class, l);
					}
				}
			});
			
			
			final Thread t3 = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					EMineEventBus.getInstance().fireEvent(new TestEvent1());
				}
			});
			
			
			final Thread t4 = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					EMineEventBus.getInstance().fireEvent(new TestEvent2());
				}
			});		
			
			
			final Thread t5 = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					for(final IEMineEventListener l : listenersEv1)
					{
						EMineEventBus.getInstance().unregisterEventListener(l);
					}
				}
			});
			
			
			final Thread t6 = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					for(final IEMineEventListener l : listenersEv2)
					{
						EMineEventBus.getInstance().unregisterEventListener(l);
					}
				}
			});		
			
			
			final TestListener listener1 = new TestListener();
			final TestListener listener2 = new TestListener();
			
			EMineEventBus.getInstance().registerEventListener(TestEvent1.class, listener1);
			EMineEventBus.getInstance().registerEventListener(TestEvent2.class, listener2);
				
			t1.start();
			t2.start();
			t5.start();
			t6.start();
			t3.start();
			t4.start();

			
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			t5.join();
			t6.join();
	
			Thread.sleep(10);
			
			
			assertTrue(listener1.event instanceof TestEvent1);
			assertTrue(listener2.event instanceof TestEvent2);
		}
	}
	
	
	
	//---------------------------------------------------------------------
	//-- Classes for test purposes
	//----------------------------------------------------------------------
	
	private static final class TestEvent1 implements IEMineEvent { }
	private static final class TestEvent2 implements IEMineEvent { }
	private static final class TestEvent3 implements IEMineEvent { }
	
	private static final class TestListener implements IEMineEventListener
	{
		public IEMineEvent event;
		public int		   count;
		
		@Override
		synchronized
		public void onEvent(final IEMineEvent event) 
		{
			this.event = event;
			count++;
		}
	}
}
