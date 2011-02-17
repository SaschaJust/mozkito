package org.se2010.emine.events;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ui.*;
import org.eclipse.ui.part.FileEditorInput;

public final class EclipseEventHandler implements IPartListener, IBufferChangedListener, IElementChangedListener
{
	private ICompilationUnit currentCU;
	
	private static EclipseEventHandler instance;
	
	


	private EclipseEventHandler() {}
	
	synchronized
	public static void init()
	{
		// ensure that the initialization is not done multiple times
		if(EclipseEventHandler.instance == null)
		{
			EclipseEventHandler.instance = new EclipseEventHandler();
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(EclipseEventHandler.instance);
			JavaCore.addElementChangedListener(EclipseEventHandler.instance);
		}
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
	
	
	private StringBuilder getPackageNameFromCU(final ICompilationUnit cu)
	{
		final StringBuilder builder  = new StringBuilder();
		final String[]      segments =  cu.getPath().segments();
		
		for(int i = 2; i < segments.length - 1; i++)
		{
			builder.append(segments[i]).append('.');
		}
		
		return builder;
	}
	
	
	private String extractClassNameFromCU(final ICompilationUnit cu)
	{
		final StringBuilder builder  = this.getPackageNameFromCU(cu);
		final String        elemName = cu.getElementName();
		
		builder.append(elemName.substring(0, elemName.lastIndexOf('.')));
		
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
				final ICompilationUnit cu      = (ICompilationUnit) delta.getElement().getAncestor(IJavaElement.COMPILATION_UNIT);
				final StringBuilder    pkgName = this.getPackageNameFromCU(cu);
				pkgName.append(delta.getElement().getElementName());
				
				interestingElements.add(pkgName.toString());
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
	
	
	//TODO minimize complexity
	private void handleChangedJavaModel(final IJavaElementDelta delta)
	{
		IJavaElement elem = delta.getElement();
		if(elem instanceof IJavaModel)
		{
			for(IJavaElementDelta tmpDelta : delta.getAffectedChildren())
			{
				elem = tmpDelta.getElement();
				if(elem instanceof IJavaProject)
				{
					for(IJavaElementDelta tmpDelta2 : tmpDelta.getAffectedChildren())
					{
						elem = tmpDelta2.getElement();
						if(elem instanceof IPackageFragmentRoot)
						{
							String pkgFromName = null;
							String pkgToName   = null;
							final ArrayList<ICompilationUnit> cuList = new ArrayList<ICompilationUnit>();
							
							for(IJavaElementDelta tmpDelta3 : tmpDelta2.getAffectedChildren())
							{
								
								for(IJavaElementDelta tmpDelta4 : tmpDelta3.getAffectedChildren())
								{
									// handle removal of package name segment which is logically the same as class renaming
									final IJavaElement movedTo = tmpDelta4.getMovedToElement();
									if(movedTo != null)
									{
										final ICompilationUnit cu        = (ICompilationUnit) movedTo;
										final String           clazzName = extractClassNameFromCU(cu); 
										
										final IEMineEvent evt = new ModificationEvent.ClassAddedEvent(clazzName, cu.getPath().toString());
										EMineEventBus.getInstance().fireEvent(evt);
										
										final ICompilationUnit cuOld        = (ICompilationUnit) tmpDelta4.getElement();
										final String           clazzNameOld = extractClassNameFromCU(cuOld); 
										
										final IEMineEvent evt2 = new ModificationEvent.ClassRemovedEvent(clazzNameOld, cuOld.getPath().toString());
										EMineEventBus.getInstance().fireEvent(evt2);
									}
								}
								
								final IJavaElement movedToElem   = tmpDelta3.getMovedToElement();
								final IJavaElement movedFromElem = tmpDelta3.getMovedFromElement();
								
								// handle removed package segment
								
								if(movedFromElem instanceof IPackageFragment)
								{
									final IPackageFragment pkgFrom = (IPackageFragment) movedFromElem;
									pkgFromName = pkgFrom.getElementName();
								}
								else if(movedToElem instanceof IPackageFragment)
								{
									final IPackageFragment pkgTo = (IPackageFragment) movedToElem;
									pkgToName = pkgTo.getElementName();
									
									try 
									{
										for(final ICompilationUnit cu : pkgTo.getCompilationUnits())
										{
											cuList.add(cu);
										}
									} 
									catch (final JavaModelException e) 
									{
										throw new RuntimeException(e);
									}
								}
							}
							
							if(pkgToName != null && pkgFromName != null)
							{
								for(final ICompilationUnit cu : cuList)
								{
									final String cuName    = cu.getElementName();
									final String tmpCuName = cuName.substring(0, cuName.lastIndexOf('.') + 1);
									
									final String removedClass = pkgFromName + tmpCuName;
									final String addedClass   = pkgToName   + tmpCuName;
									
									final IEMineEvent evt = new ModificationEvent.ClassAddedEvent(addedClass, cu.getPath().toString());
									EMineEventBus.getInstance().fireEvent(evt);
									
									final IPath         path    = cu.getPath();
									final StringBuilder oldPath = new StringBuilder('/');
									
									oldPath.append(path.segment(0)).append('/')
										   .append(path.segment(1)).append('/');
									
									final String[] tokens = pkgFromName.split("\\.");
									
									for(final String token : tokens)
									{
										oldPath.append(token).append('/');
									}
									
									oldPath.append(removedClass).append(".java");
									
									final IEMineEvent evt2 = new ModificationEvent.ClassRemovedEvent(removedClass, oldPath.toString());
									EMineEventBus.getInstance().fireEvent(evt2);
								}
							}
						}
					}
				}
			}
		}		
	}
	
	
	private void handleChangedCUs(final ArrayList<IJavaElementDelta> deltas )
	{
		for(final IJavaElementDelta delta : deltas)
		{	
			final ICompilationUnit cu        = (ICompilationUnit) delta.getElement();
			final String           clazzName = extractClassNameFromCU(cu);
			final int              deltaKind = delta.getKind();
			
			if(deltaKind == IJavaElementDelta.ADDED)
			{
				final IEMineEvent evt = new ModificationEvent.ClassAddedEvent(clazzName, cu.getPath().toString());
				EMineEventBus.getInstance().fireEvent(evt);
			}
			else if(deltaKind == IJavaElementDelta.REMOVED)
			{
				final IEMineEvent evt       = new ModificationEvent.ClassRemovedEvent(clazzName, cu.getPath().toString());
				EMineEventBus.getInstance().fireEvent(evt);
			}
			else if(deltaKind == IJavaElementDelta.CHANGED)
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
				
				final ModificationEvent.ClassChangedEvent evt = new ModificationEvent.ClassChangedEvent(clazzName, cu.getPath().toString());
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
	
	
	@Override
	public void elementChanged(final ElementChangedEvent event)
	{
  		final ArrayList<IJavaElementDelta> deltas = getAffectedCUDeltas(event.getDelta());
		
		if(event.getDelta().getElement() instanceof IJavaModel)
		{
			this.handleChangedJavaModel(event.getDelta());
		}
		else 
		{
			if( (event.getDelta().getFlags() & IJavaElementDelta.F_CHILDREN)     != 0 &&
				(event.getDelta().getFlags() & IJavaElementDelta.F_FINE_GRAINED) != 0 &&
				(event.getDelta().getFlags() & IJavaElementDelta.F_AST_AFFECTED) != 0)
				{
					this.handleChangedCUs(deltas);				
				}
		}
	}
	
	
	@Override
	public void bufferChanged(final BufferChangedEvent event) 
	{
		try 
		{
			final IJavaElement affectedElem = this.currentCU.getElementAt(event.getOffset());
			
			if((affectedElem instanceof IField)  || (affectedElem instanceof IMethod))
			{
				final String clazzName = this.extractClassNameFromCU(this.currentCU);
				final String elemName  = affectedElem.getElementName();
				final ModificationEvent.ClassChangedEvent evt = new ModificationEvent.ClassChangedEvent(clazzName, this.currentCU.getPath().toString());
				
				final StringBuilder pkgStrBuilder = this.getPackageNameFromCU(this.currentCU);
				pkgStrBuilder.append(elemName);
				
				if(affectedElem instanceof IField)
				{
					evt.addChangedField(pkgStrBuilder.toString());
				}
				else if(affectedElem instanceof IMethod)
				{
					evt.addChangedMethod(pkgStrBuilder.toString());
				}
				
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
			final IEMineEvent       event   = new EditorEvent.EditorOpenedEvent(clazzes, this.currentCU.getPath().toString());
			
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
			final IEMineEvent       event   = new EditorEvent.EditorClosedEvent(clazzes, this.currentCU.getPath().toString());
			
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
			final IEMineEvent       event   = new EditorEvent.EditorActivatedEvent(clazzes, this.currentCU.getPath().toString());
			
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
			final IEMineEvent       event   = new EditorEvent.EditorDeactivatedEvent(clazzes, this.currentCU.getPath().toString());
			
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