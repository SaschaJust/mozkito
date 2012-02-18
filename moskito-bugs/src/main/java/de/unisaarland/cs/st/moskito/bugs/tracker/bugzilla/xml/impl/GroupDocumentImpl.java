/*
 * An XML document type.
 * Localname: group
 * Namespace: 
 * Java type: GroupDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;
/**
 * A document containing one group(@) element.
 *
 * This is a complex type.
 */
public class GroupDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements GroupDocument
{
    private static final long serialVersionUID = 1L;
    
    public GroupDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GROUP$0 = 
        new javax.xml.namespace.QName("", "group");
    
    
    /**
     * Gets the "group" element
     */
    public GroupDocument.Group getGroup()
    {
        synchronized (monitor())
        {
            check_orphaned();
            GroupDocument.Group target = null;
            target = (GroupDocument.Group)get_store().find_element_user(GROUP$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "group" element
     */
    public void setGroup(GroupDocument.Group group)
    {
        generatedSetterHelperImpl(group, GROUP$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "group" element
     */
    public GroupDocument.Group addNewGroup()
    {
        synchronized (monitor())
        {
            check_orphaned();
            GroupDocument.Group target = null;
            target = (GroupDocument.Group)get_store().add_element_user(GROUP$0);
            return target;
        }
    }
    /**
     * An XML group(@).
     *
     * This is a complex type.
     */
    public static class GroupImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements GroupDocument.Group
    {
        private static final long serialVersionUID = 1L;
        
        public GroupImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName ID$0 = 
            new javax.xml.namespace.QName("", "id");
        
        
        /**
         * Gets the "id" attribute
         */
        public org.apache.xmlbeans.XmlAnySimpleType getId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlAnySimpleType target = null;
                target = (org.apache.xmlbeans.XmlAnySimpleType)get_store().find_attribute_user(ID$0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "id" attribute
         */
        public void setId(org.apache.xmlbeans.XmlAnySimpleType id)
        {
            generatedSetterHelperImpl(id, ID$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "id" attribute
         */
        public org.apache.xmlbeans.XmlAnySimpleType addNewId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlAnySimpleType target = null;
                target = (org.apache.xmlbeans.XmlAnySimpleType)get_store().add_attribute_user(ID$0);
                return target;
            }
        }
    }
}
