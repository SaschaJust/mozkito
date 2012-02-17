/*
 * An XML document type.
 * Localname: estimated_time
 * Namespace: 
 * Java type: EstimatedTimeDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one estimated_time(@) element.
 *
 * This is a complex type.
 */
public class EstimatedTimeDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements EstimatedTimeDocument
{
    private static final long serialVersionUID = 1L;
    
    public EstimatedTimeDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ESTIMATEDTIME$0 = 
        new javax.xml.namespace.QName("", "estimated_time");
    
    
    /**
     * Gets the "estimated_time" element
     */
    public java.lang.String getEstimatedTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ESTIMATEDTIME$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "estimated_time" element
     */
    public org.apache.xmlbeans.XmlString xgetEstimatedTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ESTIMATEDTIME$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "estimated_time" element
     */
    public void setEstimatedTime(java.lang.String estimatedTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ESTIMATEDTIME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ESTIMATEDTIME$0);
            }
            target.setStringValue(estimatedTime);
        }
    }
    
    /**
     * Sets (as xml) the "estimated_time" element
     */
    public void xsetEstimatedTime(org.apache.xmlbeans.XmlString estimatedTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ESTIMATEDTIME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ESTIMATEDTIME$0);
            }
            target.set(estimatedTime);
        }
    }
}
