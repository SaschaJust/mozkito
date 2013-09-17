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

package org.mozkito.mappings.engines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Queue;

import org.junit.Ignore;

import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.test.elements.EngineTest;
import org.mozkito.mappings.test.elements.Fields;
import org.mozkito.mappings.test.elements.TestResources;
import org.mozkito.persistence.FieldKey;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class AuthorEqualityEngineTest extends EngineTest {
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.test.elements.EngineTest#testNegativeScore()
	 */
	@Override
	@Ignore
	public void testNegativeScore() throws IOException {
		// this engine doesn't score negatively
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.test.elements.EngineTest#testNeutralScore()
	 */
	@Override
	@TestResources (from = "report2", to = "changeset1")
	@Fields (both = FieldKey.AUTHOR)
	public void testNeutralScore() throws IOException {
		this.engine.score(this.relation);
		
		assertNotEquals(this.relation.getFrom().get(FieldKey.AUTHOR), this.relation.getTo().get(FieldKey.AUTHOR));
		
		final Queue<Feature> features = this.relation.getFeatures();
		assertNotNull(features);
		assertFalse(features.isEmpty());
		assertEquals(1, features.size());
		
		final Feature feature = features.iterator().next();
		assertNotNull(feature);
		assertEquals((Double) 0d, (Double) feature.getConfidence());
	}
	
	/**
	 * Test method for
	 * {@link org.mozkito.mappings.engines.AuthorEqualityEngine#score(org.mozkito.mappings.model.Relation)} .
	 * 
	 * @throws IOException
	 */
	@Override
	@TestResources (from = "report1", to = "changeset1")
	@Fields (both = FieldKey.AUTHOR)
	public final void testPositiveScore() throws IOException {
		
		this.engine.score(this.relation);
		
		assertEquals(this.relation.getFrom().get(FieldKey.AUTHOR), this.relation.getTo().get(FieldKey.AUTHOR));
		
		final Queue<Feature> features = this.relation.getFeatures();
		assertNotNull(features);
		assertFalse(features.isEmpty());
		assertEquals(1, features.size());
		
		final Feature feature = features.iterator().next();
		assertNotNull(feature);
		assertEquals(AuthorEqualityEngine.DEFAULT_CONFIDENCE, (Double) feature.getConfidence());
	}
}
