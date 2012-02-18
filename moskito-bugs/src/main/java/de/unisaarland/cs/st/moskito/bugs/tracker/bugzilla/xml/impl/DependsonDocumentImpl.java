/*
 * An XML document type.
 * Localname: dependson
 * Namespace: 
 * Java type: DependsonDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.DependsonDocument;

/**
 * A document containing one dependson(@) element.
 *
 * This is a complex type.
 */
public class DependsonDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements DependsonDocument
{
    private static final long serialVersionUID = 1L;
    
    public DependsonDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DEPENDSON$0 = 
        new javax.xml.namespace.QName("", "dependson");
    
    
    /**
     * Gets the "dependson" element
     */
    public java.lang.String getDependson()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DEPENDSON$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "dependson" element
     */
    public org.apache.xmlbeans.XmlString xgetDependson()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DEPENDSON$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "dependson" element
     */
    public void setDependson(java.lang.String dependson)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DEPENDSON$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DEPENDSON$0);
            }
            target.setStringValue(dependson);
        }
    }
    
    /**
     * Sets (as xml) the "dependson" element
     */
    public void xsetDependson(org.apache.xmlbeans.XmlString dependson)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DEPENDSON$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DEPENDSON$0);
            }
            target.set(dependson);
        }
    }
}
