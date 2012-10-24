/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package org.se2010.emine.ui.views.markers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.se2010.emine.artifacts.IArtifact;
import org.se2010.emine.artifacts.ProblemArtifact;
import org.se2010.emine.artifacts.ProblemArtifactTypeList;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.IEMineEvent;
import org.se2010.emine.events.IEMineEventListener;
import org.se2010.emine.events.reposuite.RepoSuiteEvent;

public class ProblemViewContentProvider implements ITreeContentProvider,IEMineEventListener
{
	
  	private List<ProblemArtifactTypeList>                  input;
	private final ArrayList<ProblemArtifactTypeList>       display;
	private final HashMap<String, ProblemArtifactTypeList> problemArtifactTypeListMap;
    private StructuredViewer 							   viewer;
		  
		  
	public ProblemViewContentProvider() 
	{
		this.display                    = new ArrayList<ProblemArtifactTypeList>();
		this.problemArtifactTypeListMap = new HashMap<String, ProblemArtifactTypeList>();
		EMineEventBus.getInstance().registerEventListener(RepoSuiteEvent.class, this);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() { }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(Viewer v, Object oldInput, Object newInput) 
	{
		  if (viewer == null)
		  {
			  this.viewer = (StructuredViewer) v;	
			  
			  if (input == null && newInput !=null)
			  {
				  input = (List) newInput;
			  }
			  
			  if (newInput == null && input != null) 
			  {
			      input = null;
			  }
		  }  
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getChildren(Object parentElement) 
	{
		if (parentElement instanceof List) 
			return ((List) parentElement).toArray();
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getParent(Object element) 
	{
		if(element instanceof ProblemArtifact)
		{
			final ProblemArtifact artifact = (ProblemArtifact) element;
			return this.problemArtifactTypeListMap.get(artifact.getGroupName());
		}
		
		return null;
		
//		if (element instanceof ProblemArtifact)
//			return ((ProblemArtifact) element).getTypeList();
//		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasChildren(Object element) 
	{
		if (element instanceof List)
			return (!((List) element).isEmpty());
		
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(final Object inputElement) 
	{
		return getChildren(display);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEvent(final IEMineEvent event) 
	{
		if(event instanceof RepoSuiteEvent)
		{
			final RepoSuiteEvent                         repoSuiteEvent = (RepoSuiteEvent) event;
			final HashMap<String, List<ProblemArtifact>> displayMap     = new HashMap<String, List<ProblemArtifact>>();
			
			for(final IArtifact artifact : repoSuiteEvent.getArtifacts())
			{
				if(artifact instanceof ProblemArtifact)
				{
					final ProblemArtifact problemArtifact = (ProblemArtifact) artifact;
					final String          group           = problemArtifact.getGroupName();
					List<ProblemArtifact> artifactList    = displayMap.get(group);
					
					if(artifactList == null)
					{
					   artifactList = new ArrayList<ProblemArtifact>();
					   displayMap.put(group, artifactList);
					}
					
					artifactList.add(problemArtifact);
				}
			}
			
			for(final Entry<String, List<ProblemArtifact>> entry : displayMap.entrySet())
			{
				final ProblemArtifactTypeList typeList = new ProblemArtifactTypeList(entry.getKey(), entry.getValue());
				display.add(typeList);
				problemArtifactTypeListMap.put(entry.getKey(), typeList);
			}
			
			final Control ctrl = viewer.getControl();
		    if (ctrl != null && !ctrl.isDisposed()) 
		    {     
		        ctrl.getDisplay().asyncExec(new Runnable() 
		        {
		          @Override	
		          public void run() 
		          {
		        	  viewer.refresh();
		          }
		        }); 
		    }	
		}
	}
}
