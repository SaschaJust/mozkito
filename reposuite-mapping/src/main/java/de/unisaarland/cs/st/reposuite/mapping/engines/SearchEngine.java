/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.LongArgument;
import de.unisaarland.cs.st.reposuite.settings.StringArgument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class SearchEngine extends MappingEngine {
	
	private Analyzer                      analyzer         = new EnglishAnalyzer(Version.LUCENE_31);
	private final Directory               reportDirectory  = new RAMDirectory();
	private IndexWriter                   iwriterReports   = null;
	private QueryParser                   parser           = null;
	
	private final HashMap<Long, Document> reportDocuments  = new HashMap<Long, Document>();
	private IndexSearcher                 isearcherReports = null;
	
	/**
	 * @param settings
	 */
	public SearchEngine(final MappingSettings settings) {
		super(settings);
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
			doc.add(new Field("comment" + comment.getId(), "" + comment.getMessage(), Field.Store.YES,
			                  Field.Index.ANALYZED));
		}
		this.reportDocuments.put(report.getId(), doc);
		try {
			this.iwriterReports.addDocument(doc);
		} catch (Exception e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * @param queryString
	 * @param queryParser
	 * @return
	 */
	private Query buildQuery(String queryString,
	                         final QueryParser queryParser) {
		Query query = null;
		queryString = queryString.replaceAll("[^a-zA-Z0-9]", " ");
		
		if (queryString.replaceAll("[^a-zA-Z0-9]", "").length() < 8) {
			return null;
		}
		
		try {
			query = queryParser.parse(queryString);
			
			Set<Term> terms = new HashSet<Term>();
			query.extractTerms(terms);
			
			if (terms.size() < (Long) getSettings().getSetting("mapping.config.minTokens").getValue()) {
				return null;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return query;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		String value = (String) getSettings().getSetting("mapping.config.language").getValue();
		String[] split = value.split(":");
		try {
			Class<?> clazz = Class.forName("org.apache.lucene.analysis." + split[0] + "." + split[1] + "Analyzer");
			Constructor<?> constructor = clazz.getConstructor(Version.class);
			Analyzer newInstance = (Analyzer) constructor.newInstance(Version.LUCENE_31);
			this.analyzer = newInstance;
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_31, this.analyzer);
			this.iwriterReports = new IndexWriter(this.reportDirectory, indexWriterConfig);
		} catch (Exception e) {
			throw new UnrecoverableError(e);
		}
		super.init();
	}
	
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
			this.parser = new QueryParser(Version.LUCENE_31, "content", this.analyzer);
			BooleanQuery.setMaxClauseCount(102400);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#register
	 * (de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments,
	 * boolean)
	 */
	@Override
	public void register(final MappingSettings settings,
	                     final MappingArguments arguments,
	                     final boolean isRequired) {
		super.register(settings, arguments, isRequired);
		arguments.addArgument(new LongArgument(settings, "mapping.config.minTokens",
		                                       "minimum number of tokens required for a search.", "3", isRequired));
		arguments.addArgument(new StringArgument(settings, "mapping.config.language",
		                                         "minimum number of tokens required for a search.", "en:English",
		                                         isRequired));
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.rcs.model.RCSTransaction,
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report,
	 * de.unisaarland.cs.st.reposuite.mapping.model.MapScore)
	 */
	@Override
	public void score(final RCSTransaction transaction,
	                  final Report report,
	                  final MapScore score) {
		super.score(transaction, report, score);
		
		try {
			this.parser = new QueryParser(Version.LUCENE_31, "content", this.analyzer);
			Query query = buildQuery(transaction.getMessage(), this.parser);
			this.isearcherReports = new IndexSearcher(this.reportDirectory, true); // read-only=true
			// Parse a simple query that searches for "text":
			
			ScoreDoc[] hits = this.isearcherReports.search(query, null, 1000).scoreDocs;
			// Iterate through the results:
			for (ScoreDoc hit : hits) {
				Document hitDoc = this.isearcherReports.doc(hit.doc);
				Long bugId = Long.parseLong(hitDoc.get("bugid"));
				
				if (bugId.compareTo(report.getId()) == 0) {
					score.addFeature(hit.score, "message", transaction.getMessage(), this.getClass());
					break;
				}
			}
		} catch (Exception e) {
			throw new UnrecoverableError(e);
		}
	}
}
