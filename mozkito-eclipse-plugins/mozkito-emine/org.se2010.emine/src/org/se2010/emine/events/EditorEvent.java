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

import java.util.ArrayList;
import java.util.List;

/**
 * {@link EditorEvent} represents all events relating to Eclipse the Java editor.
 * 
 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
 * @version  1.0 02/2011
 */
public final class EditorEvent
{
	/**
	 * {@link EditorEvent} is not intended to be instantiated 
	 * (only the nested classes shall be created).
	 */
	private EditorEvent(){ }
	
	/**
	 * {@link AEditorEvent} is the super class of all nested {@link EditorEvent}
	 * classes.
	 *  
	 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
	 * @version  1.0 02/2011
	 */
	private static abstract class AEditorEvent implements IEMineEvent
	{
		private final List<String> clazzes;
		private final String       filePath;
		
		/**
		 * Constructor of {@link AEditorEvent}
		 * 
		 * @param clazzes  list of fully qualified class names affected by this
		 * 				   event
		 * @param filePath file path of the affected compilation unit
		 */
		public AEditorEvent(final List<String> clazzes, final String filePath)
		{
			this.clazzes  = clazzes;
			this.filePath = filePath;
		}
		
		/**
		 * Returns the file path of the affected compilation unit.
		 * 
		 * @return file path
		 */
		public String getFilePath()
		{
			return this.filePath;
		}
		
		/**
		 * Returns list of fully qualified class names affected by this
		 * event.
		 * 
		 * @return list of fully qualified class names
		 */
		public List<String> getAffectedClazzes()
		{
			return new ArrayList<String>(this.clazzes);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() 
		{
			return "AEditorEvent [clazzes=" + clazzes + ", filePath="
					+ filePath + "]";
		}
	}
	
	/**
	 * {@link EditorOpenedEvent} is triggered when an editor has been opened
	 * 
	 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
	 * @version  1.0 02/2011
	 */
	public static final class EditorOpenedEvent extends AEditorEvent
	{
		/**
		 * Constructor of {@link EditorOpenedEvent}
		 * 
		 * @param clazzes  list of fully qualified class names affected by this
		 * 				   event
		 * @param filePath file path of the affected compilation unit
		 */
		public EditorOpenedEvent(List<String> clazzes, final String filePath )
		{
			super(clazzes, filePath);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() 
		{
			return "EditorOpenedEvent [getFilePath()=" + getFilePath()
					+ ", getAffectedClazzes()=" + getAffectedClazzes() + "]";
		}
	}

	/**
	 * {@link EditorClosedEvent} when an editor has been closed
	 * 
	 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
	 * @version  1.0 02/2011
	 */
	public static final class EditorClosedEvent extends AEditorEvent
	{
		/**
		 * Constructor of {@link EditorClosedEvent}
		 * 
		 * @param clazzes  list of fully qualified class names affected by this
		 * 				   event
		 * @param filePath file path of the affected compilation unit
		 */
		public EditorClosedEvent(final List<String> clazzes, final String filePath)
		{
			super(clazzes, filePath);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() 
		{
			return "EditorClosedEvent [getFilePath()=" + getFilePath()
					+ ", getAffectedClazzes()=" + getAffectedClazzes() + "]";
		}
	}

	/**
	 * {@link EditorActivatedEvent} is triggered when an editor has got focus 
	 * 
	 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
	 * @version  1.0 02/2011
	 */
	public static final class EditorActivatedEvent extends AEditorEvent
	{
		/**
		 * Constructor of {@link EditorActivatedEvent}
		 * 
		 * @param clazzes  list of fully qualified class names affected by this
		 * 				   event
		 * @param filePath file path of the affected compilation unit
		 */
		public EditorActivatedEvent(final List<String> clazzes, final String filePath)
		{
			super(clazzes, filePath);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() 
		{
			return "EditorActivatedEvent [getFilePath()=" + getFilePath()
					+ ", getAffectedClazzes()=" + getAffectedClazzes() + "]";
		}
	}

	/**
	 * {@link EditorDeactivatedEvent} when an editor has lost focus
	 * 
	 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
	 * @version  1.0 02/2011
	 */
	public static final class EditorDeactivatedEvent extends AEditorEvent
	{
		/**
		 * Constructor of {@link EditorDeactivatedEvent}
		 * 
		 * @param clazzes  list of fully qualified class names affected by this
		 * 				   event
		 * @param filePath file path of the affected compilation unit
		 */
		public EditorDeactivatedEvent(final List<String> clazzes, final String filePath)
		{
			super(clazzes, filePath);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() 
		{
			return "EditorDeactivatedEvent [getFilePath()=" + getFilePath()
					+ ", getAffectedClazzes()=" + getAffectedClazzes() + "]";
		}
	}
}
