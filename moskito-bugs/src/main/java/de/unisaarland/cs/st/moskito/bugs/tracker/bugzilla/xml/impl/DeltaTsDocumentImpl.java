/*
 * An XML document type.
 * Localname: delta_ts
 * Namespace: 
 * Java type: DeltaTsDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one delta_ts(@) element.
 *
 * This is a complex type.
 */
public class DeltaTsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements DeltaTsDocument
{
    private static final long serialVersionUID = 1L;
    
    public DeltaTsDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DELTATS$0 = 
        new javax.xml.namespace.QName("", "delta_ts");
    
    
    /**
     * Gets the "delta_ts" element
     */
    public java.lang.String getDeltaTs()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DELTATS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "delta_ts" element
     */
    public org.apache.xmlbeans.XmlString xgetDeltaTs()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DELTATS$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "delta_ts" element
     */
    public void setDeltaTs(java.lang.String deltaTs)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DELTATS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DELTATS$0);
            }
            target.setStringValue(deltaTs);
        }
    }
    
    /**
     * Sets (as xml) the "delta_ts" element
     */
    public void xsetDeltaTs(org.apache.xmlbeans.XmlString deltaTs)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DELTATS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DELTATS$0);
            }
            target.set(deltaTs);
        }
    }
}
