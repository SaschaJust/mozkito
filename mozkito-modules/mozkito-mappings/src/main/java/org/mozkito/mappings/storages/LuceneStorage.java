/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.mappings.storages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import org.mozkito.issues.model.Comment;
import org.mozkito.issues.model.Report;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.FieldKey;
import org.mozkito.persistence.IteratableFieldKey;
import org.mozkito.persistence.PersistenceUtil;

/**
 * The Class LuceneStorage.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
/**
 * The Class LuceneStorage.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class LuceneStorage extends Storage {
	
	/** The analyzer. */
	private Analyzer                                              analyzer         = null;
	
	/** The report directory. */
	private final Directory                                       reportDirectory  = new RAMDirectory();
	
	/** The iwriter reports. */
	private IndexWriter                                           iwriterReports   = null;
	
	/** The report documents. */
	private final HashMap<String, Document>                       reportDocuments  = new HashMap<String, Document>();
	
	/** The isearcher reports. */
	private IndexSearcher                                         isearcherReports = null;
	
	/** The one type. */
	private final Class<? extends org.mozkito.persistence.Entity> oneType;
	
	/** The other type. */
	private final Class<? extends org.mozkito.persistence.Entity> otherType;
	
	/** The Constant DESCRIPTION. */
	public static final String                                    DESCRIPTION      = Messages.getString("LuceneStorage.description"); //$NON-NLS-1$
	                                                                                                                                  
	/** The Constant TAG. */
	public static final String                                    TAG              = "lucene";                                       //$NON-NLS-1$
	                                                                                                                                  
	/**
	 * Instantiates a new lucene storage.
	 */
	public LuceneStorage() {
		this(null, null);
		
	}
	
	/**
	 * Instantiates a new lucene storage.
	 * 
	 * @param one
	 *            the one
	 * @param other
	 *            the other
	 */
	public LuceneStorage(final Class<? extends org.mozkito.persistence.Entity> one,
	        final Class<? extends org.mozkito.persistence.Entity> other) {
		this.oneType = one;
		this.otherType = other;
	}
	
	/**
	 * Adds the report document.
	 * 
	 * @param report
	 *            the report
	 */
	private void addReportDocument(final Report report) {
		final Document doc = new Document();
		doc.add(new TextField(FieldKey.SUMMARY.name(), report.getSummary(), Field.Store.YES));
		doc.add(new TextField(FieldKey.DESCRIPTION.name(), report.getDescription(), Field.Store.YES));
		doc.add(new TextField(FieldKey.ID.name(), report.getId(), Field.Store.YES));
		
		for (final Comment comment : report.getComments()) {
			doc.add(new TextField(IteratableFieldKey.COMMENTS.name(), comment.getMessage(), Field.Store.YES));
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
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return LuceneStorage.DESCRIPTION;
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
	 * Gets the one type.
	 * 
	 * @return the oneType
	 */
	public final Class<? extends org.mozkito.persistence.Entity> getOneType() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.oneType;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.oneType, "Field '%s' in '%s'.", "oneType", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the other type.
	 * 
	 * @return the otherType
	 */
	public final Class<? extends org.mozkito.persistence.Entity> getOtherType() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.otherType;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.otherType, "Field '%s' in '%s'.", "otherType", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
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
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.storages.Storage#loadData()
	 */
	@Override
	public void loadData() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final PersistenceStorage storage = getStorage(PersistenceStorage.class);
			final PersistenceUtil util = storage.getUtil();
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
					this.isearcherReports = new IndexSearcher(IndexReader.open(this.reportDirectory));
				} catch (final Exception e) {
					throw new UnrecoverableError(e);
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
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
		final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_42, analyzer);
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
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.register.Node#storageDependency()
	 */
	@Override
	public Set<Class<? extends Storage>> storageDependency() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return new HashSet<Class<? extends Storage>>() {
				
				/**
                 * 
                 */
				private static final long serialVersionUID = 1L;
				
				{
					
					add(PersistenceStorage.class);
				}
			};
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
}
