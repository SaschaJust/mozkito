/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package net.ownhero.dev.andama.threads;

import java.lang.reflect.Method;
import java.util.Collection;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.messages.EventBus;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class Hook.
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class Hook<K, V> implements IHook<K, V> {
	
	/**
	 * All completed.
	 * 
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @param hooks
	 *            the hooks
	 * @return true, if successful
	 */
	public static <K, V> boolean allCompleted(final Collection<? extends Hook<K, V>> hooks) {
		for (final IHook<K, V> hook : hooks) {
			if (!hook.completed()) {
				return false;
			}
		}
		
		return true;
	}
	
	/** The completed. */
	private boolean          completed = true;
	
	/** The thread. */
	private final Node<K, V> thread;
	
	/**
	 * Instantiates a new hook.
	 * 
	 * @param thread
	 *            the thread
	 */
	public Hook(final Node<K, V> thread) {
		if (Logger.logDebug()) {
			Logger.debug("Adding '%s' to '%s'.", getHandle(), thread);
		}
		this.thread = thread;
		
		Class<?> clazz = this.getClass();
		
		while ((clazz.getSuperclass() != null) && (clazz.getSuperclass() != Hook.class)) {
			clazz = clazz.getSuperclass();
		}
		
		if (clazz.getSuperclass() != Hook.class) {
			// TODO ERROR
		}
		
		@SuppressWarnings ("unchecked")
		final Class<? extends Hook<K, V>> superclass = (Class<? extends Hook<K, V>>) clazz;
		
		try {
			final Method method = Node.class.getDeclaredMethod("add" + superclass.getSimpleName(), superclass);
			method.invoke(thread, this);
		} catch (final Exception e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
	}
	
	/**
	 * Completed.
	 * 
	 * @return if the {@link ProcessHook} is done with the current data
	 */
	@Override
	public final boolean completed() {
		return this.completed;
	}
	
	/**
	 * Gets the event bus.
	 * 
	 * @return the event bus
	 */
	@Override
	public EventBus getEventBus() {
		return getThread().getEventBus();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#getHandle()
	 */
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	@Override
	public final String getHandle() {
		return this.getClass().getSimpleName().isEmpty()
		                                                ? this.getClass().getSuperclass().getSimpleName()
		                                                : this.getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#getThread()
	 */
	/**
	 * Gets the thread.
	 * 
	 * @return the thread
	 */
	@Override
	public final Node<K, V> getThread() {
		return this.thread;
	}
	
	/**
	 * Sets the completed.
	 */
	public final void setCompleted() {
		this.completed = true;
	}
	
	/**
	 * Unset completed.
	 */
	public final void unsetCompleted() {
		this.completed = false;
	}
	
}
