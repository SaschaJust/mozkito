/*
 * An XML document type.
 * Localname: everconfirmed
 * Namespace: 
 * Java type: EverconfirmedDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.EverconfirmedDocument;

/**
 * A document containing one everconfirmed(@) element.
 *
 * This is a complex type.
 */
public class EverconfirmedDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements EverconfirmedDocument
{
    private static final long serialVersionUID = 1L;
    
    public EverconfirmedDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName EVERCONFIRMED$0 = 
        new javax.xml.namespace.QName("", "everconfirmed");
    
    
    /**
     * Gets the "everconfirmed" element
     */
    public java.lang.String getEverconfirmed()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EVERCONFIRMED$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "everconfirmed" element
     */
    public org.apache.xmlbeans.XmlString xgetEverconfirmed()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EVERCONFIRMED$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "everconfirmed" element
     */
    public void setEverconfirmed(java.lang.String everconfirmed)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EVERCONFIRMED$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(EVERCONFIRMED$0);
            }
            target.setStringValue(everconfirmed);
        }
    }
    
    /**
     * Sets (as xml) the "everconfirmed" element
     */
    public void xsetEverconfirmed(org.apache.xmlbeans.XmlString everconfirmed)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EVERCONFIRMED$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(EVERCONFIRMED$0);
            }
            target.set(everconfirmed);
        }
    }
}
