/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.mapping.storages;

import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class LuceneStorage extends MappingStorage {
	
	private Analyzer                      analyzer         = null;
	private final Directory               reportDirectory  = new RAMDirectory();
	private IndexWriter                   iwriterReports   = null;
	private final HashMap<Long, Document> reportDocuments  = new HashMap<Long, Document>();
	private IndexSearcher                 isearcherReports = null;
	
	/**
	 * 
	 */
	public LuceneStorage() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param report
	 */
	private void addReportDocument(final Report report) {
		Document doc = new Document();
		doc.add(new Field("summary", report.getSummary(), Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("description", report.getDescription(), Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("bugid", "" + report.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		for (Comment comment : report.getComments()) {
			doc.add(new Field("comment", "" + comment.getMessage(), Field.Store.YES, Field.Index.ANALYZED));
		}
		this.reportDocuments.put(report.getId(), doc);
		try {
			this.iwriterReports.addDocument(doc);
		} catch (Exception e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * @return the analyzer
	 */
	public Analyzer getAnalyzer() {
		return this.analyzer;
	}
	
	/**
	 * @return the isearcherReports
	 */
	public IndexSearcher getIsearcherReports() {
		return this.isearcherReports;
	}
	
	/**
	 * @return the iwriterReports
	 */
	public IndexWriter getIwriterReports() {
		return this.iwriterReports;
	}
	
	/**
	 * @return the reportDirectory
	 */
	public Directory getReportDirectory() {
		return this.reportDirectory;
	}
	
	/**
	 * @return the reportDocuments
	 */
	public HashMap<Long, Document> getReportDocuments() {
		return this.reportDocuments;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.storages.MappingStorage#loadData
	 * (de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil)
	 */
	@Override
	public void loadData(final PersistenceUtil util) {
		Criteria<Report> criteria = util.createCriteria(Report.class);
		List<Report> list = util.load(criteria);
		for (Report report : list) {
			addReportDocument(report);
		}
		try {
			this.iwriterReports.close();
		} catch (Exception e) {
			throw new UnrecoverableError(e);
		} finally {
			try {
				this.isearcherReports = new IndexSearcher(this.reportDirectory, true);
			} catch (Exception e) {
				throw new UnrecoverableError(e);
			}
		}
	}
	
	/**
	 * @param analyzer
	 *            the analyzer to set
	 */
	public void setAnalyzer(final Analyzer analyzer) {
		this.analyzer = analyzer;
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_31, analyzer);
		try {
			this.iwriterReports = new IndexWriter(this.reportDirectory, indexWriterConfig);
		} catch (Exception e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * @param isearcherReports
	 *            the isearcherReports to set
	 */
	public void setIsearcherReports(final IndexSearcher isearcherReports) {
		this.isearcherReports = isearcherReports;
	}
	
	/**
	 * @param iwriterReports
	 *            the iwriterReports to set
	 */
	public void setIwriterReports(final IndexWriter iwriterReports) {
		this.iwriterReports = iwriterReports;
	}
	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
