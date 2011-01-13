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

import java.util.ArrayList;
import java.util.List;

import de.unisaarland.cs.st.reposuite.ppa.PPAIndex;
import de.unisaarland.cs.st.reposuite.ppa.PPAIndexer;
import de.unisaarland.cs.st.reposuite.ppa.inference.TypeInferenceStrategy;

public abstract class AbstractInferenceStrategy implements TypeInferenceStrategy {
	protected final PPAEngine ppaEngine;
	
	protected final PPAIndexer indexer;
	
	public AbstractInferenceStrategy(final PPAIndexer indexer, final PPAEngine ppaEngine) {
		super();
		this.indexer = indexer;
		this.ppaEngine = ppaEngine;
	}
	
	@Override
	public PPAIndex getMainIndex(final ASTNode node) {
		return new PPAIndex(node);
	}
	
	protected PPADefaultBindingResolver getResolver(final ASTNode node) {
		return (PPADefaultBindingResolver) node.ast.getBindingResolver();
	}
	
	@Override
	public List<PPAIndex> getSecondaryIndexes(final ASTNode node) {
		return new ArrayList<PPAIndex>();
	}
	
	@Override
	public boolean hasDeclaration(final ASTNode node) {
		return isSafe(node);
	}
	
	
	
}
