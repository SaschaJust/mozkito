package org.se2010.emine.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class PropertyInitPage extends org.eclipse.ui.dialogs.PropertyPage {

	Composite parent;
	GridData data;

	public PropertyInitPage(Composite parent) {
		super();
		data.widthHint = PropertyPage.TEXT_FIELD_WIDTH;
		data.heightHint = PropertyPage.TEXT_FIELD_HEIGHT;
	}

	public Control createContents(Composite parent) {
		Composite myComposite = new Composite(parent, SWT.NONE);
		myComposite.setLayout(new GridLayout(1, false));
	
		Label label = new Label(myComposite, SWT.NONE);
		label.setText("We could create some kind of information to eMine here!");
		return myComposite;

	}


	public boolean performOk() {

		return super.performOk();
	
	}

}
