/*
 * An XML document type.
 * Localname: bug
 * Namespace: 
 * Java type: BugDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml;


/**
 * A document containing one bug(@) element.
 *
 * This is a complex type.
 */
public interface BugDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(BugDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("bug775bdoctype");
    
    /**
     * Gets the "bug" element
     */
    BugDocument.Bug getBug();
    
    /**
     * Sets the "bug" element
     */
    void setBug(BugDocument.Bug bug);
    
    /**
     * Appends and returns a new empty "bug" element
     */
    BugDocument.Bug addNewBug();
    
    /**
     * An XML bug(@).
     *
     * This is a complex type.
     */
    public interface Bug extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Bug.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("bug8a4belemtype");
        
        /**
         * Gets the "bug_id" element
         */
        java.lang.String getBugId();
        
        /**
         * Gets (as xml) the "bug_id" element
         */
        org.apache.xmlbeans.XmlString xgetBugId();
        
        /**
         * Sets the "bug_id" element
         */
        void setBugId(java.lang.String bugId);
        
        /**
         * Sets (as xml) the "bug_id" element
         */
        void xsetBugId(org.apache.xmlbeans.XmlString bugId);
        
        /**
         * Gets the "alias" element
         */
        java.lang.String getAlias();
        
        /**
         * Gets (as xml) the "alias" element
         */
        org.apache.xmlbeans.XmlString xgetAlias();
        
        /**
         * True if has "alias" element
         */
        boolean isSetAlias();
        
        /**
         * Sets the "alias" element
         */
        void setAlias(java.lang.String alias);
        
        /**
         * Sets (as xml) the "alias" element
         */
        void xsetAlias(org.apache.xmlbeans.XmlString alias);
        
        /**
         * Unsets the "alias" element
         */
        void unsetAlias();
        
        /**
         * Gets the "creation_ts" element
         */
        java.lang.String getCreationTs();
        
        /**
         * Gets (as xml) the "creation_ts" element
         */
        org.apache.xmlbeans.XmlString xgetCreationTs();
        
        /**
         * True if has "creation_ts" element
         */
        boolean isSetCreationTs();
        
        /**
         * Sets the "creation_ts" element
         */
        void setCreationTs(java.lang.String creationTs);
        
        /**
         * Sets (as xml) the "creation_ts" element
         */
        void xsetCreationTs(org.apache.xmlbeans.XmlString creationTs);
        
        /**
         * Unsets the "creation_ts" element
         */
        void unsetCreationTs();
        
        /**
         * Gets the "short_desc" element
         */
        java.lang.String getShortDesc();
        
        /**
         * Gets (as xml) the "short_desc" element
         */
        org.apache.xmlbeans.XmlString xgetShortDesc();
        
        /**
         * True if has "short_desc" element
         */
        boolean isSetShortDesc();
        
        /**
         * Sets the "short_desc" element
         */
        void setShortDesc(java.lang.String shortDesc);
        
        /**
         * Sets (as xml) the "short_desc" element
         */
        void xsetShortDesc(org.apache.xmlbeans.XmlString shortDesc);
        
        /**
         * Unsets the "short_desc" element
         */
        void unsetShortDesc();
        
        /**
         * Gets the "delta_ts" element
         */
        java.lang.String getDeltaTs();
        
        /**
         * Gets (as xml) the "delta_ts" element
         */
        org.apache.xmlbeans.XmlString xgetDeltaTs();
        
        /**
         * True if has "delta_ts" element
         */
        boolean isSetDeltaTs();
        
        /**
         * Sets the "delta_ts" element
         */
        void setDeltaTs(java.lang.String deltaTs);
        
        /**
         * Sets (as xml) the "delta_ts" element
         */
        void xsetDeltaTs(org.apache.xmlbeans.XmlString deltaTs);
        
        /**
         * Unsets the "delta_ts" element
         */
        void unsetDeltaTs();
        
        /**
         * Gets the "reporter_accessible" element
         */
        java.lang.String getReporterAccessible();
        
        /**
         * Gets (as xml) the "reporter_accessible" element
         */
        org.apache.xmlbeans.XmlString xgetReporterAccessible();
        
        /**
         * True if has "reporter_accessible" element
         */
        boolean isSetReporterAccessible();
        
        /**
         * Sets the "reporter_accessible" element
         */
        void setReporterAccessible(java.lang.String reporterAccessible);
        
        /**
         * Sets (as xml) the "reporter_accessible" element
         */
        void xsetReporterAccessible(org.apache.xmlbeans.XmlString reporterAccessible);
        
        /**
         * Unsets the "reporter_accessible" element
         */
        void unsetReporterAccessible();
        
        /**
         * Gets the "cclist_accessible" element
         */
        java.lang.String getCclistAccessible();
        
        /**
         * Gets (as xml) the "cclist_accessible" element
         */
        org.apache.xmlbeans.XmlString xgetCclistAccessible();
        
        /**
         * True if has "cclist_accessible" element
         */
        boolean isSetCclistAccessible();
        
        /**
         * Sets the "cclist_accessible" element
         */
        void setCclistAccessible(java.lang.String cclistAccessible);
        
        /**
         * Sets (as xml) the "cclist_accessible" element
         */
        void xsetCclistAccessible(org.apache.xmlbeans.XmlString cclistAccessible);
        
        /**
         * Unsets the "cclist_accessible" element
         */
        void unsetCclistAccessible();
        
        /**
         * Gets the "classification_id" element
         */
        java.lang.String getClassificationId();
        
        /**
         * Gets (as xml) the "classification_id" element
         */
        org.apache.xmlbeans.XmlString xgetClassificationId();
        
        /**
         * True if has "classification_id" element
         */
        boolean isSetClassificationId();
        
        /**
         * Sets the "classification_id" element
         */
        void setClassificationId(java.lang.String classificationId);
        
        /**
         * Sets (as xml) the "classification_id" element
         */
        void xsetClassificationId(org.apache.xmlbeans.XmlString classificationId);
        
        /**
         * Unsets the "classification_id" element
         */
        void unsetClassificationId();
        
        /**
         * Gets the "classification" element
         */
        java.lang.String getClassification();
        
        /**
         * Gets (as xml) the "classification" element
         */
        org.apache.xmlbeans.XmlString xgetClassification();
        
        /**
         * True if has "classification" element
         */
        boolean isSetClassification();
        
        /**
         * Sets the "classification" element
         */
        void setClassification(java.lang.String classification);
        
        /**
         * Sets (as xml) the "classification" element
         */
        void xsetClassification(org.apache.xmlbeans.XmlString classification);
        
        /**
         * Unsets the "classification" element
         */
        void unsetClassification();
        
        /**
         * Gets the "product" element
         */
        java.lang.String getProduct();
        
        /**
         * Gets (as xml) the "product" element
         */
        org.apache.xmlbeans.XmlString xgetProduct();
        
        /**
         * True if has "product" element
         */
        boolean isSetProduct();
        
        /**
         * Sets the "product" element
         */
        void setProduct(java.lang.String product);
        
        /**
         * Sets (as xml) the "product" element
         */
        void xsetProduct(org.apache.xmlbeans.XmlString product);
        
        /**
         * Unsets the "product" element
         */
        void unsetProduct();
        
        /**
         * Gets the "component" element
         */
        java.lang.String getComponent();
        
        /**
         * Gets (as xml) the "component" element
         */
        org.apache.xmlbeans.XmlString xgetComponent();
        
        /**
         * True if has "component" element
         */
        boolean isSetComponent();
        
        /**
         * Sets the "component" element
         */
        void setComponent(java.lang.String component);
        
        /**
         * Sets (as xml) the "component" element
         */
        void xsetComponent(org.apache.xmlbeans.XmlString component);
        
        /**
         * Unsets the "component" element
         */
        void unsetComponent();
        
        /**
         * Gets the "version" element
         */
        java.lang.String getVersion();
        
        /**
         * Gets (as xml) the "version" element
         */
        org.apache.xmlbeans.XmlString xgetVersion();
        
        /**
         * True if has "version" element
         */
        boolean isSetVersion();
        
        /**
         * Sets the "version" element
         */
        void setVersion(java.lang.String version);
        
        /**
         * Sets (as xml) the "version" element
         */
        void xsetVersion(org.apache.xmlbeans.XmlString version);
        
        /**
         * Unsets the "version" element
         */
        void unsetVersion();
        
        /**
         * Gets the "rep_platform" element
         */
        java.lang.String getRepPlatform();
        
        /**
         * Gets (as xml) the "rep_platform" element
         */
        org.apache.xmlbeans.XmlString xgetRepPlatform();
        
        /**
         * True if has "rep_platform" element
         */
        boolean isSetRepPlatform();
        
        /**
         * Sets the "rep_platform" element
         */
        void setRepPlatform(java.lang.String repPlatform);
        
        /**
         * Sets (as xml) the "rep_platform" element
         */
        void xsetRepPlatform(org.apache.xmlbeans.XmlString repPlatform);
        
        /**
         * Unsets the "rep_platform" element
         */
        void unsetRepPlatform();
        
        /**
         * Gets the "op_sys" element
         */
        java.lang.String getOpSys();
        
        /**
         * Gets (as xml) the "op_sys" element
         */
        org.apache.xmlbeans.XmlString xgetOpSys();
        
        /**
         * True if has "op_sys" element
         */
        boolean isSetOpSys();
        
        /**
         * Sets the "op_sys" element
         */
        void setOpSys(java.lang.String opSys);
        
        /**
         * Sets (as xml) the "op_sys" element
         */
        void xsetOpSys(org.apache.xmlbeans.XmlString opSys);
        
        /**
         * Unsets the "op_sys" element
         */
        void unsetOpSys();
        
        /**
         * Gets the "bug_status" element
         */
        java.lang.String getBugStatus();
        
        /**
         * Gets (as xml) the "bug_status" element
         */
        org.apache.xmlbeans.XmlString xgetBugStatus();
        
        /**
         * True if has "bug_status" element
         */
        boolean isSetBugStatus();
        
        /**
         * Sets the "bug_status" element
         */
        void setBugStatus(java.lang.String bugStatus);
        
        /**
         * Sets (as xml) the "bug_status" element
         */
        void xsetBugStatus(org.apache.xmlbeans.XmlString bugStatus);
        
        /**
         * Unsets the "bug_status" element
         */
        void unsetBugStatus();
        
        /**
         * Gets the "resolution" element
         */
        java.lang.String getResolution();
        
        /**
         * Gets (as xml) the "resolution" element
         */
        org.apache.xmlbeans.XmlString xgetResolution();
        
        /**
         * True if has "resolution" element
         */
        boolean isSetResolution();
        
        /**
         * Sets the "resolution" element
         */
        void setResolution(java.lang.String resolution);
        
        /**
         * Sets (as xml) the "resolution" element
         */
        void xsetResolution(org.apache.xmlbeans.XmlString resolution);
        
        /**
         * Unsets the "resolution" element
         */
        void unsetResolution();
        
        /**
         * Gets the "dup_id" element
         */
        java.lang.String getDupId();
        
        /**
         * Gets (as xml) the "dup_id" element
         */
        org.apache.xmlbeans.XmlString xgetDupId();
        
        /**
         * True if has "dup_id" element
         */
        boolean isSetDupId();
        
        /**
         * Sets the "dup_id" element
         */
        void setDupId(java.lang.String dupId);
        
        /**
         * Sets (as xml) the "dup_id" element
         */
        void xsetDupId(org.apache.xmlbeans.XmlString dupId);
        
        /**
         * Unsets the "dup_id" element
         */
        void unsetDupId();
        
        /**
         * Gets array of all "see_also" elements
         */
        java.lang.String[] getSeeAlsoArray();
        
        /**
         * Gets ith "see_also" element
         */
        java.lang.String getSeeAlsoArray(int i);
        
        /**
         * Gets (as xml) array of all "see_also" elements
         */
        org.apache.xmlbeans.XmlString[] xgetSeeAlsoArray();
        
        /**
         * Gets (as xml) ith "see_also" element
         */
        org.apache.xmlbeans.XmlString xgetSeeAlsoArray(int i);
        
        /**
         * Returns number of "see_also" element
         */
        int sizeOfSeeAlsoArray();
        
        /**
         * Sets array of all "see_also" element
         */
        void setSeeAlsoArray(java.lang.String[] seeAlsoArray);
        
        /**
         * Sets ith "see_also" element
         */
        void setSeeAlsoArray(int i, java.lang.String seeAlso);
        
        /**
         * Sets (as xml) array of all "see_also" element
         */
        void xsetSeeAlsoArray(org.apache.xmlbeans.XmlString[] seeAlsoArray);
        
        /**
         * Sets (as xml) ith "see_also" element
         */
        void xsetSeeAlsoArray(int i, org.apache.xmlbeans.XmlString seeAlso);
        
        /**
         * Inserts the value as the ith "see_also" element
         */
        void insertSeeAlso(int i, java.lang.String seeAlso);
        
        /**
         * Appends the value as the last "see_also" element
         */
        void addSeeAlso(java.lang.String seeAlso);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "see_also" element
         */
        org.apache.xmlbeans.XmlString insertNewSeeAlso(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "see_also" element
         */
        org.apache.xmlbeans.XmlString addNewSeeAlso();
        
        /**
         * Removes the ith "see_also" element
         */
        void removeSeeAlso(int i);
        
        /**
         * Gets the "bug_file_loc" element
         */
        java.lang.String getBugFileLoc();
        
        /**
         * Gets (as xml) the "bug_file_loc" element
         */
        org.apache.xmlbeans.XmlString xgetBugFileLoc();
        
        /**
         * True if has "bug_file_loc" element
         */
        boolean isSetBugFileLoc();
        
        /**
         * Sets the "bug_file_loc" element
         */
        void setBugFileLoc(java.lang.String bugFileLoc);
        
        /**
         * Sets (as xml) the "bug_file_loc" element
         */
        void xsetBugFileLoc(org.apache.xmlbeans.XmlString bugFileLoc);
        
        /**
         * Unsets the "bug_file_loc" element
         */
        void unsetBugFileLoc();
        
        /**
         * Gets the "status_whiteboard" element
         */
        java.lang.String getStatusWhiteboard();
        
        /**
         * Gets (as xml) the "status_whiteboard" element
         */
        org.apache.xmlbeans.XmlString xgetStatusWhiteboard();
        
        /**
         * True if has "status_whiteboard" element
         */
        boolean isSetStatusWhiteboard();
        
        /**
         * Sets the "status_whiteboard" element
         */
        void setStatusWhiteboard(java.lang.String statusWhiteboard);
        
        /**
         * Sets (as xml) the "status_whiteboard" element
         */
        void xsetStatusWhiteboard(org.apache.xmlbeans.XmlString statusWhiteboard);
        
        /**
         * Unsets the "status_whiteboard" element
         */
        void unsetStatusWhiteboard();
        
        /**
         * Gets array of all "keywords" elements
         */
        java.lang.String[] getKeywordsArray();
        
        /**
         * Gets ith "keywords" element
         */
        java.lang.String getKeywordsArray(int i);
        
        /**
         * Gets (as xml) array of all "keywords" elements
         */
        org.apache.xmlbeans.XmlString[] xgetKeywordsArray();
        
        /**
         * Gets (as xml) ith "keywords" element
         */
        org.apache.xmlbeans.XmlString xgetKeywordsArray(int i);
        
        /**
         * Returns number of "keywords" element
         */
        int sizeOfKeywordsArray();
        
        /**
         * Sets array of all "keywords" element
         */
        void setKeywordsArray(java.lang.String[] keywordsArray);
        
        /**
         * Sets ith "keywords" element
         */
        void setKeywordsArray(int i, java.lang.String keywords);
        
        /**
         * Sets (as xml) array of all "keywords" element
         */
        void xsetKeywordsArray(org.apache.xmlbeans.XmlString[] keywordsArray);
        
        /**
         * Sets (as xml) ith "keywords" element
         */
        void xsetKeywordsArray(int i, org.apache.xmlbeans.XmlString keywords);
        
        /**
         * Inserts the value as the ith "keywords" element
         */
        void insertKeywords(int i, java.lang.String keywords);
        
        /**
         * Appends the value as the last "keywords" element
         */
        void addKeywords(java.lang.String keywords);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "keywords" element
         */
        org.apache.xmlbeans.XmlString insertNewKeywords(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "keywords" element
         */
        org.apache.xmlbeans.XmlString addNewKeywords();
        
        /**
         * Removes the ith "keywords" element
         */
        void removeKeywords(int i);
        
        /**
         * Gets the "priority" element
         */
        java.lang.String getPriority();
        
        /**
         * Gets (as xml) the "priority" element
         */
        org.apache.xmlbeans.XmlString xgetPriority();
        
        /**
         * True if has "priority" element
         */
        boolean isSetPriority();
        
        /**
         * Sets the "priority" element
         */
        void setPriority(java.lang.String priority);
        
        /**
         * Sets (as xml) the "priority" element
         */
        void xsetPriority(org.apache.xmlbeans.XmlString priority);
        
        /**
         * Unsets the "priority" element
         */
        void unsetPriority();
        
        /**
         * Gets the "bug_severity" element
         */
        java.lang.String getBugSeverity();
        
        /**
         * Gets (as xml) the "bug_severity" element
         */
        org.apache.xmlbeans.XmlString xgetBugSeverity();
        
        /**
         * True if has "bug_severity" element
         */
        boolean isSetBugSeverity();
        
        /**
         * Sets the "bug_severity" element
         */
        void setBugSeverity(java.lang.String bugSeverity);
        
        /**
         * Sets (as xml) the "bug_severity" element
         */
        void xsetBugSeverity(org.apache.xmlbeans.XmlString bugSeverity);
        
        /**
         * Unsets the "bug_severity" element
         */
        void unsetBugSeverity();
        
        /**
         * Gets the "target_milestone" element
         */
        java.lang.String getTargetMilestone();
        
        /**
         * Gets (as xml) the "target_milestone" element
         */
        org.apache.xmlbeans.XmlString xgetTargetMilestone();
        
        /**
         * True if has "target_milestone" element
         */
        boolean isSetTargetMilestone();
        
        /**
         * Sets the "target_milestone" element
         */
        void setTargetMilestone(java.lang.String targetMilestone);
        
        /**
         * Sets (as xml) the "target_milestone" element
         */
        void xsetTargetMilestone(org.apache.xmlbeans.XmlString targetMilestone);
        
        /**
         * Unsets the "target_milestone" element
         */
        void unsetTargetMilestone();
        
        /**
         * Gets array of all "dependson" elements
         */
        java.lang.String[] getDependsonArray();
        
        /**
         * Gets ith "dependson" element
         */
        java.lang.String getDependsonArray(int i);
        
        /**
         * Gets (as xml) array of all "dependson" elements
         */
        org.apache.xmlbeans.XmlString[] xgetDependsonArray();
        
        /**
         * Gets (as xml) ith "dependson" element
         */
        org.apache.xmlbeans.XmlString xgetDependsonArray(int i);
        
        /**
         * Returns number of "dependson" element
         */
        int sizeOfDependsonArray();
        
        /**
         * Sets array of all "dependson" element
         */
        void setDependsonArray(java.lang.String[] dependsonArray);
        
        /**
         * Sets ith "dependson" element
         */
        void setDependsonArray(int i, java.lang.String dependson);
        
        /**
         * Sets (as xml) array of all "dependson" element
         */
        void xsetDependsonArray(org.apache.xmlbeans.XmlString[] dependsonArray);
        
        /**
         * Sets (as xml) ith "dependson" element
         */
        void xsetDependsonArray(int i, org.apache.xmlbeans.XmlString dependson);
        
        /**
         * Inserts the value as the ith "dependson" element
         */
        void insertDependson(int i, java.lang.String dependson);
        
        /**
         * Appends the value as the last "dependson" element
         */
        void addDependson(java.lang.String dependson);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "dependson" element
         */
        org.apache.xmlbeans.XmlString insertNewDependson(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "dependson" element
         */
        org.apache.xmlbeans.XmlString addNewDependson();
        
        /**
         * Removes the ith "dependson" element
         */
        void removeDependson(int i);
        
        /**
         * Gets array of all "blocked" elements
         */
        java.lang.String[] getBlockedArray();
        
        /**
         * Gets ith "blocked" element
         */
        java.lang.String getBlockedArray(int i);
        
        /**
         * Gets (as xml) array of all "blocked" elements
         */
        org.apache.xmlbeans.XmlString[] xgetBlockedArray();
        
        /**
         * Gets (as xml) ith "blocked" element
         */
        org.apache.xmlbeans.XmlString xgetBlockedArray(int i);
        
        /**
         * Returns number of "blocked" element
         */
        int sizeOfBlockedArray();
        
        /**
         * Sets array of all "blocked" element
         */
        void setBlockedArray(java.lang.String[] blockedArray);
        
        /**
         * Sets ith "blocked" element
         */
        void setBlockedArray(int i, java.lang.String blocked);
        
        /**
         * Sets (as xml) array of all "blocked" element
         */
        void xsetBlockedArray(org.apache.xmlbeans.XmlString[] blockedArray);
        
        /**
         * Sets (as xml) ith "blocked" element
         */
        void xsetBlockedArray(int i, org.apache.xmlbeans.XmlString blocked);
        
        /**
         * Inserts the value as the ith "blocked" element
         */
        void insertBlocked(int i, java.lang.String blocked);
        
        /**
         * Appends the value as the last "blocked" element
         */
        void addBlocked(java.lang.String blocked);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "blocked" element
         */
        org.apache.xmlbeans.XmlString insertNewBlocked(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "blocked" element
         */
        org.apache.xmlbeans.XmlString addNewBlocked();
        
        /**
         * Removes the ith "blocked" element
         */
        void removeBlocked(int i);
        
        /**
         * Gets the "everconfirmed" element
         */
        java.lang.String getEverconfirmed();
        
        /**
         * Gets (as xml) the "everconfirmed" element
         */
        org.apache.xmlbeans.XmlString xgetEverconfirmed();
        
        /**
         * True if has "everconfirmed" element
         */
        boolean isSetEverconfirmed();
        
        /**
         * Sets the "everconfirmed" element
         */
        void setEverconfirmed(java.lang.String everconfirmed);
        
        /**
         * Sets (as xml) the "everconfirmed" element
         */
        void xsetEverconfirmed(org.apache.xmlbeans.XmlString everconfirmed);
        
        /**
         * Unsets the "everconfirmed" element
         */
        void unsetEverconfirmed();
        
        /**
         * Gets the "reporter" element
         */
        ReporterDocument.Reporter getReporter();
        
        /**
         * True if has "reporter" element
         */
        boolean isSetReporter();
        
        /**
         * Sets the "reporter" element
         */
        void setReporter(ReporterDocument.Reporter reporter);
        
        /**
         * Appends and returns a new empty "reporter" element
         */
        ReporterDocument.Reporter addNewReporter();
        
        /**
         * Unsets the "reporter" element
         */
        void unsetReporter();
        
        /**
         * Gets the "assigned_to" element
         */
        AssignedToDocument.AssignedTo getAssignedTo();
        
        /**
         * True if has "assigned_to" element
         */
        boolean isSetAssignedTo();
        
        /**
         * Sets the "assigned_to" element
         */
        void setAssignedTo(AssignedToDocument.AssignedTo assignedTo);
        
        /**
         * Appends and returns a new empty "assigned_to" element
         */
        AssignedToDocument.AssignedTo addNewAssignedTo();
        
        /**
         * Unsets the "assigned_to" element
         */
        void unsetAssignedTo();
        
        /**
         * Gets array of all "cc" elements
         */
        java.lang.String[] getCcArray();
        
        /**
         * Gets ith "cc" element
         */
        java.lang.String getCcArray(int i);
        
        /**
         * Gets (as xml) array of all "cc" elements
         */
        org.apache.xmlbeans.XmlString[] xgetCcArray();
        
        /**
         * Gets (as xml) ith "cc" element
         */
        org.apache.xmlbeans.XmlString xgetCcArray(int i);
        
        /**
         * Returns number of "cc" element
         */
        int sizeOfCcArray();
        
        /**
         * Sets array of all "cc" element
         */
        void setCcArray(java.lang.String[] ccArray);
        
        /**
         * Sets ith "cc" element
         */
        void setCcArray(int i, java.lang.String cc);
        
        /**
         * Sets (as xml) array of all "cc" element
         */
        void xsetCcArray(org.apache.xmlbeans.XmlString[] ccArray);
        
        /**
         * Sets (as xml) ith "cc" element
         */
        void xsetCcArray(int i, org.apache.xmlbeans.XmlString cc);
        
        /**
         * Inserts the value as the ith "cc" element
         */
        void insertCc(int i, java.lang.String cc);
        
        /**
         * Appends the value as the last "cc" element
         */
        void addCc(java.lang.String cc);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "cc" element
         */
        org.apache.xmlbeans.XmlString insertNewCc(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "cc" element
         */
        org.apache.xmlbeans.XmlString addNewCc();
        
        /**
         * Removes the ith "cc" element
         */
        void removeCc(int i);
        
        /**
         * Gets the "estimated_time" element
         */
        java.lang.String getEstimatedTime();
        
        /**
         * Gets (as xml) the "estimated_time" element
         */
        org.apache.xmlbeans.XmlString xgetEstimatedTime();
        
        /**
         * True if has "estimated_time" element
         */
        boolean isSetEstimatedTime();
        
        /**
         * Sets the "estimated_time" element
         */
        void setEstimatedTime(java.lang.String estimatedTime);
        
        /**
         * Sets (as xml) the "estimated_time" element
         */
        void xsetEstimatedTime(org.apache.xmlbeans.XmlString estimatedTime);
        
        /**
         * Unsets the "estimated_time" element
         */
        void unsetEstimatedTime();
        
        /**
         * Gets the "remaining_time" element
         */
        java.lang.String getRemainingTime();
        
        /**
         * Gets (as xml) the "remaining_time" element
         */
        org.apache.xmlbeans.XmlString xgetRemainingTime();
        
        /**
         * True if has "remaining_time" element
         */
        boolean isSetRemainingTime();
        
        /**
         * Sets the "remaining_time" element
         */
        void setRemainingTime(java.lang.String remainingTime);
        
        /**
         * Sets (as xml) the "remaining_time" element
         */
        void xsetRemainingTime(org.apache.xmlbeans.XmlString remainingTime);
        
        /**
         * Unsets the "remaining_time" element
         */
        void unsetRemainingTime();
        
        /**
         * Gets the "actual_time" element
         */
        java.lang.String getActualTime();
        
        /**
         * Gets (as xml) the "actual_time" element
         */
        org.apache.xmlbeans.XmlString xgetActualTime();
        
        /**
         * True if has "actual_time" element
         */
        boolean isSetActualTime();
        
        /**
         * Sets the "actual_time" element
         */
        void setActualTime(java.lang.String actualTime);
        
        /**
         * Sets (as xml) the "actual_time" element
         */
        void xsetActualTime(org.apache.xmlbeans.XmlString actualTime);
        
        /**
         * Unsets the "actual_time" element
         */
        void unsetActualTime();
        
        /**
         * Gets the "deadline" element
         */
        java.lang.String getDeadline();
        
        /**
         * Gets (as xml) the "deadline" element
         */
        org.apache.xmlbeans.XmlString xgetDeadline();
        
        /**
         * True if has "deadline" element
         */
        boolean isSetDeadline();
        
        /**
         * Sets the "deadline" element
         */
        void setDeadline(java.lang.String deadline);
        
        /**
         * Sets (as xml) the "deadline" element
         */
        void xsetDeadline(org.apache.xmlbeans.XmlString deadline);
        
        /**
         * Unsets the "deadline" element
         */
        void unsetDeadline();
        
        /**
         * Gets the "qa_contact" element
         */
        QaContactDocument.QaContact getQaContact();
        
        /**
         * True if has "qa_contact" element
         */
        boolean isSetQaContact();
        
        /**
         * Sets the "qa_contact" element
         */
        void setQaContact(QaContactDocument.QaContact qaContact);
        
        /**
         * Appends and returns a new empty "qa_contact" element
         */
        QaContactDocument.QaContact addNewQaContact();
        
        /**
         * Unsets the "qa_contact" element
         */
        void unsetQaContact();
        
        /**
         * Gets the "votes" element
         */
        java.lang.String getVotes();
        
        /**
         * Gets (as xml) the "votes" element
         */
        org.apache.xmlbeans.XmlString xgetVotes();
        
        /**
         * True if has "votes" element
         */
        boolean isSetVotes();
        
        /**
         * Sets the "votes" element
         */
        void setVotes(java.lang.String votes);
        
        /**
         * Sets (as xml) the "votes" element
         */
        void xsetVotes(org.apache.xmlbeans.XmlString votes);
        
        /**
         * Unsets the "votes" element
         */
        void unsetVotes();
        
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
         * Gets array of all "group" elements
         */
        GroupDocument.Group[] getGroupArray();
        
        /**
         * Gets ith "group" element
         */
        GroupDocument.Group getGroupArray(int i);
        
        /**
         * Returns number of "group" element
         */
        int sizeOfGroupArray();
        
        /**
         * Sets array of all "group" element
         */
        void setGroupArray(GroupDocument.Group[] groupArray);
        
        /**
         * Sets ith "group" element
         */
        void setGroupArray(int i, GroupDocument.Group group);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "group" element
         */
        GroupDocument.Group insertNewGroup(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "group" element
         */
        GroupDocument.Group addNewGroup();
        
        /**
         * Removes the ith "group" element
         */
        void removeGroup(int i);
        
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
         * Gets array of all "long_desc" elements
         */
        LongDescDocument.LongDesc[] getLongDescArray();
        
        /**
         * Gets ith "long_desc" element
         */
        LongDescDocument.LongDesc getLongDescArray(int i);
        
        /**
         * Returns number of "long_desc" element
         */
        int sizeOfLongDescArray();
        
        /**
         * Sets array of all "long_desc" element
         */
        void setLongDescArray(LongDescDocument.LongDesc[] longDescArray);
        
        /**
         * Sets ith "long_desc" element
         */
        void setLongDescArray(int i, LongDescDocument.LongDesc longDesc);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "long_desc" element
         */
        LongDescDocument.LongDesc insertNewLongDesc(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "long_desc" element
         */
        LongDescDocument.LongDesc addNewLongDesc();
        
        /**
         * Removes the ith "long_desc" element
         */
        void removeLongDesc(int i);
        
        /**
         * Gets array of all "attachment" elements
         */
        AttachmentDocument.Attachment[] getAttachmentArray();
        
        /**
         * Gets ith "attachment" element
         */
        AttachmentDocument.Attachment getAttachmentArray(int i);
        
        /**
         * Returns number of "attachment" element
         */
        int sizeOfAttachmentArray();
        
        /**
         * Sets array of all "attachment" element
         */
        void setAttachmentArray(AttachmentDocument.Attachment[] attachmentArray);
        
        /**
         * Sets ith "attachment" element
         */
        void setAttachmentArray(int i, AttachmentDocument.Attachment attachment);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "attachment" element
         */
        AttachmentDocument.Attachment insertNewAttachment(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "attachment" element
         */
        AttachmentDocument.Attachment addNewAttachment();
        
        /**
         * Removes the ith "attachment" element
         */
        void removeAttachment(int i);
        
        /**
         * Gets the "error" attribute
         */
        BugDocument.Bug.Error.Enum getError();
        
        /**
         * Gets (as xml) the "error" attribute
         */
        BugDocument.Bug.Error xgetError();
        
        /**
         * True if has "error" attribute
         */
        boolean isSetError();
        
        /**
         * Sets the "error" attribute
         */
        void setError(BugDocument.Bug.Error.Enum error);
        
        /**
         * Sets (as xml) the "error" attribute
         */
        void xsetError(BugDocument.Bug.Error error);
        
        /**
         * Unsets the "error" attribute
         */
        void unsetError();
        
        /**
         * An XML error(@).
         *
         * This is an atomic type that is a restriction of BugDocument$Bug$Error.
         */
        public interface Error extends org.apache.xmlbeans.XmlToken
        {
            public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
                org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Error.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s7AC52C3C5759BA5B719B3ED4187B14DA").resolveHandle("error5dbbattrtype");
            
            org.apache.xmlbeans.StringEnumAbstractBase enumValue();
            void set(org.apache.xmlbeans.StringEnumAbstractBase e);
            
            static final Enum NOT_FOUND = Enum.forString("NotFound");
            static final Enum NOT_PERMITTED = Enum.forString("NotPermitted");
            static final Enum INVALID_BUG_ID = Enum.forString("InvalidBugId");
            
            static final int INT_NOT_FOUND = Enum.INT_NOT_FOUND;
            static final int INT_NOT_PERMITTED = Enum.INT_NOT_PERMITTED;
            static final int INT_INVALID_BUG_ID = Enum.INT_INVALID_BUG_ID;
            
            /**
             * Enumeration value class for BugDocument$Bug$Error.
             * These enum values can be used as follows:
             * <pre>
             * enum.toString(); // returns the string value of the enum
             * enum.intValue(); // returns an int value, useful for switches
             * // e.g., case Enum.INT_NOT_FOUND
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
                
                static final int INT_NOT_FOUND = 1;
                static final int INT_NOT_PERMITTED = 2;
                static final int INT_INVALID_BUG_ID = 3;
                
                public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                    new org.apache.xmlbeans.StringEnumAbstractBase.Table
                (
                    new Enum[]
                    {
                      new Enum("NotFound", INT_NOT_FOUND),
                      new Enum("NotPermitted", INT_NOT_PERMITTED),
                      new Enum("InvalidBugId", INT_INVALID_BUG_ID),
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
                public static BugDocument.Bug.Error newValue(java.lang.Object obj) {
                  return (BugDocument.Bug.Error) type.newValue( obj ); }
                
                public static BugDocument.Bug.Error newInstance() {
                  return (BugDocument.Bug.Error) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
                
                public static BugDocument.Bug.Error newInstance(org.apache.xmlbeans.XmlOptions options) {
                  return (BugDocument.Bug.Error) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
                
                private Factory() { } // No instance of this class allowed
            }
        }
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static BugDocument.Bug newInstance() {
              return (BugDocument.Bug) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static BugDocument.Bug newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (BugDocument.Bug) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static BugDocument newInstance() {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static BugDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static BugDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static BugDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static BugDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static BugDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static BugDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static BugDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static BugDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static BugDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static BugDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static BugDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static BugDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static BugDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static BugDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static BugDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static BugDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static BugDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (BugDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
