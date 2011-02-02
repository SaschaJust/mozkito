package org.se2010.emine.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.se2010.emine.artifacts.ConfigurationArtifact;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.IEMineEventBus;

/**
 * This class creates the whole back-end-configuration page and is responsible to show/store all <br>
 * information once provided by the user in a persistent and project-specific way.
 * @author Amras
 *
 */
public class PropertyPage extends org.eclipse.ui.dialogs.PropertyPage {

	protected static final int TEXT_FIELD_WIDTH = 200;
	protected static final int TEXT_FIELD_HEIGHT = 20;

	GridData data;

	private List<String> reponames = new ArrayList<String>();
	private List<PropertyTextField> inputFields = new ArrayList<PropertyTextField>();
	private List<PropertyTextField> initFields = new ArrayList<PropertyTextField>();

	/**
	 * This field is used to identify the qualified names for storing in the workbench properties.<p>
	 * <b> Don't ever touch the order of this array!</b> <p>
	 * If you do so, <code> createInitPage, storeNewRepo, saveChanges and createRepoTab </code>(in <code>PropertyPage<code>) must be changed to. 
	 */
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

	/**
	 * This method implements the initialization page of the back-end-configuration.<p>
	 * All describing labels AND text-fields are inititialized empty in a Grid-layout.
	 * 
	 * @param parent the graphical container, the page is bedded in.
	 */
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

		PropertyTextField newRepo = new PropertyTextField(
				"",
				VAR_PER_KEY[0],
				"The name of the repository is used\nto store your values locally.",
				initPage, SWT.WRAP | SWT.BORDER, data);
		initFields.add(newRepo);
		
		createDefaultLabel(initPage, "User *");

		PropertyTextField newUser = new PropertyTextField("", VAR_PER_KEY[1],
				"Username to access repository", initPage, SWT.WRAP
						| SWT.BORDER, data);
		initFields.add(newUser);


		createDefaultLabel(initPage, "Password *");

		PropertyTextField newpw = new PropertyTextField("", VAR_PER_KEY[2],
				"Password to access repository", initPage, SWT.PASSWORD
						| SWT.BORDER, data);
		initFields.add(newpw);


		createDefaultLabel(initPage, "Repository Path *");

		PropertyTextField newURI = new PropertyTextField("", VAR_PER_KEY[3],
				"URI where the rcs repository is located", initPage, SWT.WRAP
						| SWT.BORDER, data);
		initFields.add(newURI);


		createDefaultLabel(initPage, "VM Arguments");

		GridData localLayout = new GridData();
		localLayout.heightHint = 3 * TEXT_FIELD_HEIGHT;
		localLayout.widthHint = TEXT_FIELD_WIDTH;
		PropertyTextField newVMarg = new PropertyTextField("", VAR_PER_KEY[4],
				"Provide additional flags to core", initPage, SWT.WRAP
						| SWT.BORDER | SWT.MULTI | SWT.V_SCROLL, localLayout);

		initFields.add(newVMarg);

		// create Warning Message

