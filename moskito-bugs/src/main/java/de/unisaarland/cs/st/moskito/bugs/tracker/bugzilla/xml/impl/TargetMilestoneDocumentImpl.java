/*
 * An XML document type.
 * Localname: target_milestone
 * Namespace: 
 * Java type: TargetMilestoneDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.TargetMilestoneDocument;

/**
 * A document containing one target_milestone(@) element.
 *
 * This is a complex type.
 */
public class TargetMilestoneDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements TargetMilestoneDocument
{
    private static final long serialVersionUID = 1L;
    
    public TargetMilestoneDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName TARGETMILESTONE$0 = 
        new javax.xml.namespace.QName("", "target_milestone");
    
    
    /**
     * Gets the "target_milestone" element
     */
    public java.lang.String getTargetMilestone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TARGETMILESTONE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "target_milestone" element
     */
    public org.apache.xmlbeans.XmlString xgetTargetMilestone()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TARGETMILESTONE$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "target_milestone" element
     */
    public void setTargetMilestone(java.lang.String targetMilestone)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TARGETMILESTONE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TARGETMILESTONE$0);
            }
            target.setStringValue(targetMilestone);
        }
    }
    
    /**
     * Sets (as xml) the "target_milestone" element
     */
    public void xsetTargetMilestone(org.apache.xmlbeans.XmlString targetMilestone)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TARGETMILESTONE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TARGETMILESTONE$0);
            }
            target.set(targetMilestone);
        }
    }
}
