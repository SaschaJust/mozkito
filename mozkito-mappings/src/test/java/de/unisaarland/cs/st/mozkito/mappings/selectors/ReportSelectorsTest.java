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
package de.unisaarland.cs.st.mozkito.mappings.selectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.mozkito.mappings.mappable.model.MappableReport;
import de.unisaarland.cs.st.mozkito.mappings.mappable.model.MappableTransaction;
import de.unisaarland.cs.st.mozkito.mappings.selectors.ReportRegexSelector;
import de.unisaarland.cs.st.mozkito.mappings.selectors.Selector;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.versions.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ReportSelectorsTest {
	
	private RCSTransaction      transaction;
	private MappableTransaction mTransaction;
	private PersistenceUtil     util;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.transaction = this.util.loadById("abcdefg", RCSTransaction.class);
		this.mTransaction = new MappableTransaction(this.transaction);
	}
	
	@Test
	public void testReportLuceneSelector() {
		// final Selector selector = new ...
	}
	
	@Test
	public void testReportRegexSelector() {
		final Selector selector = new ReportRegexSelector("fixing\\s+(bug\\s+)?#?({id}[0-9]+)");
		final List<MappableReport> targets = selector.parse(this.mTransaction, MappableReport.class, this.util);
		
		// check number of candidates
		assertEquals("", 4, targets.size());
		
		// check for specific report
		assertTrue("", CollectionUtils.find(targets, new Predicate() {
			
			@Override
			public boolean evaluate(final Object object) {
				// PRECONDITIONS
				
				try {
					final MappableReport mReport = (MappableReport) object;
					return mReport.getId().equals("123");
				} finally {
					// POSTCONDITIONS
				}
			}
		}) != null);
	}
}
