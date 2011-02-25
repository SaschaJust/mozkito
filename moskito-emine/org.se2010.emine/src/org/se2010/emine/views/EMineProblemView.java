package org.se2010.emine.views;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.eclipse.ui.internal.registry.ViewRegistry;
import org.eclipse.ui.part.ViewPart;
import org.se2010.emine.artifacts.ProblemArtifact;
import org.se2010.emine.artifacts.ProblemArtifactTypeList;
import org.se2010.emine.ui.views.markers.ProblemViewComparator;
import org.se2010.emine.ui.views.markers.ProblemViewContentProvider;
import org.se2010.emine.ui.views.markers.ProblemViewFilter;
import org.se2010.emine.ui.views.markers.ProblemViewLabelProvider;
import org.se2010.emine.ui.views.markers.ProblemViewTableLabelProvider;


public class EMineProblemView extends ViewPart implements
		ISelectionChangedListener {

	// TestLists
	HashMap<String, String> m1 = new HashMap<String, String>();
	HashMap<String, String> m2 = new HashMap<String, String>();

	public static final String ID = "eMineProblemsView2";

	// The list of artifacts to be displayed
	// TODO:find a way, to create artifactsByType from this list;
	public List<ProblemArtifact> artifactList = new ArrayList<ProblemArtifact>();

	// Input for the TreeView: A list of artifacts to be displayed, ordered by
	// Type
	public List<ProblemArtifactTypeList> artifactsByType = new ArrayList<ProblemArtifactTypeList>();

	// The viewer for the treetable
	private TreeViewer viewer;
	
	  private Action doubleClickAction;


	// The comparator that enables sorting
	private ProblemViewComparator comparator = new ProblemViewComparator();

	// The filter to search for entries
	private ProblemViewFilter filter = new ProblemViewFilter();
	
	// Create Columns
	public List<TreeColumn> columnList = new ArrayList();

	public List<TreeColumn> getColumnList() {
		return columnList;
	}

	@Override
	public void createPartControl(Composite parent) {
		// TEST
	/*	m1.put("Type", "Bug");
		m1.put("Title", "myID");
		m1.put("Please Note", "highly dangerous");

		m2.put("Type", "Mail");
		m2.put("Title", "notmyID");
		m2.put("Please Note", "not dangerous");

		ProblemArtifact a1 = new ProblemArtifact("Important Bug", m1, "oho",
				null);
		ProblemArtifact a2 = new ProblemArtifact("Important Mail", m2, "ooo",
				null);
		ProblemArtifact a3 = new ProblemArtifact("Important Rice", m1, "oho",
				null);
		artifactList.add(a1);
		artifactList.add(a2);
		artifactList.add(a3);

		List<ProblemArtifact> l1 = new ArrayList<ProblemArtifact>();
		l1.add(a1);
		l1.add(a3);

		List<ProblemArtifact> l2 = new ArrayList<ProblemArtifact>();
		l2.add(a2);

		ProblemArtifactTypeList lt1 = new ProblemArtifactTypeList("Bug", l1);
		ProblemArtifactTypeList lt2 = new ProblemArtifactTypeList("Mail", l2);
		a1.setTypeList(lt1);
		a2.setTypeList(lt2);
		a3.setTypeList(lt1);
		artifactsByType.add(lt1);
		artifactsByType.add(lt2);*/

		// create a filtered Tree in order to enable ...wow...filtering
		FilteredTree ftree = new FilteredTree(parent, SWT.MULTI | SWT.FILL
				| SWT.H_SCROLL | SWT.V_SCROLL, filter, true);

		// Create tree viewer
		viewer = ftree.getViewer();

		// Create artifact tree
		Tree artifactTree = viewer.getTree();
		artifactTree.setHeaderVisible(true);
		artifactTree.setLinesVisible(true);

		// create a layout for the viewer
		 GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		 ftree.setLayoutData(layoutData);

		// create columns
		createColumns(artifactTree);
		
		//TEST 2
		/*a1.setColumnList(columnList);
		a2.setColumnList(columnList);
		a3.setColumnList(columnList);*/
		
		
		
		// create map to get the relation type -> list of ProblemArtifacts with
		// this type

		viewer.setContentProvider(new ProblemViewContentProvider());
		viewer.setLabelProvider(new ProblemViewTableLabelProvider());

		// TODO: get the input for the tree through the EventBus
		viewer.setInput(artifactsByType);
		viewer.expandAll();

		// Share Viewer Selection with other workbench parts
		getViewSite().setSelectionProvider(viewer);

		// Establish listener for viewer selection to keep action state correct
		viewer.addSelectionChangedListener(this);

		// Add sorting
		viewer.setComparator(comparator);
		
		  getViewSite().setSelectionProvider(viewer);

		    makeActions();

		    // Create actions and connect them to the UI
		    hookContextMenu();
		    hookDoubleClickAction();
		    contributeToActionBars();

		// Add filtering
		/*
		 * filter = new ProblemViewFilter(); ProblemViewFilter[] filterArray =
		 * new ProblemViewFilter[] { filter }; viewer.setFilters(filterArray);
		 */

	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();

	}

	/**
	 * This function creates the columns for the TreeViewer. Thereby, the first
	 * two columns are fixed, the rest of the columns is created dynamically
	 * based on the content of the first artifact's content map
	 */
	private void createColumns(Tree tree) {
//		ProblemViewTableLabelProvider labelProvider = new ProblemViewTableLabelProvider();
		// First two columns are fixed
		TreeColumn tc0 = new TreeColumn(tree, SWT.LEFT, 0);
		tc0.setAlignment(SWT.LEFT);
		tc0.setText("Title");
		tc0.setWidth(200);
		tc0.setResizable(true);
		tc0.addSelectionListener(getSelectionAdapter(tc0, 0));
		columnList.add(tc0);

		TreeColumn tc1 = new TreeColumn(tree, SWT.RIGHT, 1);
		tc1.setAlignment(SWT.LEFT);
		tc1.setText("Type");
		tc1.setWidth(200);
		tc1.setResizable(true);
		tc1.addSelectionListener(getSelectionAdapter(tc1, 1));
		columnList.add(tc1);

		// Rest of the columns depends on the keys of the properties map of the
		// first Problem Artifact
		if (!artifactList.isEmpty()) {
			final ProblemArtifact artifact = artifactList.get(0);
			Iterator<String> it = artifact.getMap().keySet().iterator();
			int i = 2;

			while (it.hasNext()) {
				String s = it.next();
				if (!(s.equalsIgnoreCase("Type") || s.equalsIgnoreCase("Title"))) {
					TreeColumn tc = new TreeColumn(tree, SWT.RIGHT, i);
					tc.setAlignment(SWT.LEFT);
					tc.setText(s);
					tc.setWidth(200);
					tc.setResizable(true);
					tc.addSelectionListener(getSelectionAdapter(tc, i));
					columnList.add(tc);

					i++;
				}
			}
		}
	}

	/** This function enables sorting the table by clicking on a column */
	private SelectionAdapter getSelectionAdapter(final TreeColumn tc0,
			final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				int dir = viewer.getTree().getSortDirection();
				if (viewer.getTree().getSortColumn() == tc0) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {

					dir = SWT.DOWN;
				}
				viewer.getTree().setSortDirection(dir);
				viewer.getTree().setSortColumn(tc0);
				viewer.refresh();
			}
		};

		return selectionAdapter;
	}

	 
	
	private void hookContextMenu() {
	    MenuManager menuMgr = new MenuManager("#PopupMenu");
	    menuMgr.setRemoveAllWhenShown(true);
	    menuMgr.addMenuListener(new IMenuListener() {

	      public void menuAboutToShow(IMenuManager manager) {
	        EMineProblemView.this.fillContextMenu(manager);
	      }
	    });

	    Menu menu = menuMgr.createContextMenu(viewer.getControl());
	    viewer.getControl().setMenu(menu);

	    // Register context menu for viewer/part with workbench
	    // This allows for the possibility of external contributions
	    getSite().registerContextMenu(menuMgr, viewer);

	  }

	  private void fillContextMenu(IMenuManager manager) {
	    manager.add(doubleClickAction);
	  //  manager.add(deleteMarkerAction);
	    // Other plug-ins can contribute there actions here
	    manager.add(new Separator("additions"));

	  }
	  
	  private void contributeToActionBars() {
	    IActionBars bars = getViewSite().getActionBars();
	    
	  }
	  
	  private void makeActions() {

		    // Double-Click to open editor for resource identified by marker
		    doubleClickAction = new Action() {

		      public void run() {
		        ISelection selection = viewer.getSelection();
		        openEditor(selection);

		      }
		    };
		    doubleClickAction.setText("Artifact Details");
		    doubleClickAction.setToolTipText("Open file identified by marker");
		    doubleClickAction.setImageDescriptor(PlatformUI.getWorkbench()
		        .getSharedImages().getImageDescriptor(
		            SharedImages.IMG_OPEN_MARKER));
		    doubleClickAction.setEnabled(false);

		   
		    
		  }
	  
	  
	  void openEditor(ISelection selection) {

		  

		  }
	  
	  /**
	   * Opens editor for the selected marker
	   */
	  private void hookDoubleClickAction() {
	    viewer.addDoubleClickListener(new IDoubleClickListener() {

	      public void doubleClick(DoubleClickEvent event) {
	        openEditor(event.getSelection());
	      }
	    });
	  }

	  

	  /**
	   * Keeps the action state correct based on the viewer selection.
	   * 
	   * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	   */
	  public void selectionChanged(SelectionChangedEvent event) {
	    if (event.getSelection().isEmpty()) {
	      doubleClickAction.setEnabled(false);
	    } else {
	      doubleClickAction.setEnabled(true);
	    }

	  }


}