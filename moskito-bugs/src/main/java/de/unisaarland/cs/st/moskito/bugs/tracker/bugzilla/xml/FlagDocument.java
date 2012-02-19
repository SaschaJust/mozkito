/*
 * An XML document type.
 * Localname: flag
 * Namespace: 
 * Java type: FlagDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml;


/**
 * A document containing one flag(@) element.
 *
 * This is a complex type.
 */
public interface FlagDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(FlagDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("flag2365doctype");
    
    /**
     * Gets the "flag" element
     */
    FlagDocument.Flag getFlag();
    
    /**
     * Sets the "flag" element
     */
    void setFlag(FlagDocument.Flag flag);
    
    /**
     * Appends and returns a new empty "flag" element
     */
    FlagDocument.Flag addNewFlag();
    
    /**
     * An XML flag(@).
     *
     * This is a complex type.
     */
    public interface Flag extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Flag.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("flag766delemtype");
        
        /**
         * Gets the "name" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType getName();
        
        /**
         * Sets the "name" attribute
         */
        void setName(org.apache.xmlbeans.XmlAnySimpleType name);
        
        /**
         * Appends and returns a new empty "name" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType addNewName();
        
        /**
         * Gets the "id" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType getId();
        
        /**
         * Sets the "id" attribute
         */
        void setId(org.apache.xmlbeans.XmlAnySimpleType id);
        
        /**
         * Appends and returns a new empty "id" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType addNewId();
        
        /**
         * Gets the "type_id" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType getTypeId();
        
        /**
         * Sets the "type_id" attribute
         */
        void setTypeId(org.apache.xmlbeans.XmlAnySimpleType typeId);
        
        /**
         * Appends and returns a new empty "type_id" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType addNewTypeId();
        
        /**
         * Gets the "status" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType getStatus();
        
        /**
         * Sets the "status" attribute
         */
        void setStatus(org.apache.xmlbeans.XmlAnySimpleType status);
        
        /**
         * Appends and returns a new empty "status" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType addNewStatus();
        
        /**
         * Gets the "setter" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType getSetter();
        
        /**
         * Sets the "setter" attribute
         */
        void setSetter(org.apache.xmlbeans.XmlAnySimpleType setter);
        
        /**
         * Appends and returns a new empty "setter" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType addNewSetter();
        
        /**
         * Gets the "requestee" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType getRequestee();
        
        /**
         * True if has "requestee" attribute
         */
        boolean isSetRequestee();
        
        /**
         * Sets the "requestee" attribute
         */
        void setRequestee(org.apache.xmlbeans.XmlAnySimpleType requestee);
        
        /**
         * Appends and returns a new empty "requestee" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType addNewRequestee();
        
        /**
         * Unsets the "requestee" attribute
         */
        void unsetRequestee();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static FlagDocument.Flag newInstance() {
              return (FlagDocument.Flag) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static FlagDocument.Flag newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (FlagDocument.Flag) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static FlagDocument newInstance() {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static FlagDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static FlagDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static FlagDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static FlagDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static FlagDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static FlagDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static FlagDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static FlagDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static FlagDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static FlagDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static FlagDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static FlagDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static FlagDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static FlagDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static FlagDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static FlagDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static FlagDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (FlagDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
