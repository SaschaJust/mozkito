/*
 * An XML document type.
 * Localname: see_also
 * Namespace: 
 * Java type: SeeAlsoDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.SeeAlsoDocument;

/**
 * A document containing one see_also(@) element.
 *
 * This is a complex type.
 */
public class SeeAlsoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements SeeAlsoDocument
{
    private static final long serialVersionUID = 1L;
    
    public SeeAlsoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SEEALSO$0 = 
        new javax.xml.namespace.QName("", "see_also");
    
    
    /**
     * Gets the "see_also" element
     */
    public java.lang.String getSeeAlso()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SEEALSO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "see_also" element
     */
    public org.apache.xmlbeans.XmlString xgetSeeAlso()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SEEALSO$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "see_also" element
     */
    public void setSeeAlso(java.lang.String seeAlso)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SEEALSO$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SEEALSO$0);
            }
            target.setStringValue(seeAlso);
        }
    }
    
    /**
     * Sets (as xml) the "see_also" element
     */
    public void xsetSeeAlso(org.apache.xmlbeans.XmlString seeAlso)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SEEALSO$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SEEALSO$0);
            }
            target.set(seeAlso);
        }
    }
}
