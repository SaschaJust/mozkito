/*
 * An XML document type.
 * Localname: who
 * Namespace: 
 * Java type: WhoDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.WhoDocument;

/**
 * A document containing one who(@) element.
 *
 * This is a complex type.
 */
public class WhoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements WhoDocument
{
    private static final long serialVersionUID = 1L;
    
    public WhoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName WHO$0 = 
        new javax.xml.namespace.QName("", "who");
    
    
    /**
     * Gets the "who" element
     */
    public WhoDocument.Who getWho()
    {
        synchronized (monitor())
        {
            check_orphaned();
            WhoDocument.Who target = null;
            target = (WhoDocument.Who)get_store().find_element_user(WHO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "who" element
     */
    public void setWho(WhoDocument.Who who)
    {
        generatedSetterHelperImpl(who, WHO$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "who" element
     */
    public WhoDocument.Who addNewWho()
    {
        synchronized (monitor())
        {
            check_orphaned();
            WhoDocument.Who target = null;
            target = (WhoDocument.Who)get_store().add_element_user(WHO$0);
            return target;
        }
    }
    /**
     * An XML who(@).
     *
     * This is a complex type.
     */
    public static class WhoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements WhoDocument.Who
    {
        private static final long serialVersionUID = 1L;
        
        public WhoImpl(org.apache.xmlbeans.SchemaType sType)
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
