package org.se2010.emine.properties;

import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class PropertyTextField {

	String VALUE_PROP_KEY;
	String repoName;
	Text textField;

	public PropertyTextField(String repoName, String key, String tooltip,
			Composite parent, int style, GridData layoutData) {
		this.VALUE_PROP_KEY = repoName + key;
		this.repoName = repoName;
		this.textField = new Text(parent, style);
		textField.setLayoutData(layoutData);
		textField.setToolTipText(tooltip);

		textField.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_TAB_NEXT
						|| e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
					e.doit = true;
				}
			}
		});
		
	}

	public String getVALUE_PROP_KEY() {
		return VALUE_PROP_KEY;
	}

	public Text getTextField() {
		return textField;
	}

	public void setTextField(Text textField) {
		this.textField = textField;
	}

	public String getRepoName() {
		return repoName;
	}

	public void setText(String string) {
		this.textField.setText(string);
	}

}
