package org.se2010.emine.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RepositoryPropertiePage extends PropertyPage {

	private static QualifiedName USER_PROP_KEY;
	private static QualifiedName PASSWORD_PROP_KEY;
	private static QualifiedName URL_PROP_KEY;
	private static QualifiedName VMARG_PROP_KEY;
	private String repoName;

	private static Text userField;
	private static Text passwordField;
	private static Text urlField;
	private static Text vmargField;
	
	private GridData data;

	public RepositoryPropertiePage(String repoName) {

		super();
		this.repoName = repoName;
		USER_PROP_KEY = new QualifiedName(repoName + "_User", repoName
				+ "_User");
		PASSWORD_PROP_KEY = new QualifiedName(repoName + "_Password", repoName
				+ "_Password");
		VMARG_PROP_KEY = new QualifiedName(repoName + "_VMarg", repoName
				+ "_VMarg");
		URL_PROP_KEY = new QualifiedName(repoName + "_URL", repoName + "_URL");

		
	}

	protected Control createContents(Composite parent) {
		// Composite myComposite = new Composite(parent, SWT.NONE);

		Composite myComposite = createDefaultComposite(parent);

		createUserField(myComposite);
		createPasswordField(myComposite);
		createURLField(myComposite);
		createVMargField(myComposite);
		return myComposite;

	}

	private String getValue(QualifiedName ident) {
		IResource res = (IResource) getElement();
		try {
			String val = res.getPersistentProperty(ident);
			if (val != null) {
				return val;
			} else
				return "";
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			setValue(ident, "");
			return "";
		}
	}

	private void setValue(QualifiedName ident, String string) {
		IResource res = (IResource) getElement();

		try {
			res.setPersistentProperty(ident, string);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void createUserField(Composite parent) {

		Label userLabel = new Label(parent, SWT.NONE);
		userLabel.setText("User");

		userField = new Text(parent, SWT.WRAP | SWT.BORDER);
		userField.setLayoutData(data);
		userField.setText(getValue(USER_PROP_KEY));

	}

	private void createPasswordField(Composite parent) {
		Label userLabel = new Label(parent, SWT.NONE);
		userLabel.setText("Password");

		passwordField = new Text(parent, SWT.PASSWORD | SWT.BORDER);
		passwordField.setLayoutData(data);
		passwordField.setText(getValue(PASSWORD_PROP_KEY));
	}

	private void createURLField(Composite parent) {

		Label urlLabel = new Label(parent, SWT.NONE);
		urlLabel.setText("Repository-Path");

		urlField = new Text(parent, SWT.WRAP | SWT.BORDER);
		urlField.setLayoutData(data);
		urlField.setText(getValue(URL_PROP_KEY));

	}

	private void createVMargField(Composite parent) {
		Label vmArgsLabel = new Label(parent, SWT.NONE);
		vmArgsLabel.setText("VM-Arguments");

		vmargField = new Text(parent, SWT.WRAP | SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		GridData localLayout = new GridData();
		localLayout.heightHint = 3 * TEXT_FIELD_HEIGHT;
		localLayout.widthHint = TEXT_FIELD_WIDTH;
		vmargField.setLayoutData(localLayout);
		vmargField.setText("default");
	}

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		separator.setLayoutData(gridData);
	}

	
	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 20;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);

		data = new GridData();
		data.verticalAlignment = GridData.FILL_VERTICAL;
		data.horizontalAlignment = GridData.FILL_HORIZONTAL;
		data.widthHint = TEXT_FIELD_WIDTH;
		data.heightHint = TEXT_FIELD_HEIGHT;
		composite.setLayoutData(data);

		return composite;
	}

	
	public boolean performOk() {
		setValue(USER_PROP_KEY, userField.getText());
		setValue(PASSWORD_PROP_KEY, passwordField.getText());
		setValue(URL_PROP_KEY, urlField.getText());
		setValue(VMARG_PROP_KEY, vmargField.getText());
		
		return super.performOk();
	}

}
