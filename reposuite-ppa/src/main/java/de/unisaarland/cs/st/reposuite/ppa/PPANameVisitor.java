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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;

public class PPANameVisitor extends ASTVisitor {
	
	public static String getKey(final Name node) {
		return node.getFullyQualifiedName()+ "" + node.getStartPosition() + "" + node.getLength();
	}
	
	private final Map<String,Name> names = new HashMap<String, Name>();
	
	@Override
	public void endVisit(final QualifiedName node) {
		names.put(getKey(node), node);
		super.endVisit(node);
	}
	
	@Override
	public void endVisit(final SimpleName node) {
		names.put(getKey(node), node);
		super.endVisit(node);
	}
	
	public Name getName(final Name node) {
		return names.get(getKey(node));
	}
	
	public Map<String, Name> getNames() {
		return names;
	}
	
}
