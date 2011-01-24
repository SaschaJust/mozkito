package org.se2010.emine.ui.views.markers;

import org.eclipse.core.resources.IMarker;
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

/**
 * This content provider processes the input (<code>IWorkspace</code>) to find the
 * content required in the viewer.  This is defined as recent edit markers whose 
 * marker id is defined in the <code>MarkerView.MARKER_ID</code> field.
 * <p>
 * A <code>IResourceChangeListener</code> is implemented by this content provider so 
 * that it can react to marker changes.  
 */

class MarkerContentProvider implements IStructuredContentProvider,
    IResourceChangeListener {

  private StructuredViewer viewer;

  /**
   * The constructor.
   */
  MarkerContentProvider() {
  }

  private IWorkspace input = null;

  /**
   * Saves input reference and adds change listener first time around.
   * When the newInput is null, change listener is removed and input released.
   *  
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
   */
  public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    // JDG2E: 66d - View/ContentProvider listens for model changes
    if (viewer == null)
      this.viewer = (StructuredViewer) v;

    if (input == null && newInput != null) {
      input = (IWorkspace) newInput;
      input.addResourceChangeListener(this,
          IResourceChangeEvent.POST_CHANGE);
    }
    if (newInput == null && input != null) {
      input.removeResourceChangeListener(this);
      input = null;
    }
  }

  /**
   * Remove resource change listener
   * 
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  public void dispose() {
    // Make sure the listener has been removed.
    if (input != null) {
      input.removeResourceChangeListener(this);
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
    IMarker[] markers = null;
    try {
      markers = input.getRoot().findMarkers(eMineProblemViewMarker.MARKER_ID,
          false, IResource.DEPTH_INFINITE);
    } catch (CoreException e) {
    }
    return markers;
  }

  /**
   * React to changes that impact the elements in the associated viewer.
   * <p>
   * As this method is not fired on the UI thread we need to use the SWT <code>Display</code>
   * to get back on the UI thread before telling the viewer to refresh.  An <code>asyncExec</code>
   * will work here as we do not need to visit the delta in the runnable. 
   * <p>
   * If resource delta must be visited inside the runnable then a <code>syncExec</code> must 
   * be used to ensure the delta is not discarded.
   *  
   * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
   */
  public void resourceChanged(IResourceChangeEvent event) {

    // Make sure control exists - no sense telling a disposed widget to react
    Control ctrl = viewer.getControl();
    if (ctrl != null && !ctrl.isDisposed()) {

     
      // If there are any markers of interest in the delta we refresh the viewer
      IMarkerDelta[] mDeltas =  event.findMarkerDeltas(eMineProblemViewMarker.MARKER_ID,false);
      if (mDeltas.length !=0) {
        
        ctrl.getDisplay().asyncExec(new Runnable() {
          public void run() {
            if (null == null) {
              viewer.refresh();
            } else {
            }
          }
        });
        
      }
    }
  }

}
