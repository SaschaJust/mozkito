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

import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * This class is serves as an extension for a normal Text field. <br>
 * It uses a standard layout and provides a field for the Persistent Key (storing reason).
 *  
 * @author  Andreas Rau
 * @version 02/2011 1.0
 */
public class PropertyTextField {
	

	String VALUE_PROP_KEY;
	String repoName;
	Text textField;

	public PropertyTextField(String repoName, String key, String tooltip,
			Composite parent, int style, GridData layoutData) {
		this.VALUE_PROP_KEY = key;
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
