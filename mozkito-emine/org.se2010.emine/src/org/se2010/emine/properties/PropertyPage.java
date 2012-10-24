/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package org.se2010.emine.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.se2010.emine.artifacts.ConfigurationArtifact;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.IEMineEventBus;

/**
 * This class creates the whole back-end-configuration page and is responsible to show/store all <br>
 * information once provided by the user in a persistent and project-specific way.
 * 
 * @author  Andreas Rau
 * @version 02/2011 1.0
 */
public class PropertyPage extends org.eclipse.ui.dialogs.PropertyPage {

	protected static final int TEXT_FIELD_WIDTH = 200;
	protected static final int TEXT_FIELD_HEIGHT = 20;

	GridData data;

	private List<String> reponames = new ArrayList<String>();
	private List<PropertyTextField> inputFields = new ArrayList<PropertyTextField>();
	
	String projectName = "";
	public static final String MSA_CORE_name = "Repo-Panther";
	/**
	 * This field is used to identify the qualified names for storing in the workbench properties.<p>
	 * <b> Don't ever touch the order of this array!</b> <p>
	 * If you do so, <code> createInitPage, storeNewRepo, saveChanges and createRepoTab </code>(in <code>PropertyPage<code>) must be changed to. 
	 */
	private final String[] VAR_PER_KEY = new String[] { "eMine_repos",
			"_Drepository.user", "_Drepository.password", "_Drepository.uri",
			"_vmArg", "_Drepository_type", "_Dcache_size", "_Ddatabase_type", "Dlog_level" };
	
	private static final String[] SUPPORTED_REPOS = new String[]{ "CVS","GIT","MERCURIAL","SUBVERSION" };
	private static final String[] SUPPORTED_DBS = new String[] {"POSTGRESQL","MYSQL"};
	
	Combo repoConfig;
	Combo databaseConfig;
	
	public PropertyPage() {
		super();
		this.data = new GridData();
		data.widthHint = TEXT_FIELD_WIDTH;
		data.heightHint = TEXT_FIELD_HEIGHT;

	}
	
	/**
	 * This is the initial method to build the content of the property-page.
	 */
	protected Control createContents(Composite parent) {
		
		Composite backendpage = new Composite(parent, SWT.NONE);
		backendpage.setLayout(new GridLayout(1, false));
		
		createDefaultLabel(backendpage, "Here you can define your values for mining the repositories via " + MSA_CORE_name + ".");
		

		Composite configuration = new Composite(backendpage, SWT.NONE);
		createRepoList();
		
		IResource res = (IResource) getElement();
		this.projectName = res.getLocation().lastSegment();
		
		createRepoTab(configuration, projectName);

		return backendpage;

	}


	
	/**
	 * creates a standardized label for back-end-page 
	 */

