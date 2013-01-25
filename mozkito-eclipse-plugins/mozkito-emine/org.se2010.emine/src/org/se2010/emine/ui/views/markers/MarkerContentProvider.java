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
import java.util.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.se2010.emine.artifacts.IArtifact;
import org.se2010.emine.artifacts.ProblemArtifact;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.IEMineEvent;
import org.se2010.emine.events.IEMineEventListener;
import org.se2010.emine.events.reposuite.RepoSuiteEvent;

/**
 * This content provider processes the input (<code>IWorkspace</code>) to find
 * the content required in the viewer. This is defined as recent edit markers
 * whose marker id is defined in the <code>MarkerView.MARKER_ID</code> field.
 * <p>
 * A <code>IResourceChangeListener</code> is implemented by this content
 * provider so that it can react to marker changes.
 */

public final class MarkerContentProvider implements IStructuredContentProvider, IEMineEventListener 
{
	private StructuredViewer 		   viewer;
	private RepoSuiteEvent   		   event;
	private IArtifact                  input;
	private final ArrayList<IArtifact> display;

	private static Object[] EMPTY_ARRAY = new Object[0];

	
	
	/**
	 * The constructor.
	 */
	public MarkerContentProvider() 
	{
		EMineEventBus.getInstance().registerEventListener(RepoSuiteEvent.class, this);
		this.display = new ArrayList<IArtifact>();
	}


	/**
	 * Saves input reference and adds change listener first time around. When
	 * the newInput is null, change listener is removed and input released.
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) 
	{

		if (viewer == null) {

			this.viewer = (StructuredViewer) v;

			if (input == null && newInput != null) {

				input = (ProblemArtifact) newInput;
			}

			if (newInput == null && input != null) {
				input = null;
			}

		}

		// JDG2E: 66d - View/ContentProvider listens for model changes
		/*
		 * if (viewer == null) this.viewer = (StructuredViewer) v;
		 * 
		 * if (input == null && newInput != null) { input = (IWorkspace)
		 * newInput; input.addResourceChangeListener(this,
		 * IResourceChangeEvent.POST_CHANGE); } if (newInput == null && input !=
		 * null) { input.removeResourceChangeListener(this); input = null; }
		 */

	}

	/**
	 * Remove resource change listener
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() 
	{
		// Make sure the listener has been removed.
		if (input != null) {
			// input.removeResourceChangeListener(this);
			input = null;
		}
	}

	/**
	 * Obtains content from the input in the form of an array of
	 * <code>IMarker</code> elements.
	 * 
	 * @return Object[] - Array of <code>IMarker</code> objects from the
	 *         workspace
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(final Object parent) 
	{
		if (event != null)
			return display.toArray();
		else
			return EMPTY_ARRAY;
	}

	@Override
	public void onEvent(final IEMineEvent event) 
	{
		final List<IArtifact> eventArtifacts = ((RepoSuiteEvent) event).getArtifacts();

		this.event = ((RepoSuiteEvent) event);
		
		for(final IArtifact artifact : eventArtifacts)
		{
			if(artifact instanceof ProblemArtifact)
			{
				this.display.add(artifact);
			}
		}
		
		final Control ctrl = viewer.getControl();
		if (ctrl != null && !ctrl.isDisposed()) 
		{
			ctrl.getDisplay().asyncExec(new Runnable() 
			{
				public void run() 
				{
					viewer.refresh();
				}
			});
		}
	}
}
