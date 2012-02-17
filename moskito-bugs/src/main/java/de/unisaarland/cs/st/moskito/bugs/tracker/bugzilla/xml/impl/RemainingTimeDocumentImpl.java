/*
 * An XML document type.
 * Localname: remaining_time
 * Namespace: 
 * Java type: RemainingTimeDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one remaining_time(@) element.
 *
 * This is a complex type.
 */
public class RemainingTimeDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements RemainingTimeDocument
{
    private static final long serialVersionUID = 1L;
    
    public RemainingTimeDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName REMAININGTIME$0 = 
        new javax.xml.namespace.QName("", "remaining_time");
    
    
    /**
     * Gets the "remaining_time" element
     */
    public java.lang.String getRemainingTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REMAININGTIME$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "remaining_time" element
     */
    public org.apache.xmlbeans.XmlString xgetRemainingTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REMAININGTIME$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "remaining_time" element
     */
    public void setRemainingTime(java.lang.String remainingTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REMAININGTIME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(REMAININGTIME$0);
            }
            target.setStringValue(remainingTime);
        }
    }
    
    /**
     * Sets (as xml) the "remaining_time" element
     */
    public void xsetRemainingTime(org.apache.xmlbeans.XmlString remainingTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REMAININGTIME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REMAININGTIME$0);
            }
            target.set(remainingTime);
        }
    }
}
