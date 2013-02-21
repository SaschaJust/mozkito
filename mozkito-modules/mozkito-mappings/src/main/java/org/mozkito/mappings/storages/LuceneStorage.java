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
import java.util.List;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

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

import org.mozkito.issues.model.Comment;
import org.mozkito.issues.model.Report;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.persistence.Criteria;
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
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends ArgumentSetOptions<LuceneStorage, ArgumentSet<LuceneStorage, Options>> {
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, LuceneStorage.TAG, LuceneStorage.DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public LuceneStorage init() {
			// PRECONDITIONS
			
			try {
				return new LuceneStorage();
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
		                                                                                    SettingsParseError {
			// PRECONDITIONS
			
			try {
				return new HashMap<>();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
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
	
	/** The Constant DESCRIPTION. */
	private static final String             DESCRIPTION      = Messages.getString("LuceneStorage.description"); //$NON-NLS-1$
	                                                                                                            
	/** The Constant TAG. */
	private static final String             TAG              = "lucene";                                       //$NON-NLS-1$
	                                                                                                            
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
		doc.add(new Field(FieldKey.SUMMARY.name(), report.getSummary(), Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field(FieldKey.DESCRIPTION.name(), report.getDescription(), Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field(FieldKey.ID.name(), report.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		
		for (final Comment comment : report.getComments()) {
			doc.add(new Field(FieldKey.COMMENT.name(), comment.getMessage(), Field.Store.YES, Field.Index.ANALYZED));
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
				this.isearcherReports = new IndexSearcher(IndexReader.open(this.reportDirectory));
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
