/*
 * An XML document type.
 * Localname: classification_id
 * Namespace: 
 * Java type: ClassificationIdDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.ClassificationIdDocument;

/**
 * A document containing one classification_id(@) element.
 *
 * This is a complex type.
 */
public class ClassificationIdDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements ClassificationIdDocument
{
    private static final long serialVersionUID = 1L;
    
    public ClassificationIdDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CLASSIFICATIONID$0 = 
        new javax.xml.namespace.QName("", "classification_id");
    
    
    /**
     * Gets the "classification_id" element
     */
    public java.lang.String getClassificationId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CLASSIFICATIONID$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "classification_id" element
     */
    public org.apache.xmlbeans.XmlString xgetClassificationId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CLASSIFICATIONID$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "classification_id" element
     */
    public void setClassificationId(java.lang.String classificationId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CLASSIFICATIONID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CLASSIFICATIONID$0);
            }
            target.setStringValue(classificationId);
        }
    }
    
    /**
     * Sets (as xml) the "classification_id" element
     */
    public void xsetClassificationId(org.apache.xmlbeans.XmlString classificationId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CLASSIFICATIONID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CLASSIFICATIONID$0);
            }
            target.set(classificationId);
        }
    }
}
