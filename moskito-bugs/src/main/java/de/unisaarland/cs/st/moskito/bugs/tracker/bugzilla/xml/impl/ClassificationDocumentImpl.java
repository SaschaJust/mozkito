/*
 * An XML document type.
 * Localname: classification
 * Namespace: 
 * Java type: ClassificationDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one classification(@) element.
 *
 * This is a complex type.
 */
public class ClassificationDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements ClassificationDocument
{
    private static final long serialVersionUID = 1L;
    
    public ClassificationDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CLASSIFICATION$0 = 
        new javax.xml.namespace.QName("", "classification");
    
    
    /**
     * Gets the "classification" element
     */
    public java.lang.String getClassification()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CLASSIFICATION$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "classification" element
     */
    public org.apache.xmlbeans.XmlString xgetClassification()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CLASSIFICATION$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "classification" element
     */
    public void setClassification(java.lang.String classification)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CLASSIFICATION$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CLASSIFICATION$0);
            }
            target.setStringValue(classification);
        }
    }
    
    /**
     * Sets (as xml) the "classification" element
     */
    public void xsetClassification(org.apache.xmlbeans.XmlString classification)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CLASSIFICATION$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CLASSIFICATION$0);
            }
            target.set(classification);
        }
    }
}
