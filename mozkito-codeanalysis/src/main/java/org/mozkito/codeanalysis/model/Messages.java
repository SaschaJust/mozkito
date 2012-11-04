/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.mozkito.codeanalysis.model;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kim Herzig <herzig@mozkito.org>
 *
 */
public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "org.mozkito.ppa.model.messages"; //$NON-NLS-1$
	public static String        JavaMethodDefinition_fullQualifiedName_extract_error;
	public static String        JavaMethodDefinition_methodName_check;
	public static String        JavaMethodDefinition_parentName_check;
	public static String        JavaMethodDefinition_unrecognized_root_element;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages() {
	}
}