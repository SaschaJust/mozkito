/*
 * An XML document type.
 * Localname: assigned_to
 * Namespace: 
 * Java type: AssignedToDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.AssignedToDocument;

/**
 * A document containing one assigned_to(@) element.
 *
 * This is a complex type.
 */
public class AssignedToDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements AssignedToDocument
{
    private static final long serialVersionUID = 1L;
    
    public AssignedToDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ASSIGNEDTO$0 = 
        new javax.xml.namespace.QName("", "assigned_to");
    
    
    /**
     * Gets the "assigned_to" element
     */
    public AssignedToDocument.AssignedTo getAssignedTo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            AssignedToDocument.AssignedTo target = null;
            target = (AssignedToDocument.AssignedTo)get_store().find_element_user(ASSIGNEDTO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "assigned_to" element
     */
    public void setAssignedTo(AssignedToDocument.AssignedTo assignedTo)
    {
        generatedSetterHelperImpl(assignedTo, ASSIGNEDTO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "assigned_to" element
     */
    public AssignedToDocument.AssignedTo addNewAssignedTo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            AssignedToDocument.AssignedTo target = null;
            target = (AssignedToDocument.AssignedTo)get_store().add_element_user(ASSIGNEDTO$0);
            return target;
        }
    }
    /**
     * An XML assigned_to(@).
     *
     * This is a complex type.
     */
    public static class AssignedToImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements AssignedToDocument.AssignedTo
    {
        private static final long serialVersionUID = 1L;
        
        public AssignedToImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName NAME$0 = 
            new javax.xml.namespace.QName("", "name");
        
        
        /**
         * Gets the "name" attribute
         */
        public org.apache.xmlbeans.XmlAnySimpleType getName()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlAnySimpleType target = null;
                target = (org.apache.xmlbeans.XmlAnySimpleType)get_store().find_attribute_user(NAME$0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "name" attribute
         */
        public void setName(org.apache.xmlbeans.XmlAnySimpleType name)
        {
            generatedSetterHelperImpl(name, NAME$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "name" attribute
         */
        public org.apache.xmlbeans.XmlAnySimpleType addNewName()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlAnySimpleType target = null;
                target = (org.apache.xmlbeans.XmlAnySimpleType)get_store().add_attribute_user(NAME$0);
                return target;
            }
        }
    }
}
