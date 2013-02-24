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

import java.io.IOException;
import java.lang.reflect.Method;

import net.ownhero.dev.ioda.Tuple;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import org.mozkito.mappings.engines.AuthorEqualityEngine;
import org.mozkito.mappings.engines.Engine;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.model.Candidate;
import org.mozkito.mappings.model.Relation;

/**
 * The Class EngineTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class EngineTest {
	
	/** The name. */
	@Rule
	public TestName    name = new TestName();
	
	/** The engine. */
	protected Engine   engine;
	
	/** The relation. */
	protected Relation relation;
	
	/**
	 * Setup.
	 */
	@Before
	public final void setup() {
		final String methodName = this.name.getMethodName();
		try {
			final Method method = getClass().getMethod(methodName);
			final TestResources testResources = method.getAnnotation(TestResources.class);
			if (testResources != null) {
				this.engine = new AuthorEqualityEngine();
				final MappableEntity fromEntity = new Dummy(testResources.from());
				final MappableEntity toEntity = new Dummy(testResources.to());
				this.relation = new Relation(new Candidate(new Tuple<MappableEntity, MappableEntity>(fromEntity,
				                                                                                     toEntity)));
				assertNotNull(this.engine);
				assertNotNull(this.relation);
				final Fields fields = method.getAnnotation(Fields.class);
				if (fields != null) {
					for (final FieldKey key : fields.from()) {
						assertNotNull(fromEntity.supported());
						assertTrue("There is no supported for getting " + key + " for a "
						        + fromEntity.getBaseType().getSimpleName(), fromEntity.supported().contains(key));
						assertNotNull("Required accesss to field " + key + " but resource " + testResources.from()
						        + " is lacking entry.", this.relation.getFrom().get(key));
					}
					
					for (final FieldKey key : fields.to()) {
						assertNotNull(toEntity.supported());
						assertTrue("There is no supported for getting " + key + " for a "
						        + toEntity.getBaseType().getSimpleName(), toEntity.supported().contains(key));
						assertNotNull("Required accesss to field " + key + " but resource " + testResources.to()
						        + " is lacking entry.", this.relation.getTo().get(key));
					}
					
					for (final FieldKey key : fields.both()) {
						assertNotNull(fromEntity.supported());
						assertTrue("There is no supported for getting " + key + " for a "
						        + fromEntity.getBaseType().getSimpleName(), fromEntity.supported().contains(key));
						assertNotNull("Required accesss to field " + key + " but resource " + testResources.from()
						        + " is lacking entry.", this.relation.getFrom().get(key));
						assertNotNull(toEntity.supported());
						assertTrue("There is no supported for getting " + key + " for a "
						        + toEntity.getBaseType().getSimpleName(), toEntity.supported().contains(key));
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
