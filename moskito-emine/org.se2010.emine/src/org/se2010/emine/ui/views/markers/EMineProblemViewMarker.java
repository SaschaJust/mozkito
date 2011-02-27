package org.se2010.emine.ui.views.markers;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;


public class EMineProblemViewMarker extends ViewPart 
{

	private TableViewer viewer;

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
		
		
		viewer = multiColumnViewer(parent);
		viewer.setContentProvider(new MarkerContentProvider());
		viewer.setLabelProvider(new MarkerLabelProvider());
		
		//create a layout for the viewer
		GridData layoutData = new GridData (SWT.FILL, SWT.FILL, true, true);
		viewer.getControl().setLayoutData(layoutData);
		
		// Share Viewer Selection with other workbench parts
		getViewSite().setSelectionProvider(viewer);

		// Create actions and connect them to the UI
		contributeToActionBars();
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

		layout.addColumnData(new ColumnWeightData(10, true));
		TableColumn tc1 = new TableColumn(table, SWT.NONE);
		tc1.setText(COLUMN_NAMES[1]);
		tc1.setAlignment(SWT.LEFT);
		tc1.setResizable(true);

		layout.addColumnData(new ColumnWeightData(10, true));
		TableColumn tc2 = new TableColumn(table, SWT.NONE);
		tc2.setText(COLUMN_NAMES[2]);
		tc2.setAlignment(SWT.LEFT);
		tc2.setResizable(true);
		
		layout.addColumnData(new ColumnWeightData(10, true));
		TableColumn tc3 = new TableColumn(table, SWT.NONE);
		tc3.setText(COLUMN_NAMES[3]);
		tc3.setAlignment(SWT.LEFT);
		tc3.setResizable(true);

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

	/**
	 * Passing the focus request to the viewer's control.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}