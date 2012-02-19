/*
 * An XML document type.
 * Localname: attachment
 * Namespace: 
 * Java type: AttachmentDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.AttachmentDocument;

/**
 * A document containing one attachment(@) element.
 *
 * This is a complex type.
 */
public class AttachmentDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements AttachmentDocument
{
    private static final long serialVersionUID = 1L;
    
    public AttachmentDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ATTACHMENT$0 = 
        new javax.xml.namespace.QName("", "attachment");
    
    
    /**
     * Gets the "attachment" element
     */
    public AttachmentDocument.Attachment getAttachment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            AttachmentDocument.Attachment target = null;
            target = (AttachmentDocument.Attachment)get_store().find_element_user(ATTACHMENT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "attachment" element
     */
    public void setAttachment(AttachmentDocument.Attachment attachment)
    {
        generatedSetterHelperImpl(attachment, ATTACHMENT$0, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }
    
    /**
     * Appends and returns a new empty "attachment" element
     */
    public AttachmentDocument.Attachment addNewAttachment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            AttachmentDocument.Attachment target = null;
            target = (AttachmentDocument.Attachment)get_store().add_element_user(ATTACHMENT$0);
            return target;
        }
    }
    /**
     * An XML attachment(@).
     *
     * This is a complex type.
     */
    public static class AttachmentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements AttachmentDocument.Attachment
    {
        private static final long serialVersionUID = 1L;
        
