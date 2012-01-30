package de.unisaarland.cs.st.moskito.genealogies.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.BooleanArgument;
import net.ownhero.dev.andama.settings.DirectoryArgument;
import net.ownhero.dev.andama.settings.OutputFileArgument;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.pgm.util.graphml.GraphMLWriter;

import de.unisaarland.cs.st.moskito.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.genealogies.utils.GenealogyTestEnvironment.TestEnvironmentOperation;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.rcs.RepositoryFactory;
import de.unisaarland.cs.st.moskito.rcs.RepositoryType;
import de.unisaarland.cs.st.moskito.rcs.model.RCSRevision;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

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
	
	private static void exportToDOT(final CoreChangeGenealogy genealogy,
	                                final File dotFile) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(dotFile));
		
		out.write("digraph g {");
		out.write(FileUtils.lineSeparator);
		Iterator<JavaChangeOperation> vertexIterator = genealogy.vertexIterator();
		while (vertexIterator.hasNext()) {
			JavaChangeOperation op = vertexIterator.next();
			String opId = genealogy.getNodeId(op);
			for (JavaChangeOperation parent : genealogy.getAllParents(op)) {
				String parentId = genealogy.getNodeId(parent);
				out.write(opId);
				out.write(" -> ");
				out.write(parentId);
				out.write(FileUtils.lineSeparator);
			}
		}
		out.write("}");
		out.close();
	}
	
	public static void exportToGraphML(final CoreChangeGenealogy genealogy,
	                                   final File outFile) {
		try {
			FileOutputStream out = new FileOutputStream(outFile);
			Graph g = new Neo4jGraph(genealogy.getGraphDBService());
			GraphMLWriter.outputGraph(g, out);
		} catch (FileNotFoundException e) {
			if (Logger.logError()) {
				Logger.error(e.getLocalizedMessage(), e);
			}
		} catch (XMLStreamException e) {
			if (Logger.logError()) {
				Logger.error(e.getLocalizedMessage(), e);
			}
		}
	}
	
	public static String getGenealogyStats(final CoreChangeGenealogy genealogy) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("#Vertices: ");
		sb.append(genealogy.vertexSize());
		sb.append(FileUtils.lineSeparator);
		sb.append("#Edges: ");
		
		sb.append(genealogy.edgeSize());
		sb.append(FileUtils.lineSeparator);
		sb.append("Edge types used: ");
		
		for (GenealogyEdgeType t : genealogy.getExistingEdgeTypes()) {
			sb.append(t.toString());
			sb.append(" ");
		}
		sb.append(FileUtils.lineSeparator);
		return sb.toString();
	}
	
	public static GenealogyTestEnvironment getGenealogyTestEnvironment(final File tmpGraphDBFile,
	                                                                   final PersistenceUtil persistenceUtil) {
		
		// UNZIP git repo
		URL zipURL = ChangeGenealogyUtils.class.getResource(FileUtils.fileSeparator + "genealogies_test.git.zip");
		if (zipURL == null) {
			return null;
		}
		
		File baseDir = null;
		try {
			baseDir = new File(
			                   (new URL(zipURL.toString().substring(0,
			                                                        zipURL.toString()
			                                                              .lastIndexOf(FileUtils.fileSeparator)))).toURI());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return null;
		}
		
		File zipFile = null;
		try {
			zipFile = new File(zipURL.toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return null;
		}
		if (Logger.logInfo()) {
			Logger.info("Unzipping " + zipFile.getAbsolutePath() + " to " + baseDir.getAbsolutePath());
		}
		FileUtils.unzip(zipFile, baseDir);
		// UNZIP END
		
		Repository repository = null;
		try {
			repository = RepositoryFactory.getRepositoryHandler(RepositoryType.GIT).newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
			return null;
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
			return null;
		} catch (UnregisteredRepositoryTypeException e1) {
			e1.printStackTrace();
			return null;
		}
		
		URL url = ChangeGenealogyUtils.class.getResource(FileUtils.fileSeparator + "genealogies_test.git");
		File urlFile = null;
		try {
			urlFile = new File(url.toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return null;
		}
		
		try {
			repository.setup(urlFile.toURI(), null, null, persistenceUtil);
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage());
			}
			return null;
		}
		
		// unzip the database dump
		// zipURL = ChangeGenealogyUtils.class.getResource(FileUtils.fileSeparator
		// + "moskito_genealogies_test_environment.psql.zip");
		// if (zipURL == null) {
		// return null;
		// }
		// try {
		// zipFile = new File(zipURL.toURI());
		// } catch (URISyntaxException e1) {
		// e1.printStackTrace();
		// return null;
		// }
		// if (Logger.logInfo()) {
		// Logger.info("Unzipping " + zipFile.getAbsolutePath() + " to " + baseDir.getAbsolutePath());
		// }
		// FileUtils.unzip(zipFile, baseDir);
		//
		// //load the database dump into the test database
		// url = ChangeGenealogyUtils.class.getResource(FileUtils.fileSeparator
		// + "moskito_genealogies_test_environment.psql");
		// try {
		// urlFile = new File(url.toURI());
		// } catch (URISyntaxException e1) {
		// e1.printStackTrace();
		// return null;
		// }
		//
		// String psqlString = null;
		// try {
		// psqlString = FileUtils.readFileToString(urlFile);
		// } catch (IOException e) {
		// e.printStackTrace();
		// return null;
		// }
		// persistenceUtil.executeNativeQuery(psqlString);
		
		// till here we loaded the database dump and extracted the repository.
		
		Map<TestEnvironmentOperation, JavaChangeOperation> environmentOperations = new HashMap<TestEnvironmentOperation, JavaChangeOperation>();
		Map<Integer, RCSTransaction> environmentTransactions = new HashMap<Integer, RCSTransaction>();
		Map<RCSTransaction, Set<JavaChangeOperation>> transactionMap = new HashMap<RCSTransaction, Set<JavaChangeOperation>>();
		
		// read all transactions and JavaChangeOperations
		Criteria<RCSTransaction> transactionCriteria = persistenceUtil.createCriteria(RCSTransaction.class);
		List<RCSTransaction> transactionList = persistenceUtil.load(transactionCriteria);
		
		for (RCSTransaction transaction : transactionList) {
			if (transaction.getId().equals("a64df287a21f8a7b0690d13c1561171cbf48a0e1")) {
				environmentTransactions.put(1, transaction);
			} else if (transaction.getId().equals("a10344533c2b442235aa3bf3dc87dd0ac37cb0af")) {
				environmentTransactions.put(2, transaction);
			} else if (transaction.getId().equals("f281d550d264f53c7e5fd8c7390627c2aaaf2b8a")) {
				environmentTransactions.put(3, transaction);
			} else if (transaction.getId().equals("b38a68d16490c120920fe2281c40317fae960f86")) {
				environmentTransactions.put(4, transaction);
			} else if (transaction.getId().equals("47e6e4206b716af283f583e4d1963a32bef38a92")) {
				environmentTransactions.put(5, transaction);
			} else if (transaction.getId().equals("2005a1a45c9d28a03166d2f61df82552e9b9d502")) {
				environmentTransactions.put(6, transaction);
			} else if (transaction.getId().equals("f3cb1d5a03f6ecda2ce67e2f716f8b0c2d2842f0")) {
				environmentTransactions.put(7, transaction);
			} else if (transaction.getId().equals("0c078d2b779e24fe341028ee132f9613e58763c2")) {
				environmentTransactions.put(8, transaction);
			} else if (transaction.getId().equals("3039e34e53c1bfecfac2e21544b041c890bac8b4")) {
				environmentTransactions.put(9, transaction);
			} else if (transaction.getId().equals("5658606e2f80c30d0b835ed4216e9f8e0cc996fb")) {
				environmentTransactions.put(10, transaction);
			} else {
				throw new UnrecoverableError("Got unexpected RCSTransaction from database: " + transaction.getId());
			}
			
			Set<JavaChangeOperation> operations = new HashSet<JavaChangeOperation>();
			for (RCSRevision revision : transaction.getRevisions()) {
				Criteria<JavaChangeOperation> operationCriteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
				operationCriteria.eq("revision", revision);
				List<JavaChangeOperation> changeOps = persistenceUtil.load(operationCriteria);
				operations.addAll(changeOps);
				for (JavaChangeOperation op : changeOps) {
					switch ((int) op.getId()) {
						case 204:
							environmentOperations.put(TestEnvironmentOperation.T1F2, op);
							break;
						case 201:
							environmentOperations.put(TestEnvironmentOperation.T1F1, op);
							break;
						case 207:
							environmentOperations.put(TestEnvironmentOperation.T2F3, op);
							break;
						case 212:
							environmentOperations.put(TestEnvironmentOperation.T3F1D, op);
							break;
						case 213:
							environmentOperations.put(TestEnvironmentOperation.T3F1A, op);
							break;
						case 210:
							environmentOperations.put(TestEnvironmentOperation.T3F2M, op);
							break;
						case 209:
							environmentOperations.put(TestEnvironmentOperation.T3F2, op);
							break;
						case 217:
							environmentOperations.put(TestEnvironmentOperation.T4F3D, op);
							break;
						case 218:
							environmentOperations.put(TestEnvironmentOperation.T4F3A, op);
							break;
						case 215:
							environmentOperations.put(TestEnvironmentOperation.T4F4, op);
							break;
						case 221:
							environmentOperations.put(TestEnvironmentOperation.T5F4, op);
							break;
						case 224:
							environmentOperations.put(TestEnvironmentOperation.T6F2, op);
							break;
						case 227:
							environmentOperations.put(TestEnvironmentOperation.T7F2, op);
							break;
						case 230:
							environmentOperations.put(TestEnvironmentOperation.T8F2, op);
							break;
						case 234:
							environmentOperations.put(TestEnvironmentOperation.T9F1, op);
							break;
						case 235:
							environmentOperations.put(TestEnvironmentOperation.T10F3, op);
							break;
						case 240:
							environmentOperations.put(TestEnvironmentOperation.T10F4, op);
							break;
						default:
							break;
					}
				}
			}
			transactionMap.put(transaction, operations);
		}
		
		if (transactionMap.size() != 10) {
			System.err.println("The imported database dump must contain exactly 10 transaction entries but was "
			        + transactionMap.size());
			
			return null;
		}
		
		CoreChangeGenealogy changeGenealogy = ChangeGenealogyUtils.readFromDB(tmpGraphDBFile, persistenceUtil);
		
		if (changeGenealogy == null) {
			if (Logger.logError()) {
				Logger.error("Could not generate test change genealogy environment. Reading the CoreChangeGenealogy failed!");
			}
			return null;
		}
		
		for (Entry<RCSTransaction, Set<JavaChangeOperation>> transactionEntry : transactionMap.entrySet()) {
			for (JavaChangeOperation operation : transactionEntry.getValue()) {
				changeGenealogy.addVertex(operation);
			}
		}
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F1D),
		                        environmentOperations.get(TestEnvironmentOperation.T1F1),
		                        GenealogyEdgeType.DeletedDefinitionOnDefinition);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T2F3),
		                        environmentOperations.get(TestEnvironmentOperation.T1F2),
		                        GenealogyEdgeType.CallOnDefinition);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F1D),
		                        environmentOperations.get(TestEnvironmentOperation.T1F1),
		                        GenealogyEdgeType.DeletedDefinitionOnDefinition);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F2M),
		                        environmentOperations.get(TestEnvironmentOperation.T1F2),
		                        GenealogyEdgeType.DefinitionOnDefinition);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F2),
		                        environmentOperations.get(TestEnvironmentOperation.T3F1A),
		                        GenealogyEdgeType.CallOnDefinition);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T4F3D),
		                        environmentOperations.get(TestEnvironmentOperation.T2F3),
		                        GenealogyEdgeType.DeletedCallOnCall);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T4F3A),
		                        environmentOperations.get(TestEnvironmentOperation.T3F1A),
		                        GenealogyEdgeType.CallOnDefinition);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T4F4),
		                        environmentOperations.get(TestEnvironmentOperation.T3F1A),
		                        GenealogyEdgeType.CallOnDefinition);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T5F4),
		                        environmentOperations.get(TestEnvironmentOperation.T3F1A),
		                        GenealogyEdgeType.CallOnDefinition);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T6F2),
		                        environmentOperations.get(TestEnvironmentOperation.T3F2M),
		                        GenealogyEdgeType.DeletedDefinitionOnDefinition);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T7F2),
		                        environmentOperations.get(TestEnvironmentOperation.T6F2),
		                        GenealogyEdgeType.DefinitionOnDeletedDefinition);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T8F2),
		                        environmentOperations.get(TestEnvironmentOperation.T7F2),
		                        GenealogyEdgeType.DefinitionOnDefinition);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T9F1),
		                        environmentOperations.get(TestEnvironmentOperation.T3F1A),
		                        GenealogyEdgeType.DeletedDefinitionOnDefinition);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F3),
		                        environmentOperations.get(TestEnvironmentOperation.T9F1),
		                        GenealogyEdgeType.DeletedCallOnDeletedDefinition);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F4),
		                        environmentOperations.get(TestEnvironmentOperation.T9F1),
		                        GenealogyEdgeType.DeletedCallOnDeletedDefinition);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F3),
		                        environmentOperations.get(TestEnvironmentOperation.T4F3A),
		                        GenealogyEdgeType.DeletedCallOnCall);
		
		changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F4),
		                        environmentOperations.get(TestEnvironmentOperation.T5F4),
		                        GenealogyEdgeType.DeletedCallOnCall);
		
		return new GenealogyTestEnvironment(persistenceUtil, transactionMap, environmentTransactions,
		                                    environmentOperations, repository, changeGenealogy, tmpGraphDBFile);
	}
	
	/**
	 * Creates a ChangeGenealogy using the specified dbFile directory as graphDB directory. If there exists a graph DB
	 * within the dbFile directory, the ChangeGenealogy will load the ChangeGenealogy from this directory. Otherwise it
	 * will create a new one.
	 * 
	 * @param dbFile
	 *            the db file
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the change genealogy stored within5 the graph DB directory, if possible. Otherwise, creates a new
	 *         ChangeGenealogy using graph DB within specified directory.
	 */
	@NoneNull
	public static CoreChangeGenealogy readFromDB(final File dbFile,
	                                             final PersistenceUtil persistenceUtil) {
		GraphDatabaseService graph = new EmbeddedGraphDatabase(dbFile.getAbsolutePath());
		registerShutdownHook(graph);
		CoreChangeGenealogy genealogy = new CoreChangeGenealogy(graph, dbFile, persistenceUtil);
		genealogies.put(genealogy, dbFile);
		return genealogy;
	}
	
	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
	
	public static void run() {
		
		RepositorySettings settings = new RepositorySettings();
		
		DatabaseArguments persistenceArgs = new DatabaseArguments(settings, true, "ppa");
		
		DirectoryArgument graphDBArg = new DirectoryArgument(settings, "genealogy.graphdb",
		                                                     "Directory in which to load the GraphDB from.", null,
		                                                     true, true);
		
		BooleanArgument statsArg = new BooleanArgument(settings, "stats",
		                                               "Print vertex/edge statistic for ChangeGenealogy", "false",
		                                               false);
		
		OutputFileArgument graphmlArg = new OutputFileArgument(settings, "graphml.out",
		                                                       "Export the graph as GraphML file into this file.",
		                                                       null, false, false);
		
		OutputFileArgument dotArg = new OutputFileArgument(
		                                                   settings,
		                                                   "dot.out",
		                                                   "Export the graph as DOT file (must be processed using graphviz).",
		                                                   null, false, true);
		
		settings.parseArguments();
		
		PersistenceUtil persistenceUtil = persistenceArgs.getValue();
		
		CoreChangeGenealogy genealogy = ChangeGenealogyUtils.readFromDB(graphDBArg.getValue(), persistenceUtil);
		
		if (statsArg.getValue()) {
			System.out.println(getGenealogyStats(genealogy));
		}
		
		File graphmlFile = graphmlArg.getValue();
		if (graphmlFile != null) {
			exportToGraphML(genealogy, graphmlFile);
		}
		
		File dotFile = dotArg.getValue();
		if (dotFile != null) {
			try {
				exportToDOT(genealogy, dotFile);
			} catch (IOException e) {
				throw new UnrecoverableError(e);
			}
		}
		
		genealogy.close();
	}
	
}
