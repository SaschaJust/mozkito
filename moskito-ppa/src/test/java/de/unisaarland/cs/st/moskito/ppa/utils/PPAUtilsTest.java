package de.unisaarland.cs.st.moskito.ppa.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.moskito.ppa.utils.PPAUtils;


public class PPAUtilsTest {
	
	@Test
	public void getDefinitionNamesForCallNametest() {
		String orgName = "src.com.google.gwt.sample.kitchensink.client.Popups.MyPopup.<init>()";
		
		@SuppressWarnings("deprecation") JavaMethodCall call = new JavaMethodCall();
		call.setElementType(JavaMethodCall.class.getCanonicalName());
		call.setFullQualifiedName(orgName);
		
		String[] possibleNames = PPAUtils.getDefinitionNamesForCallName(call);
		assertEquals("Too less or too many names produced!", 4, possibleNames.length);
		
		assertEquals("com.google.gwt.sample.kitchensink.client.Popups.MyPopup.MyPopup()", possibleNames[0]);
		assertEquals("com.google.gwt.sample.kitchensink.client.MyPopup.MyPopup()", possibleNames[1]);
		
	}
	
}
