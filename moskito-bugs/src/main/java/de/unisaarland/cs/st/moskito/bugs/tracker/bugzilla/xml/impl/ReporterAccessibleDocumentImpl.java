/*
 * An XML document type.
 * Localname: reporter_accessible
 * Namespace: 
 * Java type: ReporterAccessibleDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.ReporterAccessibleDocument;

/**
 * A document containing one reporter_accessible(@) element.
 *
 * This is a complex type.
 */
public class ReporterAccessibleDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements ReporterAccessibleDocument
{
    private static final long serialVersionUID = 1L;
    
    public ReporterAccessibleDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName REPORTERACCESSIBLE$0 = 
        new javax.xml.namespace.QName("", "reporter_accessible");
    
    
    /**
     * Gets the "reporter_accessible" element
     */
    public java.lang.String getReporterAccessible()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REPORTERACCESSIBLE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "reporter_accessible" element
     */
    public org.apache.xmlbeans.XmlString xgetReporterAccessible()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REPORTERACCESSIBLE$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "reporter_accessible" element
     */
    public void setReporterAccessible(java.lang.String reporterAccessible)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REPORTERACCESSIBLE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(REPORTERACCESSIBLE$0);
            }
            target.setStringValue(reporterAccessible);
        }
    }
    
    /**
     * Sets (as xml) the "reporter_accessible" element
     */
    public void xsetReporterAccessible(org.apache.xmlbeans.XmlString reporterAccessible)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REPORTERACCESSIBLE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REPORTERACCESSIBLE$0);
            }
            target.set(reporterAccessible);
        }
    }
}
