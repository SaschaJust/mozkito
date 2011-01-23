package org.se2010.emine.properties;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

@SuppressWarnings("unused")
public class PropertyPage extends org.eclipse.ui.dialogs.PropertyPage implements
IWorkbenchPropertyPage {

	
	String user = "testi";

	private Text textField;
	public static QualifiedName  AUTHOR_PROP_KEY = new QualifiedName("User", "User");
	private static String USER_ID = "RMP.user";
	private static TextPropertyDescriptor USER_PROP_DESC = new TextPropertyDescriptor(USER_ID, "user");
	private static final IPropertyDescriptor[] DESCRIPTORS = {USER_PROP_DESC};

	public PropertyPage() {
		super();
	}

	protected Control createContents(Composite parent)
	{
		Composite myComposite = new Composite(parent, SWT.NONE);

		GridLayout mylayout = new GridLayout();
		mylayout.marginHeight = 1;
		mylayout.marginWidth = 1;
		myComposite.setLayout(mylayout);
		Label mylabel = new Label(myComposite, SWT.NONE);
		mylabel.setLayoutData(new GridData());
		mylabel.setText("User");
		textField = new Text(myComposite, SWT.BORDER);
		textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textField.setText(getUser());
		return myComposite;


	}

	protected String getUser() {
		// TODO Auto-generated method stub
		return "TEST";
	}


	public static IPropertyDescriptor[] getDescriptors() {
		return DESCRIPTORS;
	}

	/**
	 * some kind of dummy function simply to test, if we can set author
	 * not persistent in any case
	 * no writeback to workspace!
	 * 
	 * other implementation below
	 * @param string
	 */
	protected void setUser(String string) {
		this.user = string;

	}


//	protected void setAuthor(String author) {
//		IResource resource =
//			((TreeObject) getElement()).getResouce();
//		String value = author;
//		if (value.equals(""))
//			value = null;
//		try {
//			resource.setPersistentProperty(
//					AUTHOR_PROP_KEY,
//					value);
//		}
//		catch (CoreException e) {
//		}
//	}


	public boolean performOk(){
		setUser(textField.getText());
		return super.performOk();
	}




}