        public AttachmentImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName ATTACHID$0 = 
            new javax.xml.namespace.QName("", "attachid");
        private static final javax.xml.namespace.QName DATE$2 = 
            new javax.xml.namespace.QName("", "date");
        private static final javax.xml.namespace.QName DELTATS$4 = 
            new javax.xml.namespace.QName("", "delta_ts");
        private static final javax.xml.namespace.QName DESC$6 = 
            new javax.xml.namespace.QName("", "desc");
        private static final javax.xml.namespace.QName FILENAME$8 = 
            new javax.xml.namespace.QName("", "filename");
        private static final javax.xml.namespace.QName TYPE$10 = 
            new javax.xml.namespace.QName("", "type");
        private static final javax.xml.namespace.QName SIZE$12 = 
            new javax.xml.namespace.QName("", "size");
        private static final javax.xml.namespace.QName ATTACHER$14 = 
            new javax.xml.namespace.QName("", "attacher");
        private static final javax.xml.namespace.QName TOKEN$16 = 
            new javax.xml.namespace.QName("", "token");
        private static final javax.xml.namespace.QName DATA$18 = 
            new javax.xml.namespace.QName("", "data");
        private static final javax.xml.namespace.QName FLAG$20 = 
            new javax.xml.namespace.QName("", "flag");
        private static final javax.xml.namespace.QName ISOBSOLETE$22 = 
            new javax.xml.namespace.QName("", "isobsolete");
        private static final javax.xml.namespace.QName ISPATCH$24 = 
            new javax.xml.namespace.QName("", "ispatch");
        private static final javax.xml.namespace.QName ISPRIVATE$26 = 
            new javax.xml.namespace.QName("", "isprivate");
        private static final javax.xml.namespace.QName ISURL$28 = 
            new javax.xml.namespace.QName("", "isurl");
        
        
        /**
         * Gets the "attachid" element
         */
        public java.lang.String getAttachid()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ATTACHID$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "attachid" element
         */
        public org.apache.xmlbeans.XmlString xgetAttachid()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ATTACHID$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "attachid" element
         */
        public void setAttachid(java.lang.String attachid)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ATTACHID$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ATTACHID$0);
                }
                target.setStringValue(attachid);
            }
        }
        
        /**
         * Sets (as xml) the "attachid" element
         */
        public void xsetAttachid(org.apache.xmlbeans.XmlString attachid)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ATTACHID$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ATTACHID$0);
                }
                target.set(attachid);
            }
        }
        
        /**
         * Gets the "date" element
         */
        public java.lang.String getDate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATE$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "date" element
         */
        public org.apache.xmlbeans.XmlString xgetDate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DATE$2, 0);
                return target;
            }
        }
        
        /**
         * Sets the "date" element
         */
        public void setDate(java.lang.String date)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DATE$2);
                }
                target.setStringValue(date);
            }
        }
        
        /**
         * Sets (as xml) the "date" element
         */
        public void xsetDate(org.apache.xmlbeans.XmlString date)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DATE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DATE$2);
                }
                target.set(date);
            }
        }
        
        /**
         * Gets the "delta_ts" element
         */
        public java.lang.String getDeltaTs()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DELTATS$4, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "delta_ts" element
         */
        public org.apache.xmlbeans.XmlString xgetDeltaTs()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DELTATS$4, 0);
                return target;
            }
        }
        
        /**
         * Sets the "delta_ts" element
         */
        public void setDeltaTs(java.lang.String deltaTs)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DELTATS$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DELTATS$4);
                }
                target.setStringValue(deltaTs);
            }
        }
        
        /**
         * Sets (as xml) the "delta_ts" element
         */
        public void xsetDeltaTs(org.apache.xmlbeans.XmlString deltaTs)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DELTATS$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DELTATS$4);
                }
                target.set(deltaTs);
            }
        }
        
        /**
         * Gets the "desc" element
         */
        public java.lang.String getDesc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DESC$6, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "desc" element
         */
        public org.apache.xmlbeans.XmlString xgetDesc()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DESC$6, 0);
                return target;
            }
        }
        
        /**
         * Sets the "desc" element
         */
        public void setDesc(java.lang.String desc)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DESC$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DESC$6);
                }
                target.setStringValue(desc);
            }
        }
        
        /**
         * Sets (as xml) the "desc" element
         */
        public void xsetDesc(org.apache.xmlbeans.XmlString desc)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DESC$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DESC$6);
                }
                target.set(desc);
            }
        }
        
        /**
         * Gets the "filename" element
         */
        public java.lang.String getFilename()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FILENAME$8, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "filename" element
         */
        public org.apache.xmlbeans.XmlString xgetFilename()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$8, 0);
                return target;
            }
        }
        
        /**
         * Sets the "filename" element
         */
        public void setFilename(java.lang.String filename)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FILENAME$8, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FILENAME$8);
                }
                target.setStringValue(filename);
            }
        }
        
        /**
         * Sets (as xml) the "filename" element
         */
        public void xsetFilename(org.apache.xmlbeans.XmlString filename)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FILENAME$8, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FILENAME$8);
                }
                target.set(filename);
            }
        }
        
        /**
         * Gets the "type" element
         */
        public java.lang.String getType()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TYPE$10, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "type" element
         */
        public org.apache.xmlbeans.XmlString xgetType()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TYPE$10, 0);
                return target;
            }
        }
        
        /**
         * Sets the "type" element
         */
        public void setType(java.lang.String type)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TYPE$10, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TYPE$10);
                }
                target.setStringValue(type);
            }
        }
        
        /**
         * Sets (as xml) the "type" element
         */
        public void xsetType(org.apache.xmlbeans.XmlString type)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TYPE$10, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TYPE$10);
                }
                target.set(type);
            }
        }
        
        /**
         * Gets the "size" element
         */
        public java.lang.String getSize()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIZE$12, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "size" element
         */
        public org.apache.xmlbeans.XmlString xgetSize()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SIZE$12, 0);
                return target;
            }
        }
        
        /**
         * Sets the "size" element
         */
        public void setSize(java.lang.String size)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SIZE$12, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SIZE$12);
                }
                target.setStringValue(size);
            }
        }
        
        /**
         * Sets (as xml) the "size" element
         */
        public void xsetSize(org.apache.xmlbeans.XmlString size)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SIZE$12, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SIZE$12);
                }
                target.set(size);
            }
        }
        
        /**
         * Gets the "attacher" element
         */
        public java.lang.String getAttacher()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ATTACHER$14, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "attacher" element
         */
        public org.apache.xmlbeans.XmlString xgetAttacher()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ATTACHER$14, 0);
                return target;
            }
        }
        
        /**
         * Sets the "attacher" element
         */
        public void setAttacher(java.lang.String attacher)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ATTACHER$14, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ATTACHER$14);
                }
                target.setStringValue(attacher);
            }
        }
        
        /**
         * Sets (as xml) the "attacher" element
         */
        public void xsetAttacher(org.apache.xmlbeans.XmlString attacher)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ATTACHER$14, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ATTACHER$14);
                }
                target.set(attacher);
            }
        }
        
        /**
         * Gets the "token" element
         */
        public java.lang.String getToken()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOKEN$16, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "token" element
         */
        public org.apache.xmlbeans.XmlString xgetToken()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOKEN$16, 0);
                return target;
            }
        }
        
        /**
         * True if has "token" element
         */
        public boolean isSetToken()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(TOKEN$16) != 0;
            }
        }
        
        /**
         * Sets the "token" element
         */
        public void setToken(java.lang.String token)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOKEN$16, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TOKEN$16);
                }
                target.setStringValue(token);
            }
        }
        
        /**
         * Sets (as xml) the "token" element
         */
        public void xsetToken(org.apache.xmlbeans.XmlString token)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TOKEN$16, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TOKEN$16);
                }
                target.set(token);
            }
        }
        
        /**
         * Unsets the "token" element
         */
        public void unsetToken()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(TOKEN$16, 0);
            }
        }
        
        /**
         * Gets the "data" element
         */
        public DataDocument.Data getData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                DataDocument.Data target = null;
                target = (DataDocument.Data)get_store().find_element_user(DATA$18, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * True if has "data" element
         */
        public boolean isSetData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(DATA$18) != 0;
            }
        }
        
        /**
         * Sets the "data" element
         */
        public void setData(DataDocument.Data data)
        {
            generatedSetterHelperImpl(data, DATA$18, 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
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
                target = (DataDocument.Data)get_store().add_element_user(DATA$18);
                return target;
            }
        }
        
        /**
         * Unsets the "data" element
         */
        public void unsetData()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(DATA$18, 0);
            }
        }
        
        /**
         * Gets array of all "flag" elements
         */
        public FlagDocument.Flag[] getFlagArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                java.util.List targetList = new java.util.ArrayList();
                get_store().find_all_element_users(FLAG$20, targetList);
                FlagDocument.Flag[] result = new FlagDocument.Flag[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        /**
         * Gets ith "flag" element
         */
        public FlagDocument.Flag getFlagArray(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                FlagDocument.Flag target = null;
                target = (FlagDocument.Flag)get_store().find_element_user(FLAG$20, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        /**
         * Returns number of "flag" element
         */
        public int sizeOfFlagArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(FLAG$20);
            }
        }
        
        /**
         * Sets array of all "flag" element  WARNING: This method is not atomicaly synchronized.
         */
        public void setFlagArray(FlagDocument.Flag[] flagArray)
        {
            check_orphaned();
            arraySetterHelper(flagArray, FLAG$20);
        }
        
        /**
         * Sets ith "flag" element
         */
        public void setFlagArray(int i, FlagDocument.Flag flag)
        {
            synchronized (monitor())
            {
                check_orphaned();
                FlagDocument.Flag target = null;
                target = (FlagDocument.Flag)get_store().find_element_user(FLAG$20, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                target.set(flag);
            }
        }
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "flag" element
         */
        public FlagDocument.Flag insertNewFlag(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                FlagDocument.Flag target = null;
                target = (FlagDocument.Flag)get_store().insert_element_user(FLAG$20, i);
                return target;
            }
        }
        
        /**
         * Appends and returns a new empty value (as xml) as the last "flag" element
         */
        public FlagDocument.Flag addNewFlag()
        {
            synchronized (monitor())
            {
                check_orphaned();
                FlagDocument.Flag target = null;
                target = (FlagDocument.Flag)get_store().add_element_user(FLAG$20);
                return target;
            }
        }
        
        /**
         * Removes the ith "flag" element
         */
        public void removeFlag(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(FLAG$20, i);
            }
        }
        
        /**
         * Gets the "isobsolete" attribute
         */
        public AttachmentDocument.Attachment.Isobsolete.Enum getIsobsolete()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ISOBSOLETE$22);
                if (target == null)
                {
                    return null;
                }
                return (AttachmentDocument.Attachment.Isobsolete.Enum)target.getEnumValue();
            }
        }
        
        /**
         * Gets (as xml) the "isobsolete" attribute
         */
        public AttachmentDocument.Attachment.Isobsolete xgetIsobsolete()
        {
            synchronized (monitor())
            {
                check_orphaned();
                AttachmentDocument.Attachment.Isobsolete target = null;
                target = (AttachmentDocument.Attachment.Isobsolete)get_store().find_attribute_user(ISOBSOLETE$22);
                return target;
            }
        }
        
        /**
         * Sets the "isobsolete" attribute
         */
        public void setIsobsolete(AttachmentDocument.Attachment.Isobsolete.Enum isobsolete)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ISOBSOLETE$22);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ISOBSOLETE$22);
                }
                target.setEnumValue(isobsolete);
            }
        }
        
        /**
         * Sets (as xml) the "isobsolete" attribute
         */
        public void xsetIsobsolete(AttachmentDocument.Attachment.Isobsolete isobsolete)
        {
            synchronized (monitor())
            {
                check_orphaned();
                AttachmentDocument.Attachment.Isobsolete target = null;
                target = (AttachmentDocument.Attachment.Isobsolete)get_store().find_attribute_user(ISOBSOLETE$22);
                if (target == null)
                {
                    target = (AttachmentDocument.Attachment.Isobsolete)get_store().add_attribute_user(ISOBSOLETE$22);
                }
                target.set(isobsolete);
            }
        }
        
        /**
         * Gets the "ispatch" attribute
         */
        public AttachmentDocument.Attachment.Ispatch.Enum getIspatch()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ISPATCH$24);
                if (target == null)
                {
                    return null;
                }
                return (AttachmentDocument.Attachment.Ispatch.Enum)target.getEnumValue();
            }
        }
        
        /**
         * Gets (as xml) the "ispatch" attribute
         */
        public AttachmentDocument.Attachment.Ispatch xgetIspatch()
        {
            synchronized (monitor())
            {
                check_orphaned();
                AttachmentDocument.Attachment.Ispatch target = null;
                target = (AttachmentDocument.Attachment.Ispatch)get_store().find_attribute_user(ISPATCH$24);
                return target;
            }
        }
        
        /**
         * Sets the "ispatch" attribute
         */
        public void setIspatch(AttachmentDocument.Attachment.Ispatch.Enum ispatch)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ISPATCH$24);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ISPATCH$24);
                }
                target.setEnumValue(ispatch);
            }
        }
        
        /**
         * Sets (as xml) the "ispatch" attribute
         */
        public void xsetIspatch(AttachmentDocument.Attachment.Ispatch ispatch)
        {
            synchronized (monitor())
            {
                check_orphaned();
                AttachmentDocument.Attachment.Ispatch target = null;
                target = (AttachmentDocument.Attachment.Ispatch)get_store().find_attribute_user(ISPATCH$24);
                if (target == null)
                {
                    target = (AttachmentDocument.Attachment.Ispatch)get_store().add_attribute_user(ISPATCH$24);
                }
                target.set(ispatch);
            }
        }
        
        /**
         * Gets the "isprivate" attribute
         */
        public AttachmentDocument.Attachment.Isprivate.Enum getIsprivate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ISPRIVATE$26);
                if (target == null)
                {
                    return null;
                }
                return (AttachmentDocument.Attachment.Isprivate.Enum)target.getEnumValue();
            }
        }
        
        /**
         * Gets (as xml) the "isprivate" attribute
         */
        public AttachmentDocument.Attachment.Isprivate xgetIsprivate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                AttachmentDocument.Attachment.Isprivate target = null;
                target = (AttachmentDocument.Attachment.Isprivate)get_store().find_attribute_user(ISPRIVATE$26);
                return target;
            }
        }
        
        /**
         * Sets the "isprivate" attribute
         */
        public void setIsprivate(AttachmentDocument.Attachment.Isprivate.Enum isprivate)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ISPRIVATE$26);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ISPRIVATE$26);
                }
                target.setEnumValue(isprivate);
            }
        }
        
        /**
         * Sets (as xml) the "isprivate" attribute
         */
        public void xsetIsprivate(AttachmentDocument.Attachment.Isprivate isprivate)
        {
            synchronized (monitor())
            {
                check_orphaned();
                AttachmentDocument.Attachment.Isprivate target = null;
                target = (AttachmentDocument.Attachment.Isprivate)get_store().find_attribute_user(ISPRIVATE$26);
                if (target == null)
                {
                    target = (AttachmentDocument.Attachment.Isprivate)get_store().add_attribute_user(ISPRIVATE$26);
                }
                target.set(isprivate);
            }
        }
        
        /**
         * Gets the "isurl" attribute
         */
        public AttachmentDocument.Attachment.Isurl.Enum getIsurl()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ISURL$28);
                if (target == null)
                {
                    return null;
                }
                return (AttachmentDocument.Attachment.Isurl.Enum)target.getEnumValue();
            }
        }
        
        /**
         * Gets (as xml) the "isurl" attribute
         */
        public AttachmentDocument.Attachment.Isurl xgetIsurl()
        {
            synchronized (monitor())
            {
                check_orphaned();
                AttachmentDocument.Attachment.Isurl target = null;
                target = (AttachmentDocument.Attachment.Isurl)get_store().find_attribute_user(ISURL$28);
                return target;
            }
        }
        
        /**
         * Sets the "isurl" attribute
         */
        public void setIsurl(AttachmentDocument.Attachment.Isurl.Enum isurl)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ISURL$28);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ISURL$28);
                }
                target.setEnumValue(isurl);
            }
        }
        
        /**
         * Sets (as xml) the "isurl" attribute
         */
        public void xsetIsurl(AttachmentDocument.Attachment.Isurl isurl)
        {
            synchronized (monitor())
            {
                check_orphaned();
                AttachmentDocument.Attachment.Isurl target = null;
                target = (AttachmentDocument.Attachment.Isurl)get_store().find_attribute_user(ISURL$28);
                if (target == null)
                {
                    target = (AttachmentDocument.Attachment.Isurl)get_store().add_attribute_user(ISURL$28);
                }
                target.set(isurl);
            }
        }
        /**
         * An XML isobsolete(@).
         *
         * This is an atomic type that is a restriction of AttachmentDocument$Attachment$Isobsolete.
         */
        public static class IsobsoleteImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements AttachmentDocument.Attachment.Isobsolete
        {
            private static final long serialVersionUID = 1L;
            
            public IsobsoleteImpl(org.apache.xmlbeans.SchemaType sType)
            {
                super(sType, false);
            }
            
            protected IsobsoleteImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
            {
                super(sType, b);
            }
        }
        /**
         * An XML ispatch(@).
         *
         * This is an atomic type that is a restriction of AttachmentDocument$Attachment$Ispatch.
         */
        public static class IspatchImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements AttachmentDocument.Attachment.Ispatch
        {
            private static final long serialVersionUID = 1L;
            
            public IspatchImpl(org.apache.xmlbeans.SchemaType sType)
            {
                super(sType, false);
            }
            
            protected IspatchImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
            {
                super(sType, b);
            }
        }
        /**
         * An XML isprivate(@).
         *
         * This is an atomic type that is a restriction of AttachmentDocument$Attachment$Isprivate.
         */
        public static class IsprivateImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements AttachmentDocument.Attachment.Isprivate
        {
            private static final long serialVersionUID = 1L;
            
            public IsprivateImpl(org.apache.xmlbeans.SchemaType sType)
            {
                super(sType, false);
            }
            
            protected IsprivateImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
            {
                super(sType, b);
            }
        }
        /**
         * An XML isurl(@).
         *
         * This is an atomic type that is a restriction of AttachmentDocument$Attachment$Isurl.
         */
        public static class IsurlImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements AttachmentDocument.Attachment.Isurl
        {
            private static final long serialVersionUID = 1L;
            
            public IsurlImpl(org.apache.xmlbeans.SchemaType sType)
            {
                super(sType, false);
            }
            
            protected IsurlImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
            {
                super(sType, b);
            }
        }
    }
}
