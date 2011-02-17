package org.se2010.emine.events;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

public final class EditorEvent
{
	private EditorEvent(){ }
	
	private static abstract class AEditorEvent  implements IEMineEvent
	{
		private final List<String> clazzes;
		private final String       filePath;
		
		public AEditorEvent(final List<String> clazzes, final String filePath)
		{
			this.clazzes  = clazzes;
			this.filePath = filePath;
		}
		
		public String getFilePath()
		{
			return this.filePath;
		}
		
		public List<String> getAffectedClazzes()
		{
			return new ArrayList<String>(this.clazzes);
		}

		@Override
		public String toString() 
		{
			return "AEditorEvent [clazzes=" + clazzes + ", filePath="
					+ filePath + "]";
		}
	}
	
	public static final class EditorOpenedEvent extends AEditorEvent
	{
		public EditorOpenedEvent(List<String> clazzes, final String filePath )
		{
			super(clazzes, filePath);
		}

		@Override
		public String toString() 
		{
			return "EditorOpenedEvent [getFilePath()=" + getFilePath()
					+ ", getAffectedClazzes()=" + getAffectedClazzes() + "]";
		}
	}
	
	public static final class EditorClosedEvent extends AEditorEvent
	{
		public EditorClosedEvent(final List<String> clazzes, final String filePath)
		{
			super(clazzes, filePath);
		}

		@Override
		public String toString() 
		{
			return "EditorClosedEvent [getFilePath()=" + getFilePath()
					+ ", getAffectedClazzes()=" + getAffectedClazzes() + "]";
		}
	}
	
	public static final class EditorActivatedEvent extends AEditorEvent
	{
		public EditorActivatedEvent(final List<String> clazzes, final String filePath)
		{
			super(clazzes, filePath);
		}

		@Override
		public String toString() 
		{
			return "EditorActivatedEvent [getFilePath()=" + getFilePath()
					+ ", getAffectedClazzes()=" + getAffectedClazzes() + "]";
		}
	}
	
	public static final class EditorDeactivatedEvent extends AEditorEvent
	{
		public EditorDeactivatedEvent(final List<String> clazzes, final String filePath)
		{
			super(clazzes, filePath);
		}

		@Override
		public String toString() 
		{
			return "EditorDeactivatedEvent [getFilePath()=" + getFilePath()
					+ ", getAffectedClazzes()=" + getAffectedClazzes() + "]";
		}
	}
}