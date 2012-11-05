/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.andama.messages;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;

/**
 * The Class Event.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class Event implements IEvent {
	
	/** The message. */
	private String               message;
	
	/** The callbacks. */
	private final List<Callback> callbacks = new LinkedList<Callback>();
	
	/** The fired. */
	private DateTime             fired;
	
	/** The issued. */
	private DateTime             issued;
	
	/** The level. */
	private AccessLevel          level;
	
	/** The type. */
	private EventType            type;
	
	/** The origin. */
	private Class<?>             origin;
	
	/** The location. */
	private String               location;
	
	/**
	 * Instantiates a new event.
	 * 
	 * @param message
	 *            the message
	 * @param type
	 *            the type
	 * @param level
	 *            the level
	 */
	public Event(@NotNull final String message, @NotNull final EventType type, @NotNull final AccessLevel level) {
		// PRECONDITIONS
		
		try {
			this.message = message;
			this.type = type;
			this.level = level;
			this.issued = new DateTime();
			final Throwable throwable = new Throwable();
			
			throwable.fillInStackTrace();
			
			final Integer lineNumber = throwable.getStackTrace()[1].getLineNumber();
			final String methodName = throwable.getStackTrace()[1].getMethodName();
			final String className = throwable.getStackTrace()[1].getClassName();
			
			try {
				this.origin = Class.forName(className);
			} catch (final ClassNotFoundException e) {
				if (Logger.logWarn()) {
					Logger.warn(e);
				}
				this.origin = getClass();
			}
			
			this.location = "[" + className + "::" + methodName + "#" + lineNumber + "]";
			
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.message, "Field '%s' in '%s'.", "message", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.type, "Field '%s' in '%s'.", "type", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.level, "Field '%s' in '%s'.", "level", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.issued, "Field '%s' in '%s'.", "issued", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.callbacks, "Field '%s' in '%s'.", "callbacks", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			CollectionCondition.empty(this.callbacks, "Field '%s' in '%s'.", "callbacks", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.messages.IEvent#addCallback(net.ownhero.dev.andama.messages.Callback)
	 */
	@Override
	public final void addCallback(final Callback callback) {
		// PRECONDITIONS
		Condition.notNull(this.callbacks, "Field '%s' in '%s'.", "callbacks", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		final int size = this.callbacks.size();
		
		try {
			this.callbacks.add(callback);
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.callbacks, "Field '%s' in '%s'.", "callbacks", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			CollectionCondition.size(this.callbacks, size + 1, "Adding '%s' to the field '%s' failed.", callback,
			                         "callbacks");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.messages.IEvent#accept()
	 */
	@Override
	public final void done() {
		// PRECONDITIONS
		
		try {
			if (Logger.logTrace()) {
				Logger.trace(String.format("Executing %s callbacks.", this.callbacks.size()));
			}
			for (final Callback callback : this.callbacks) {
				if (Logger.logTrace()) {
					Logger.trace(String.format("Executing callback '%s'.", callback));
				}
				callback.execute(this);
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Fired.
	 * 
	 */
	@Override
	public synchronized final void fired() {
		// PRECONDITIONS
		
		try {
			this.fired = new DateTime();
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.fired, "Field '%s' in '%s'.", "fired", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.messages.IEvent#getFired()
	 */
	@Override
	public DateTime getFired() {
		// PRECONDITIONS
		
		try {
			return this.fired;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	@Override
	public final String getHandle() {
		// PRECONDITIONS
		
		final StringBuilder builder = new StringBuilder();
		
		try {
			final LinkedList<Class<?>> list = new LinkedList<Class<?>>();
			Class<?> clazz = getClass();
			list.add(clazz);
			
			while ((clazz = clazz.getEnclosingClass()) != null) {
				list.addFirst(clazz);
			}
			
			for (final Class<?> c : list) {
				if (builder.length() > 0) {
					builder.append('.');
				}
				
				builder.append(c.getSimpleName());
			}
			
			return builder.toString();
		} finally {
			// POSTCONDITIONS
			Condition.notNull(builder,
			                  "Local variable '%s' in '%s:%s'.", "builder", getClass().getSimpleName(), "getHandle"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.messages.IEvent#getIssued()
	 */
	@Override
	public DateTime getIssued() {
		// PRECONDITIONS
		Condition.notNull(this.issued, "Field '%s' in '%s'.", "issued", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.issued;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.messages.IEvent#getLevel()
	 */
	@Override
	public AccessLevel getLevel() {
		// PRECONDITIONS
		Condition.notNull(this.level, "Field '%s' in '%s'.", "level", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.level;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.messages.IEvent#getLocation()
	 */
	@Override
	public String getLocation() {
		// PRECONDITIONS
		Condition.notNull(this.location, "Field '%s' in '%s'.", "location", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.location;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.messages.IEvent#getMessage()
	 */
	@Override
	public String getMessage() {
		// PRECONDITIONS
		Condition.notNull(this.message, "Field '%s' in '%s'.", "message", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.message;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.messages.IEvent#getOrigin()
	 */
	@Override
	public Class<?> getOrigin() {
		// PRECONDITIONS
		Condition.notNull(this.origin, "Field '%s' in '%s'.", "origin", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.origin;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.messages.IEvent#getType()
	 */
	@Override
	public EventType getType() {
		// PRECONDITIONS
		Condition.notNull(this.type, "Field '%s' in '%s'.", "type", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.type;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
