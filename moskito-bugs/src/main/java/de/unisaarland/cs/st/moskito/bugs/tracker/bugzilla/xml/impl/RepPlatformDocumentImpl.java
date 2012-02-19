/*
 * An XML document type.
 * Localname: rep_platform
 * Namespace: 
 * Java type: RepPlatformDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.RepPlatformDocument;

/**
 * A document containing one rep_platform(@) element.
 *
 * This is a complex type.
 */
public class RepPlatformDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements RepPlatformDocument
{
    private static final long serialVersionUID = 1L;
    
    public RepPlatformDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName REPPLATFORM$0 = 
        new javax.xml.namespace.QName("", "rep_platform");
    
    
    /**
     * Gets the "rep_platform" element
     */
    public java.lang.String getRepPlatform()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REPPLATFORM$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "rep_platform" element
     */
    public org.apache.xmlbeans.XmlString xgetRepPlatform()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REPPLATFORM$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "rep_platform" element
     */
    public void setRepPlatform(java.lang.String repPlatform)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REPPLATFORM$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(REPPLATFORM$0);
            }
            target.setStringValue(repPlatform);
        }
    }
    
    /**
     * Sets (as xml) the "rep_platform" element
     */
    public void xsetRepPlatform(org.apache.xmlbeans.XmlString repPlatform)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REPPLATFORM$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REPPLATFORM$0);
            }
            target.set(repPlatform);
        }
    }
}
