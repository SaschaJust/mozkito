/*
 * An XML document type.
 * Localname: flag
 * Namespace: 
 * Java type: FlagDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.FlagDocument;

/**
 * A document containing one flag(@) element.
 *
 * This is a complex type.
 */
public class FlagDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements FlagDocument
{
    private static final long serialVersionUID = 1L;
    
    public FlagDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName FLAG$0 = 
        new javax.xml.namespace.QName("", "flag");
    
    
    /**
     * Gets the "flag" element
     */
    public FlagDocument.Flag getFlag()
    {
        synchronized (monitor())
        {
            check_orphaned();
            FlagDocument.Flag target = null;
            target = (FlagDocument.Flag)get_store().find_element_user(FLAG$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "flag" element
     */
    public void setFlag(FlagDocument.Flag flag)
    {
        generatedSetterHelperImpl(flag, FLAG$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "flag" element
     */
    public FlagDocument.Flag addNewFlag()
    {
        synchronized (monitor())
        {
            check_orphaned();
            FlagDocument.Flag target = null;
            target = (FlagDocument.Flag)get_store().add_element_user(FLAG$0);
            return target;
        }
    }
    /**
     * An XML flag(@).
     *
     * This is a complex type.
     */
    public static class FlagImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements FlagDocument.Flag
    {
        private static final long serialVersionUID = 1L;
        
        public FlagImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName NAME$0 = 
            new javax.xml.namespace.QName("", "name");
        private static final javax.xml.namespace.QName ID$2 = 
            new javax.xml.namespace.QName("", "id");
        private static final javax.xml.namespace.QName TYPEID$4 = 
            new javax.xml.namespace.QName("", "type_id");
        private static final javax.xml.namespace.QName STATUS$6 = 
            new javax.xml.namespace.QName("", "status");
        private static final javax.xml.namespace.QName SETTER$8 = 
            new javax.xml.namespace.QName("", "setter");
        private static final javax.xml.namespace.QName REQUESTEE$10 = 
            new javax.xml.namespace.QName("", "requestee");
        
        
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
        
        /**
         * Gets the "id" attribute
         */
        public org.apache.xmlbeans.XmlAnySimpleType getId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlAnySimpleType target = null;
                target = (org.apache.xmlbeans.XmlAnySimpleType)get_store().find_attribute_user(ID$2);
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
            generatedSetterHelperImpl(id, ID$2, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
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
                target = (org.apache.xmlbeans.XmlAnySimpleType)get_store().add_attribute_user(ID$2);
                return target;
            }
        }
        
        /**
         * Gets the "type_id" attribute
         */
        public org.apache.xmlbeans.XmlAnySimpleType getTypeId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlAnySimpleType target = null;
                target = (org.apache.xmlbeans.XmlAnySimpleType)get_store().find_attribute_user(TYPEID$4);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "type_id" attribute
         */
        public void setTypeId(org.apache.xmlbeans.XmlAnySimpleType typeId)
        {
            generatedSetterHelperImpl(typeId, TYPEID$4, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "type_id" attribute
         */
        public org.apache.xmlbeans.XmlAnySimpleType addNewTypeId()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlAnySimpleType target = null;
                target = (org.apache.xmlbeans.XmlAnySimpleType)get_store().add_attribute_user(TYPEID$4);
                return target;
            }
        }
        
        /**
         * Gets the "status" attribute
         */
        public org.apache.xmlbeans.XmlAnySimpleType getStatus()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlAnySimpleType target = null;
                target = (org.apache.xmlbeans.XmlAnySimpleType)get_store().find_attribute_user(STATUS$6);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "status" attribute
         */
        public void setStatus(org.apache.xmlbeans.XmlAnySimpleType status)
        {
            generatedSetterHelperImpl(status, STATUS$6, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "status" attribute
         */
        public org.apache.xmlbeans.XmlAnySimpleType addNewStatus()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlAnySimpleType target = null;
                target = (org.apache.xmlbeans.XmlAnySimpleType)get_store().add_attribute_user(STATUS$6);
                return target;
            }
        }
        
        /**
         * Gets the "setter" attribute
         */
        public org.apache.xmlbeans.XmlAnySimpleType getSetter()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlAnySimpleType target = null;
                target = (org.apache.xmlbeans.XmlAnySimpleType)get_store().find_attribute_user(SETTER$8);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "setter" attribute
         */
        public void setSetter(org.apache.xmlbeans.XmlAnySimpleType setter)
        {
            generatedSetterHelperImpl(setter, SETTER$8, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "setter" attribute
         */
        public org.apache.xmlbeans.XmlAnySimpleType addNewSetter()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlAnySimpleType target = null;
                target = (org.apache.xmlbeans.XmlAnySimpleType)get_store().add_attribute_user(SETTER$8);
                return target;
            }
        }
        
        /**
         * Gets the "requestee" attribute
         */
        public org.apache.xmlbeans.XmlAnySimpleType getRequestee()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlAnySimpleType target = null;
                target = (org.apache.xmlbeans.XmlAnySimpleType)get_store().find_attribute_user(REQUESTEE$10);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * True if has "requestee" attribute
         */
        public boolean isSetRequestee()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().find_attribute_user(REQUESTEE$10) != null;
            }
        }
        
        /**
         * Sets the "requestee" attribute
         */
        public void setRequestee(org.apache.xmlbeans.XmlAnySimpleType requestee)
        {
            generatedSetterHelperImpl(requestee, REQUESTEE$10, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
        }
        
        /**
         * Appends and returns a new empty "requestee" attribute
         */
        public org.apache.xmlbeans.XmlAnySimpleType addNewRequestee()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlAnySimpleType target = null;
                target = (org.apache.xmlbeans.XmlAnySimpleType)get_store().add_attribute_user(REQUESTEE$10);
                return target;
            }
        }
        
        /**
         * Unsets the "requestee" attribute
         */
        public void unsetRequestee()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_attribute(REQUESTEE$10);
            }
        }
    }
}
