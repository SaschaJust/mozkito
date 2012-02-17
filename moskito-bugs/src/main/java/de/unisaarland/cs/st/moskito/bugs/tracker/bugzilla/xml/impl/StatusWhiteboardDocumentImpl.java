/*
 * An XML document type.
 * Localname: status_whiteboard
 * Namespace: 
 * Java type: StatusWhiteboardDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one status_whiteboard(@) element.
 *
 * This is a complex type.
 */
public class StatusWhiteboardDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements StatusWhiteboardDocument
{
    private static final long serialVersionUID = 1L;
    
    public StatusWhiteboardDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName STATUSWHITEBOARD$0 = 
        new javax.xml.namespace.QName("", "status_whiteboard");
    
    
    /**
     * Gets the "status_whiteboard" element
     */
    public java.lang.String getStatusWhiteboard()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STATUSWHITEBOARD$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "status_whiteboard" element
     */
    public org.apache.xmlbeans.XmlString xgetStatusWhiteboard()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STATUSWHITEBOARD$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "status_whiteboard" element
     */
    public void setStatusWhiteboard(java.lang.String statusWhiteboard)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STATUSWHITEBOARD$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(STATUSWHITEBOARD$0);
            }
            target.setStringValue(statusWhiteboard);
        }
    }
    
    /**
     * Sets (as xml) the "status_whiteboard" element
     */
    public void xsetStatusWhiteboard(org.apache.xmlbeans.XmlString statusWhiteboard)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STATUSWHITEBOARD$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(STATUSWHITEBOARD$0);
            }
            target.set(statusWhiteboard);
        }
    }
}
