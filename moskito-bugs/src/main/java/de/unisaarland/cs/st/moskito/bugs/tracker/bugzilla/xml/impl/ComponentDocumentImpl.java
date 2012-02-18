/*
 * An XML document type.
 * Localname: component
 * Namespace: 
 * Java type: ComponentDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.ComponentDocument;

/**
 * A document containing one component(@) element.
 *
 * This is a complex type.
 */
public class ComponentDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements ComponentDocument
{
    private static final long serialVersionUID = 1L;
    
    public ComponentDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName COMPONENT$0 = 
        new javax.xml.namespace.QName("", "component");
    
    
    /**
     * Gets the "component" element
     */
    public java.lang.String getComponent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMPONENT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "component" element
     */
    public org.apache.xmlbeans.XmlString xgetComponent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COMPONENT$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "component" element
     */
    public void setComponent(java.lang.String component)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMPONENT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(COMPONENT$0);
            }
            target.setStringValue(component);
        }
    }
    
    /**
     * Sets (as xml) the "component" element
     */
    public void xsetComponent(org.apache.xmlbeans.XmlString component)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COMPONENT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(COMPONENT$0);
            }
            target.set(component);
        }
    }
}
