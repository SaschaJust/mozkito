package org.se2010.emine.events;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public final class EditorEvent
{
	private EditorEvent(){ }
	
	private static abstract class AEditorEvent  implements IEMineEvent
	{
		private final List<Class> clazzes;
		
		public AEditorEvent(List<Class> clazzes)
		{
			this.clazzes = clazzes;
		}
		
		public List<Class> getAffectedClazzes()
		{
			return new ArrayList<Class>(clazzes);
		}
	}
	
	public static final class EditorOpenedEvent extends AEditorEvent
	{
		public EditorOpenedEvent(List<Class> clazzes)
		{
			super(clazzes);
		}
	}
	
	public static final class EditorClosedEvent extends AEditorEvent
	{
		public EditorClosedEvent(List<Class> clazzes)
		{
			super(clazzes);
		}
	}
	
	public static final class EditorActivatedEvent extends AEditorEvent
	{
		public EditorActivatedEvent(List<Class> clazzes)
		{
			super(clazzes);
		}
	}
	
	public static final class EditorDeactivatedEvent extends AEditorEvent
	{
		public EditorDeactivatedEvent(List<Class> clazzes)
		{
			super(clazzes);
		}
	}
}
