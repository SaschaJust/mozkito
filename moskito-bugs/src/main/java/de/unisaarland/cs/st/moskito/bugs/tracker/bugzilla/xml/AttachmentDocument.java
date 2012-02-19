/*
 * An XML document type.
 * Localname: attachment
 * Namespace: 
 * Java type: AttachmentDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml;


/**
 * A document containing one attachment(@) element.
 *
 * This is a complex type.
 */
public interface AttachmentDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(AttachmentDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("attachmentde5cdoctype");
    
    /**
     * Gets the "attachment" element
     */
    AttachmentDocument.Attachment getAttachment();
    
    /**
     * Sets the "attachment" element
     */
    void setAttachment(AttachmentDocument.Attachment attachment);
    
    /**
     * Appends and returns a new empty "attachment" element
     */
    AttachmentDocument.Attachment addNewAttachment();
    
    /**
     * An XML attachment(@).
     *
     * This is a complex type.
     */
    public interface Attachment extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Attachment.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("attachment790delemtype");
        
        /**
         * Gets the "attachid" element
         */
        java.lang.String getAttachid();
        
        /**
         * Gets (as xml) the "attachid" element
         */
        org.apache.xmlbeans.XmlString xgetAttachid();
        
        /**
         * Sets the "attachid" element
         */
        void setAttachid(java.lang.String attachid);
        
        /**
         * Sets (as xml) the "attachid" element
         */
        void xsetAttachid(org.apache.xmlbeans.XmlString attachid);
        
        /**
         * Gets the "date" element
         */
        java.lang.String getDate();
        
        /**
         * Gets (as xml) the "date" element
         */
        org.apache.xmlbeans.XmlString xgetDate();
        
        /**
         * Sets the "date" element
         */
        void setDate(java.lang.String date);
        
        /**
         * Sets (as xml) the "date" element
         */
        void xsetDate(org.apache.xmlbeans.XmlString date);
        
        /**
         * Gets the "delta_ts" element
         */
        java.lang.String getDeltaTs();
        
        /**
         * Gets (as xml) the "delta_ts" element
         */
        org.apache.xmlbeans.XmlString xgetDeltaTs();
        
        /**
         * Sets the "delta_ts" element
         */
        void setDeltaTs(java.lang.String deltaTs);
        
        /**
         * Sets (as xml) the "delta_ts" element
         */
        void xsetDeltaTs(org.apache.xmlbeans.XmlString deltaTs);
        
        /**
         * Gets the "desc" element
         */
        java.lang.String getDesc();
        
        /**
         * Gets (as xml) the "desc" element
         */
        org.apache.xmlbeans.XmlString xgetDesc();
        
        /**
         * Sets the "desc" element
         */
        void setDesc(java.lang.String desc);
        
        /**
         * Sets (as xml) the "desc" element
         */
        void xsetDesc(org.apache.xmlbeans.XmlString desc);
        
        /**
         * Gets the "filename" element
         */
        java.lang.String getFilename();
        
        /**
         * Gets (as xml) the "filename" element
         */
        org.apache.xmlbeans.XmlString xgetFilename();
        
        /**
         * Sets the "filename" element
         */
        void setFilename(java.lang.String filename);
        
        /**
         * Sets (as xml) the "filename" element
         */
        void xsetFilename(org.apache.xmlbeans.XmlString filename);
        
        /**
         * Gets the "type" element
         */
        java.lang.String getType();
        
        /**
         * Gets (as xml) the "type" element
         */
        org.apache.xmlbeans.XmlString xgetType();
        
        /**
         * Sets the "type" element
         */
        void setType(java.lang.String type);
        
        /**
         * Sets (as xml) the "type" element
         */
        void xsetType(org.apache.xmlbeans.XmlString type);
        
        /**
         * Gets the "size" element
         */
        java.lang.String getSize();
        
        /**
         * Gets (as xml) the "size" element
         */
        org.apache.xmlbeans.XmlString xgetSize();
        
        /**
         * Sets the "size" element
         */
        void setSize(java.lang.String size);
        
        /**
         * Sets (as xml) the "size" element
         */
        void xsetSize(org.apache.xmlbeans.XmlString size);
        
        /**
         * Gets the "attacher" element
         */
        java.lang.String getAttacher();
        
        /**
         * Gets (as xml) the "attacher" element
         */
        org.apache.xmlbeans.XmlString xgetAttacher();
        
        /**
         * Sets the "attacher" element
         */
        void setAttacher(java.lang.String attacher);
        
        /**
         * Sets (as xml) the "attacher" element
         */
        void xsetAttacher(org.apache.xmlbeans.XmlString attacher);
        
        /**
         * Gets the "token" element
         */
        java.lang.String getToken();
        
        /**
         * Gets (as xml) the "token" element
         */
        org.apache.xmlbeans.XmlString xgetToken();
        
        /**
         * True if has "token" element
         */
        boolean isSetToken();
        
        /**
         * Sets the "token" element
         */
        void setToken(java.lang.String token);
        
        /**
         * Sets (as xml) the "token" element
         */
        void xsetToken(org.apache.xmlbeans.XmlString token);
        
        /**
         * Unsets the "token" element
         */
        void unsetToken();
        
        /**
         * Gets the "data" element
         */
        DataDocument.Data getData();
        
        /**
         * True if has "data" element
         */
        boolean isSetData();
        
        /**
         * Sets the "data" element
         */
        void setData(DataDocument.Data data);
        
        /**
         * Appends and returns a new empty "data" element
         */
        DataDocument.Data addNewData();
        
        /**
         * Unsets the "data" element
         */
        void unsetData();
        
        /**
         * Gets array of all "flag" elements
         */
        FlagDocument.Flag[] getFlagArray();
        
        /**
         * Gets ith "flag" element
         */
        FlagDocument.Flag getFlagArray(int i);
        
        /**
         * Returns number of "flag" element
         */
        int sizeOfFlagArray();
        
        /**
         * Sets array of all "flag" element
         */
        void setFlagArray(FlagDocument.Flag[] flagArray);
        
        /**
         * Sets ith "flag" element
         */
        void setFlagArray(int i, FlagDocument.Flag flag);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "flag" element
         */
        FlagDocument.Flag insertNewFlag(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "flag" element
         */
        FlagDocument.Flag addNewFlag();
        
        /**
         * Removes the ith "flag" element
         */
        void removeFlag(int i);
        
        /**
         * Gets the "isobsolete" attribute
         */
        AttachmentDocument.Attachment.Isobsolete.Enum getIsobsolete();
        
        /**
         * Gets (as xml) the "isobsolete" attribute
         */
        AttachmentDocument.Attachment.Isobsolete xgetIsobsolete();
        
        /**
         * Sets the "isobsolete" attribute
         */
        void setIsobsolete(AttachmentDocument.Attachment.Isobsolete.Enum isobsolete);
        
        /**
         * Sets (as xml) the "isobsolete" attribute
         */
        void xsetIsobsolete(AttachmentDocument.Attachment.Isobsolete isobsolete);
        
        /**
         * Gets the "ispatch" attribute
         */
        AttachmentDocument.Attachment.Ispatch.Enum getIspatch();
        
        /**
         * Gets (as xml) the "ispatch" attribute
         */
        AttachmentDocument.Attachment.Ispatch xgetIspatch();
        
        /**
         * Sets the "ispatch" attribute
         */
        void setIspatch(AttachmentDocument.Attachment.Ispatch.Enum ispatch);
        
        /**
         * Sets (as xml) the "ispatch" attribute
         */
        void xsetIspatch(AttachmentDocument.Attachment.Ispatch ispatch);
        
        /**
         * Gets the "isprivate" attribute
         */
        AttachmentDocument.Attachment.Isprivate.Enum getIsprivate();
        
        /**
         * Gets (as xml) the "isprivate" attribute
         */
        AttachmentDocument.Attachment.Isprivate xgetIsprivate();
        
        /**
         * Sets the "isprivate" attribute
         */
        void setIsprivate(AttachmentDocument.Attachment.Isprivate.Enum isprivate);
        
        /**
         * Sets (as xml) the "isprivate" attribute
         */
        void xsetIsprivate(AttachmentDocument.Attachment.Isprivate isprivate);
        
        /**
         * Gets the "isurl" attribute
         */
        AttachmentDocument.Attachment.Isurl.Enum getIsurl();
        
        /**
         * Gets (as xml) the "isurl" attribute
         */
        AttachmentDocument.Attachment.Isurl xgetIsurl();
        
        /**
         * Sets the "isurl" attribute
         */
        void setIsurl(AttachmentDocument.Attachment.Isurl.Enum isurl);
        
        /**
         * Sets (as xml) the "isurl" attribute
         */
        void xsetIsurl(AttachmentDocument.Attachment.Isurl isurl);
        
        /**
         * An XML isobsolete(@).
         *
         * This is an atomic type that is a restriction of AttachmentDocument$Attachment$Isobsolete.
         */
        public interface Isobsolete extends org.apache.xmlbeans.XmlToken
        {
            public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
                org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Isobsolete.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("isobsolete3130attrtype");
            
            org.apache.xmlbeans.StringEnumAbstractBase enumValue();
            void set(org.apache.xmlbeans.StringEnumAbstractBase e);
            
            static final Enum X_0 = Enum.forString("0");
            static final Enum X_1 = Enum.forString("1");
            
            static final int INT_X_0 = Enum.INT_X_0;
            static final int INT_X_1 = Enum.INT_X_1;
            
            /**
             * Enumeration value class for AttachmentDocument$Attachment$Isobsolete.
             * These enum values can be used as follows:
             * <pre>
             * enum.toString(); // returns the string value of the enum
             * enum.intValue(); // returns an int value, useful for switches
             * // e.g., case Enum.INT_X_0
             * Enum.forString(s); // returns the enum value for a string
             * Enum.forInt(i); // returns the enum value for an int
             * </pre>
             * Enumeration objects are immutable singleton objects that
             * can be compared using == object equality. They have no
             * public constructor. See the constants defined within this
             * class for all the valid values.
             */
            static final class Enum extends org.apache.xmlbeans.StringEnumAbstractBase
            {
                /**
                 * Returns the enum value for a string, or null if none.
                 */
                public static Enum forString(java.lang.String s)
                    { return (Enum)table.forString(s); }
                /**
                 * Returns the enum value corresponding to an int, or null if none.
                 */
                public static Enum forInt(int i)
                    { return (Enum)table.forInt(i); }
                
                private Enum(java.lang.String s, int i)
                    { super(s, i); }
                
                static final int INT_X_0 = 1;
                static final int INT_X_1 = 2;
                
                public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                    new org.apache.xmlbeans.StringEnumAbstractBase.Table
                (
                    new Enum[]
                    {
                      new Enum("0", INT_X_0),
                      new Enum("1", INT_X_1),
                    }
                );
                private static final long serialVersionUID = 1L;
                private java.lang.Object readResolve() { return forInt(intValue()); } 
            }
            
            /**
             * A factory class with static methods for creating instances
             * of this type.
             */
            
            public static final class Factory
            {
                public static AttachmentDocument.Attachment.Isobsolete newValue(java.lang.Object obj) {
                  return (AttachmentDocument.Attachment.Isobsolete) type.newValue( obj ); }
                
                public static AttachmentDocument.Attachment.Isobsolete newInstance() {
                  return (AttachmentDocument.Attachment.Isobsolete) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
                
                public static AttachmentDocument.Attachment.Isobsolete newInstance(org.apache.xmlbeans.XmlOptions options) {
                  return (AttachmentDocument.Attachment.Isobsolete) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
                
                private Factory() { } // No instance of this class allowed
            }
        }
        
        /**
         * An XML ispatch(@).
         *
         * This is an atomic type that is a restriction of AttachmentDocument$Attachment$Ispatch.
         */
        public interface Ispatch extends org.apache.xmlbeans.XmlToken
        {
            public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
                org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Ispatch.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("ispatchcb53attrtype");
            
            org.apache.xmlbeans.StringEnumAbstractBase enumValue();
            void set(org.apache.xmlbeans.StringEnumAbstractBase e);
            
            static final Enum X_0 = Enum.forString("0");
            static final Enum X_1 = Enum.forString("1");
            
            static final int INT_X_0 = Enum.INT_X_0;
            static final int INT_X_1 = Enum.INT_X_1;
            
            /**
             * Enumeration value class for AttachmentDocument$Attachment$Ispatch.
             * These enum values can be used as follows:
             * <pre>
             * enum.toString(); // returns the string value of the enum
             * enum.intValue(); // returns an int value, useful for switches
             * // e.g., case Enum.INT_X_0
             * Enum.forString(s); // returns the enum value for a string
             * Enum.forInt(i); // returns the enum value for an int
             * </pre>
             * Enumeration objects are immutable singleton objects that
             * can be compared using == object equality. They have no
             * public constructor. See the constants defined within this
             * class for all the valid values.
             */
            static final class Enum extends org.apache.xmlbeans.StringEnumAbstractBase
            {
                /**
                 * Returns the enum value for a string, or null if none.
                 */
                public static Enum forString(java.lang.String s)
                    { return (Enum)table.forString(s); }
                /**
                 * Returns the enum value corresponding to an int, or null if none.
                 */
                public static Enum forInt(int i)
                    { return (Enum)table.forInt(i); }
                
                private Enum(java.lang.String s, int i)
                    { super(s, i); }
                
                static final int INT_X_0 = 1;
                static final int INT_X_1 = 2;
                
                public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                    new org.apache.xmlbeans.StringEnumAbstractBase.Table
                (
                    new Enum[]
                    {
                      new Enum("0", INT_X_0),
                      new Enum("1", INT_X_1),
                    }
                );
                private static final long serialVersionUID = 1L;
                private java.lang.Object readResolve() { return forInt(intValue()); } 
            }
            
            /**
             * A factory class with static methods for creating instances
             * of this type.
             */
            
            public static final class Factory
            {
                public static AttachmentDocument.Attachment.Ispatch newValue(java.lang.Object obj) {
                  return (AttachmentDocument.Attachment.Ispatch) type.newValue( obj ); }
                
                public static AttachmentDocument.Attachment.Ispatch newInstance() {
                  return (AttachmentDocument.Attachment.Ispatch) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
                
                public static AttachmentDocument.Attachment.Ispatch newInstance(org.apache.xmlbeans.XmlOptions options) {
                  return (AttachmentDocument.Attachment.Ispatch) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
                
                private Factory() { } // No instance of this class allowed
            }
        }
        
        /**
         * An XML isprivate(@).
         *
         * This is an atomic type that is a restriction of AttachmentDocument$Attachment$Isprivate.
         */
        public interface Isprivate extends org.apache.xmlbeans.XmlToken
        {
            public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
                org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Isprivate.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("isprivate164eattrtype");
            
            org.apache.xmlbeans.StringEnumAbstractBase enumValue();
            void set(org.apache.xmlbeans.StringEnumAbstractBase e);
            
            static final Enum X_0 = Enum.forString("0");
            static final Enum X_1 = Enum.forString("1");
            
            static final int INT_X_0 = Enum.INT_X_0;
            static final int INT_X_1 = Enum.INT_X_1;
            
            /**
             * Enumeration value class for AttachmentDocument$Attachment$Isprivate.
             * These enum values can be used as follows:
             * <pre>
             * enum.toString(); // returns the string value of the enum
             * enum.intValue(); // returns an int value, useful for switches
             * // e.g., case Enum.INT_X_0
             * Enum.forString(s); // returns the enum value for a string
             * Enum.forInt(i); // returns the enum value for an int
             * </pre>
             * Enumeration objects are immutable singleton objects that
             * can be compared using == object equality. They have no
             * public constructor. See the constants defined within this
             * class for all the valid values.
             */
            static final class Enum extends org.apache.xmlbeans.StringEnumAbstractBase
            {
                /**
                 * Returns the enum value for a string, or null if none.
                 */
                public static Enum forString(java.lang.String s)
                    { return (Enum)table.forString(s); }
                /**
                 * Returns the enum value corresponding to an int, or null if none.
                 */
                public static Enum forInt(int i)
                    { return (Enum)table.forInt(i); }
                
                private Enum(java.lang.String s, int i)
                    { super(s, i); }
                
                static final int INT_X_0 = 1;
                static final int INT_X_1 = 2;
                
                public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                    new org.apache.xmlbeans.StringEnumAbstractBase.Table
                (
                    new Enum[]
                    {
                      new Enum("0", INT_X_0),
                      new Enum("1", INT_X_1),
                    }
                );
                private static final long serialVersionUID = 1L;
                private java.lang.Object readResolve() { return forInt(intValue()); } 
            }
            
            /**
             * A factory class with static methods for creating instances
             * of this type.
             */
            
            public static final class Factory
            {
                public static AttachmentDocument.Attachment.Isprivate newValue(java.lang.Object obj) {
                  return (AttachmentDocument.Attachment.Isprivate) type.newValue( obj ); }
                
                public static AttachmentDocument.Attachment.Isprivate newInstance() {
                  return (AttachmentDocument.Attachment.Isprivate) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
                
                public static AttachmentDocument.Attachment.Isprivate newInstance(org.apache.xmlbeans.XmlOptions options) {
                  return (AttachmentDocument.Attachment.Isprivate) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
                
                private Factory() { } // No instance of this class allowed
            }
        }
        
        /**
         * An XML isurl(@).
         *
         * This is an atomic type that is a restriction of AttachmentDocument$Attachment$Isurl.
         */
        public interface Isurl extends org.apache.xmlbeans.XmlToken
        {
            public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
                org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Isurl.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("isurlb43aattrtype");
            
            org.apache.xmlbeans.StringEnumAbstractBase enumValue();
            void set(org.apache.xmlbeans.StringEnumAbstractBase e);
            
            static final Enum X_0 = Enum.forString("0");
            static final Enum X_1 = Enum.forString("1");
            
            static final int INT_X_0 = Enum.INT_X_0;
            static final int INT_X_1 = Enum.INT_X_1;
            
            /**
             * Enumeration value class for AttachmentDocument$Attachment$Isurl.
             * These enum values can be used as follows:
             * <pre>
             * enum.toString(); // returns the string value of the enum
             * enum.intValue(); // returns an int value, useful for switches
             * // e.g., case Enum.INT_X_0
             * Enum.forString(s); // returns the enum value for a string
             * Enum.forInt(i); // returns the enum value for an int
             * </pre>
             * Enumeration objects are immutable singleton objects that
             * can be compared using == object equality. They have no
             * public constructor. See the constants defined within this
             * class for all the valid values.
             */
            static final class Enum extends org.apache.xmlbeans.StringEnumAbstractBase
            {
                /**
                 * Returns the enum value for a string, or null if none.
                 */
                public static Enum forString(java.lang.String s)
                    { return (Enum)table.forString(s); }
                /**
                 * Returns the enum value corresponding to an int, or null if none.
                 */
                public static Enum forInt(int i)
                    { return (Enum)table.forInt(i); }
                
                private Enum(java.lang.String s, int i)
                    { super(s, i); }
                
                static final int INT_X_0 = 1;
                static final int INT_X_1 = 2;
                
                public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                    new org.apache.xmlbeans.StringEnumAbstractBase.Table
                (
                    new Enum[]
                    {
                      new Enum("0", INT_X_0),
                      new Enum("1", INT_X_1),
                    }
                );
                private static final long serialVersionUID = 1L;
                private java.lang.Object readResolve() { return forInt(intValue()); } 
            }
            
            /**
             * A factory class with static methods for creating instances
             * of this type.
             */
            
            public static final class Factory
            {
                public static AttachmentDocument.Attachment.Isurl newValue(java.lang.Object obj) {
                  return (AttachmentDocument.Attachment.Isurl) type.newValue( obj ); }
                
                public static AttachmentDocument.Attachment.Isurl newInstance() {
                  return (AttachmentDocument.Attachment.Isurl) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
                
                public static AttachmentDocument.Attachment.Isurl newInstance(org.apache.xmlbeans.XmlOptions options) {
                  return (AttachmentDocument.Attachment.Isurl) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
                
                private Factory() { } // No instance of this class allowed
            }
        }
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static AttachmentDocument.Attachment newInstance() {
              return (AttachmentDocument.Attachment) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static AttachmentDocument.Attachment newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (AttachmentDocument.Attachment) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static AttachmentDocument newInstance() {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static AttachmentDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static AttachmentDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static AttachmentDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static AttachmentDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static AttachmentDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static AttachmentDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static AttachmentDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static AttachmentDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static AttachmentDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static AttachmentDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static AttachmentDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static AttachmentDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static AttachmentDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static AttachmentDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static AttachmentDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static AttachmentDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static AttachmentDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (AttachmentDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
