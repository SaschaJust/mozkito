package org.mozkito.issues.adaptive;


import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;


public class ODMNamespaceContext implements NamespaceContext {
	 
    public String getNamespaceURI(String prefix) {
        if (prefix == null)
            throw new NullPointerException("Null prefix");
        else if ("ns".equals(prefix))
            return "http://www.w3.org/1999/xhtml";
        return XMLConstants.NULL_NS_URI;
    }

	@Override
	public String getPrefix(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator getPrefixes(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
