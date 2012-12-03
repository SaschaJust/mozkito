/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.codeanalysis.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.mozkito.codeanalysis.model.JavaMethodCall;

public class PPAUtilsTest {
	
	@Test
	public void getDefinitionNamesForCallNametest() {
		final String orgName = "src.com.google.gwt.sample.kitchensink.client.Popups.MyPopup.<init>()";
		
		@SuppressWarnings ("deprecation")
		final JavaMethodCall call = new JavaMethodCall();
		call.setElementType(JavaMethodCall.class.getCanonicalName());
		call.setFullQualifiedName(orgName);
		
		final String[] possibleNames = PPAUtils.getDefinitionNamesForCallName(call);
		assertEquals("Too less or too many names produced!", 4, possibleNames.length);
		
		assertEquals("com.google.gwt.sample.kitchensink.client.Popups.MyPopup.MyPopup()", possibleNames[0]);
		assertEquals("com.google.gwt.sample.kitchensink.client.MyPopup.MyPopup()", possibleNames[1]);
		
	}
	
}
