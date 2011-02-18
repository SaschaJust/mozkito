 package org.se2010.emine.ui.views.markers;

import java.util.List;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.eclipse.ui.internal.registry.ViewRegistry;
import org.eclipse.ui.part.ViewPart;
import org.se2010.emine.artifacts.Artifact;
import org.se2010.emine.artifacts.ProblemArtifact;
import org.se2010.emine.listeners.Controller;

public class eMineProblemViewMarker extends ViewPart 
		  {

	private TableViewer viewer;
	//private ProblemViewComparator comparator;
	//private ProblemViewFilter filter;
	private String searchString;

	//static final String MARKER_ID = "xyz";
	private List<Artifact> artifactList;

	/**
	 * Columns for the table viewer
	 */
	static final String[] COLUMN_NAMES = { "Artifact Title", "Artifcat ID", "Message", "Resource" };
	/**
	 * Attributes that will be obtained from the marker
	 */
	static final String[] MARKER_ATTRIBUTES = { "title", "id", "message","resource" };
  

	public void createPartControl(Composite parent) {
		// Create a Layout for the filter field
		GridLayout layout = new GridLayout(2,false);
		parent.setLayout(layout);		
		
		// Add search field and filter tool for filtering functionality
		final Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setMessage("Enter search term");
		searchText.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 10,1));
		//searchText.addKeyListener(new KeyAdapter() {
		//	@Override
		/*	public void keyReleased(final KeyEvent ke) 
			{
				filter.setSearchText(searchText.getText());
				viewer.refresh();
			}
		});*/
		
		
		viewer = multiColumnViewer(parent);
		viewer.setContentProvider(new MarkerContentProvider());
		viewer.setLabelProvider(new MarkerLabelProvider());
		// viewer.setSorter(new NameSorter());
	//viewer.setInput(ResourcesPlugin.getWorkspace());
		
		//ProblemArtifact pArtifact = new ProblemArtifact("hello", null, "world", null, "method");
		
		viewer.setInput(new ProblemArtifact("Tom", 0, "Dick", "Harry"));
		
		//create a layout for the viewer
		GridData layoutData = new GridData (SWT.FILL, SWT.FILL, true, true);
		viewer.getControl().setLayoutData(layoutData);
		
		
		// Share Viewer Selection with other workbench parts
		getViewSite().setSelectionProvider(viewer);

		makeActions();

		// Create actions and connect them to the UI
		 
		contributeToActionBars();

		// Establish listener for viewer selection to keep action state correct
		//viewer.addSelectionChangedListener(this);

		

		// Add comparator for sorting
		//comparator = new ProblemViewComparator(new ViewRegistry());
		//viewer.setComparator(comparator);

		
		//filter = new ProblemViewFilter();
		//viewer.addFilter(filter);

	}

 

	/**
	 * Creates a <code>TableViewer</code> that has a table with multiple
	 * columns.
	 * 
	 * @param parent
	 * @return - a TableViewer with columns.
	 */
	TableViewer multiColumnViewer(Composite parent) {
		Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
				| SWT.FULL_SELECTION);
		
		TableLayout layout = new TableLayout();
		table.setLayout(layout);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		layout.addColumnData(new ColumnWeightData(5, 40, true));
		TableColumn tc0 = new TableColumn(table, SWT.NONE);
		tc0.setText(COLUMN_NAMES[0]);
		tc0.setAlignment(SWT.LEFT);
		tc0.setResizable(true);
		//tc0.addSelectionListener(getSelectionAdapter(tc0, 0));

		layout.addColumnData(new ColumnWeightData(10, true));
		TableColumn tc1 = new TableColumn(table, SWT.NONE);
		tc1.setText(COLUMN_NAMES[1]);
		tc1.setAlignment(SWT.LEFT);
		tc1.setResizable(true);
		//tc1.addSelectionListener(getSelectionAdapter(tc1, 1));

		layout.addColumnData(new ColumnWeightData(10, true));
		TableColumn tc2 = new TableColumn(table, SWT.NONE);
		tc2.setText(COLUMN_NAMES[2]);
		tc2.setAlignment(SWT.LEFT);
		tc2.setResizable(true);
	//	tc2.addSelectionListener(getSelectionAdapter(tc2, 2));
		
		layout.addColumnData(new ColumnWeightData(10, true));
		TableColumn tc3 = new TableColumn(table, SWT.NONE);
		tc3.setText(COLUMN_NAMES[3]);
		tc3.setAlignment(SWT.LEFT);
		tc3.setResizable(true);
		//tc3.addSelectionListener(getSelectionAdapter(tc3, 3));

		return new TableViewer(table);

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

		 
		// Filter Functionality
		//TODO: find out how to attach the filter field to the menu
		  
		/*filterAction = new Action() {

			public void run() {
				JPopupMenu popup =new JPopupMenu();
				final JTextField searchText = new JTextField();
				popup.add(searchText);
				popup.setEnabled(true);
				popup.setVisible(true);

				
				KeyStroke enterStroke = KeyStroke.getKeyStroke("enter");
				searchText.getInputMap().put(enterStroke, "applyFilter");
				searchText.getActionMap().put("applyfilter", );
				
			}
		};
		filterAction.setText("Filter");
		filterAction.setToolTipText("Opens a Popup Menu where a filter for the table can be set");
		filterAction.setEnabled(true); */
		
		//Remove Filter
		// Filter Functionality
		/*unFilterAction = new Action() {

			public void run() {
				filter.setSearchText(null);
			}
		};
		unFilterAction.setText("Remove Filter");
		unFilterAction.setToolTipText("Removes the filter for the table");
		unFilterAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		unFilterAction.setEnabled(true);*/
	}

	 
 

	/**
	 * Passing the focus request to the viewer's control.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

 

	/** This function enables sorting the table by clicking on a column */
	/*private SelectionAdapter getSelectionAdapter(final TableColumn column,
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
	
	
	public void addArtifact(ProblemArtifact pArtifact){
		
		
		
	}*/

}
