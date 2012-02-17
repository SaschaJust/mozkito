/*
 * An XML document type.
 * Localname: keywords
 * Namespace: 
 * Java type: KeywordsDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one keywords(@) element.
 *
 * This is a complex type.
 */
public class KeywordsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements KeywordsDocument
{
    private static final long serialVersionUID = 1L;
    
    public KeywordsDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName KEYWORDS$0 = 
        new javax.xml.namespace.QName("", "keywords");
    
    
    /**
     * Gets the "keywords" element
     */
    public java.lang.String getKeywords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(KEYWORDS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "keywords" element
     */
    public org.apache.xmlbeans.XmlString xgetKeywords()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KEYWORDS$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "keywords" element
     */
    public void setKeywords(java.lang.String keywords)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(KEYWORDS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(KEYWORDS$0);
            }
            target.setStringValue(keywords);
        }
    }
    
    /**
     * Sets (as xml) the "keywords" element
     */
    public void xsetKeywords(org.apache.xmlbeans.XmlString keywords)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(KEYWORDS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(KEYWORDS$0);
            }
            target.set(keywords);
        }
    }
}
