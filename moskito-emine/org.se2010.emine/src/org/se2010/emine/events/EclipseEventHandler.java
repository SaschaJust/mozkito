package org.se2010.emine.events;

import java.util.ArrayList;

import javax.xml.crypto.dsig.keyinfo.PGPData;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.JavaElementDelta;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public final class EclipseEventHandler 
{
	private EclipseEventHandler(){}
	
	
	private static String extractAffectedClassNameFromDelta(IJavaElementDelta delta)
	{
		final org.eclipse.jdt.internal.core.CompilationUnit  cu = (org.eclipse.jdt.internal.core.CompilationUnit) delta.getElement();
		
		final StringBuilder builder = new StringBuilder();
		for(final char[] pkgSegm : cu.getPackageName())
		{
			builder.append(pkgSegm).append(".");
		}
		
		builder.append(cu.getMainTypeName());
		
		return builder.toString();
		
////		final String[]      segments = delta.getElement().getResource().getProjectRelativePath().segments();
////		final StringBuilder builder  = new StringBuilder();
////		
////		for(int i = 1; i < segments.length; i++)
////		{
////			builder.append(segments[i]);
////			
////			if(i != (segments.length - 1))
////			{
////				builder.append(".");
////			}
////			
////			final String tmpClazzName = builder.toString();
////			if(tmpClazzName.length() > 0)
////			{
//////				System.out.println("--coutn-> " + tmpClazzName.length());
//////				System.out.println("--last Index-> " + tmpClazzName.lastIndexOf("."));
////				return tmpClazzName.substring(0, tmpClazzName.lastIndexOf("."));
//////				System.out.println("---> " + qClazzName);
////				
////			}
////			else
////			{
////				System.out.println("WARNING: " + tmpClazzName + " is emtpy");
////			}
//		}
		
//		return null;
	}
	
	
	
	private static IJavaElementDelta getAfftectedClassDelta(IJavaElementDelta delta)
	{
		if(delta == null)
		{
			return null;
		}
		else if(delta.getElement().getElementType() == IJavaElement.COMPILATION_UNIT)
		{
			return delta;
		}
		else
		{
			if(delta.getAffectedChildren().length > 0)
			{
				//TODO: Do we need to consider all affected children? Can this happen, when we are looking only at the first time for searching for the affected class?
				return getAfftectedClassDelta(delta.getAffectedChildren()[0]);
			}
			
			return null;
		}
	}
	
	
	public static void initElementChangedListener()
	{
		JavaCore.addElementChangedListener(new IElementChangedListener() 
		{
			@Override
			public void elementChanged(final ElementChangedEvent event) 
			{
//					final IJavaElementDelta delta = event.getDelta();
					
					final IJavaElementDelta delta = getAfftectedClassDelta(event.getDelta());
					System.out.println("AFFECTED DELTA: " + delta);
					
					if(delta == null)
					{
						return;
					}
					
					
					final int deltaKind = delta.getKind();
					if(deltaKind == IJavaElementDelta.ADDED)
					{
						System.out.println("==ADDED==> " + event.getDelta());
						System.out.println("ADDED class " + extractAffectedClassNameFromDelta(delta));
					
					}
					else if(deltaKind == IJavaElementDelta.REMOVED)
					{
						System.out.println("==REMOVED==> " + event.getDelta());
						System.out.println("REMOVED class " + extractAffectedClassNameFromDelta(delta));
					}
					else if(deltaKind == IJavaElementDelta.CHANGED)
					{
//						System.out.println("==CHANGED==> " + event.getDelta());
//						System.out.println("==AFFECTED==> " + event.getDelta().getAffectedChildren().length);
//						System.out.println("==ADDED==> " + event.getDelta().getAddedChildren().length);
//						System.out.println("==REMOVED== " + event.getDelta().getRemovedChildren().length);
//						
						final int flags = delta.getFlags();
						
						if( ( (flags & IJavaElementDelta.F_CHILDREN)     != 0  &&
							  (flags & IJavaElementDelta.F_FINE_GRAINED) != 0  &&	
							  (flags & IJavaElementDelta.F_AST_AFFECTED) != 0) || 
							  
							( (flags & IJavaElementDelta.F_CONTENT)      != 0 &&
							  (flags & IJavaElementDelta.F_FINE_GRAINED) != 0 &&
							  (flags & IJavaElementDelta.F_AST_AFFECTED) != 0))
						{
							System.out.println("CHANGED " + extractAffectedClassNameFromDelta(delta));
						}
						
						else
						{
//							System.out.println("OUT: " + delta);
						}
						
					}
					else
					{
						System.out.println("WARNING: Unknown delta kind " + deltaKind);
					}
						

							
//							// receives all events when a Java element is changed
//							System.out.println("xxxx      : " + event);
//							System.out.println("xxxx delta: " + event.getDelta());
//							System.out.println("xxxx source: " + event.getSource());
	
					}
		});
	}
	
	
	private static ArrayList<Class> handleEditorLifeCycleEvent(IWorkbenchPart part)
	{
		final ArrayList<Class> clazzesList = new ArrayList<Class>();
		
		if(part instanceof IEditorPart)
		{
			final IEditorPart  ePart = (IEditorPart) part;
			final IEditorInput eIn   = ePart.getEditorInput();
			
			if(eIn instanceof FileEditorInput)
			{
				final IFileEditorInput fIn = (IFileEditorInput) eIn;
				final ICompilationUnit cu  = JavaCore.createCompilationUnitFrom(fIn.getFile());
				
				try 
				{
					for(final IType type : cu.getAllTypes())
					{
						final Class clazz = Class.forName(type.getFullyQualifiedName());
						clazzesList.add(clazz);
					}
				}
				catch (final Exception e) 
				{
					// TODO: introduce new exception type
					new RuntimeException(e);
				}
				
//				try 
//				{
//					System.out.println("______ALL TYPEs____");
//					for(final IType type : cu.getAllTypes())
//					{
//						type.getClassFile().getBytes() Class.forName(arg0)
//						
//						System.out.println("-- class --> " + type.getFullyQualifiedName());
//					
//						for(final IMethod method : type.getMethods())
//						{
//							System.out.println("-- method --> " + method);
//						}
//					}
//				} 
//				catch (final JavaModelException e1) 
//				{
//					e1.printStackTrace();
//				}
			}
		}	
		
		return clazzesList;
	}
	
	
	public static void initEditorLifeCycleEvents()
	{
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(new IPartListener() 
		{
			@Override
			public void partOpened(IWorkbenchPart part) 
			{
				final ArrayList<Class> clazzes = handleEditorLifeCycleEvent(part);
				final IEMineEvent      event   = new EditorEvent.EditorOpenedEvent(clazzes);
				
				EMineEventBus.getInstance().fireEvent(event);
				
//				//----------------------------------
//
//				if(part instanceof IEditorPart)
//				{
//					final IEditorPart  ePart = (IEditorPart) part;
//					
//					
//					
//					if(ePart instanceof  AbstractDecoratedTextEditor)
//					{
//						 AbstractDecoratedTextEditor txtEditor= ( AbstractDecoratedTextEditor) ePart;
//						
//						 txtEditor.get
//		
//					}
//					
//					
//					ePart.getEditorSite().getShell().addMouseTrackListener(new MouseTrackListener() 
//					{
//						@Override
//						public void mouseHover(MouseEvent e) 
//						{
//							System.out.println("=== mouseHover === " + e);
//						}
//						
//						@Override
//						public void mouseExit(MouseEvent e) {
//							System.out.println("=== mouseExit === " + e);
//						}
//						
//						@Override
//						public void mouseEnter(MouseEvent e) {
//							System.out.println("=== mouseEnter === " + e);
//						}
//					});
//					
//					
//				
//				ePart.getEditorSite().getShell().addMouseListener(new MouseListener() 
//				{
//					@Override
//					public void mouseUp(MouseEvent e) 
//					{
//						System.out.println("=== mouseUp === " + e);
//					}
//					
//					@Override
//					public void mouseDown(MouseEvent e) 
//					{
//						System.out.println("=== mouseDown === " + e);
//					}
//					
//					@Override
//					public void mouseDoubleClick(MouseEvent e) 
//					{
//						System.out.println("=== mouseDoubleClick === " + e);
//					}
//				});
//				
//				
//				//----------------------------------
//				
//				
//				ePart.getEditorSite().getShell().addMouseMoveListener(new MouseMoveListener() 
//				{
//					@Override
//					public void mouseMove(MouseEvent e) 
//					{
//						System.out.println("=== mouseMove === " + e);
//					}
//				});
//				
//				}
//				
			}

			
			
			
			
			
			
			@Override
			public void partDeactivated(final IWorkbenchPart part) 
			{
				final ArrayList<Class> clazzes = handleEditorLifeCycleEvent(part);
				final IEMineEvent      event   = new EditorEvent.EditorDeactivatedEvent(clazzes);
				
				EMineEventBus.getInstance().fireEvent(event);
			}
			
			@Override
			public void partClosed(final IWorkbenchPart part) 
			{
				final ArrayList<Class> clazzes = handleEditorLifeCycleEvent(part);
				final IEMineEvent      event   = new EditorEvent.EditorClosedEvent(clazzes);
				
				EMineEventBus.getInstance().fireEvent(event);
			}
			
			@Override
			public void partBroughtToTop(final IWorkbenchPart part) 
			{
//				handleEditorLifeCycleEvent(part);
			}
			
			@Override
			public void partActivated(final IWorkbenchPart part) 
			{
				final ArrayList<Class> clazzes = handleEditorLifeCycleEvent(part);
				final IEMineEvent      event   = new EditorEvent.EditorActivatedEvent(clazzes);
				
				EMineEventBus.getInstance().fireEvent(event);
			}
		});		
	}
}
