/*
 * An XML document type.
 * Localname: op_sys
 * Namespace: 
 * Java type: OpSysDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.OpSysDocument;

/**
 * A document containing one op_sys(@) element.
 *
 * This is a complex type.
 */
public class OpSysDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements OpSysDocument
{
    private static final long serialVersionUID = 1L;
    
    public OpSysDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName OPSYS$0 = 
        new javax.xml.namespace.QName("", "op_sys");
    
    
    /**
     * Gets the "op_sys" element
     */
    public java.lang.String getOpSys()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPSYS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "op_sys" element
     */
    public org.apache.xmlbeans.XmlString xgetOpSys()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPSYS$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "op_sys" element
     */
    public void setOpSys(java.lang.String opSys)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(OPSYS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(OPSYS$0);
            }
            target.setStringValue(opSys);
        }
    }
    
    /**
     * Sets (as xml) the "op_sys" element
     */
    public void xsetOpSys(org.apache.xmlbeans.XmlString opSys)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(OPSYS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(OPSYS$0);
            }
            target.set(opSys);
        }
    }
}