	private void createDefaultLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE | SWT.TRAVERSE_NONE);
		label.setText(text);

	}


	/**
	 * Actual saving of any changes made by the user in the interface. <p>
	 * A change is reported via <Code> EventBus</Code> to the MSA-Core.
	 * @return if there was a change
	 */
	private boolean saveChanges() {

		boolean change = false;
		
		for (PropertyTextField field : inputFields) {
			String key = field.getVALUE_PROP_KEY();
			String value = field.getTextField().getText();
			
			if (!value.contentEquals(getValue(key))) {
				if (key.contentEquals(VAR_PER_KEY[4])) {
					storeVMarg(key, value);
					change = true;
					continue;
				}
				if (key.contentEquals(VAR_PER_KEY[6]))
				{
					int csize = Integer.valueOf(value);
					setValue(key, String.valueOf(csize));
					change = true;
					continue;
				}
				
				setValue(key, value);

			}

		}
		setValue(VAR_PER_KEY[5], repoConfig.getText());
		setValue(VAR_PER_KEY[7], databaseConfig.getText());
		fireChangeEvent(this.projectName);

		return change;
	}
	
	/**
	 * fires a Configuration-Artifact via the <Code> EventBus</Code> to the MSA-Core.
	 */
	private void fireChangeEvent(String name) {
		
		String vmArg = getValue(name + VAR_PER_KEY[4]);
		String repoType = getValue(VAR_PER_KEY[5]);
		String csize = getValue(VAR_PER_KEY[6]);
		int csizeInt = Integer.valueOf(csize);
		String dbType = getValue(VAR_PER_KEY[7]);
		String logLevel = getValue(VAR_PER_KEY[8]);

		
		ConfigurationArtifact artifact = new ConfigurationArtifact(this.projectName,
				getValue(name + VAR_PER_KEY[1]), getValue(name
						+ VAR_PER_KEY[2]), getValue(name
						+ VAR_PER_KEY[3]), csizeInt, dbType , logLevel, repoType ,
				vmArg);
		
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

		createDefaultLabel(parent, "User*");
		PropertyTextField user = new PropertyTextField(repoName,
				VAR_PER_KEY[1], "Username to access repository", parent,
				SWT.WRAP | SWT.BORDER, data);
		user.setText(getValue(VAR_PER_KEY[1]));
		inputFields.add(user);

		createDefaultLabel(parent, "Password*");
		PropertyTextField password = new PropertyTextField(repoName,
				VAR_PER_KEY[2], "Password to access repository" + " "
						+ repoName, parent, SWT.PASSWORD | SWT.BORDER, data);
		password.setText(getValue(VAR_PER_KEY[2]));
		inputFields.add(password);

		createDefaultLabel(parent, "Repository-URI*");
		PropertyTextField uri = new PropertyTextField(repoName, VAR_PER_KEY[3],
				"URI where the rcs repository is located" + " " + repoName,
				parent, SWT.WRAP | SWT.BORDER, data);
		uri.setText(getValue(VAR_PER_KEY[3]));
		inputFields.add(uri);

		createDefaultLabel(parent,"Loglevel");
		PropertyTextField loglevel = new PropertyTextField(repoName, VAR_PER_KEY[8], "determines the log level", parent,  SWT.WRAP | SWT.BORDER, data);
		loglevel.setText(getValue(VAR_PER_KEY[8]));
		inputFields.add(loglevel);
		
		createDefaultLabel(parent, "Cachesize*");
		PropertyTextField csize = new PropertyTextField(repoName, VAR_PER_KEY[6],"determines the cache size (number of logs) that are prefetched during reading", parent,  SWT.WRAP | SWT.BORDER, data);
		csize.setText(getValue(VAR_PER_KEY[6]));
		inputFields.add(csize);
	
		
		
		createRepoDropdown(parent, repoName);
		createVMargFields(parent, repoName);
		
	}
	
	/**
	 * The VM-argument field is a special field (size)
	 * @param parent
	 * @param repoName
	 */
	protected void createVMargFields(Composite parent, String repoName){
		
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
	 * Creates the two Dropdown-Menus for DBType and REPOTYPE <br>
	 * Per default (if no values were added yet) it displays the first entry from the respective List. 
	 * @param parent
	 * @param repoName
	 */
	protected void createRepoDropdown(Composite parent, String repoName){
		
		String key = VAR_PER_KEY[5];
		
		createDefaultLabel(parent, "Type of Repository");
		repoConfig = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		
		for(String repotype:SUPPORTED_REPOS) {
		      repoConfig.add(repotype);
		}
		
		String test = getValue(key);
		if (test.contentEquals("")){
			repoConfig.setText(SUPPORTED_REPOS[0]);
		} else {
			repoConfig.setText(test);
		}
		repoConfig.setToolTipText("Type of the repository");
		
		String dbKey = VAR_PER_KEY[7];
		
		createDefaultLabel(parent, "Type of Database");
		databaseConfig = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		
		for(String database:SUPPORTED_DBS){
			databaseConfig.add(database);
		}
		
		String dbTest = getValue(dbKey);
		
		if (dbTest.contentEquals("")){
			databaseConfig.setText(SUPPORTED_DBS[0]);
		} else {
			databaseConfig.setText(dbTest);
		}
		
		databaseConfig.setToolTipText("Type of the repository");
		
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
			if (result == null || result.contentEquals(""))
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

	
	/**
	 * Overrides the perform button. If one field is entered wrongly, you can't exit.
	 */
	public boolean performOk() {

		
		try {
			saveChanges();
		} catch (NumberFormatException e){
			return false;
		}
		return super.performOk();

	}

}
