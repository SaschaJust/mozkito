/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.persistence;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElement;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * The Class PPAPersistenceUtil.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAPersistenceUtil {
	
	public static List<JavaChangeOperation> getChangeOperation(final PersistenceUtil persistenceUtil,
	                                                           final RCSTransaction transaction) {
		List<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		
		for (RCSRevision revision : transaction.getRevisions()) {
			Criteria<JavaChangeOperation> criteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
			criteria.eq("revision", revision);
			result.addAll(persistenceUtil.load(criteria));
		}
		return result;
	}
	
	public static List<JavaChangeOperation> getChangeOperation(final PersistenceUtil persistenceUtil,
	                                                           final String transactionId) {
		List<JavaChangeOperation> result = new ArrayList<JavaChangeOperation>(0);
		
		RCSTransaction transaction = persistenceUtil.loadById(transactionId, RCSTransaction.class);
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
		Criteria<? extends JavaElement> criteria = persistenceUtil.createCriteria(e.getClass());
		criteria.eq("fullQualifiedName", e.getFullQualifiedName()).eq("elementType", e.getElementType());
		List<? extends JavaElement> elements = persistenceUtil.load(criteria);
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
