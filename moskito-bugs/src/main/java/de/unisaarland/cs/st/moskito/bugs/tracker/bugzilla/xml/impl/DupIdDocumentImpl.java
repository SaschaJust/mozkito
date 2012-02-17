/*
 * An XML document type.
 * Localname: dup_id
 * Namespace: 
 * Java type: DupIdDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one dup_id(@) element.
 *
 * This is a complex type.
 */
public class DupIdDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements DupIdDocument
{
    private static final long serialVersionUID = 1L;
    
    public DupIdDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DUPID$0 = 
        new javax.xml.namespace.QName("", "dup_id");
    
    
    /**
     * Gets the "dup_id" element
     */
    public java.lang.String getDupId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DUPID$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "dup_id" element
     */
    public org.apache.xmlbeans.XmlString xgetDupId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DUPID$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "dup_id" element
     */
    public void setDupId(java.lang.String dupId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DUPID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DUPID$0);
            }
            target.setStringValue(dupId);
        }
    }
    
    /**
     * Sets (as xml) the "dup_id" element
     */
    public void xsetDupId(org.apache.xmlbeans.XmlString dupId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DUPID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DUPID$0);
            }
            target.set(dupId);
        }
    }
}
