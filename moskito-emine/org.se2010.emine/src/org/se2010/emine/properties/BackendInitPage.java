package org.se2010.emine.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BackendInitPage {
	
	Composite parent;
	GridData data;
	
	public BackendInitPage(Composite parent){
		
		this.parent = parent;
		this.data = new GridData();
		data.verticalAlignment = GridData.FILL_VERTICAL;
		data.horizontalAlignment = GridData.FILL_HORIZONTAL;
		data.widthHint =  PropertyPage.TEXT_FIELD_WIDTH;
		data.heightHint = PropertyPage.TEXT_FIELD_HEIGHT;
		
	}
	
	public Control createContents(Composite parent)
	{
		//Composite myComposite = new Composite(parent, SWT.NONE);

		Composite myComposite = createDefaultComposite(parent);
		
//		createUserField(myComposite);
//		setPasswordField(myComposite);
//		seturl(myComposite);
//		setVMargs(myComposite);
		return myComposite;


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

		
		composite.setLayoutData(data);

		return composite;
	}
	

}
