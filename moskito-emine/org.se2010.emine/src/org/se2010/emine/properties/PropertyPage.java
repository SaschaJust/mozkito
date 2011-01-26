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

	Text repoNameInit;
	Text userInit;
	Text passwordInit;
	// Text urlLabel;
	Text urlInit;
	Text vmArgInit;

	GridData data;

	// Only for testing
	private QualifiedName USER_PROP_KEY = new QualifiedName("User", "User");
	private QualifiedName PASSWORD_PROP_KEY = new QualifiedName("pw", "pw");
	private QualifiedName URL_PROP_KEY = new QualifiedName("url", "url");
	private QualifiedName VMARG_PROP_KEY = new QualifiedName("vm", "vm");

	private static final QualifiedName REPO_PROP_KEY = new QualifiedName(
			"eMine_repos", "eMine_repos");
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

		if (reponames.isEmpty()) {
			createInitPage(backendpage);
		} else {

			final TabFolder tabFolder = new TabFolder(backendpage, SWT.BORDER
					| SWT.TOP | SWT.TRANSPARENT);
			
			for (String repo:reponames) {
				TabItem item = new TabItem(tabFolder, SWT.NONE | SWT.TRANSPARENT);
				item.setText(repo);

				Composite repoTab = new Composite(tabFolder, SWT.NONE);

				createRepoTab(repoTab, repo);

				item.setControl(repoTab);
			}
			
			TabItem item = new TabItem(tabFolder, SWT.None | SWT.TRANSPARENT);
			item.setText("New Entry");
			
			Composite newRepoTab = new Composite(tabFolder, SWT.NONE);
			createInitPage(newRepoTab);
			item.setControl(newRepoTab);
			
			tabFolder.pack();

		}

		return backendpage;

	}

	private void createInitPage(Composite parent) {

		GridLayout overview = new GridLayout(1, false);
		overview.verticalSpacing = 15;
		parent.setLayout(overview);

		
		createDefaultLabel(parent,"Here you can provide a new repository for eMine.");

		// creating data fields for a new type of repository

		Composite initPage = new Composite(parent, SWT.NONE);

		GridLayout initPageLayout = new GridLayout(2, false);
		initPageLayout.horizontalSpacing = 15;
		initPage.setLayout(initPageLayout);

		createDefaultLabel(initPage, "Repository Name *");

		repoNameInit = new Text(initPage, SWT.WRAP | SWT.BORDER);
		repoNameInit.setLayoutData(data);
		repoNameInit.setText("");

		Label userLabel = new Label(initPage, SWT.NONE);
		userLabel.setText("User *");

		userInit = new Text(initPage, SWT.WRAP | SWT.BORDER);
		userInit.setLayoutData(data);
		userInit.setText("");

		createDefaultLabel(initPage, "Password *");

		passwordInit = new Text(initPage, SWT.PASSWORD | SWT.BORDER);
		passwordInit.setLayoutData(data);
		passwordInit.setText("");

		createDefaultLabel(initPage, "Repository Path *");

		urlInit = new Text(initPage, SWT.WRAP | SWT.BORDER);
		urlInit.setLayoutData(data);
		urlInit.setText("");

		createDefaultLabel(initPage, "VM Arguments");

		vmArgInit = new Text(initPage, SWT.WRAP | SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		GridData localLayout = new GridData();
		localLayout.heightHint = 3 * TEXT_FIELD_HEIGHT;
		localLayout.widthHint = TEXT_FIELD_WIDTH;
		vmArgInit.setLayoutData(localLayout);
		vmArgInit.setText("");

		// create Warning Message

		createDefaultLabel(parent,"* necessary values:\n \t If empty, nothing is stored.");

	}
	
	private void createDefaultLabel(Composite parent, String text){
		
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		
	}

	private boolean storeNewRepo() {

		String newRepoName = repoNameInit.getText();
		if (newRepoName == "") {
			return true;
		}

		String repoList = newRepoName;
		for (String registeredRepos : reponames) {
			if (newRepoName.contentEquals(registeredRepos)) {
				// TODO: someAlertMessage: RepoName already registered.
				return false;
			}
			repoList += ";"+ registeredRepos;
		}

		String newUserName = userInit.getText();
		String newPassword = passwordInit.getText();
		String newUrl = urlInit.getText();
		String newVMarg = vmArgInit.getText();

		if (newRepoName.contains(";") || newUserName == "" || newPassword == ""
			|| newUrl == "") {
			// TODO: some AlertMessage: Insufficient information provided
			return false;
		}

		setValue(REPO_PROP_KEY, repoList);
		setValue(
				new QualifiedName(newRepoName + "_user", newRepoName + "_user"),
				newUserName);
		setValue(new QualifiedName(newRepoName + "_password", newRepoName
				+ "_password"), newPassword);
		setValue(new QualifiedName(newRepoName + "_url", newRepoName + "_url"),
				newUrl);
		setValue(new QualifiedName(newRepoName + "_vmArg", newRepoName
				+ "_vmArg"), newVMarg);

		return true;

	}

	@SuppressWarnings("static-access")
	private void createRepoList() {

		String names = getValue(this.REPO_PROP_KEY);

		if (names == null || names == "") {
			setValue(this.REPO_PROP_KEY, ""); // eMine_repos not initialized yet
			return;
		}

		String[] list = names.split(";");

		for (String name : list) {
			reponames.add(name);
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

		createDefaultLabel(parent,"User");

		userField = new Text(parent, SWT.WRAP | SWT.BORDER);
		userField.setLayoutData(data);
		userField.setText(getValue(USER_PROP_KEY));

	}

	private void createPasswordField(Composite parent) {
		createDefaultLabel(parent,"Password");

		passwordField = new Text(parent, SWT.PASSWORD | SWT.BORDER);
		passwordField.setLayoutData(data);
		passwordField.setText(getValue(PASSWORD_PROP_KEY));
	}

	private void createURLField(Composite parent) {

		createDefaultLabel(parent,"Repository-Path");

		urlField = new Text(parent, SWT.WRAP | SWT.BORDER);
		urlField.setLayoutData(data);
		urlField.setText(getValue(URL_PROP_KEY));

	}

	private void createVMargField(Composite parent) {
		createDefaultLabel(parent,"VM-Arguments");

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
			// TODO: Can that happen?
			throw new RuntimeException();
		}
	}

	protected void setValue(QualifiedName perKey, String value) {
		IResource res = (IResource) getElement();
		// TODO: properly implement + Test
		try {
			res.setPersistentProperty(perKey, value);
		} catch (final CoreException e) {
			// TODO: Can that happen?
			throw new RuntimeException(e);
		}

	}

	public boolean performOk() {

		if (storeNewRepo()) {
			return super.performOk();
		}

		return false;
	}

}
