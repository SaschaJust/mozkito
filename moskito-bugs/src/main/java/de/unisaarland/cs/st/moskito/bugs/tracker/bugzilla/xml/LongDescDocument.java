/*
 * An XML document type.
 * Localname: long_desc
 * Namespace: 
 * Java type: LongDescDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml;


/**
 * A document containing one long_desc(@) element.
 *
 * This is a complex type.
 */
public interface LongDescDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(LongDescDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("longdesc0e1bdoctype");
    
    /**
     * Gets the "long_desc" element
     */
    LongDescDocument.LongDesc getLongDesc();
    
    /**
     * Sets the "long_desc" element
     */
    void setLongDesc(LongDescDocument.LongDesc longDesc);
    
    /**
     * Appends and returns a new empty "long_desc" element
     */
    LongDescDocument.LongDesc addNewLongDesc();
    
    /**
     * An XML long_desc(@).
     *
     * This is a complex type.
     */
    public interface LongDesc extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(LongDesc.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("longdesc130belemtype");
        
        /**
         * Gets the "commentid" element
         */
        java.lang.String getCommentid();
        
        /**
         * Gets (as xml) the "commentid" element
         */
        org.apache.xmlbeans.XmlString xgetCommentid();
        
        /**
         * Sets the "commentid" element
         */
        void setCommentid(java.lang.String commentid);
        
        /**
         * Sets (as xml) the "commentid" element
         */
        void xsetCommentid(org.apache.xmlbeans.XmlString commentid);
        
        /**
         * Gets the "attachid" element
         */
        java.lang.String getAttachid();
        
        /**
         * Gets (as xml) the "attachid" element
         */
        org.apache.xmlbeans.XmlString xgetAttachid();
        
        /**
         * True if has "attachid" element
         */
        boolean isSetAttachid();
        
        /**
         * Sets the "attachid" element
         */
        void setAttachid(java.lang.String attachid);
        
        /**
         * Sets (as xml) the "attachid" element
         */
        void xsetAttachid(org.apache.xmlbeans.XmlString attachid);
        
        /**
         * Unsets the "attachid" element
         */
        void unsetAttachid();
        
        /**
         * Gets the "who" element
         */
        WhoDocument.Who getWho();
        
        /**
         * Sets the "who" element
         */
        void setWho(WhoDocument.Who who);
        
        /**
         * Appends and returns a new empty "who" element
         */
        WhoDocument.Who addNewWho();
        
        /**
         * Gets the "bug_when" element
         */
        java.lang.String getBugWhen();
        
        /**
         * Gets (as xml) the "bug_when" element
         */
        org.apache.xmlbeans.XmlString xgetBugWhen();
        
        /**
         * Sets the "bug_when" element
         */
        void setBugWhen(java.lang.String bugWhen);
        
        /**
         * Sets (as xml) the "bug_when" element
         */
        void xsetBugWhen(org.apache.xmlbeans.XmlString bugWhen);
        
        /**
         * Gets the "work_time" element
         */
        java.lang.String getWorkTime();
        
        /**
         * Gets (as xml) the "work_time" element
         */
        org.apache.xmlbeans.XmlString xgetWorkTime();
        
        /**
         * True if has "work_time" element
         */
        boolean isSetWorkTime();
        
        /**
         * Sets the "work_time" element
         */
        void setWorkTime(java.lang.String workTime);
        
        /**
         * Sets (as xml) the "work_time" element
         */
        void xsetWorkTime(org.apache.xmlbeans.XmlString workTime);
        
        /**
         * Unsets the "work_time" element
         */
        void unsetWorkTime();
        
        /**
         * Gets the "thetext" element
         */
        java.lang.String getThetext();
        
        /**
         * Gets (as xml) the "thetext" element
         */
        org.apache.xmlbeans.XmlString xgetThetext();
        
        /**
         * Sets the "thetext" element
         */
        void setThetext(java.lang.String thetext);
        
        /**
         * Sets (as xml) the "thetext" element
         */
        void xsetThetext(org.apache.xmlbeans.XmlString thetext);
        
        /**
         * Gets the "isprivate" attribute
         */
        LongDescDocument.LongDesc.Isprivate.Enum getIsprivate();
        
        /**
         * Gets (as xml) the "isprivate" attribute
         */
        LongDescDocument.LongDesc.Isprivate xgetIsprivate();
        
        /**
         * Sets the "isprivate" attribute
         */
        void setIsprivate(LongDescDocument.LongDesc.Isprivate.Enum isprivate);
        
        /**
         * Sets (as xml) the "isprivate" attribute
         */
        void xsetIsprivate(LongDescDocument.LongDesc.Isprivate isprivate);
        
        /**
         * An XML isprivate(@).
         *
         * This is an atomic type that is a restriction of LongDescDocument$LongDesc$Isprivate.
         */
        public interface Isprivate extends org.apache.xmlbeans.XmlToken
        {
            public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
                org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Isprivate.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("isprivate0c8cattrtype");
            
            org.apache.xmlbeans.StringEnumAbstractBase enumValue();
            void set(org.apache.xmlbeans.StringEnumAbstractBase e);
            
            static final Enum X_0 = Enum.forString("0");
            static final Enum X_1 = Enum.forString("1");
            
            static final int INT_X_0 = Enum.INT_X_0;
            static final int INT_X_1 = Enum.INT_X_1;
            
            /**
             * Enumeration value class for LongDescDocument$LongDesc$Isprivate.
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
                public static LongDescDocument.LongDesc.Isprivate newValue(java.lang.Object obj) {
                  return (LongDescDocument.LongDesc.Isprivate) type.newValue( obj ); }
                
                public static LongDescDocument.LongDesc.Isprivate newInstance() {
                  return (LongDescDocument.LongDesc.Isprivate) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
                
                public static LongDescDocument.LongDesc.Isprivate newInstance(org.apache.xmlbeans.XmlOptions options) {
                  return (LongDescDocument.LongDesc.Isprivate) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
                
                private Factory() { } // No instance of this class allowed
            }
        }
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static LongDescDocument.LongDesc newInstance() {
              return (LongDescDocument.LongDesc) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static LongDescDocument.LongDesc newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (LongDescDocument.LongDesc) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static LongDescDocument newInstance() {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static LongDescDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static LongDescDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static LongDescDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static LongDescDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static LongDescDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static LongDescDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static LongDescDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static LongDescDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static LongDescDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static LongDescDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static LongDescDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static LongDescDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static LongDescDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static LongDescDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static LongDescDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static LongDescDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static LongDescDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (LongDescDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
