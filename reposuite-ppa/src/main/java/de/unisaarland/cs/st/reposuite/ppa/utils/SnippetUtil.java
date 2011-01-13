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
package de.unisaarland.cs.st.reposuite.ppa.utils;

import java.io.IOException;

public class SnippetUtil {
	
	public final static String SNIPPET_PACKAGE = "zzzsnippet";
	
	public final static String SNIPPET_FILE = "ZZZSnippet.java";
	
	public final static String SNIPPET_CLASS = "ZZZSnippet";
	
	public final static String SNIPPET_METHOD = "mainZZZSnippet";
	
	public final static String SNIPPET_SUPER_CLASS = "ZZZSuperZZZSnippet";
	
	public static String getMethodBody(final String snippet) throws IOException {
		StringBuffer newContent = new StringBuffer();
		newContent.append("package " + SNIPPET_PACKAGE + ";\n");
		newContent.append("public class " + SNIPPET_CLASS + " extends "+ SNIPPET_SUPER_CLASS + " {\n");
		newContent.append("  public void " + SNIPPET_METHOD + "() {\n");
		newContent.append(snippet);
		newContent.append("\n  }\n");
		newContent.append("\n}\n");
		
		return newContent.toString();
	}
	
	public static String getTypeBody(final String snippet) throws IOException {
		StringBuffer newContent = new StringBuffer();
		newContent.append("package " + SNIPPET_PACKAGE + ";\n");
		newContent.append("public class " + SNIPPET_CLASS + " extends "+ SNIPPET_SUPER_CLASS + " {\n");
		newContent.append(snippet);
		newContent.append("\n}\n");
		
		return newContent.toString();
	}
	
}
