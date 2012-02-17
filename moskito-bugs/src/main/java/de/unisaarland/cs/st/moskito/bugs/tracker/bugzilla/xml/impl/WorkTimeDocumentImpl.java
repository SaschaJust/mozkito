/*
 * An XML document type.
 * Localname: work_time
 * Namespace: 
 * Java type: WorkTimeDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one work_time(@) element.
 *
 * This is a complex type.
 */
public class WorkTimeDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements WorkTimeDocument
{
    private static final long serialVersionUID = 1L;
    
    public WorkTimeDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName WORKTIME$0 = 
        new javax.xml.namespace.QName("", "work_time");
    
    
    /**
     * Gets the "work_time" element
     */
    public java.lang.String getWorkTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(WORKTIME$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "work_time" element
     */
    public org.apache.xmlbeans.XmlString xgetWorkTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(WORKTIME$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "work_time" element
     */
    public void setWorkTime(java.lang.String workTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(WORKTIME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(WORKTIME$0);
            }
            target.setStringValue(workTime);
        }
    }
    
    /**
     * Sets (as xml) the "work_time" element
     */
    public void xsetWorkTime(org.apache.xmlbeans.XmlString workTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(WORKTIME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(WORKTIME$0);
            }
            target.set(workTime);
        }
    }
}
