package de.unisaarland.cs.st.reposuite.genealogies.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;



/**
 * The Class ChangeGenealogyUtils.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeGenealogyUtils {
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				for (CoreChangeGenealogy genealogy : genealogies.keySet()) {
					if (genealogies.get(genealogy).exists()) {
						genealogy.close();
					}
				}
			}
		});
	}

	/** The genealogies. */
	private static Map<CoreChangeGenealogy, File> genealogies = new HashMap<CoreChangeGenealogy, File>();
	
	/**
	 * Creates a ChangeGenealogy using the specified dbFile directory as graphDB
	 * directory. If there exists a graph DB within the dbFile directory, the
	 * ChangeGenealogy will load the ChangeGenealogy from this directory.
	 * Otherwise it will create a new one.
	 * 
	 * @param dbFile
	 *            the db file
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the change genealogy stored within5 the graph DB directory, if
	 *         possible. Otherwise, creates a new ChangeGenealogy using graph DB
	 *         within specified directory.
	 */
	@NoneNull
	public static CoreChangeGenealogy readFromDB(final File dbFile) {
		GraphDatabaseService graph = new EmbeddedGraphDatabase(dbFile.getAbsolutePath());
		CoreChangeGenealogy genealogy = new CoreChangeGenealogy(graph, dbFile);
		genealogies.put(genealogy, dbFile);
		return genealogy;
	}
}
