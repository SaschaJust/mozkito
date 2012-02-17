/*
 * An XML document type.
 * Localname: reporter
 * Namespace: 
 * Java type: ReporterDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one reporter(@) element.
 *
 * This is a complex type.
 */
public class ReporterDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements ReporterDocument
{
    private static final long serialVersionUID = 1L;
    
    public ReporterDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName REPORTER$0 = 
        new javax.xml.namespace.QName("", "reporter");
    
    
    /**
     * Gets the "reporter" element
     */
    public ReporterDocument.Reporter getReporter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            ReporterDocument.Reporter target = null;
            target = (ReporterDocument.Reporter)get_store().find_element_user(REPORTER$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "reporter" element
     */
    public void setReporter(ReporterDocument.Reporter reporter)
    {
        generatedSetterHelperImpl(reporter, REPORTER$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "reporter" element
     */
    public ReporterDocument.Reporter addNewReporter()
    {
        synchronized (monitor())
        {
            check_orphaned();
            ReporterDocument.Reporter target = null;
            target = (ReporterDocument.Reporter)get_store().add_element_user(REPORTER$0);
            return target;
        }
    }
    /**
     * An XML reporter(@).
     *
     * This is a complex type.
     */
    public static class ReporterImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements ReporterDocument.Reporter
    {
        private static final long serialVersionUID = 1L;
        
        public ReporterImpl(org.apache.xmlbeans.SchemaType sType)
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
