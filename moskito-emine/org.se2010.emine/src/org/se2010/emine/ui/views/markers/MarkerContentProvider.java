 package org.se2010.emine.ui.views.markers;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.se2010.emine.artifacts.ProblemArtifact;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.IEMineEvent;
import org.se2010.emine.events.IEMineEventListener;
import org.se2010.emine.events.reposuite.RepoSuiteEvent;


/**
 * This content provider processes the input (<code>IWorkspace</code>) to find the
 * content required in the viewer.  This is defined as recent edit markers whose 
 * marker id is defined in the <code>MarkerView.MARKER_ID</code> field.
 * <p>
 * A <code>IResourceChangeListener</code> is implemented by this content provider so 
 * that it can react to marker changes.  
 */

class MarkerContentProvider implements IStructuredContentProvider,
    IEMineEventListener {

  private StructuredViewer viewer;
  
  private RepoSuiteEvent event;

  /**
   * The constructor.
   */
  MarkerContentProvider() {
	  EMineEventBus.getInstance().registerEventListener(RepoSuiteEvent.class, this);	

  }

  	private ProblemArtifact input = null;
  
	private static Object[] EMPTY_ARRAY = new Object[0];
	
	private static ArrayList<ProblemArtifact> display = new ArrayList<ProblemArtifact>();


  /**
   * Saves input reference and adds change listener first time around.
   * When the newInput is null, change listener is removed and input released.
   *  
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
   */
  public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	  
	  if (viewer == null){
		  
		  this.viewer = (StructuredViewer) v;
		  
	  if (input == null && newInput !=null){
		  
		  input = (ProblemArtifact) newInput;
	  }
	  
	  if (newInput == null && input != null) {
	      input = null;
	    }
	  
	  }
	  
    // JDG2E: 66d - View/ContentProvider listens for model changes
   /* if (viewer == null)
      this.viewer = (StructuredViewer) v;

    if (input == null && newInput != null) {
      input = (IWorkspace) newInput;
      input.addResourceChangeListener(this,
          IResourceChangeEvent.POST_CHANGE);
    }
    if (newInput == null && input != null) {
      input.removeResourceChangeListener(this);
      input = null;
    }*/
	  
	  
  }

  /**
   * Remove resource change listener
   * 
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  public void dispose() {
    // Make sure the listener has been removed.
   if (input != null) {
      //input.removeResourceChangeListener(this);
     input = null;
    }
  }

  /** 
   * Obtains content from the input in the form of an array of <code>IMarker</code>
   * elements.
   * 
   * @return Object[] - Array of <code>IMarker</code> objects from the workspace 
   * 
   * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
   */
  public Object[] getElements(Object parent) {
    
	  if (event !=null)
 
	 return  display.toArray();
	  
	  else 
		  
		 return EMPTY_ARRAY;
  }

  
@Override
public void onEvent(IEMineEvent event) {
	// TODO Auto-generated method stub	 input = ((RepoSuiteEvent)event).getArtifact();	
	 ArrayList<ProblemArtifact> eventArtifacts = ((RepoSuiteEvent)event).getArtifacts();
	 //Control ctrl = viewer.getControl();
	 this.event = ((RepoSuiteEvent)event);
	 for (int i = 0 ; i < eventArtifacts.size() ; i++){
		 
		ProblemArtifact p = eventArtifacts.get(i);
		 display.add(p);
	 }
	 
	 
	 Control ctrl = viewer.getControl();
	    if (ctrl != null && !ctrl.isDisposed()) {     
	        ctrl.getDisplay().asyncExec(new Runnable() {
	          public void run() {
	              viewer.refresh();  
	          }
	        }); 
	    }	
}



}
