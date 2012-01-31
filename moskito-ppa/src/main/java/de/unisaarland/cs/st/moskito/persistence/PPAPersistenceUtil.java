/*******************************************************************************
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
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElement;
import de.unisaarland.cs.st.moskito.rcs.model.RCSRevision;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class PPAPersistenceUtil.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAPersistenceUtil {
	
	public static Collection<JavaChangeOperation> getChangeOperation(final PersistenceUtil persistenceUtil,
	                                                                 final RCSTransaction transaction) {
		final List<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		
		for (final RCSRevision revision : transaction.getRevisions()) {
			final Criteria<JavaChangeOperation> criteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
			criteria.eq("revision", revision);
			result.addAll(persistenceUtil.load(criteria));
		}
		return result;
	}
	
	public static Collection<JavaChangeOperation> getChangeOperation(final PersistenceUtil persistenceUtil,
	                                                                 final String transactionId) {
		final List<JavaChangeOperation> result = new ArrayList<JavaChangeOperation>(0);
		
		final RCSTransaction transaction = persistenceUtil.loadById(transactionId, RCSTransaction.class);
		if (transaction != null) {
			return getChangeOperation(persistenceUtil, transaction);
		}
		
		return result;
	}
	
	/**
	 * Gets the java element.
	 * 
	 * @param persistenceUtil
	 *            the persistence middleware util
	 * @param e
	 *            the e
	 * @return the java element
	 */
	public static JavaElement getJavaElement(final PersistenceUtil persistenceUtil,
	                                         final JavaElement e) {
		final Criteria<? extends JavaElement> criteria = persistenceUtil.createCriteria(e.getClass());
		final CriteriaBuilder cb = criteria.getBuilder();
		final Root<? extends JavaElement> root = criteria.getRoot();
		final Predicate predicate = cb.and(cb.equal(root.get("fullQualifiedName"), e.getFullQualifiedName()),
		                                   cb.equal(root.get("elementType"), e.getElementType()));
		criteria.getQuery().where(predicate);
		
		final List<? extends JavaElement> elements = persistenceUtil.load(criteria);
		if (elements.isEmpty()) {
			return null;
		}
		if (elements.size() > 1) {
			if (Logger.logError()) {
				Logger.error("Found more that one JavaElement with same primaryKey! This is impossible!");
			}
			return null;
		}
		return elements.get(0);
	}
	
}
