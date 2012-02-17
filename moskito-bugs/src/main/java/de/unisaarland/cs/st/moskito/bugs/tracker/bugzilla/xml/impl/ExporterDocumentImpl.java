/*
 * An XML document type.
 * Localname: exporter
 * Namespace: 
 * Java type: ExporterDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one exporter(@) element.
 *
 * This is a complex type.
 */
public class ExporterDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements ExporterDocument
{
    private static final long serialVersionUID = 1L;
    
    public ExporterDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName EXPORTER$0 = 
        new javax.xml.namespace.QName("", "exporter");
    
    
    /**
     * Gets the "exporter" element
     */
    public java.lang.String getExporter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EXPORTER$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "exporter" element
     */
    public org.apache.xmlbeans.XmlString xgetExporter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EXPORTER$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "exporter" element
     */
    public void setExporter(java.lang.String exporter)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EXPORTER$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(EXPORTER$0);
            }
            target.setStringValue(exporter);
        }
    }
    
    /**
     * Sets (as xml) the "exporter" element
     */
    public void xsetExporter(org.apache.xmlbeans.XmlString exporter)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EXPORTER$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(EXPORTER$0);
            }
            target.set(exporter);
        }
    }
}
