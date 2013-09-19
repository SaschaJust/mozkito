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

package org.mozkito.mappings.test.elements;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.HashSet;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import org.mozkito.issues.model.Comment;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.model.Report;
import org.mozkito.mappings.elements.CandidateFactory;
import org.mozkito.mappings.engines.AuthorEqualityEngine;
import org.mozkito.mappings.engines.Engine;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.selectors.Selector;
import org.mozkito.persistence.Entity;
import org.mozkito.persistence.FieldKey;
import org.mozkito.persons.model.Person;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.VersionArchive;

/**
 * The Class EngineTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class EngineTest {
	
	public static void main(final String[] args) {
		final IssueTracker issueTracker = new IssueTracker();
		final Report report = new Report(issueTracker, "123");
		report.setSummary("there are several remaining warnings");
		report.setDescription("description");
		report.setSubject("subject");
		final Person author = new Person("just", "sascha.just@mozkito.org", "Sascha Just");
		report.setSubmitter(author);
		
		report.addComment(new Comment(1, author, new DateTime(), "fixed in 04ff08dd3f073dc98e39d68a324632ad4ba0450f"));
		
		final ChangeSet changeSet = new ChangeSet(new VersionArchive(), "04ff08dd3f073dc98e39d68a324632ad4ba0450f",
		                                          "fixing bug 123. cleaning remaining warnings", new DateTime(),
		                                          author, null);
		
		final Report report2 = new Report(issueTracker, "345");
		report2.setSummary("there are several remaining warnings");
		report2.setDescription("description");
		report2.setSubject("subject");
		final Person author2 = new Person("kim", "kim@mozkito.org", "Kim Herzig");
		report2.setSubmitter(author2);
		
		report2.addComment(new Comment(1, author2, new DateTime(), "fixed in 04ff08dd3f073dc98e39d68a324632ad4ba0abcd"));
		
		serialize(report, "report1.dmy");
		serialize(changeSet, "changeset1.dmy");
		serialize(report2, "report2.dmy");
	}
	
	private static void serialize(final Object o,
	                              final String filename) {
		try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(new File(filename)))) {
			stream.writeObject(o);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	/** The name. */
	@Rule
	public TestName    name = new TestName();
	
	/** The engine. */
	protected Engine   engine;
	
	/** The relation. */
	protected Relation relation;
	
	/**
	 * Setup.
	 * 
	 * @throws ClassNotFoundException
	 */
	@Before
	public final void setup() throws ClassNotFoundException {
		final String methodName = this.name.getMethodName();
		try {
			final Method method = getClass().getMethod(methodName);
			final TestResources testResources = method.getAnnotation(TestResources.class);
			if (testResources != null) {
				this.engine = new AuthorEqualityEngine();
				
				final String fromResourcePath = File.separatorChar
				        + AuthorEqualityEngine.class.getPackage().getName().replace('.', File.separatorChar)
				        + File.separatorChar + testResources.from() + ".dmy";
				final String toResourcePath = File.separatorChar
				        + AuthorEqualityEngine.class.getPackage().getName().replace('.', File.separatorChar)
				        + File.separatorChar + testResources.to() + ".dmy";
				
				System.err.println("Loading " + fromResourcePath);
				final ObjectInputStream inputStream = new ObjectInputStream(
				                                                            getClass().getResourceAsStream(fromResourcePath));
				final Entity fromEntity = (Entity) inputStream.readObject();
				System.err.println(fromEntity);
				System.err.println("Loading " + toResourcePath);
				final ObjectInputStream inputStream2 = new ObjectInputStream(
				                                                             getClass().getResourceAsStream(toResourcePath));
				final Entity toEntity = (Entity) inputStream2.readObject();
				System.err.println(toEntity);
				@SuppressWarnings ("unchecked")
				final CandidateFactory<Entity, Entity> cF = (CandidateFactory<Entity, Entity>) CandidateFactory.getInstance(fromEntity.getClass(),
				                                                                                                            toEntity.getClass());
				cF.add(fromEntity, toEntity, new HashSet<Selector>());
				
				this.relation = new Relation(cF.get(fromEntity, toEntity));
				assertNotNull(this.engine);
				assertNotNull(this.relation);
				final Fields fields = method.getAnnotation(Fields.class);
				if (fields != null) {
					for (final FieldKey key : fields.from()) {
						assertNotNull(fromEntity.supportedFields());
						assertTrue("There is no supported for getting " + key + " for a " + fromEntity.getClassName(),
						           fromEntity.supportedFields().contains(key));
						assertNotNull("Required accesss to field " + key + " but resource " + testResources.from()
						        + " is lacking entry.", this.relation.getFrom().get(key));
					}
					
					for (final FieldKey key : fields.to()) {
						assertNotNull(toEntity.supportedFields());
						assertTrue("There is no supported for getting " + key + " for a " + toEntity.getClassName(),
						           toEntity.supportedFields().contains(key));
						assertNotNull("Required accesss to field " + key + " but resource " + testResources.to()
						        + " is lacking entry.", this.relation.getTo().get(key));
					}
					
					for (final FieldKey key : fields.both()) {
						assertNotNull(fromEntity.supportedFields());
						assertTrue("There is no supported for getting " + key + " for a " + fromEntity.getClassName(),
						           fromEntity.supportedFields().contains(key));
						assertNotNull("Required accesss to field " + key + " but resource " + testResources.from()
						        + " is lacking entry.", this.relation.getFrom().get(key));
						assertNotNull(toEntity.supportedFields());
						assertTrue("There is no supported for getting " + key + " for a " + toEntity.getClassName(),
						           toEntity.supportedFields().contains(key));
						assertNotNull("Required accesss to field " + key + " but resource " + testResources.to()
						        + " is lacking entry.", this.relation.getTo().get(key));
					}
				}
				return;
			} else {
				this.engine = null;
				this.relation = null;
			}
		} catch (NoSuchMethodException | SecurityException | IOException e) {
			fail(e.getMessage());
		}
		
	}
	
	/**
	 * Test negative score.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public abstract void testNegativeScore() throws IOException;
	
	/**
	 * Test neutral score.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public abstract void testNeutralScore() throws IOException;
	
	/**
	 * Test score.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public abstract void testPositiveScore() throws IOException;
	
}
