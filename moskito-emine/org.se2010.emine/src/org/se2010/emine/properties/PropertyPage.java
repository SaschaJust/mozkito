package org.se2010.emine.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.Configuration;

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

	private final String[] VAR_PER_KEY = new String[] { "eMine_repos",
			"_Drepository.user", "_Drepository.password", "_Drepository.uri",
			"_vmArg" };

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

		createDefaultLabel(parent, "* necessary values");

	}

	private void createDefaultLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);

	}

	private boolean storeNewRepo() {

		String newRepoName = repoNameInit.getText();
		if (newRepoName.contentEquals("")) {
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

		if (newRepoName.contains(";") || newUserName.contentEquals("") || newPassword.contentEquals("")
				|| newUrl.contentEquals("")) {
			// TODO: some AlertMessage: Insufficient information provided
			return false;
		}

		setValue(VAR_PER_KEY[0], repoList);
		setValue(newRepoName + VAR_PER_KEY[1], newUserName);
		setValue(newRepoName + VAR_PER_KEY[2], newPassword);
		setValue(newRepoName + VAR_PER_KEY[3], newUrl);
		setValue(newRepoName + VAR_PER_KEY[VAR_PER_KEY.length - 1], newVMarg);

		return true;

	}

	private boolean saveChanges() {

		boolean change = false;

		String changes = "";
		for (int i = 0; i < inputFields.size(); i++) {

			Text input = inputFields.get(i);
			String[] info = input.getToolTipText().split(" ");
			String repoName = info[info.length - 1];

			int index = i % (VAR_PER_KEY.length - 1);
			String key = repoName + VAR_PER_KEY[index + 1];

			String value = input.getText();

			if (!value.contentEquals(getValue(key))) {
				if (index == (VAR_PER_KEY.length -1)){
					storeVMarg(key, value);
				} else {
				setValue(key, value);
				}
				change = true;

				if (!changes.contains(repoName)) {
					changes += repoName + ";";
				}
			}
		}

		String[] changed = changes.split(";");
		for (String repoName : changed) {

			ConfigurationArtifact artifact = new ConfigurationArtifact(null,
					getValue(repoName + VAR_PER_KEY[1]), getValue(repoName
							+ VAR_PER_KEY[2]), getValue(repoName
							+ VAR_PER_KEY[3]), 0, null, null, null,
					getValue(repoName + VAR_PER_KEY[VAR_PER_KEY.length - 1]));
			fireChangeEvent(artifact);
		}

		return change;
	}

	private void fireChangeEvent(ConfigurationArtifact artifact) {
		// TODO: implement
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

		createUserField(parent, repoName, VAR_PER_KEY[1]);
		createPasswordField(parent, repoName, VAR_PER_KEY[2]);
		createURIField(parent, repoName, VAR_PER_KEY[3]);
		createVMargField(parent, repoName, VAR_PER_KEY[VAR_PER_KEY.length - 1]);

	}

	private void createUserField(Composite parent, String reponame,
			String variable) {

		createDefaultLabel(parent, "User");

		Text userField = new Text(parent, SWT.WRAP | SWT.BORDER);
		userField.setLayoutData(data);
		userField.setText(getValue(reponame + variable));
		userField.setToolTipText("Username to access repository" + " "
				+ reponame);
		inputFields.add(userField);

	}

	private void createPasswordField(Composite parent, String reponame,
			String variable) {
		createDefaultLabel(parent, "Password");

		Text passwordField = new Text(parent, SWT.PASSWORD | SWT.BORDER);
		passwordField.setLayoutData(data);
		passwordField.setText(getValue(reponame + variable));
		passwordField.setToolTipText("Password to access repository" + " "
				+ reponame);
		inputFields.add(passwordField);

	}

	private void createURIField(Composite parent, String reponame,
			String variable) {

		createDefaultLabel(parent, "Repository-Path");

		Text uriField = new Text(parent, SWT.WRAP | SWT.BORDER);
		uriField.setLayoutData(data);
		uriField.setText(getValue(reponame + variable));
		uriField.setToolTipText("URI where the rcs repository is located" + " "
				+ reponame);
		inputFields.add(uriField);

	}

	private void createVMargField(Composite parent, String reponame,
			String variable) {
		createDefaultLabel(parent, "VM-Arguments");

		Text vmargField = new Text(parent, SWT.WRAP | SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);

		GridData localLayout = new GridData();
		localLayout.heightHint = 3 * TEXT_FIELD_HEIGHT;
		localLayout.widthHint = TEXT_FIELD_WIDTH;
		vmargField.setLayoutData(localLayout);

		vmargField.setText(getValue(reponame + variable));
		vmargField.setToolTipText("Provide additional flags to core" + " "
				+ reponame);
		inputFields.add(vmargField);

	}

	protected String getValue(String key) {

		QualifiedName perKey = new QualifiedName(key, key);
		IResource res = (IResource) getElement();
		try {

			String result = res.getPersistentProperty(perKey);
			if (result == null)
				return "";
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
			saveChanges();
			return super.performOk();
		}

		return false;
	}

}
