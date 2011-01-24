package org.se2010.emine.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

@SuppressWarnings("unused")
public class PropertyPage extends org.eclipse.ui.dialogs.PropertyPage {

	private Text user;
	private Text password;
	private Text url;
	private Text vmArgs;

	private Text descriptionText;
	
	public static final int TEXT_FIELD_WIDTH = 200;
	public static final int TEXT_FIELD_HEIGHT = 20;

	GridData data;

	public PropertyPage() {
		super();
	}

	protected Control createContents(Composite parent) {
		Composite myComposite = new Composite(parent, SWT.NONE);

		// Composite myComposite = createDefaultComposite(parent);

		RepositoryPropertiePage repoPage = new RepositoryPropertiePage("Init");
		repoPage.createContents(myComposite);
		return myComposite;

	}

	private void setHeadline(Composite parent) {

	}

	private void addFirstSection(Composite parent) {

		// Label for value field
		Label locationLabel = new Label(parent, SWT.NONE);
		locationLabel.setText("Somethingelse");

		// Location text field
		Text location = new Text(parent, SWT.WRAP | SWT.READ_ONLY);
		location.setText(getElement().toString());
	}

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		separator.setLayoutData(gridData);
	}

	private void addSecondSection(Composite parent) {

	}

	private void addDescription(Composite parent) {
		// Label for description field
		Label descriptionLabel = new Label(parent, SWT.NONE);
		descriptionLabel.setText("Something");
		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		descriptionLabel.setLayoutData(gridData);

		// Descripton text field

		descriptionText = new Text(parent, SWT.V_SCROLL | SWT.BORDER | SWT.WRAP);
		gridData = new GridData();
		gridData.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		gridData.heightHint = convertWidthInCharsToPixels(TEXT_FIELD_HEIGHT);
		descriptionText.setLayoutData(gridData);

		// String descripton = ((ILocation) getElement()).getDescription();
		// descriptionText.setText((descripton != null) ? descripton
		// : ILocation.DESCRIPTION_DEFAULT);
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 20;
		layout.verticalSpacing = 10;
		// layout.marginHeight = TEXT_FIELD_HEIGHT;
		// layout.marginWidth = TEXT_FIELD_WIDTH;
		composite.setLayout(layout);

		data = new GridData();
		data.verticalAlignment = GridData.FILL_VERTICAL;
		data.horizontalAlignment = GridData.FILL_HORIZONTAL;
		data.widthHint = TEXT_FIELD_WIDTH;
		data.heightHint = TEXT_FIELD_HEIGHT;
		composite.setLayoutData(data);

		return composite;
	}

	/**
	 * some kind of dummy function simply to test, if we can set author not
	 * persistent in any case no writeback to workspace!
	 * 
	 * other implementation below
	 * 
	 * @param string
	 */
	// protected void setUser(String string) {
	// this.user = string;
	//
	// }

	public boolean performOk() {
		return super.performOk();
	}

}
