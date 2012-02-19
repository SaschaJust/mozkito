/*
 * An XML document type.
 * Localname: commentid
 * Namespace: 
 * Java type: CommentidDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.CommentidDocument;

/**
 * A document containing one commentid(@) element.
 *
 * This is a complex type.
 */
public class CommentidDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements CommentidDocument
{
    private static final long serialVersionUID = 1L;
    
    public CommentidDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName COMMENTID$0 = 
        new javax.xml.namespace.QName("", "commentid");
    
    
    /**
     * Gets the "commentid" element
     */
    public java.lang.String getCommentid()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMMENTID$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "commentid" element
     */
    public org.apache.xmlbeans.XmlString xgetCommentid()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COMMENTID$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "commentid" element
     */
    public void setCommentid(java.lang.String commentid)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMMENTID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(COMMENTID$0);
            }
            target.setStringValue(commentid);
        }
    }
    
    /**
     * Sets (as xml) the "commentid" element
     */
    public void xsetCommentid(org.apache.xmlbeans.XmlString commentid)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COMMENTID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(COMMENTID$0);
            }
            target.set(commentid);
        }
    }
}
