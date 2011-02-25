package org.se2010.emine.ui.views.markers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.se2010.emine.artifacts.ProblemArtifact;
import org.se2010.emine.artifacts.ProblemArtifactTypeList;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.IEMineEvent;
import org.se2010.emine.events.IEMineEventListener;
import org.se2010.emine.events.reposuite.RepoSuiteEvent;

public class ProblemViewContentProvider implements ITreeContentProvider,IEMineEventListener {
	
	  private RepoSuiteEvent event;
	  	private List< ProblemArtifactTypeList> input = null;

	  
		private static ArrayList<ProblemArtifactTypeList> display = new ArrayList<ProblemArtifactTypeList>();
		  private StructuredViewer viewer;
		  
			private static Object[] EMPTY_ARRAY = new Object[0];

		  
		  
		  public ProblemViewContentProvider() {
			  EMineEventBus.getInstance().registerEventListener(RepoSuiteEvent.class, this);	

		  }


	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	  public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		  
		  if (viewer == null){
			  
			  this.viewer = (StructuredViewer) v;	
			  
		  if (input == null && newInput !=null){
			  
			  input = (List) newInput;
		  }
		  
		  if (newInput == null && input != null) {
		      input = null;
		    }
		  
		  }  
	  }

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof List) 
			return ((List) parentElement).toArray();
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof ProblemArtifact)
			return ((ProblemArtifact) element).getList();
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof List)
			return (!((List) element).isEmpty());
		return false;
	}
	
	  /** 
	   * Obtains content from the input in the form of an array of <code>IMarker</code>
	   * elements.
	   * 
	   * @return Object[] - Array of <code>IMarker</code> objects from the workspace 
	   * 
	   * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	   */
//	  public Object[] getElements(Object parent) {
//	    
//		  if (event !=null)
//	 
//		 return  display.toArray();
//		  
//		  else 
//			  
//			 return EMPTY_ARRAY;
//	  }

	  

	public Object[] getElements(Object inputElement) {
		
		return getChildren(display);
	}
	@Override
	public void onEvent(IEMineEvent event) {

		// TODO Auto-generated method stub	 input = ((RepoSuiteEvent)event).getArtifact();	
		 ArrayList<ProblemArtifactTypeList> eventArtifacts = ((RepoSuiteEvent)event).getArtifactTypeList();
		 //Control ctrl = viewer.getControl();
		 this.event = ((RepoSuiteEvent)event);
		 for (int i = 0 ; i < eventArtifacts.size() ; i++){
			 
			ProblemArtifactTypeList p = eventArtifacts.get(i);
			 display.add(p);
		 }
		 
		 
		 Control ctrl = viewer.getControl();
		    if (ctrl != null && !ctrl.isDisposed()) {     
		        ctrl.getDisplay().asyncExec(new Runnable() {
		          public void run() {
		        	  viewer.refresh();
		            //  viewer.refresh();  
		          }
		        }); 
		    }	
		
	}
}
