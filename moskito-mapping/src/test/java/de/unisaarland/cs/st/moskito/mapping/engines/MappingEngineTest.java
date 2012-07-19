/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

package de.unisaarland.cs.st.moskito.mapping.engines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.Tuple;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Severity;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableTransaction;
import de.unisaarland.cs.st.moskito.mapping.model.Relation;
import de.unisaarland.cs.st.moskito.mapping.model.Feature;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;
import de.unisaarland.cs.st.moskito.mapping.settings.MappingOptions;
import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class MappingEngineTest.
 */
public class MappingEngineTest {
	
	/** The mappable report. */
	static MappableReport      mappableReport;
	
	/** The mappable transaction. */
	static MappableTransaction mappableTransaction;
	
	/** The report. */
	static Report              report;
	
	/** The score. */
	static Relation             score;
	
	/** The transaction. */
	static RCSTransaction      transaction;
	
	/**
	 * Setup class.
	 */
	@BeforeClass
	public static void setupClass() {
		report = new Report("84698384"); // T E S T
		mappableReport = new MappableReport(report);
		
		final Person author = new Person("author", "Au Thor", "author@unit.test");
		final Person developer = new Person("developer", "Devel Loper", "developer@unit.test");
		
		report.setSubmitter(author);
		report.setResolver(developer);
		report.setAssignedTo(developer);
		
		report.setCategory("Core");
		report.setComponent("Network Connector");
		report.setDescription(""); // TODO
		report.setPriority(Priority.HIGH);
		report.setProduct("UNIT TEST");
		report.setResolution(Resolution.RESOLVED);
		report.setSeverity(Severity.MAJOR);
		report.setStatus(Status.CLOSED);
		report.setSubject("Network parser crashes when reading unexpected EOF.");
		report.setSummary(""); // TODO
		report.setType(Type.BUG);
		report.setVersion("0.2");
		
		// 2012/01/02 04:37:19
		report.setCreationTimestamp(new DateTime(2012, 01, 02, 4, 37, 19, 0));
		// 2012/01/16 19:56:35
		report.setResolutionTimestamp(new DateTime(2012, 01, 16, 19, 56, 35, 0));
		
		final Comment comment1 = new Comment(1, developer, new DateTime(2012, 01, 03, 13, 37, 52, 0), "");
		final Comment comment2 = new Comment(2, author, new DateTime(2012, 01, 05, 3, 44, 29, 0), "");
		final Comment comment3 = new Comment(3, developer, new DateTime(2012, 01, 07, 23, 2, 31, 0),
		                                     "Fixed in rev 673fdbf2f792c8c81fd9d398194cc0eb1dab8938.");
		
		report.addComment(comment1);
		report.addComment(comment2);
		report.addComment(comment3);
		
		final HistoryElement element1 = new HistoryElement("84698384", developer, new DateTime(2012, 01, 03, 13, 37,
		                                                                                       52, 0));
		element1.addChangedValue("Status", Status.NEW, Status.ASSIGNED);
		element1.addChangedValue("Priority", Priority.NORMAL, Priority.HIGH);
		element1.addChangedValue("Severity", Severity.NORMAL, Severity.MAJOR);
		element1.addChangedValue("AssignedTo", null, developer);
		report.addHistoryElement(element1);
		
		final HistoryElement element2 = new HistoryElement("84698384", developer, new DateTime(2012, 01, 16, 19, 56,
		                                                                                       35, 0));
		element2.addChangedValue("Status", Status.ASSIGNED, Status.CLOSED);
		element2.addChangedValue("ResolutionTimestamp", null, new DateTime(2012, 01, 16, 19, 56, 35, 0));
		element2.addChangedValue("Resolution", Resolution.UNKNOWN, Resolution.RESOLVED);
		element2.addChangedValue("Resolver", null, developer);
		report.addHistoryElement(element2);
		
		report.setLastUpdateTimestamp(new DateTime(2012, 01, 16, 19, 56, 35, 0));
		
		transaction = RCSTransaction.createTransaction("673fdbf2f792c8c81fd9d398194cc0eb1dab8938",
		                                               "Fixing bug 84698384.",
		                                               new DateTime(2012, 01, 16, 19, 32, 12, 0), developer,
		                                               "673fdbf2f792c8c81fd9d398194cc0eb1dab8938");
		mappableTransaction = new MappableTransaction(transaction);
	}
	
	/** The arguments. */
	MappingOptions                    arguments;
	
	/** The settings. */
	Settings                          settings;
	
	/** The Constant chainName. */
	static final String               chainName = "test";
	
	/** The engines. */
	private Collection<MappingEngine> engines;
	
	private MappingOptions            mappingOptions;
	
	/**
	 * Setup.
	 * 
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 * @throws SettingsParseError
	 *             the settings parse error
	 * @throws ArgumentSetRegistrationException
	 */
	@Before
	public void setup() throws ArgumentRegistrationException, SettingsParseError, ArgumentSetRegistrationException {
		final Properties properties = System.getProperties();
		properties.put(chainName + ".engines", "BackrefEngine");
		properties.put(chainName + ".engine.backref.confidence", "1.0");
		System.setProperties(properties);
		
		this.settings = new Settings();
		this.mappingOptions = new MappingOptions(this.settings.getRoot(), Requirement.required);
		final ArgumentSet<MappingFinder, MappingOptions> mappingArguments = ArgumentSetFactory.create(this.mappingOptions);
		final MappingFinder mappingFinder = mappingArguments.getValue();
		this.engines = mappingFinder.getEngines().values();
		
		score = new Relation(mappableReport, mappableTransaction);
	}
	
