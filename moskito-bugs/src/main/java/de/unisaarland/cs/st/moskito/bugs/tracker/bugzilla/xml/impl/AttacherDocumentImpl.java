/*
 * An XML document type.
 * Localname: attacher
 * Namespace: 
 * Java type: AttacherDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.AttacherDocument;

/**
 * A document containing one attacher(@) element.
 *
 * This is a complex type.
 */
public class AttacherDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements AttacherDocument
{
    private static final long serialVersionUID = 1L;
    
    public AttacherDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ATTACHER$0 = 
        new javax.xml.namespace.QName("", "attacher");
    
    
    /**
     * Gets the "attacher" element
     */
    public java.lang.String getAttacher()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ATTACHER$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "attacher" element
     */
    public org.apache.xmlbeans.XmlString xgetAttacher()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ATTACHER$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "attacher" element
     */
    public void setAttacher(java.lang.String attacher)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ATTACHER$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ATTACHER$0);
            }
            target.setStringValue(attacher);
        }
    }
    
    /**
     * Sets (as xml) the "attacher" element
     */
    public void xsetAttacher(org.apache.xmlbeans.XmlString attacher)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ATTACHER$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ATTACHER$0);
            }
            target.set(attacher);
        }
    }
}
