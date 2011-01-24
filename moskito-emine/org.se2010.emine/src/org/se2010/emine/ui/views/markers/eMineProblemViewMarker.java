package org.se2010.emine.ui.views.markers;


import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.eclipse.ui.internal.registry.ViewRegistry;
import org.eclipse.ui.part.ViewPart;
import org.se2010.emine.artifacts.Artifact;


 

public class eMineProblemViewMarker extends ViewPart implements ISelectionChangedListener {
	
	private TableViewer viewer;
	private ProblemViewComparator comparator;
	static final String MARKER_ID  ="xyz";
	private List <Artifact> artifactList;
	
	  /**
	   * Columns for the table viewer
	   */
	  static final String[] COLUMN_NAMES = {"Type",
	      "Title", "Message", "State"};
	  /**
	   * Attributes that will be obtained from the marker
	   */
	  static final String[] MARKER_ATTRIBUTES = {"type",
	      "id", "message", "transient"};

	  private Action deleteMarkerAction;
	  private Action doubleClickAction;

	  
	  public void createPartControl(Composite parent) {
		    viewer = multiColumnViewer(parent);
		    viewer.setContentProvider(new MarkerContentProvider());
		    viewer.setLabelProvider(new MarkerLabelProvider());
		   // viewer.setSorter(new NameSorter());
		    viewer.setInput(ResourcesPlugin.getWorkspace());

		    // Share Viewer Selection with other workbench parts
		    getViewSite().setSelectionProvider(viewer);

		    makeActions();

		    // Create actions and connect them to the UI
		    hookContextMenu();
		    hookDoubleClickAction();
		    contributeToActionBars();
		    
		    // Establish listener for viewer selection to keep action state correct
		    viewer.addSelectionChangedListener(this);

		    setupHelp();
		    
		    // Add comparator for sorting
		    comparator = new ProblemViewComparator(new ViewRegistry());
			viewer.setComparator(comparator);

		  }

		  /**
		   * Markers are displayed in the viewer in this view. As markers can have help, and the 
		   * type of marker being displayed does have help defined (in the 
		   * <code>com.ibm.jdg2e.resources.extensions</code> project).
		   * <p>
		   * The marker help API is used to find and display any help defined for the marker being
		   * displayed.
		   * 
		   */
		  void setupHelp() {
		    // Set help on the view itself
		    viewer.getControl().addHelpListener(new HelpListener() {
		      /*
		       *  (non-Javadoc)
		       * @see org.eclipse.swt.events.HelpListener#helpRequested(org.eclipse.swt.events.HelpEvent)
		       */
		      public void helpRequested(HelpEvent e) {
		        String contextId = null;
		        // See if there is a context registered for the current selection
		        IMarker marker = (IMarker) ((IStructuredSelection) viewer
		            .getSelection()).getFirstElement();
		        if (marker != null) {
		          contextId = IDE.getMarkerHelpRegistry().getHelp(marker);
		        }

		        if (contextId != null) {
		     //     PlatformUI.getWorkbench().getHelpSystem().displayHelp(contextId);
		          // FIXED - removed deprecation
		          // WorkbenchHelp.displayHelp(contextId);
		        }
		      }
		    });

		  }

		  /**
		   * Creates a <code>TableViewer</code> that has a table with multiple columns.
		   * 
		   * @param parent
		   * @return - a TableViewer with columns.
		   */
		  TableViewer multiColumnViewer(Composite parent) {
		    Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL
		        | SWT.MULTI | SWT.FULL_SELECTION);

		    TableLayout layout = new TableLayout();
		    table.setLayout(layout);

		    table.setLinesVisible(true);
		    table.setHeaderVisible(true);

		    layout.addColumnData(new ColumnWeightData(5, 40, true));
		    TableColumn tc0 = new TableColumn(table, SWT.NONE);
		    tc0.setText(COLUMN_NAMES[0]);
		    tc0.setAlignment(SWT.LEFT);
		    tc0.setResizable(true);
		    tc0.addSelectionListener(getSelectionAdapter(tc0, 0));

		    layout.addColumnData(new ColumnWeightData(10, true));
		    TableColumn tc1 = new TableColumn(table, SWT.NONE);
		    tc1.setText(COLUMN_NAMES[1]);
		    tc1.setAlignment(SWT.LEFT);
		    tc1.setResizable(true);
		    tc1.addSelectionListener(getSelectionAdapter(tc1, 1));
		    
		    layout.addColumnData(new ColumnWeightData(10, true));
		    TableColumn tc2 = new TableColumn(table, SWT.NONE);
		    tc2.setText(COLUMN_NAMES[2]);
		    tc2.setAlignment(SWT.LEFT);
		    tc2.setResizable(true);
		    tc2.addSelectionListener(getSelectionAdapter(tc2, 2));
		    
