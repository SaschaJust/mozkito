/*
 * An XML document type.
 * Localname: data
 * Namespace: 
 * Java type: DataDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one data(@) element.
 *
 * This is a complex type.
 */
public class DataDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements DataDocument
{
    private static final long serialVersionUID = 1L;
    
    public DataDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DATA$0 = 
        new javax.xml.namespace.QName("", "data");
    
    
    /**
     * Gets the "data" element
     */
    public DataDocument.Data getData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            DataDocument.Data target = null;
            target = (DataDocument.Data)get_store().find_element_user(DATA$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "data" element
     */
    public void setData(DataDocument.Data data)
    {
        generatedSetterHelperImpl(data, DATA$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "data" element
     */
    public DataDocument.Data addNewData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            DataDocument.Data target = null;
            target = (DataDocument.Data)get_store().add_element_user(DATA$0);
            return target;
        }
    }
    /**
     * An XML data(@).
     *
     * This is a complex type.
     */
    public static class DataImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements DataDocument.Data
    {
        private static final long serialVersionUID = 1L;
        
        public DataImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName ENCODING$0 = 
            new javax.xml.namespace.QName("", "encoding");
        
        
        /**
         * Gets the "encoding" attribute
         */
        public DataDocument.Data.Encoding.Enum getEncoding()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ENCODING$0);
                if (target == null)
                {
                    return null;
                }
                return (DataDocument.Data.Encoding.Enum)target.getEnumValue();
            }
        }
        
        /**
         * Gets (as xml) the "encoding" attribute
         */
        public DataDocument.Data.Encoding xgetEncoding()
        {
            synchronized (monitor())
            {
                check_orphaned();
                DataDocument.Data.Encoding target = null;
                target = (DataDocument.Data.Encoding)get_store().find_attribute_user(ENCODING$0);
                return target;
            }
        }
        
        /**
         * True if has "encoding" attribute
         */
        public boolean isSetEncoding()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().find_attribute_user(ENCODING$0) != null;
            }
        }
        
        /**
         * Sets the "encoding" attribute
         */
        public void setEncoding(DataDocument.Data.Encoding.Enum encoding)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ENCODING$0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ENCODING$0);
                }
                target.setEnumValue(encoding);
            }
        }
        
        /**
         * Sets (as xml) the "encoding" attribute
         */
        public void xsetEncoding(DataDocument.Data.Encoding encoding)
        {
            synchronized (monitor())
            {
                check_orphaned();
                DataDocument.Data.Encoding target = null;
                target = (DataDocument.Data.Encoding)get_store().find_attribute_user(ENCODING$0);
                if (target == null)
                {
                    target = (DataDocument.Data.Encoding)get_store().add_attribute_user(ENCODING$0);
                }
                target.set(encoding);
            }
        }
        
        /**
         * Unsets the "encoding" attribute
         */
        public void unsetEncoding()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_attribute(ENCODING$0);
            }
        }
        /**
         * An XML encoding(@).
         *
         * This is an atomic type that is a restriction of DataDocument$Data$Encoding.
         */
        public static class EncodingImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements DataDocument.Data.Encoding
        {
            private static final long serialVersionUID = 1L;
            
            public EncodingImpl(org.apache.xmlbeans.SchemaType sType)
            {
                super(sType, false);
            }
            
            protected EncodingImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
            {
                super(sType, b);
            }
        }
    }
}
