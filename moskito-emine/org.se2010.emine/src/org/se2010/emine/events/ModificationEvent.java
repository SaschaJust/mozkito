package org.se2010.emine.events;

import java.util.ArrayList;
import java.util.List;

public final class ModificationEvent 
{
	private ModificationEvent() { }
	
	
	private static abstract class ACompilationUnitEvent implements IEMineEvent
	{
		private final String claszzName;
		
		public ACompilationUnitEvent(final String clazzName)
		{
			if(clazzName == null)
			{
				throw new NullPointerException("Class name must not be null!");
			}

			if(clazzName.trim().isEmpty())
			{
				throw new IllegalArgumentException("Class name must not be emtpy!");
			}

			this.claszzName = clazzName;
		}
		
		public String getClaszzName() 
		{
			return claszzName;
		}

		@Override
		public String toString() 
		{
			return "ACompilationUnitEvent [claszzName=" + claszzName + "]";
		}
	}

	public static final class ClassAddedEvent extends ACompilationUnitEvent
	{

		public ClassAddedEvent(final String clazzName) 
		{
			super(clazzName);
		}

		@Override
		public String toString() 
		{
			return "ClassAddedEvent [getClaszzName()=" + getClaszzName() + "]";
		}
	}
	
	
	public static final class ClassRemovedEvent extends ACompilationUnitEvent
	{
		public ClassRemovedEvent(final String clazzName) 
		{
			super(clazzName);
		}

		@Override
		public String toString() 
		{
			return "ClassRemovedEvent [getClaszzName()=" + getClaszzName() + "]";
		}
	}
	
	
	public static final class ClassChangedEvent extends ACompilationUnitEvent
	{
		private final List<String>  addedFields;
		private final List<String>  removedFields;
		private final List<String>  changedFields;
		private final List<String>  addedMethods;
		private final List<String>  removedMethods;
		private final List<String>  changedMethods;

		public ClassChangedEvent(final String clazzName)
		{
			super(clazzName);
			
			this.addedFields    = new ArrayList<String>();
			this.changedFields  = new ArrayList<String>();
			this.removedFields  = new ArrayList<String>();
			this.addedMethods   = new ArrayList<String>();
			this.changedMethods = new ArrayList<String>();
			this.removedMethods = new ArrayList<String>();
		}

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
		
		public void addAddedField(final String fieldName)
		{
			this.checkInput(fieldName);
			this.addedFields.add(fieldName);
		}
		
		public void addRemovedField(final String fieldName)
		{
			this.checkInput(fieldName);
			this.removedFields.add(fieldName);
		}
		
		public void addChangedField(final String fieldName)
		{
			this.checkInput(fieldName);
			this.changedFields.add(fieldName);
		}
		
		public void addAddedMethod(final String methodName)
		{
			this.checkInput(methodName);
			this.addedMethods.add(methodName);
		}
		
		public void addRemovedMethod(final String methodName)
		{
			this.checkInput(methodName);
			this.removedMethods.add(methodName);
		}
		
		public void addChangedMethod(final String methodName)
		{
			this.checkInput(methodName);
			this.changedMethods.add(methodName);
		}
		
		private void checkInputList(final List<String> inputs)
		{
			for(final String input : inputs)
			{
				this.checkInput(input);
			}
		}
		
		public void addAllAddedFields(final List<String> fieldNames)
		{
			this.checkInputList(fieldNames);
			this.addedFields.addAll(fieldNames);
		}
		
		public void addAllRemovedFields(final List<String> fieldNames)
		{
			this.checkInputList(fieldNames);
			this.removedFields.addAll(fieldNames);
		}
		
		public void addAllChangedFields(final List<String> fieldNames)
		{
			this.checkInputList(fieldNames);
			this.changedFields.addAll(fieldNames);
		}
		
		public void addAllAddedMethods(final List<String> methodNames)
		{
			this.checkInputList(methodNames);
			this.addedMethods.addAll(methodNames);
		}
		
		public void addAllRemovedMethods(final List<String> methodNames)
		{
			this.checkInputList(methodNames);
			this.removedMethods.addAll(methodNames);
		}
		
		public void addAllChangedMethods(final List<String> methodNames)
		{
			this.checkInputList(methodNames);
			this.changedMethods.addAll(methodNames);
		}
		
		public List<String> getAddedFields() 
		{
			return new ArrayList<String>(this.addedFields);
		}

		public List<String> getRemovedFields() 
		{
			return new ArrayList<String>(this.removedFields);
		}

		public List<String> getChangedFields() 
		{
			return new ArrayList<String>(this.changedFields);
		}

		public List<String> getAddedMethods() 
		{
			return new ArrayList<String>(this.addedMethods);
		}

		public List<String> getRemovedMethods() 
		{
			return new ArrayList<String>(this.removedMethods);
		}

		public List<String> getChangedMethods() 
		{
			return new ArrayList<String>(this.changedMethods);
		}

		@Override
		public String toString() 
		{
			return "ClassChangedEvent [addedFields=" + addedFields
					+ ", removedFields=" + removedFields + ", changedFields="
					+ changedFields + ", addedMethods=" + addedMethods
					+ ", removedMethods=" + removedMethods
					+ ", changedMethods=" + changedMethods
					+ ", getClaszzName()=" + getClaszzName() + "]";
		}
	}
}