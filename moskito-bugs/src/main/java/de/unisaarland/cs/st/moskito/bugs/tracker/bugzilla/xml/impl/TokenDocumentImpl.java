/*
 * An XML document type.
 * Localname: token
 * Namespace: 
 * Java type: TokenDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.TokenDocument;

/**
 * A document containing one token(@) element.
 *
 * This is a complex type.
 */
public class TokenDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements TokenDocument
{
    private static final long serialVersionUID = 1L;
    
    public TokenDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName TOKEN$0 = 
        new javax.xml.namespace.QName("", "token");
    
    
    /**
     * Gets the "token" element
     */
    public java.lang.String getToken()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOKEN$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "token" element
     */
    public org.apache.xmlbeans.XmlString xgetToken()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOKEN$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "token" element
     */
    public void setToken(java.lang.String token)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOKEN$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TOKEN$0);
            }
            target.setStringValue(token);
        }
    }
    
    /**
     * Sets (as xml) the "token" element
     */
    public void xsetToken(org.apache.xmlbeans.XmlString token)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOKEN$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOKEN$0);
            }
            target.set(token);
        }
    }
}
