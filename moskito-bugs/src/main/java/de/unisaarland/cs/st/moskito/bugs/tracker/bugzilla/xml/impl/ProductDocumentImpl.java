/*
 * An XML document type.
 * Localname: product
 * Namespace: 
 * Java type: ProductDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.ProductDocument;

/**
 * A document containing one product(@) element.
 *
 * This is a complex type.
 */
public class ProductDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements ProductDocument
{
    private static final long serialVersionUID = 1L;
    
    public ProductDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PRODUCT$0 = 
        new javax.xml.namespace.QName("", "product");
    
    
    /**
     * Gets the "product" element
     */
    public java.lang.String getProduct()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PRODUCT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "product" element
     */
    public org.apache.xmlbeans.XmlString xgetProduct()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PRODUCT$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "product" element
     */
    public void setProduct(java.lang.String product)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PRODUCT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PRODUCT$0);
            }
            target.setStringValue(product);
        }
    }
    
    /**
     * Sets (as xml) the "product" element
     */
    public void xsetProduct(org.apache.xmlbeans.XmlString product)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PRODUCT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PRODUCT$0);
            }
            target.set(product);
        }
    }
}