		createDefaultLabel(parent, "* necessary values");

	}
	
	/**
	 * creates a standardized label for back-end-page 
	 */

	private void createDefaultLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE | SWT.TRAVERSE_NONE);
		label.setText(text);

	}

	/**
	 * Stores any kind of new repository provided by the user in the back-end-property-interface.<p>
	 * 
	 * It checks crucial parts of provided information: <p>
	 * * no separator symbol (e.g. ';') <br>
	 * * no emptiness of as necessary marked information <br>
	 * * no double occurrence of unique identifier (e.g. Repository Name)
	 * @return: <b>true</b> indicates if storing process was successful or nothing was stored.
	 *          <b>false</b> indicates that some input was malformed.
	 */
	private boolean storeNewRepo() {

		String newRepoName = initFields.get(0).getTextField().getText();
		if (newRepoName == null || newRepoName.contentEquals(""))
			return true;

		String newUserName = initFields.get(1).getTextField().getText();
		String newPassword = initFields.get(2).getTextField().getText();
		String newUrl = initFields.get(3).getTextField().getText();
		String newVMarg = initFields.get(4).getTextField().getText();
		String repoList = getValue(VAR_PER_KEY[0]);

		if (repoList.contains(newRepoName) || newRepoName.contains(";")
				|| newUserName.contentEquals("")
				|| newPassword.contentEquals("") || newUrl.contentEquals("")) {
			// TODO: some AlertMessage: Insufficient information provided
			return false;
		}

		repoList += newRepoName + ";";

		setValue(VAR_PER_KEY[0], repoList);
		setValue(newRepoName + VAR_PER_KEY[1], newUserName);
		setValue(newRepoName + VAR_PER_KEY[2], newPassword);
		setValue(newRepoName + VAR_PER_KEY[3], newUrl);
		storeVMarg(newRepoName + VAR_PER_KEY[4], newVMarg);
		
		fireChangeEvent(newRepoName);

		return true;

	}

	/**
	 * Actual saving of any changes made by the user in the interface. <p>
	 * A change is reported via <Code> EventBus</Code> to the MSA-Core.
	 * @return if there was a change
	 */
	private boolean saveChanges() {

		boolean change = false;
		String changes = "";

		for (PropertyTextField field : inputFields) {
			String key = field.getVALUE_PROP_KEY();
			String value = field.getTextField().getText();
			String repo = field.getRepoName();

			if (!value.contentEquals(getValue(key))) {
				if (key.contentEquals(VAR_PER_KEY[4])) {
					storeVMarg(key, value);
				} else {
					setValue(key, value);
				}
				change = true;
				changes += repo + ";";
			}
		}

		String[] changed = changes.split(";");
		for (String repoName : changed) {

			
			fireChangeEvent(repoName);
		}

		return change;
	}
	
	/**
	 * fires a Configuration-Artifact via the <Code> EventBus</Code> to the MSA-Core.
	 */
	private void fireChangeEvent(String repoName) {
		ConfigurationArtifact artifact = new ConfigurationArtifact(null,
				getValue(repoName + VAR_PER_KEY[1]), getValue(repoName
						+ VAR_PER_KEY[2]), getValue(repoName
						+ VAR_PER_KEY[3]), 0, null, null, null,
				getValue(repoName + VAR_PER_KEY[4]));
		
		 IEMineEventBus bus = EMineEventBus.getInstance();
		 bus.fireEvent(artifact);
	
	}

	/**
	 * Filters and Overrides given VM-Arguments directly in the configuration file.
	 * @param perKeyName: Key for Saving VM-Arguments.
	 * @param value: new VM-Args -> are postprocessed.
	 */
	private void storeVMarg(String perKeyName, String value) {
		// TODO check and overwrite values
		setValue(perKeyName, value);

	}

	/**
	 * Gathers information about the already stored Repositories from the Configuration-File. <p>
	 * This information is needed to create the tab-view in the back-end-page.
	 */
	private void createRepoList() {
		String names = getValue(VAR_PER_KEY[0]);

		if (names == null || names == "") {
			setValue(VAR_PER_KEY[0], ""); // eMine_repos not initialized yet
			return;
		}

		String[] list = names.split(";");

		for (String name : list) {
			reponames.add(name);
		}

	}
	/**
	 * Creates a complete tab-entry for back-end-configuration. <p>
	 * 
	 * Determines a necessary entries for the Text-Fields and creates corresponding values.
	 * @param parent embedded Tab
	 * @param repoName repository-identifier for gathering current information
	 */
	private void createRepoTab(Composite parent, String repoName) {

		GridLayout tablayout = new GridLayout(2, false);
		parent.setLayout(tablayout);

		createDefaultLabel(parent, "User");
		PropertyTextField user = new PropertyTextField(repoName,
				VAR_PER_KEY[1], "Username to access repository", parent,
				SWT.WRAP | SWT.BORDER, data);
		user.setText(getValue(repoName + VAR_PER_KEY[1]));
		inputFields.add(user);

		createDefaultLabel(parent, "Password");
		PropertyTextField password = new PropertyTextField(repoName,
				VAR_PER_KEY[2], "Password to access repository" + " "
						+ repoName, parent, SWT.PASSWORD | SWT.BORDER, data);
		password.setText(getValue(repoName + VAR_PER_KEY[2]));
		inputFields.add(password);

		createDefaultLabel(parent, "Repository-URI");
		PropertyTextField uri = new PropertyTextField(repoName, VAR_PER_KEY[3],
				"URI where the rcs repository is located" + " " + repoName,
				parent, SWT.WRAP | SWT.BORDER, data);
		uri.setText(getValue(repoName + VAR_PER_KEY[3]));
		inputFields.add(uri);

		createDefaultLabel(parent, "VM-Arguments");
		GridData localLayout = new GridData();
		localLayout.heightHint = 3 * TEXT_FIELD_HEIGHT;
		localLayout.widthHint = TEXT_FIELD_WIDTH;
		PropertyTextField vmarg = new PropertyTextField(repoName,
				VAR_PER_KEY[4], "Provide additional flags to core" + " "
						+ repoName, parent, SWT.WRAP | SWT.BORDER | SWT.MULTI
						| SWT.V_SCROLL, localLayout);
		vmarg.setText(getValue(repoName + VAR_PER_KEY[4]));
		inputFields.add(vmarg);
	}

	/**
	 * This method encapsulates the IResource from project's workspace. <p>
	 * 
	 * @param key given string determines the identifier for the QualifiedName.
	 * @return value stored altogether with the identifier.
	 */
	protected String getValue(String key) {

		QualifiedName perKey = new QualifiedName(key, key);
		IResource res = (IResource) getElement();
		try {

			String result = res.getPersistentProperty(perKey);
			if (result == null)
				return "";
			return result;
		} catch (CoreException e) {
			throw new RuntimeException();
		}
	}

	/**
	 * This method encapsulates the IResource from project's workspace. <p>
	 * 
	 * @param perKeyName identifies the QualifiedName
	 * @param value which is stored for given argument
	 */
	protected void setValue(String perKeyName, String value) {
		QualifiedName perKey = new QualifiedName(perKeyName, perKeyName);
		IResource res = (IResource) getElement();
		try {
			res.setPersistentProperty(perKey, value);
		} catch (final CoreException e) {
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
