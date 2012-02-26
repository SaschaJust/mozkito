/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
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
package de.unisaarland.cs.st.moskito.mapping.storages;

import java.util.HashMap;
import java.util.List;

import net.ownhero.dev.hiari.settings.DynamicArgumentSet;
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

import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

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
		
	}
	
	/**
	 * @param report
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
	
	@Override
	public void afterParse() {
		// TODO Auto-generated method stub
		
	}
	
	/**
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
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#initSettings(net.ownhero.dev.andama.settings.
	 * DynamicArgumentSet)
	 */
	@Override
	public boolean initSettings(final DynamicArgumentSet<Boolean> set) {
		// TODO Auto-generated method stub
		return false;
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
}
