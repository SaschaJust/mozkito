/*
 * An XML document type.
 * Localname: attachid
 * Namespace: 
 * Java type: AttachidDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one attachid(@) element.
 *
 * This is a complex type.
 */
public class AttachidDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements AttachidDocument
{
    private static final long serialVersionUID = 1L;
    
    public AttachidDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ATTACHID$0 = 
        new javax.xml.namespace.QName("", "attachid");
    
    
    /**
     * Gets the "attachid" element
     */
    public java.lang.String getAttachid()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ATTACHID$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "attachid" element
     */
    public org.apache.xmlbeans.XmlString xgetAttachid()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ATTACHID$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "attachid" element
     */
    public void setAttachid(java.lang.String attachid)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ATTACHID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ATTACHID$0);
            }
            target.setStringValue(attachid);
        }
    }
    
    /**
     * Sets (as xml) the "attachid" element
     */
    public void xsetAttachid(org.apache.xmlbeans.XmlString attachid)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ATTACHID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ATTACHID$0);
            }
            target.set(attachid);
        }
    }
}
