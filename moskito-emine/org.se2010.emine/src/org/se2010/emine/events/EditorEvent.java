package org.se2010.emine.events;

import java.util.ArrayList;
import java.util.List;

public final class EditorEvent
{
	private EditorEvent(){ }
	
	private static abstract class AEditorEvent  implements IEMineEvent
	{
		private final List<String> clazzes;
		
		public AEditorEvent(List<String> clazzes)
		{
			this.clazzes = clazzes;
		}
		
		public List<String> getAffectedClazzes()
		{
			return new ArrayList<String>(this.clazzes);
		}

		@Override
		public String toString() 
		{
			return "AEditorEvent [clazzes=" + clazzes + "]";
		}
	}
	
	public static final class EditorOpenedEvent extends AEditorEvent
	{
		public EditorOpenedEvent(List<String> clazzes)
		{
			super(clazzes);
		}

		@Override
		public String toString() 
		{
			return "EditorOpenedEvent [getAffectedClazzes()="
					+ getAffectedClazzes() + "]";
		}
	}
	
	public static final class EditorClosedEvent extends AEditorEvent
	{
		public EditorClosedEvent(List<String> clazzes)
		{
			super(clazzes);
		}

		@Override
		public String toString() 
		{
			return "EditorClosedEvent [getAffectedClazzes()="
					+ getAffectedClazzes() + "]";
		}
	}
	
	public static final class EditorActivatedEvent extends AEditorEvent
	{
		public EditorActivatedEvent(List<String> clazzes)
		{
			super(clazzes);
		}

		@Override
		public String toString() 
		{
			return "EditorActivatedEvent [getAffectedClazzes()="
					+ getAffectedClazzes() + "]";
		}
	}
	
	public static final class EditorDeactivatedEvent extends AEditorEvent
	{
		public EditorDeactivatedEvent(final List<String> clazzes)
		{
			super(clazzes);
		}

		@Override
		public String toString() 
		{
			return "EditorDeactivatedEvent [getAffectedClazzes()="
					+ getAffectedClazzes() + "]";
		}
	}
}