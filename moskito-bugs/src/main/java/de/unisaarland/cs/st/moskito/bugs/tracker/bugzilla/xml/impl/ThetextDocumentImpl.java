/*
 * An XML document type.
 * Localname: thetext
 * Namespace: 
 * Java type: ThetextDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.ThetextDocument;

/**
 * A document containing one thetext(@) element.
 *
 * This is a complex type.
 */
public class ThetextDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements ThetextDocument
{
    private static final long serialVersionUID = 1L;
    
    public ThetextDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName THETEXT$0 = 
        new javax.xml.namespace.QName("", "thetext");
    
    
    /**
     * Gets the "thetext" element
     */
    public java.lang.String getThetext()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(THETEXT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "thetext" element
     */
    public org.apache.xmlbeans.XmlString xgetThetext()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(THETEXT$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "thetext" element
     */
    public void setThetext(java.lang.String thetext)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(THETEXT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(THETEXT$0);
            }
            target.setStringValue(thetext);
        }
    }
    
    /**
     * Sets (as xml) the "thetext" element
     */
    public void xsetThetext(org.apache.xmlbeans.XmlString thetext)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(THETEXT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(THETEXT$0);
            }
            target.set(thetext);
        }
    }
}
