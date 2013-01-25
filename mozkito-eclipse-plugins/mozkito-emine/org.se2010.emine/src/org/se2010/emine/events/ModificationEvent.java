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
 * {@link ModificationEvent} represents all events relating to class modifications.
 * 
 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
 * @version  1.0 02/2011
 */
public final class ModificationEvent 
{
	/**
	 * {@link ModificationEvent} is not intended to be instantiated 
	 * (only the nested classes shall be created).
	 */
	private ModificationEvent() { }
	
	/**
	 * {@link ACompilationUnitEvent} is the abstract super class of all modifications related to
	 * a class modification.
	 * 
	 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
	 * @version  1.0 02/2011
	 */
	private static abstract class ACompilationUnitEvent implements IEMineEvent
	{
		private final String claszzName;
		private final String filePath;
		
		/**
		 * Constructor of {@link ACompilationUnitEvent}.
		 * 
		 * @param clazzName  fully qualified name of the affected class
		 * @param filePath   file path of the affected compilation unit
		 */
		public ACompilationUnitEvent(final String clazzName, final String filePath)
		{
			if(clazzName == null)
			{
				throw new NullPointerException("Class name must not be null!");
			}

			if(clazzName.trim().isEmpty())
			{
				throw new IllegalArgumentException("Class name must not be emtpy!");
			}

			if(filePath == null)
			{
				throw new NullPointerException("File path must not be null!");
			}

			if(filePath.trim().isEmpty())
			{
				throw new IllegalArgumentException("File path must not be emtpy!");
			}
			
			this.claszzName = clazzName;
			this.filePath   = filePath;
		}
		
		/**
		 * Returns fully qualified name of affected class.
		 * 
		 * @return class name
		 */
		public String getClaszzName() 
		{
			return this.claszzName;
		}
		
		/**
		 * Return file path of the affected compilation unit.
		 * 
		 * @return file path
		 */
		public String getFilePath()
		{
			return this.filePath;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() 
		{
			return "ACompilationUnitEvent [claszzName=" + claszzName
					+ ", filePath=" + filePath + "]";
		}
	}

	/**
	 * {@link ClassAddedEvent} is fired when a class has been created.
	 * 
	 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
	 * @version  1.0 02/2011
	 */
	public static final class ClassAddedEvent extends ACompilationUnitEvent
	{

		/**
		 * Constructor of {@link ClassAddedEvent}.
		 * 
		 * @param clazzName  fully qualified name of the affected class
		 * @param filePath   file path of the affected compilation unit
		 */
		public ClassAddedEvent(final String clazzName, final String filePath) 
		{
			super(clazzName, filePath);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() 
		{
			return "ClassAddedEvent [getClaszzName()=" + getClaszzName()
					+ ", getFilePath()=" + getFilePath() + "]";
		}
	}
	
	/**
	 * {@link ClassRemovedEvent} is fired when a class has been removed.
	 * 
	 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
	 * @version  1.0 02/2011
	 */
	public static final class ClassRemovedEvent extends ACompilationUnitEvent
	{
		/**
		 * Constructor of {@link ClassRemovedEvent}.
		 * 
		 * @param clazzName  fully qualified name of the affected class
		 * @param filePath   file path of the affected compilation unit
		 */
		public ClassRemovedEvent(final String clazzName, final String filePath) 
		{
			super(clazzName, filePath);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() 
		{
			return "ClassRemovedEvent [getClaszzName()=" + getClaszzName()
					+ ", getFilePath()=" + getFilePath() + "]";
		}
	}
	
	/**
	 * {@link ClassChangedEvent} is fired when the content of a class has been changed
	 * 
	 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
	 * @version  1.0 02/2011
	 */
	public static final class ClassChangedEvent extends ACompilationUnitEvent
	{
		private final List<String> addedFields;
		private final List<String> removedFields;
		private final List<String> changedFields;
		private final List<String> addedMethods;
		private final List<String> removedMethods;
		private final List<String> changedMethods;

		/**
		 * Constructor of {@link ClassChangedEvent}.
		 * 
		 * @param clazzName  fully qualified name of the affected class
		 * @param filePath   file path of the affected compilation unit
		 */
		public ClassChangedEvent(final String clazzName, final String filePath) 
		{
			super(clazzName, filePath);
			
			this.addedFields    = new ArrayList<String>();
			this.changedFields  = new ArrayList<String>();
			this.removedFields  = new ArrayList<String>();
			this.addedMethods   = new ArrayList<String>();
			this.changedMethods = new ArrayList<String>();
			this.removedMethods = new ArrayList<String>();
		}

		/**
		 * Helper method for checking inputs.
		 * 
		 * @throws  NullPointerException 
		 * 				if the given input is null
		 * @throws  IllegalArgumentException 
		 * 				if the given input is empty
		 * @param   input 
		 * 				input to be checked
		 */
		private void checkInput(final String input)
		{
			if(input == null)
			{
				throw new NullPointerException("Name must not be null!");
			}
			
			if(input.trim().isEmpty())
			{
				throw new IllegalArgumentException("Name must not be  empty!");
			}
		}
		
		/**
		 * Adds the fully qualified name of the recently added field.
		 * 
		 * @param fieldName
		 * 			fully qualified field name
		 */
		public void addAddedField(final String fieldName)
		{
			this.checkInput(fieldName);
			this.addedFields.add(fieldName);
		}

		/**
		 * Adds the fully qualified name of the recently removed field.
		 * 
		 * @param fieldName
		 * 			fully qualified field name
		 */		
		public void addRemovedField(final String fieldName)
		{
			this.checkInput(fieldName);
			this.removedFields.add(fieldName);
		}
		