	/**
	 * Test backref engine.
	 */
	@Test
	public void testBackrefEngine() {
		for (final MappingEngine mEngine : this.engines) {
			if (mEngine.getHandle().equals(BackrefEngine.class.getSimpleName())) {
				final BackrefEngine engine = new BackrefEngine(1.0d);
				System.err.println(this.settings.toString());
				
				engine.score(mappableReport, mappableTransaction, score);
				Feature feature = score.getFeatures().iterator().next();
				double confidence = feature.getConfidence();
				System.err.println(confidence);
				System.err.println(engine.getConfidence());
				System.err.println(feature.getReportFieldName());
				System.err.println(feature.getReportSubstring());
				System.err.println(feature.getTransactionFieldName());
				System.err.println(feature.getTransactionSubstring());
				assertEquals("Confidence differes from expected (match).", engine.getConfidence(), confidence, 0.0001);
				
				score = new Relation(mappableTransaction, mappableReport);
				engine.score(mappableTransaction, mappableReport, score);
				feature = score.getFeatures().iterator().next();
				confidence = feature.getConfidence();
				System.err.println(confidence);
				System.err.println(engine.getConfidence());
				System.err.println(feature.getReportFieldName());
				System.err.println(feature.getReportSubstring());
				System.err.println(feature.getTransactionFieldName());
				System.err.println(feature.getTransactionSubstring());
				assertEquals("Confidence differes from expected (match).", engine.getConfidence(), confidence, 0.0001);
			}
		}
	}
	
	/**
	 * Test supported.
	 */
	@SuppressWarnings ({ "deprecation", "serial" })
	@Test
	public void testSupported() {
		int failed = 0;
		final Set<Class<? extends MappableEntity>> mappableClasses = new HashSet<Class<? extends MappableEntity>>();
		final Collection<MappableEntity> mappableEntities = new LinkedList<MappableEntity>();
		
		try {
			mappableClasses.addAll(ClassFinder.getClassesExtendingClass(MappableEntity.class.getPackage(),
			                                                            MappableEntity.class, Modifier.ABSTRACT
			                                                                    | Modifier.INTERFACE | Modifier.PRIVATE));
			for (final Class<? extends MappableEntity> clazz : mappableClasses) {
				mappableEntities.add(clazz.newInstance());
			}
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		new MappableTransaction();
		new MappableReport();
		
		final Map<MappingEngine, List<Tuple<MappableEntity, MappableEntity>>> map = new HashMap<MappingEngine, List<Tuple<MappableEntity, MappableEntity>>>() {
			
			{
				put(new AuthorEqualityEngine(1d), new LinkedList<Tuple<MappableEntity, MappableEntity>>() {
					
					{
						for (final MappableEntity fromEntity : mappableEntities) {
							for (final MappableEntity toEntity : mappableEntities) {
								if (fromEntity.getBaseType() != toEntity.getBaseType()) {
									add(new Tuple<MappableEntity, MappableEntity>(fromEntity, toEntity));
								}
							}
							
						}
						
					}
				});
				put(new TimestampEngine(new Interval(transaction.getTimestamp().getMillis() - (1000 * 3600),
				                                     transaction.getTimestamp().getMillis() + (1000 * 3600))),
				    new ArrayList<Tuple<MappableEntity, MappableEntity>>(1) {
					    
					    {
						    add(new Tuple<MappableEntity, MappableEntity>(new MappableTransaction(transaction),
						                                                  new MappableReport(report)));
					    }
				    });
				
			}
		};
		
		for (final MappingEngine engine : map.keySet()) {
			System.out.println("Checking engine support for: " + engine.getHandle() + " with " + map.get(engine).size()
			        + " combinations.");
			final Expression supported = engine.supported();
			for (final Tuple<MappableEntity, MappableEntity> tuple : map.get(engine)) {
				final MappableEntity fromEntity = tuple.getFirst();
				final MappableEntity toEntity = tuple.getSecond();
				
				if (supported.check(fromEntity.getClass(), toEntity.getClass()) == 0) {
					List<Expression> failureCause = null;
					String oneEquals = null;
					if (!supported.check(fromEntity.getClass(), toEntity.getClass(), Index.FROM)) {
						failureCause = supported.getFailureCause(fromEntity.getClass(), toEntity.getClass(), Index.FROM);
						oneEquals = "FROM";
					} else {
						failureCause = supported.getFailureCause(fromEntity.getClass(), toEntity.getClass(), Index.TO);
						oneEquals = "TO";
					}
					
					System.err.println("["
					        + engine.getClass().getSimpleName()
					        + "] While checking if the engine supports mapping FROM:"
					        + fromEntity.getBaseType().getSimpleName()
					        + " => TO:"
					        + toEntity.getBaseType().getSimpleName()
					        + " the required condition failed. Engine requires the following expression to evaluate to true: "
					        + supported.toString() + " (with ONE == " + oneEquals + "). Minimized failure cause: "
					        + failureCause);
					++failed;
				} else {
					System.out.println("[" + engine.getClass().getSimpleName() + "] Test for supported mapping FROM:"
					        + fromEntity.getBaseType().getSimpleName() + " => TO:"
					        + toEntity.getBaseType().getSimpleName() + " succeeded. Matched following criterion: "
					        + supported.toString());
				}
			}
		}
		if (failed > 0) {
			fail(("Test had " + failed + " errors."));
		}
	}
}
