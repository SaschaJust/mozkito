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
package org.mozkito.genealogies.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.core.GenealogyEdgeType;
import org.mozkito.genealogies.utils.GenealogyTestEnvironment.TestEnvironmentOperation;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.Repository;
import org.mozkito.versions.RepositoryFactory;
import org.mozkito.versions.RepositoryType;
import org.mozkito.versions.exceptions.UnregisteredRepositoryTypeException;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Revision;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.pgm.util.io.graphml.GraphMLWriter;

/**
 * The Class ChangeGenealogyUtils.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class ChangeGenealogyUtils {
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				for (final CoreChangeGenealogy genealogy : ChangeGenealogyUtils.genealogies.keySet()) {
					if (ChangeGenealogyUtils.genealogies.get(genealogy).exists()) {
						genealogy.close();
					}
				}
			}
		});
	}
	
	/** The genealogies. */
	private static Map<CoreChangeGenealogy, File> genealogies = new HashMap<CoreChangeGenealogy, File>();
	
	/**
	 * Export to graph ml.
	 * 
	 * @param genealogy
	 *            the genealogy
	 * @param outFile
	 *            the out file
	 */
	public static void exportToGraphML(final CoreChangeGenealogy genealogy,
	                                   final File outFile) {
		try {
			final FileOutputStream out = new FileOutputStream(outFile);
			final Graph g = new Neo4jGraph(genealogy.getGraphDBService());
			GraphMLWriter.outputGraph(g, out);
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		}
	}
	
	/**
	 * Gets the genealogy test environment.
	 * 
	 * @param tmpGraphDBFile
	 *            the tmp graph db file
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the genealogy test environment
	 * @throws FilePermissionException
	 *             the file permission exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static GenealogyTestEnvironment getGenealogyTestEnvironment(@NotNull final File tmpGraphDBFile,
	                                                                   @NotNull final PersistenceUtil persistenceUtil) throws FilePermissionException,
	                                                                                                                  IOException {
		
		// UNZIP git repo
		final URL zipURL = ChangeGenealogyUtils.class.getResource(FileUtils.fileSeparator + "genealogies_test.git.zip");
		if (zipURL == null) {
			return null;
		}
		
		File baseDir = null;
		try {
			baseDir = new File(
			                   (new URL(zipURL.toString().substring(0,
			                                                        zipURL.toString()
			                                                              .lastIndexOf(FileUtils.fileSeparator)))).toURI());
		} catch (final MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		} catch (final URISyntaxException e1) {
			e1.printStackTrace();
			return null;
		}
		
		File zipFile = null;
		try {
			zipFile = new File(zipURL.toURI());
		} catch (final URISyntaxException e1) {
			throw new UnrecoverableError(e1);
		}
		if (Logger.logInfo()) {
			Logger.info("Unzipping " + zipFile.getAbsolutePath() + " to " + baseDir.getAbsolutePath());
		}
		FileUtils.unzip(zipFile, baseDir);
		// UNZIP END
		
		Repository repository = null;
		try {
			repository = RepositoryFactory.getRepositoryHandler(RepositoryType.GIT).newInstance();
		} catch (final InstantiationException | IllegalAccessException | UnregisteredRepositoryTypeException e1) {
			throw new UnrecoverableError(e1);
		}
		
		final URL url = ChangeGenealogyUtils.class.getResource(FileUtils.fileSeparator + "genealogies_test.git");
		File urlFile = null;
		try {
			urlFile = new File(url.toURI());
		} catch (final URISyntaxException e1) {
			throw new UnrecoverableError(e1);
		}
		
		try {
			repository.setup(urlFile.toURI(), null, "master");
		} catch (final Exception e) {
			throw new UnrecoverableError(e);
		}
		
		// unzip the database dump
		// zipURL = ChangeGenealogyUtils.class.getResource(FileUtils.fileSeparator
		// + "mozkito_genealogies_test_environment.psql.zip");
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
		// + "mozkito_genealogies_test_environment.psql");
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
		
		final Map<TestEnvironmentOperation, JavaChangeOperation> environmentOperations = new HashMap<TestEnvironmentOperation, JavaChangeOperation>();
		final Map<Integer, ChangeSet> environmentTransactions = new HashMap<Integer, ChangeSet>();
		final Map<ChangeSet, Set<JavaChangeOperation>> transactionMap = new HashMap<ChangeSet, Set<JavaChangeOperation>>();
		
		// read all transactions and JavaChangeOperations
		final Criteria<ChangeSet> transactionCriteria = persistenceUtil.createCriteria(ChangeSet.class);
		final List<ChangeSet> transactionList = persistenceUtil.load(transactionCriteria);
		CollectionCondition.size(transactionList, 10, "Transaction list from database has fixed precomputed size.");
		if (Logger.logDebug()) {
			Logger.debug(JavaUtils.collectionToString(transactionList));
		}
		for (final ChangeSet transaction : transactionList) {
			if ("a64df287a21f8a7b0690d13c1561171cbf48a0e1".equals(transaction.getId())) {
				environmentTransactions.put(1, transaction);
			} else if ("a10344533c2b442235aa3bf3dc87dd0ac37cb0af".equals(transaction.getId())) {
				environmentTransactions.put(2, transaction);
			} else if ("f281d550d264f53c7e5fd8c7390627c2aaaf2b8a".equals(transaction.getId())) {
				environmentTransactions.put(3, transaction);
			} else if ("b38a68d16490c120920fe2281c40317fae960f86".equals(transaction.getId())) {
				environmentTransactions.put(4, transaction);
			} else if ("47e6e4206b716af283f583e4d1963a32bef38a92".equals(transaction.getId())) {
				environmentTransactions.put(5, transaction);
			} else if ("2005a1a45c9d28a03166d2f61df82552e9b9d502".equals(transaction.getId())) {
				environmentTransactions.put(6, transaction);
			} else if ("f3cb1d5a03f6ecda2ce67e2f716f8b0c2d2842f0".equals(transaction.getId())) {
				environmentTransactions.put(7, transaction);
			} else if ("0c078d2b779e24fe341028ee132f9613e58763c2".equals(transaction.getId())) {
				environmentTransactions.put(8, transaction);
			} else if ("3039e34e53c1bfecfac2e21544b041c890bac8b4".equals(transaction.getId())) {
				environmentTransactions.put(9, transaction);
			} else if ("5658606e2f80c30d0b835ed4216e9f8e0cc996fb".equals(transaction.getId())) {
				environmentTransactions.put(10, transaction);
			} else {
				throw new UnrecoverableError("Got unexpected Transaction from database: " + transaction.getId());
			}
			
			final Set<JavaChangeOperation> operations = new HashSet<JavaChangeOperation>();
			
			for (final Revision revision : transaction.getRevisions()) {
				final Criteria<JavaChangeOperation> operationCriteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
				operationCriteria.eq("revision", revision);
				final List<JavaChangeOperation> changeOps = persistenceUtil.load(operationCriteria);
				if (Logger.logDebug()) {
					Logger.debug(JavaUtils.collectionToString(changeOps));
				}
				operations.addAll(changeOps);
				for (final JavaChangeOperation op : changeOps) {
					final String changeSetId = op.getRevision().getChangeSet().getId();
					
					switch (changeSetId) {
						case "a64df287a21f8a7b0690d13c1561171cbf48a0e1":
							// // case 203:
							// // environmentOperations.put(TestEnvironmentOperation.T1F2, op);
							// // break;
							// //case 201:
							// //environmentOperations.put(TestEnvironmentOperation.T1F1, op);
							// //break;
							if (op.getChangedElementLocation().getFilePath()
							      .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/B.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodDefinition")
							        && (op.getChangedElementLocation().getPosition() == 90)) {
								environmentOperations.put(TestEnvironmentOperation.T1F2, op);
							} else if (op.getChangedElementLocation()
							             .getFilePath()
							             .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/A.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodDefinition")
							        && (op.getChangedElementLocation().getPosition() == 90)) {
								environmentOperations.put(TestEnvironmentOperation.T1F1, op);
							}
							break;
						case "a10344533c2b442235aa3bf3dc87dd0ac37cb0af":
							// case 224:
							// environmentOperations.put(TestEnvironmentOperation.T2F3, op);
							// break;
							if (op.getChangedElementLocation().getFilePath()
							      .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/C.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodCall")
							        && (op.getChangedElementLocation().getPosition() == 125)) {
								environmentOperations.put(TestEnvironmentOperation.T2F3, op);
							}
							break;
						case "f281d550d264f53c7e5fd8c7390627c2aaaf2b8a":
							// case 228:
							// environmentOperations.put(TestEnvironmentOperation.T3F1D, op);
							// break;
							// case 229:
							// environmentOperations.put(TestEnvironmentOperation.T3F1A, op);
							// break;
							// case 232:
							// environmentOperations.put(TestEnvironmentOperation.T3F2M, op);
							// break;
							// case 231:
							// environmentOperations.put(TestEnvironmentOperation.T3F2, op);
							// break;
							if (op.getChangedElementLocation().getFilePath()
							      .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/A.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodDefinition")
							        && (op.getChangedElementLocation().getPosition() == 90)
							        && op.getChangedElementLocation()
							             .getElement()
							             .getFullQualifiedName()
							             .equals("de.unisaarland.cs.st.reposuite.genealogies.test.evironment.A.foo(int)")) {
								environmentOperations.put(TestEnvironmentOperation.T3F1D, op);
							} else if (op.getChangedElementLocation()
							             .getFilePath()
							             .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/A.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodDefinition")
							        && (op.getChangedElementLocation().getPosition() == 90)
							        && op.getChangedElementLocation()
							             .getElement()
							             .getFullQualifiedName()
							             .equals("de.unisaarland.cs.st.reposuite.genealogies.test.evironment.A.foo(float)")) {
								environmentOperations.put(TestEnvironmentOperation.T3F1A, op);
							} else if (op.getChangedElementLocation()
							             .getFilePath()
							             .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/B.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodDefinition")
							        && (op.getChangedElementLocation().getPosition() == 90)
							        && op.getChangedElementLocation()
							             .getElement()
							             .getFullQualifiedName()
							             .equals("de.unisaarland.cs.st.reposuite.genealogies.test.evironment.B.bar(int)")) {
								environmentOperations.put(TestEnvironmentOperation.T3F2M, op);
							} else if (op.getChangedElementLocation()
							             .getFilePath()
							             .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/B.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodCall")
							        && (op.getChangedElementLocation().getPosition() == 129)
							        && op.getChangedElementLocation()
							             .getElement()
							             .getFullQualifiedName()
							             .equals("de.unisaarland.cs.st.reposuite.genealogies.test.evironment.B.foo(float)")) {
								environmentOperations.put(TestEnvironmentOperation.T3F2, op);
							}
							break;
						case "b38a68d16490c120920fe2281c40317fae960f86":
							// case 238:
							// environmentOperations.put(TestEnvironmentOperation.T4F3D, op);
							// break;
							// case 239:
							// environmentOperations.put(TestEnvironmentOperation.T4F3A, op);
							// break;
							// case 234:
							// environmentOperations.put(TestEnvironmentOperation.T4F4, op);
							// break;
							if (op.getChangedElementLocation().getFilePath()
							      .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/C.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodCall")
							        && (op.getChangedElementLocation().getPosition() == 125)
							        && op.getChangedElementLocation()
							             .getElement()
							             .getFullQualifiedName()
							             .equals("de.unisaarland.cs.st.reposuite.genealogies.test.evironment.B.bar(int)")) {
								environmentOperations.put(TestEnvironmentOperation.T4F3D, op);
							} else if (op.getChangedElementLocation()
							             .getFilePath()
							             .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/C.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodCall")
							        && (op.getChangedElementLocation().getPosition() == 125)
							        && op.getChangedElementLocation()
							             .getElement()
							             .getFullQualifiedName()
							             .equals("de.unisaarland.cs.st.reposuite.genealogies.test.evironment.A.foo(float)")) {
								environmentOperations.put(TestEnvironmentOperation.T4F3A, op);
							} else if (op.getChangedElementLocation()
							             .getFilePath()
							             .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/D.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodCall")
							        && (op.getChangedElementLocation().getPosition() == 128)
							        && op.getChangedElementLocation()
							             .getElement()
							             .getFullQualifiedName()
							             .equals("de.unisaarland.cs.st.reposuite.genealogies.test.evironment.A.foo(float)")) {
								environmentOperations.put(TestEnvironmentOperation.T4F4, op);
							}
							break;
						case "47e6e4206b716af283f583e4d1963a32bef38a92":
							// case 218:
							// environmentOperations.put(TestEnvironmentOperation.T5F4, op);
							// break;
							if (op.getChangedElementLocation().getFilePath()
							      .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/D.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodCall")
							        && (op.getChangedElementLocation().getPosition() == 150)
							        && op.getChangedElementLocation()
							             .getElement()
							             .getFullQualifiedName()
							             .equals("de.unisaarland.cs.st.reposuite.genealogies.test.evironment.A.foo(float)")) {
								environmentOperations.put(TestEnvironmentOperation.T5F4, op);
							}
							break;
						case "2005a1a45c9d28a03166d2f61df82552e9b9d502":
							// case 207:
							// environmentOperations.put(TestEnvironmentOperation.T6F2, op);
							// break;
							if (op.getChangedElementLocation().getFilePath()
							      .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/B.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodeDefinition")
							        && (op.getChangedElementLocation().getPosition() == 90)
							        && op.getChangedElementLocation()
							             .getElement()
							             .getFullQualifiedName()
							             .equals("de.unisaarland.cs.st.reposuite.genealogies.test.evironment.B.bar(int)")) {
								environmentOperations.put(TestEnvironmentOperation.T6F2, op);
							}
							break;
						case "f3cb1d5a03f6ecda2ce67e2f716f8b0c2d2842f0":
							// case 210:
							// environmentOperations.put(TestEnvironmentOperation.T7F2, op);
							// break;
							if (op.getChangedElementLocation().getFilePath()
							      .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/B.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodeDefinition")
							        && (op.getChangedElementLocation().getPosition() == 90)
							        && op.getChangedElementLocation()
							             .getElement()
							             .getFullQualifiedName()
							             .equals("de.unisaarland.cs.st.reposuite.genealogies.test.evironment.B.bar(int)")) {
								environmentOperations.put(TestEnvironmentOperation.T7F2, op);
							}
							break;
						case "0c078d2b779e24fe341028ee132f9613e58763c2":
							// case 221:
							// environmentOperations.put(TestEnvironmentOperation.T8F2, op);
							// break;
							if (op.getChangedElementLocation().getFilePath()
							      .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/B.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodeDefinition")
							        && (op.getChangedElementLocation().getPosition() == 90)
							        && op.getChangedElementLocation()
							             .getElement()
							             .getFullQualifiedName()
							             .equals("de.unisaarland.cs.st.reposuite.genealogies.test.evironment.B.bar(int)")) {
								environmentOperations.put(TestEnvironmentOperation.T8F2, op);
							}
							break;
						case "3039e34e53c1bfecfac2e21544b041c890bac8b4":
							// case 241:
							// environmentOperations.put(TestEnvironmentOperation.T9F1, op);
							// break;
							if (op.getChangedElementLocation().getFilePath()
							      .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/A.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodeDefinition")
							        && (op.getChangedElementLocation().getPosition() == 90)
							        && op.getChangedElementLocation()
							             .getElement()
							             .getFullQualifiedName()
							             .equals("de.unisaarland.cs.st.reposuite.genealogies.test.evironment.A.foo(float)")) {
								environmentOperations.put(TestEnvironmentOperation.T9F1, op);
							}
							break;
						case "5658606e2f80c30d0b835ed4216e9f8e0cc996fb":
							// case 217:
							// environmentOperations.put(TestEnvironmentOperation.T10F3, op);
							// break;
							// case 211:
							// environmentOperations.put(TestEnvironmentOperation.T10F4, op);
							// break;
							if (op.getChangedElementLocation().getFilePath()
							      .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/D.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodeCall")
							        && (op.getChangedElementLocation().getPosition() == 150)
							        && op.getChangedElementLocation()
							             .getElement()
							             .getFullQualifiedName()
							             .equals("de.unisaarland.cs.st.reposuite.genealogies.test.evironment.A.foo(float)")) {
								environmentOperations.put(TestEnvironmentOperation.T10F4, op);
							} else if (op.getChangedElementLocation()
							             .getFilePath()
							             .equals("src/de/unisaarland/cs/st/reposuite/genealogies/test/evironment/C.java")
							        && op.getChangedElementLocation().getElement().getElementType()
							             .equals("org.mozkito.codeanalysis.model.JavaMethodeCall")
							        && (op.getChangedElementLocation().getPosition() == 125)
							        && op.getChangedElementLocation()
							             .getElement()
							             .getFullQualifiedName()
							             .equals("de.unisaarland.cs.st.reposuite.genealogies.test.evironment.A.foo(int)")) {
								environmentOperations.put(TestEnvironmentOperation.T10F3, op);
							}
							break;
						default:
							break;
					}
				}
			}
			transactionMap.put(transaction, operations);
		}
		
		if (transactionMap.size() != 10) {
			throw new UnrecoverableError(
			                             "The imported database dump must contain exactly 10 transaction entries but was "
			                                     + transactionMap.size());
		}
		
		final CoreChangeGenealogy changeGenealogy = ChangeGenealogyUtils.readFromDB(tmpGraphDBFile, persistenceUtil);
		
		if (changeGenealogy == null) {
			throw new UnrecoverableError(
			                             "Could not generate test change genealogy environment. Reading the CoreChangeGenealogy failed!");
		}
		
		for (final Entry<ChangeSet, Set<JavaChangeOperation>> transactionEntry : transactionMap.entrySet()) {
			for (final JavaChangeOperation operation : transactionEntry.getValue()) {
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
		final GraphDatabaseService graph = new EmbeddedGraphDatabase(dbFile.getAbsolutePath());
		registerShutdownHook(graph);
		final CoreChangeGenealogy genealogy = new CoreChangeGenealogy(graph, dbFile, persistenceUtil);
		ChangeGenealogyUtils.genealogies.put(genealogy, dbFile);
		return genealogy;
	}
	
	/**
	 * Register shutdown hook.
	 * 
	 * @param graphDb
	 *            the graph db
	 */
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
}
