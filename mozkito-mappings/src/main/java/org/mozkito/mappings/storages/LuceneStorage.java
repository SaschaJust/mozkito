/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just - mozkito.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.mozkito.mappings.storages;

import java.util.HashMap;
import java.util.List;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.mozkito.issues.tracker.model.Comment;
import org.mozkito.issues.tracker.model.Report;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
/**
 * The Class LuceneStorage.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class LuceneStorage extends Storage {
	
	/** The analyzer. */
	private Analyzer                        analyzer         = null;
	
	/** The report directory. */
	private final Directory                 reportDirectory  = new RAMDirectory();
	
	/** The iwriter reports. */
	private IndexWriter                     iwriterReports   = null;
	
	/** The report documents. */
	private final HashMap<String, Document> reportDocuments  = new HashMap<String, Document>();
	
	/** The isearcher reports. */
	private IndexSearcher                   isearcherReports = null;
	
	/**
	 * Instantiates a new lucene storage.
	 */
	public LuceneStorage() {
		
	}
	
	/**
	 * Adds the report document.
	 * 
	 * @param report
	 *            the report
	 */
	private void addReportDocument(final Report report) {
		final Document doc = new Document();
		doc.add(new Field("summary", report.getSummary(), Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("description", report.getDescription(), Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("bugid", "" + report.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		for (final Comment comment : report.getComments()) {
			doc.add(new Field("comment", "" + comment.getMessage(), Field.Store.YES, Field.Index.ANALYZED));
		}
		this.reportDocuments.put(report.getId(), doc);
		try {
			this.iwriterReports.addDocument(doc);
		} catch (final Exception e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * Gets the analyzer.
	 * 
	 * @return the analyzer
	 */
	public Analyzer getAnalyzer() {
		return this.analyzer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.register.Registered#getDescription ()
	 */
	@Override
	public String getDescription() {
		return "Manages the lucene search engine data objects (required by several engines).";
	}
	
	/**
	 * Gets the isearcher reports.
	 * 
	 * @return the isearcherReports
	 */
	public IndexSearcher getIsearcherReports() {
		return this.isearcherReports;
	}
	
	/**
	 * Gets the iwriter reports.
	 * 
	 * @return the iwriterReports
	 */
	public IndexWriter getIwriterReports() {
		return this.iwriterReports;
	}
	
	/**
	 * Gets the report directory.
	 * 
	 * @return the reportDirectory
	 */
	public Directory getReportDirectory() {
		return this.reportDirectory;
	}
	
	/**
	 * Gets the report documents.
	 * 
	 * @return the reportDocuments
	 */
	public HashMap<String, Document> getReportDocuments() {
		return this.reportDocuments;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.storages.MappingStorage#loadData
	 * (de.unisaarland.cs.st.moskito.persistence.PersistenceUtil)
	 */
	@Override
	public void loadData(final PersistenceUtil util) {
		final Criteria<Report> criteria = util.createCriteria(Report.class);
		final List<Report> list = util.load(criteria);
		for (final Report report : list) {
			addReportDocument(report);
		}
		try {
			this.iwriterReports.close();
		} catch (final Exception e) {
			throw new UnrecoverableError(e);
		} finally {
			try {
				this.isearcherReports = new IndexSearcher(IndexReader.open(this.reportDirectory, true));
			} catch (final Exception e) {
				throw new UnrecoverableError(e);
			}
		}
	}
	
	/**
	 * Sets the analyzer.
	 * 
	 * @param analyzer
	 *            the analyzer to set
	 */
	public void setAnalyzer(final Analyzer analyzer) {
		this.analyzer = analyzer;
		final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_31, analyzer);
		try {
			this.iwriterReports = new IndexWriter(this.reportDirectory, indexWriterConfig);
		} catch (final Exception e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * Sets the isearcher reports.
	 * 
	 * @param isearcherReports
	 *            the isearcherReports to set
	 */
	public void setIsearcherReports(final IndexSearcher isearcherReports) {
		this.isearcherReports = isearcherReports;
	}
	
	/**
	 * Sets the iwriter reports.
	 * 
	 * @param iwriterReports
	 *            the iwriterReports to set
	 */
	public void setIwriterReports(final IndexWriter iwriterReports) {
		this.iwriterReports = iwriterReports;
	}
}
