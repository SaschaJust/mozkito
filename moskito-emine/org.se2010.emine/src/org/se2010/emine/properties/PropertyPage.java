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
import org.se2010.emine.artifacts.ConfigurationArtifact;

@SuppressWarnings("unused")
public class PropertyPage extends org.eclipse.ui.dialogs.PropertyPage {

	protected static final int TEXT_FIELD_WIDTH = 200;
	protected static final int TEXT_FIELD_HEIGHT = 20;

	Text repoNameInit;
	Text userInit;
	Text passwordInit;
	Text uriInit;
	Text vmArgInit;

	GridData data;

	private List<String> reponames = new ArrayList<String>();
	private List<Text> inputFields = new ArrayList<Text>();

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

			for (String repo : reponames) {
				TabItem item = new TabItem(tabFolder, SWT.NONE
						| SWT.TRANSPARENT);
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

		createDefaultLabel(parent,
				"Here you can provide a new repository for eMine.");

		// creating data fields for a new type of repository

		Composite initPage = new Composite(parent, SWT.NONE);

		GridLayout initPageLayout = new GridLayout(2, false);
		initPageLayout.horizontalSpacing = 15;
		initPage.setLayout(initPageLayout);

		createDefaultLabel(initPage, "Repository Name *");

		repoNameInit = new Text(initPage, SWT.WRAP | SWT.BORDER);
		repoNameInit.setLayoutData(data);
		repoNameInit.setText("");
		repoNameInit
				.setToolTipText("The name of the repository is used\nto store your values locally.");

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

		uriInit = new Text(initPage, SWT.WRAP | SWT.BORDER);
		uriInit.setLayoutData(data);
		uriInit.setText("");

		createDefaultLabel(initPage, "VM Arguments");

		vmArgInit = new Text(initPage, SWT.WRAP | SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		GridData localLayout = new GridData();
		localLayout.heightHint = 3 * TEXT_FIELD_HEIGHT;
		localLayout.widthHint = TEXT_FIELD_WIDTH;
		vmArgInit.setLayoutData(localLayout);
		vmArgInit.setText("");

		// create Warning Message

		createDefaultLabel(parent, "* necessary values.");

		createDefaultLabel(parent, "You have: " + reponames.size());

	}

	private void createDefaultLabel(Composite parent, String text) {

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
			repoList += ";" + registeredRepos;
		}

		String newUserName = userInit.getText();
		String newPassword = passwordInit.getText();
		String newUrl = uriInit.getText();
		String newVMarg = vmArgInit.getText();

		if (newRepoName.contains(";") || newUserName == "" || newPassword == ""
				|| newUrl == "") {
			// TODO: some AlertMessage: Insufficient information provided
			return false;
		}

		setValue("eMine_repos", repoList);
		setValue(newRepoName + "_Drepository.user", newUserName);
		setValue(newRepoName + "_Drepository.password", newPassword);
		setValue(newRepoName + "_Drepository.uri", newUrl);
		setValue(newRepoName + "_vmArg", newVMarg);

		return true;

	}

	private List<ConfigurationArtifact> saveChanges() {

		List<ConfigurationArtifact> changedRepos = new ArrayList<ConfigurationArtifact>();
		int numStoredRepos = reponames.size();
		int numInputfields = inputFields.size();
		int fieldstosave = numInputfields / numStoredRepos;
		int j = 0;

		// TODO: possible Indexoutofbounds?
		for (int i = 0; i < numInputfields; i++) {

			String repoName = reponames.get(j++);

			String Drepository_user = inputFields.get(i++).getText();
			String Drepository_password = inputFields.get(i++).getText();
			String Drepository_uri = inputFields.get(i++).getText();
			String vmArg = inputFields.get(i).getText();

			String olduser = repoName + "_Drepository_user";
			String oldpw = repoName + "_Drepository_password";
			String old_uri = repoName + "_Drepository_uri";
			String old_vmArg = repoName + "_vmArg";

			if (Drepository_user.contentEquals(getValue(olduser))
					& Drepository_password.contentEquals(getValue(oldpw))
					& Drepository_uri.contentEquals(getValue(old_uri))
					& vmArg.contentEquals(getValue(old_vmArg))) {

				break;
			}
			
			setValue(olduser, Drepository_user);
			setValue(oldpw, Drepository_password);
			setValue(old_uri, Drepository_uri);
			storeVMarg(old_vmArg, vmArg);
			
			changedRepos.add(new ConfigurationArtifact(null, Drepository_uri, Drepository_user, Drepository_password, 100, null, null, null));

		}

		// TODO: implement
		return changedRepos;
	}

	private void storeVMarg(String perKeyName, String value) {
		// TODO check and overwrite values
		setValue(perKeyName, value);
		
		
		
	}

	private void createRepoList() {
		String names = getValue("eMine_repos");

		if (names == null || names == "") {
			setValue("eMine_repos", ""); // eMine_repos not initialized yet
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

		createUserField(parent, repoName + "_Drepository.user");
		createPasswordField(parent, repoName + "_Drepository.password");
		createURIField(parent, repoName + "_Drepository.uri");
		createVMargField(parent, repoName + "_vmArg");

	}

	private void createUserField(Composite parent, String qualifiedName) {

		createDefaultLabel(parent, "User");

		Text userField = new Text(parent, SWT.WRAP | SWT.BORDER);
		userField.setLayoutData(data);
		userField.setText(getValue(qualifiedName));
		inputFields.add(userField);

	}

	private void createPasswordField(Composite parent, String qualifiedName) {
		createDefaultLabel(parent, "Password");

		Text passwordField = new Text(parent, SWT.PASSWORD | SWT.BORDER);
		passwordField.setLayoutData(data);
		passwordField.setText(getValue(qualifiedName));
		inputFields.add(passwordField);
	}

	private void createURIField(Composite parent, String qualifiedName) {

		createDefaultLabel(parent, "Repository-Path");

		Text uriField = new Text(parent, SWT.WRAP | SWT.BORDER);
		uriField.setLayoutData(data);
		uriField.setText(getValue(qualifiedName));
		inputFields.add(uriField);

	}

	private void createVMargField(Composite parent, String qualifiedName) {
		createDefaultLabel(parent, "VM-Arguments");

		Text vmargField = new Text(parent, SWT.WRAP | SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		GridData localLayout = new GridData();
		localLayout.heightHint = 3 * TEXT_FIELD_HEIGHT;
		localLayout.widthHint = TEXT_FIELD_WIDTH;
		vmargField.setLayoutData(localLayout);
		vmargField.setText(getValue(qualifiedName));
		inputFields.add(vmargField);
	}

	protected String getValue(String key) {

		QualifiedName perKey = new QualifiedName(key, key);
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

	protected void setValue(String perKeyName, String value) {
		QualifiedName perKey = new QualifiedName(perKeyName, perKeyName);
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
		
		saveChanges();

		return false;
	}

}
