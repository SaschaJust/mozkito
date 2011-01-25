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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

@SuppressWarnings("unused")
public class PropertyPage extends org.eclipse.ui.dialogs.PropertyPage {


	private Text descriptionText;
	public static QualifiedName  AUTHOR_PROP_KEY = new QualifiedName("User", "User");
	private static String USER_ID = "RMP.user";
	private static TextPropertyDescriptor USER_PROP_DESC = new TextPropertyDescriptor(USER_ID, "user");
	private static final IPropertyDescriptor[] DESCRIPTORS = {USER_PROP_DESC};
	protected static final int TEXT_FIELD_WIDTH = 200;
	protected static final int TEXT_FIELD_HEIGHT = 20;

	GridData data;
	
	public PropertyPage() {
		super();
	}

	protected Control createContents(Composite parent)
	{

		 Composite backendpage = new Composite(parent, SWT.NONE);

		 final TabFolder tabFolder = new TabFolder(backendpage, SWT.BORDER|SWT.TOP);
		    
		 for (int i = 0; i < 6; i++) {
		      TabItem item = new TabItem(tabFolder, SWT.NONE);
		      item.setText("TabItem " + i);
		   
		      RepositoryPropertiePage repo = new RepositoryPropertiePage("Page_"+i);
		      Composite repoTab = new Composite(tabFolder, SWT.NONE);
		     
		      repo.createContents(repoTab);
		      
		      item.setControl(repoTab);
		    }
		 tabFolder.pack();
		
		return backendpage;


	}
	
	protected String getValue(QualifiedName perKey){
		IResource res = (IResource) getElement();
		 try {
			return res.getPersistentProperty(perKey);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return "default";
	}
	
	protected void setValue(QualifiedName perKey, String value){
		IResource res = (IResource) getElement();
		
		try {
			res.setPersistentProperty(perKey, value);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

	
	
	
	
	private void addFirstSection(Composite parent) {

		
		
		//Label for value field
		Label locationLabel = new Label(parent, SWT.NONE);
		locationLabel.setText("Somethingelse" );

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
//		layout.marginHeight = TEXT_FIELD_HEIGHT;
//		layout.marginWidth = TEXT_FIELD_WIDTH;
		composite.setLayout(layout);

		data = new GridData();
		data.verticalAlignment = GridData.FILL_VERTICAL;
		data.horizontalAlignment = GridData.FILL_HORIZONTAL;
		data.widthHint =  TEXT_FIELD_WIDTH;
		data.heightHint = TEXT_FIELD_HEIGHT;
		composite.setLayoutData(data);

		return composite;
	}


	public boolean performOk(){
		//setUser(user);
		return super.performOk();
	}



}
