package org.se2010.emine.properties;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

@SuppressWarnings("unused")
public class PropertyPage extends org.eclipse.ui.dialogs.PropertyPage {

	protected static final int TEXT_FIELD_WIDTH = 200;
	protected static final int TEXT_FIELD_HEIGHT = 20;

	GridData data;

	// Only for testing
	private QualifiedName USER_PROP_KEY = new QualifiedName("User", "User");
	private QualifiedName PASSWORD_PROP_KEY = new QualifiedName("pw", "pw");
	private QualifiedName URL_PROP_KEY = new QualifiedName("url", "url");
	private QualifiedName VMARG_PROP_KEY = new QualifiedName("vm", "vm");

	private List<String> reponames = new ArrayList<String>();

	private static Text userField;
	private static Text passwordField;
	private static Text urlField;
	private static Text vmargField;

	public PropertyPage() {
		super();
		this.data = new GridData();
		data.widthHint = TEXT_FIELD_WIDTH;
		data.heightHint = TEXT_FIELD_HEIGHT;
	}

	protected Control createContents(Composite parent) {

		Composite backendpage = new Composite(parent, SWT.NONE);

		
		createRepoList();
		
		if (this.reponames.isEmpty()){
			createInitPage(backendpage);
			return backendpage;
		}
		
		final TabFolder tabFolder = new TabFolder(backendpage, SWT.BORDER
				| SWT.TOP);

		
		for (String repo:this.reponames){
			TabItem item = new TabItem(tabFolder, SWT.NONE | SWT.COLOR_GRAY);
			item.setText(repo);

			Composite repoTab = new Composite(tabFolder, SWT.NONE);

			createRepoTab(repoTab, repo);
		
			
		}
		
//		for (int i = 0; i < 6; i++) {
//			TabItem item = new TabItem(tabFolder, SWT.NONE | SWT.COLOR_GRAY);
//			item.setText("TabItem " + i);
//
//			Composite repoTab = new Composite(tabFolder, SWT.NONE);
//
//			createRepoTab(repoTab, "Init");
//			//		      
//			item.setControl(repoTab);
//		}
		tabFolder.pack();

		return backendpage;

	}

	private void createInitPage(Composite parent) {
		
		GridLayout initPageLayout = new GridLayout(2,false);
		parent.setLayout(initPageLayout);
		
		Label userLabel = new Label(parent, SWT.NONE);
		userLabel.setText("Repository Name");

		Text name = new Text(parent, SWT.WRAP | SWT.BORDER);
		name.setLayoutData(data);
		name.setText(getValue(USER_PROP_KEY));
		
		
	}

	private void createRepoList() {

		QualifiedName repo = new QualifiedName("eMine_repos", "eMine_repos");

		IResource res = (IResource) getElement();
		try {
			String names = res.getPersistentProperty(repo);
			if (names == null || names == "") {
				return;
			}
			String[] list = names.split(";");

			for (String name : list) {
				reponames.add(name);
			}

		} catch (CoreException e) {

		}

	}

	private void createRepoTab(Composite parent, String repoName) {

		GridLayout tablayout = new GridLayout(2, false);
		parent.setLayout(tablayout);
		createUserField(parent);
		createPasswordField(parent);
		createURLField(parent);
		createVMargField(parent);

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
		vmargField.setText(getValue(VMARG_PROP_KEY));
	}

	protected String getValue(QualifiedName perKey) {
		IResource res = (IResource) getElement();
		try {

			String result = res.getPersistentProperty(perKey);
			if (result == null)
				return "DEF";
			return result;
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return "default";
	}

	protected void setValue(QualifiedName perKey, String value) {
		IResource res = (IResource) getElement();
			//TODO: properly implement + Test
		try {
			res.setPersistentProperty(perKey, value);
		} catch (CoreException e) {
		}

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

	public boolean performOk() {
		// setUser(user);
		return super.performOk();
	}

}
