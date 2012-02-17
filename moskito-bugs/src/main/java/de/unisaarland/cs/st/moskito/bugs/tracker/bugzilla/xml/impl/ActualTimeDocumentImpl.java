/*
 * An XML document type.
 * Localname: actual_time
 * Namespace: 
 * Java type: ActualTimeDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one actual_time(@) element.
 *
 * This is a complex type.
 */
public class ActualTimeDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements ActualTimeDocument
{
    private static final long serialVersionUID = 1L;
    
    public ActualTimeDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ACTUALTIME$0 = 
        new javax.xml.namespace.QName("", "actual_time");
    
    
    /**
     * Gets the "actual_time" element
     */
    public java.lang.String getActualTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ACTUALTIME$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "actual_time" element
     */
    public org.apache.xmlbeans.XmlString xgetActualTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ACTUALTIME$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "actual_time" element
     */
    public void setActualTime(java.lang.String actualTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ACTUALTIME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ACTUALTIME$0);
            }
            target.setStringValue(actualTime);
        }
    }
    
    /**
     * Sets (as xml) the "actual_time" element
     */
    public void xsetActualTime(org.apache.xmlbeans.XmlString actualTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ACTUALTIME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ACTUALTIME$0);
            }
            target.set(actualTime);
        }
    }
}
