/*
 * An XML document type.
 * Localname: bugzilla
 * Namespace: 
 * Java type: BugzillaDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml;


/**
 * A document containing one bugzilla(@) element.
 *
 * This is a complex type.
 */
public interface BugzillaDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(BugzillaDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("bugzilla7ad7doctype");
    
    /**
     * Gets the "bugzilla" element
     */
    BugzillaDocument.Bugzilla getBugzilla();
    
    /**
     * Sets the "bugzilla" element
     */
    void setBugzilla(BugzillaDocument.Bugzilla bugzilla);
    
    /**
     * Appends and returns a new empty "bugzilla" element
     */
    BugzillaDocument.Bugzilla addNewBugzilla();
    
    /**
     * An XML bugzilla(@).
     *
     * This is a complex type.
     */
    public interface Bugzilla extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Bugzilla.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("bugzilla86adelemtype");
        
        /**
         * Gets array of all "bug" elements
         */
        BugDocument.Bug[] getBugArray();
        
        /**
         * Gets ith "bug" element
         */
        BugDocument.Bug getBugArray(int i);
        
        /**
         * Returns number of "bug" element
         */
        int sizeOfBugArray();
        
        /**
         * Sets array of all "bug" element
         */
        void setBugArray(BugDocument.Bug[] bugArray);
        
        /**
         * Sets ith "bug" element
         */
        void setBugArray(int i, BugDocument.Bug bug);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "bug" element
         */
        BugDocument.Bug insertNewBug(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "bug" element
         */
        BugDocument.Bug addNewBug();
        
        /**
         * Removes the ith "bug" element
         */
        void removeBug(int i);
        
        /**
         * Gets the "version" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType getVersion();
        
        /**
         * Sets the "version" attribute
         */
        void setVersion(org.apache.xmlbeans.XmlAnySimpleType version);
        
        /**
         * Appends and returns a new empty "version" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType addNewVersion();
        
        /**
         * Gets the "urlbase" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType getUrlbase();
        
        /**
         * Sets the "urlbase" attribute
         */
        void setUrlbase(org.apache.xmlbeans.XmlAnySimpleType urlbase);
        
        /**
         * Appends and returns a new empty "urlbase" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType addNewUrlbase();
        
        /**
         * Gets the "maintainer" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType getMaintainer();
        
        /**
         * Sets the "maintainer" attribute
         */
        void setMaintainer(org.apache.xmlbeans.XmlAnySimpleType maintainer);
        
        /**
         * Appends and returns a new empty "maintainer" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType addNewMaintainer();
        
        /**
         * Gets the "exporter" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType getExporter();
        
        /**
         * True if has "exporter" attribute
         */
        boolean isSetExporter();
        
        /**
         * Sets the "exporter" attribute
         */
        void setExporter(org.apache.xmlbeans.XmlAnySimpleType exporter);
        
        /**
         * Appends and returns a new empty "exporter" attribute
         */
        org.apache.xmlbeans.XmlAnySimpleType addNewExporter();
        
        /**
         * Unsets the "exporter" attribute
         */
        void unsetExporter();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static BugzillaDocument.Bugzilla newInstance() {
              return (BugzillaDocument.Bugzilla) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static BugzillaDocument.Bugzilla newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (BugzillaDocument.Bugzilla) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static BugzillaDocument newInstance() {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static BugzillaDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static BugzillaDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static BugzillaDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static BugzillaDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static BugzillaDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static BugzillaDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static BugzillaDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static BugzillaDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static BugzillaDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static BugzillaDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static BugzillaDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static BugzillaDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static BugzillaDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static BugzillaDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static BugzillaDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static BugzillaDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static BugzillaDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (BugzillaDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
