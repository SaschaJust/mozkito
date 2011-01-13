/*******************************************************************************
 * PPA - Partial Program Analysis for Java
 * Copyright (C) 2008 Barthelemy Dagenais
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.txt>
 *******************************************************************************/
package org.eclipse.jdt.core.dom;

import de.unisaarland.cs.st.reposuite.ppa.PPAIndexer;

public class SeedVisitor extends ASTVisitor {
	
	private final PPAIndexer indexer;
	private final PPAEngine ppaEngine;
	
	public SeedVisitor(final PPAIndexer indexer, final PPAEngine ppaEngine) {
		super();
		this.indexer = indexer;
		this.ppaEngine = ppaEngine;
	}
	
	@Override
	public void postVisit(final ASTNode node) {
		if (!this.indexer.isSafe(node)) {
			this.ppaEngine.reportUnsafe(node);
			this.indexer.inferTypes(node);
		}
	}
	
}