		    return new TableViewer(table);

		  }

		  private void hookContextMenu() {
		    MenuManager menuMgr = new MenuManager("#PopupMenu");
		    menuMgr.setRemoveAllWhenShown(true);
		    menuMgr.addMenuListener(new IMenuListener() {

		      public void menuAboutToShow(IMenuManager manager) {
		        eMineProblemViewMarker.this.fillContextMenu(manager);
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
		    manager.add(deleteMarkerAction);
		    // Other plug-ins can contribute there actions here
		    manager.add(new Separator("additions"));

		  }
		  
		  private void contributeToActionBars() {
		    IActionBars bars = getViewSite().getActionBars();
		    fillLocalPullDown(bars.getMenuManager());
		    fillLocalToolBar(bars.getToolBarManager());
		  }

		  private void fillLocalPullDown(IMenuManager manager) {
		    // Method left to keep standard structure - not used
		  }

		  private void fillLocalToolBar(IToolBarManager manager) {
		    // Method left to keep standard structure - not used
		  }

		  private void makeActions() {

		    // Double-Click to open editor for resource identified by marker
		    doubleClickAction = new Action() {

		      public void run() {
		        ISelection selection = viewer.getSelection();
		        openEditor(selection);

		      }
		    };
		    doubleClickAction.setText("Open File");
		    doubleClickAction.setToolTipText("Open file identified by marker");
		    doubleClickAction.setImageDescriptor(PlatformUI.getWorkbench()
		        .getSharedImages().getImageDescriptor(
		            SharedImages.IMG_OPEN_MARKER));
		    doubleClickAction.setEnabled(false);

		    // Delete Marker
		    deleteMarkerAction = new Action() {

		      public void run() {

		        IStructuredSelection ssel = (IStructuredSelection) viewer
		            .getSelection();
		        IMarker marker = (IMarker) ssel.getFirstElement();
		        try {
		          marker.delete();
		        } catch (CoreException e) {
		        }

		      }
		    };
		    deleteMarkerAction.setText("Delete Marker");
		    deleteMarkerAction.setToolTipText("Delete marker from workspace");
		    deleteMarkerAction.setImageDescriptor(PlatformUI.getWorkbench()
		        .getSharedImages().getImageDescriptor(
		            ISharedImages.IMG_TOOL_DELETE));
		    deleteMarkerAction.setEnabled(false);
		    
		    
		  }

		  /**
		   * Opens the file referenced by the marker in the appropriate editor.
		   * <p>
		   * Logic extracted from similar Eclipse actions. Some of the Eclipse actions are a bit more
		   * robust in their implementation. 
		   * 
		   * See org.eclipse.ui.views.markers.internal.ActionOpenMarker for a more detailed example.
		   * 
		   * @param selection - the marker that will be used to open an editor.
		   */
		  void openEditor(ISelection selection) {

		    IMarker marker = (IMarker) ((IStructuredSelection) selection)
		        .getFirstElement();
		    if (marker.getResource() instanceof IFile) {
		      try {
		        IDE.openEditor(this.getSite().getPage(), marker, OpenStrategy
		            .activateOnOpen());
		      } catch (PartInitException e) {
		        MessageDialog.openError(this.getSite().getShell(),
		            "Unable to open file in editor for given marker", e
		                .getMessage());
		      }
		    }

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
		   * Passing the focus request to the viewer's control.
		   * 
		   * @see org.eclipse.ui.IWorkbenchPart#setFocus()
		   */
		  public void setFocus() {
		    viewer.getControl().setFocus();
		  }

		  /**
		   * Keeps the action state correct based on the viewer selection.
		   * 
		   * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
		   */
		  public void selectionChanged(SelectionChangedEvent event) {
		    if (event.getSelection().isEmpty()) {
		      deleteMarkerAction.setEnabled(false);
		      doubleClickAction.setEnabled(false);
		    } else {
		      deleteMarkerAction.setEnabled(true);
		      doubleClickAction.setEnabled(true);
		    }

		  }
		 
		  
		  /** This function enables sorting the table by clicking on a column*/
		  private SelectionAdapter getSelectionAdapter(final TableColumn column,
					final int index) {
				SelectionAdapter selectionAdapter = new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						comparator.setColumn(index);
						int dir = viewer.getTable().getSortDirection();
						if (viewer.getTable().getSortColumn() == column) {
							dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
						} else {

							dir = SWT.DOWN;
						}
						viewer.getTable().setSortDirection(dir);
						viewer.getTable().setSortColumn(column);
						viewer.refresh();
					}
				};
				return selectionAdapter;
			}
		 
	  
}

	