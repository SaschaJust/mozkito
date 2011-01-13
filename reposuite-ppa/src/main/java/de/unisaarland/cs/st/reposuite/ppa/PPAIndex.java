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
package de.unisaarland.cs.st.reposuite.ppa;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.PPABindingsUtil;

public class PPAIndex {
	
	private ASTNode node;
	
	private IBinding binding;
	
	public PPAIndex(final ASTNode node) {
		super();
		this.node = node;
	}
	
	public PPAIndex(final IBinding binding) {
		super();
		this.binding = binding;
	}
	
	@Override
	public boolean equals(final Object arg0) {
		if (!(arg0 instanceof PPAIndex)) {
			return false;
		}
		PPAIndex other = (PPAIndex) arg0;
		
		if (node != null) {
			return other.node == node;
		} else if (other.binding == null) {
			return false;
		} else {
			// This is in case the binding partially changed.
			return PPABindingsUtil.isEquivalent(other.binding, binding);
		}
		
	}
	
	public IBinding getBinding() {
		return binding;
	}
	
	public ASTNode getNode() {
		return node;
	}
	
	@Override
	public int hashCode() {
		if (node != null) {
			return node.hashCode();
		} else {
			return binding.getKey().hashCode();
		}
	}
	
	public void setBinding(final IBinding binding) {
		this.binding = binding;
	}
	
	
	
	public void setNode(final ASTNode node) {
		this.node = node;
	}
	
}
