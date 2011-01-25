package org.se2010.emine.events;

import java.util.ArrayList;
import org.eclipse.jdt.core.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.FileEditorInput;

public class EclipseEventHandler implements IPartListener, IBufferChangedListener, IElementChangedListener
{
	private ICompilationUnit currentCU;
	
	private EclipseEventHandler() {}
	
	public static void init()
	{
		final EclipseEventHandler handler = new EclipseEventHandler();
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(handler);
		JavaCore.addElementChangedListener(handler);
	}
	
	
	private ArrayList<IJavaElementDelta> getAffectedCUDeltas(IJavaElementDelta delta)
	{
		final ArrayList<IJavaElementDelta> cus = new ArrayList<IJavaElementDelta>();
		
		if(delta == null)
		{
			return cus;
		}
		else if(delta.getElement().getElementType() == IJavaElement.COMPILATION_UNIT)
		{
			cus.add(delta);
			return cus;
		}
		else
		{
			for(final IJavaElementDelta affChild : delta.getAffectedChildren())
			{
				cus.addAll(getAffectedCUDeltas(affChild));
			}
			
			return cus;
		}
	}
	
	private String extractClassNameFromCU(final ICompilationUnit cu)
	{
		final StringBuilder builder  = new StringBuilder();
		final String[]      segments =  cu.getPath().segments();
		
		for(int i = 2; i < segments.length - 1; i++)
		{
			builder.append(segments[i]).append(".");
		}
		
		final String elemName = cu.getElementName();
		builder.append(elemName.substring(0, elemName.lastIndexOf(".")));
		
		return builder.toString();
	}
	
	
	private void collectInterestingDeltas(final IJavaElementDelta[] deltas,  final int kind, final int targetType, final ArrayList<String> interestingElements)
	{
		for(final IJavaElementDelta delta : deltas)
		{
			final IJavaElement javaElem = delta.getElement();
			final int 		   type		= javaElem.getElementType();
			
			if(type == targetType)
			{
				interestingElements.add(delta.getElement().getElementName());
			}
			else
			{
				if(kind == IJavaElementDelta.ADDED)
				{
					collectInterestingDeltas(delta.getAddedChildren(), kind, targetType, interestingElements);
				}
				else if(kind == IJavaElementDelta.CHANGED)
				{
					collectInterestingDeltas(delta.getChangedChildren(), kind, targetType, interestingElements);
				}
				else if(kind == IJavaElementDelta.REMOVED)
				{
					collectInterestingDeltas(delta.getRemovedChildren(), kind, targetType, interestingElements);
				}
			}
		}		
	}
	
	
	@Override
	public void elementChanged(final ElementChangedEvent event)
	{
		final ArrayList<IJavaElementDelta> deltas = getAffectedCUDeltas(event.getDelta());
		
		for(final IJavaElementDelta delta : deltas)
		{
			final String clazzName = extractClassNameFromCU((ICompilationUnit)delta.getElement());
			final int deltaKind    = delta.getKind();
			
			if(deltaKind == IJavaElementDelta.ADDED)
			{
				
				final IEMineEvent evt = new ModificationEvent.ClassAddedEvent(clazzName);
				EMineEventBus.getInstance().fireEvent(evt);
			}
			else if(deltaKind == IJavaElementDelta.REMOVED)
			{
				final IEMineEvent evt       = new ModificationEvent.ClassRemovedEvent(clazzName);
				EMineEventBus.getInstance().fireEvent(evt);
			}
			else if(deltaKind == IJavaElementDelta.CHANGED)
			{
				final int flags = event.getDelta().getFlags();
				
				if( (flags & IJavaElementDelta.F_CHILDREN)     != 0 &&
					(flags & IJavaElementDelta.F_FINE_GRAINED) != 0	&&
					(flags & IJavaElementDelta.F_AST_AFFECTED) != 0)
				{
					final ArrayList<String> addedFields   = new ArrayList<String>();
					final ArrayList<String> changedFields = new ArrayList<String>();
					final ArrayList<String> removedFields = new ArrayList<String>();
							
					final ArrayList<String> addedMethods   = new ArrayList<String>();
					final ArrayList<String> changedMethods = new ArrayList<String>();
					final ArrayList<String> removedMethods = new ArrayList<String>();
					
					
					collectInterestingDeltas(delta.getAffectedChildren(), IJavaElementDelta.ADDED,   IJavaElement.FIELD, addedFields);
					collectInterestingDeltas(delta.getAffectedChildren(), IJavaElementDelta.CHANGED, IJavaElement.FIELD, changedFields);
					collectInterestingDeltas(delta.getAffectedChildren(), IJavaElementDelta.REMOVED, IJavaElement.FIELD, removedFields);
					
					collectInterestingDeltas(delta.getAffectedChildren(), IJavaElementDelta.ADDED,   IJavaElement.METHOD, addedMethods);
					collectInterestingDeltas(delta.getAffectedChildren(), IJavaElementDelta.CHANGED, IJavaElement.METHOD, changedMethods);
					collectInterestingDeltas(delta.getAffectedChildren(), IJavaElementDelta.REMOVED, IJavaElement.METHOD, removedMethods);
					
					final ModificationEvent.ClassChangedEvent evt = new ModificationEvent.ClassChangedEvent(clazzName);
					evt.addAllAddedFields(addedFields);
					evt.addAllAddedMethods(addedMethods);
					evt.addAllChangedFields(changedFields);
					evt.addAllChangedMethods(changedMethods);
					evt.addAllRemovedFields(removedFields);
					evt.addAllRemovedMethods(removedMethods);
					
					EMineEventBus.getInstance().fireEvent(evt);
				}
			}
		}
	}
	
	
	@Override
	public void bufferChanged(final BufferChangedEvent event) 
	{
		try 
		{
			final IJavaElement affectedElem = this.currentCU.getElementAt(event.getOffset());
			
			if((affectedElem instanceof IField))
			{
				final String clazzName = this.extractClassNameFromCU(this.currentCU);
				final String elemName  = affectedElem.getElementName();
				final ModificationEvent.ClassChangedEvent evt = new ModificationEvent.ClassChangedEvent(clazzName);
				evt.addChangedField(elemName);
				EMineEventBus.getInstance().fireEvent(evt);
			}
			else if((affectedElem instanceof IMethod))
			{
				final String clazzName = this.extractClassNameFromCU(this.currentCU);
				final String elemName  = affectedElem.getElementName();
				final ModificationEvent.ClassChangedEvent evt = new ModificationEvent.ClassChangedEvent(clazzName);
				evt.addChangedMethod(elemName);
				EMineEventBus.getInstance().fireEvent(evt);
			}
		} 
		catch (final JavaModelException e) 
		{
			throw new RuntimeException(e);
		}
	}
	
	
	private ArrayList<String> extractCorrespondingClasses()
	{
		final ArrayList<String> clazzesList = new ArrayList<String>();
		
		try 
		{
			for(final IType type : this.currentCU.getAllTypes())
			{
				clazzesList.add(type.getFullyQualifiedName());
			}
		}
		catch (final Exception e) 
		{
			// TODO: introduce new exception type
			throw new RuntimeException(e);
		}
		
		return clazzesList;
	}
	
	
	private void extractCurrentCompilationUnit(final IWorkbenchPart part)
	{
		this.currentCU = null;
		
		if(part instanceof IEditorPart)
		{
			final IEditorPart  ePart = (IEditorPart) part;
			final IEditorInput eIn   = ePart.getEditorInput();
			
			if(eIn instanceof FileEditorInput)
			{
				final IFileEditorInput fIn = (IFileEditorInput) eIn;
				this.currentCU  = JavaCore.createCompilationUnitFrom(fIn.getFile());
			}
		}	
	}
	
	

