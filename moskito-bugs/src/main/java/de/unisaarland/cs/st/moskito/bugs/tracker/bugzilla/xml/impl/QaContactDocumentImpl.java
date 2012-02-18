/*
 * An XML document type.
 * Localname: qa_contact
 * Namespace: 
 * Java type: QaContactDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.QaContactDocument;

/**
 * A document containing one qa_contact(@) element.
 *
 * This is a complex type.
 */
public class QaContactDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements QaContactDocument
{
    private static final long serialVersionUID = 1L;
    
    public QaContactDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QACONTACT$0 = 
        new javax.xml.namespace.QName("", "qa_contact");
    
    
    /**
     * Gets the "qa_contact" element
     */
    public QaContactDocument.QaContact getQaContact()
    {
        synchronized (monitor())
        {
            check_orphaned();
            QaContactDocument.QaContact target = null;
            target = (QaContactDocument.QaContact)get_store().find_element_user(QACONTACT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "qa_contact" element
     */
    public void setQaContact(QaContactDocument.QaContact qaContact)
    {
        generatedSetterHelperImpl(qaContact, QACONTACT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "qa_contact" element
     */
    public QaContactDocument.QaContact addNewQaContact()
    {
        synchronized (monitor())
        {
            check_orphaned();
            QaContactDocument.QaContact target = null;
            target = (QaContactDocument.QaContact)get_store().add_element_user(QACONTACT$0);
            return target;
        }
    }
    /**
     * An XML qa_contact(@).
     *
     * This is a complex type.
     */
    public static class QaContactImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements QaContactDocument.QaContact
    {
        private static final long serialVersionUID = 1L;
        
        public QaContactImpl(org.apache.xmlbeans.SchemaType sType)
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
