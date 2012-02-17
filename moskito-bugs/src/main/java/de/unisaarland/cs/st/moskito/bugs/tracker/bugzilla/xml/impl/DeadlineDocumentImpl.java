/*
 * An XML document type.
 * Localname: deadline
 * Namespace: 
 * Java type: DeadlineDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one deadline(@) element.
 *
 * This is a complex type.
 */
public class DeadlineDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements DeadlineDocument
{
    private static final long serialVersionUID = 1L;
    
    public DeadlineDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DEADLINE$0 = 
        new javax.xml.namespace.QName("", "deadline");
    
    
    /**
     * Gets the "deadline" element
     */
    public java.lang.String getDeadline()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DEADLINE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "deadline" element
     */
    public org.apache.xmlbeans.XmlString xgetDeadline()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DEADLINE$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "deadline" element
     */
    public void setDeadline(java.lang.String deadline)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DEADLINE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DEADLINE$0);
            }
            target.setStringValue(deadline);
        }
    }
    
    /**
     * Sets (as xml) the "deadline" element
     */
    public void xsetDeadline(org.apache.xmlbeans.XmlString deadline)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DEADLINE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DEADLINE$0);
            }
            target.set(deadline);
        }
    }
}