		/**
		 * Adds the fully qualified name of the recently changed field.
		 * 
		 * @param fieldName
		 * 			fully qualified field name
		 */
		public void addChangedField(final String fieldName)
		{
			this.checkInput(fieldName);
			this.changedFields.add(fieldName);
		}
		
		/**
		 * Adds the fully qualified name of the recently added method.
		 * 
		 * @param methodName
		 * 			fully qualified method name
		 */
		public void addAddedMethod(final String methodName)
		{
			this.checkInput(methodName);
			this.addedMethods.add(methodName);
		}

		/**
		 * Adds the fully qualified name of the recently removed method.
		 * 
		 * @param methodName
		 * 			fully qualified method name
		 */		
		public void addRemovedMethod(final String methodName)
		{
			this.checkInput(methodName);
			this.removedMethods.add(methodName);
		}
		
		/**
		 * Adds the fully qualified name of the recently changed method.
		 * 
		 * @param methodName
		 * 			fully qualified method name
		 */
		public void addChangedMethod(final String methodName)
		{
			this.checkInput(methodName);
			this.changedMethods.add(methodName);
		}
		
		/**
		 * Checks list of inputs utilizing {@link ClassChangedEvent#checkInput(String)}.
		 * 
		 * @param inputs
		 * 			list of inputs to be checked
		 */
		private void checkInputList(final List<String> inputs)
		{
			for(final String input : inputs)
			{
				this.checkInput(input);
			}
		}

		/**
		 * Adds a list of fully qualified names of the recently added fields.
		 * 
		 * @param fieldNames
		 * 			fully qualified list of qualified field names
		 */
		public void addAllAddedFields(final List<String> fieldNames)
		{
			this.checkInputList(fieldNames);
			this.addedFields.addAll(fieldNames);
		}

		/**
		 * Adds a list of fully qualified names of the recently removed fields.
		 * 
		 * @param fieldNames
		 * 			fully qualified list of qualified field names
		 */		
		public void addAllRemovedFields(final List<String> fieldNames)
		{
			this.checkInputList(fieldNames);
			this.removedFields.addAll(fieldNames);
		}
		
		/**
		 * Adds a list of fully qualified names of the recently changed fields.
		 * 
		 * @param fieldNames
		 * 			fully qualified list of qualified field names
		 */		
		public void addAllChangedFields(final List<String> fieldNames)
		{
			this.checkInputList(fieldNames);
			this.changedFields.addAll(fieldNames);
		}
		
		/**
		 * Adds a list of fully qualified names of the recently added methods.
		 * 
		 * @param methodNames
		 * 			fully qualified list of qualified method names
		 */
		public void addAllAddedMethods(final List<String> methodNames)
		{
			this.checkInputList(methodNames);
			this.addedMethods.addAll(methodNames);
		}

		/**
		 * Adds a list of fully qualified names of the recently removed methods.
		 * 
		 * @param methodNames
		 * 			fully qualified list of qualified method names
		 */		
		public void addAllRemovedMethods(final List<String> methodNames)
		{
			this.checkInputList(methodNames);
			this.removedMethods.addAll(methodNames);
		}
		
		/**
		 * Adds a list of fully qualified names of the recently changed methods.
		 * 
		 * @param methodNames
		 * 			fully qualified list of qualified method names
		 */		
		public void addAllChangedMethods(final List<String> methodNames)
		{
			this.checkInputList(methodNames);
			this.changedMethods.addAll(methodNames);
		}
		
		/**
		 * Returns list of fully qualified field names which has been added.
		 * 
		 * @return 
		 * 		list of fully qualified field names
		 */
		public List<String> getAddedFields() 
		{
			return new ArrayList<String>(this.addedFields);
		}

		/**
		 * Returns list of fully qualified field names which has been removed.
		 * 
		 * @return 
		 * 		list of fully qualified field names
		 */		
		public List<String> getRemovedFields() 
		{
			return new ArrayList<String>(this.removedFields);
		}

		/**
		 * Returns list of fully qualified field names which has been changed.
		 * 
		 * @return 
		 * 		list of fully qualified field names
		 */
		public List<String> getChangedFields() 
		{
			return new ArrayList<String>(this.changedFields);
		}

		/**
		 * Returns list of fully qualified method names which has been added.
		 * 
		 * @return 
		 * 		list of fully qualified method names
		 */
		public List<String> getAddedMethods() 
		{
			return new ArrayList<String>(this.addedMethods);
		}

		/**
		 * Returns list of fully qualified method names which has been removed.
		 * 
		 * @return 
		 * 		list of fully qualified method names
		 */		
		public List<String> getRemovedMethods() 
		{
			return new ArrayList<String>(this.removedMethods);
		}

		/**
		 * Returns list of fully qualified method names which has been changed.
		 * 
		 * @return 
		 * 		list of fully qualified method names
		 */		
		public List<String> getChangedMethods() 
		{
			return new ArrayList<String>(this.changedMethods);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() 
		{
			return "ClassChangedEvent [addedFields=" + addedFields
					+ ", removedFields=" + removedFields + ", changedFields="
					+ changedFields + ", addedMethods=" + addedMethods
					+ ", removedMethods=" + removedMethods
					+ ", changedMethods=" + changedMethods
					+ ", getClaszzName()=" + getClaszzName()
					+ ", getFilePath()=" + getFilePath() + "]";
		}
	}
}