	@Override
	public void partOpened(IWorkbenchPart part) 
	{
		this.extractCurrentCompilationUnit(part);
		
		// do not consider editor for non-existing or non-Java compilation units
		if(this.currentCU != null &&  this.currentCU.exists())
		{
			final ArrayList<String> clazzes = this.extractCorrespondingClasses();
			final IEMineEvent       event   = new EditorEvent.EditorOpenedEvent(clazzes);
			
			EMineEventBus.getInstance().fireEvent(event);
		}
	}			
	
	@Override
	public void partClosed(final IWorkbenchPart part) 
	{
		this.extractCurrentCompilationUnit(part);
		
		// do not consider editor for non-existing or non-Java compilation units
		if(this.currentCU != null &&  this.currentCU.exists())
		{
			final ArrayList<String> clazzes = this.extractCorrespondingClasses();
			final IEMineEvent       event   = new EditorEvent.EditorClosedEvent(clazzes);
			
			EMineEventBus.getInstance().fireEvent(event);
		}
	}
	
	@Override
	public void partBroughtToTop(final IWorkbenchPart part) {	}
	
	@Override
	public void partActivated(final IWorkbenchPart part) 
	{
		this.extractCurrentCompilationUnit(part);
		
		// do not consider editor for non-existing or non-Java compilation units
		if(this.currentCU != null &&  this.currentCU.exists())
		{
			final ArrayList<String> clazzes = this.extractCorrespondingClasses();
			final IEMineEvent       event   = new EditorEvent.EditorActivatedEvent(clazzes);
			
			EMineEventBus.getInstance().fireEvent(event);
			
			try 
			{
				this.currentCU.getBuffer().addBufferChangedListener(this);
			} 
			catch (JavaModelException e) 
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	
	@Override
	public void partDeactivated(final IWorkbenchPart part) 
	{
		this.extractCurrentCompilationUnit(part);
		
		// do not consider editor for non-existing or non-Java compilation units
		if(this.currentCU != null &&  this.currentCU.exists())
		{
			final ArrayList<String> clazzes = this.extractCorrespondingClasses();
			final IEMineEvent       event   = new EditorEvent.EditorDeactivatedEvent(clazzes);
			
			EMineEventBus.getInstance().fireEvent(event);
			
			
			try 
			{
				this.currentCU.getBuffer().removeBufferChangedListener(this);
			} 
			catch (JavaModelException e) 
			{
				throw new RuntimeException(e);
			}
		}
	}
}