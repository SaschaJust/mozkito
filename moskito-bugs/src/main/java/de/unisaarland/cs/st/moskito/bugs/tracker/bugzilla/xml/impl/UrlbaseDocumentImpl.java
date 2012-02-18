/*
 * An XML document type.
 * Localname: urlbase
 * Namespace: 
 * Java type: UrlbaseDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.UrlbaseDocument;

/**
 * A document containing one urlbase(@) element.
 *
 * This is a complex type.
 */
public class UrlbaseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements UrlbaseDocument
{
    private static final long serialVersionUID = 1L;
    
    public UrlbaseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName URLBASE$0 = 
        new javax.xml.namespace.QName("", "urlbase");
    
    
    /**
     * Gets the "urlbase" element
     */
    public java.lang.String getUrlbase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(URLBASE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "urlbase" element
     */
    public org.apache.xmlbeans.XmlString xgetUrlbase()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(URLBASE$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "urlbase" element
     */
    public void setUrlbase(java.lang.String urlbase)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(URLBASE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(URLBASE$0);
            }
            target.setStringValue(urlbase);
        }
    }
    
    /**
     * Sets (as xml) the "urlbase" element
     */
    public void xsetUrlbase(org.apache.xmlbeans.XmlString urlbase)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(URLBASE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(URLBASE$0);
            }
            target.set(urlbase);
        }
    }
}
