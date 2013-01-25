/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.mozkito.ppa.visitors;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.joda.time.DateTime;

import org.mozkito.ppa.CompilationUnitException;
import org.mozkito.ppa.model.JavaClassDefinition;
import org.mozkito.ppa.model.JavaMethodDefinition;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.ioda.specification.NoneNull;

/**
 * The Class PPATypeVisitor.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PPATypeVisitor extends ASTVisitor {
	
	public static String                        UNKNOWN_PACKAGE  = "<UNKNOWN>";
	private static String[]                     primTypes        = { "Class", "Character", "Byte", "Short", "Integer",
		"Long", "Float", "Double", "Boolean", "Void", "String", "TestCase", "ClassMapper", "Thread", "ClassLoader",
		"Color", "AbstractCollectionConverter", "ObjectTree" };
	private final HashSet<JavaClassDefinition>  classContexts    = new HashSet<JavaClassDefinition>();
	private final HashSet<JavaMethodDefinition> methodContexts   = new HashSet<JavaMethodDefinition>();
	private final Stack<JavaClassDefinition>    classStack       = new Stack<JavaClassDefinition>();
	private final Stack<JavaMethodDefinition>   methodStack      = new Stack<JavaMethodDefinition>();
	protected CompilationUnit                   cu;
	private final File                          file;
	protected String[]                          packageFilter;
	private String                              packageName;
	protected HashSet<String>                   primitives;
	private final Set<PPAVisitor>               visitors         = new HashSet<PPAVisitor>();
	private String                              relativeFilePath = "";
	
	/**
	 * Instantiates a new pPA type visitor.
	 * 
	 * @param file
	 *            the file
	 * @param packageFilter
	 *            the package filter
	 * @throws CompilationUnitException
	 *             the compilation unit exception
	 */
	@NoneNull
	public PPATypeVisitor(final CompilationUnit cu, final File file, final String filePathPrefix,
			final String[] packageFilter, final Set<PPAVisitor> visitors) throws CompilationUnitException {
		this.packageFilter = packageFilter;
		this.file = file;
		this.cu = cu;
		this.visitors.addAll(visitors);
		
		if (file.getAbsolutePath().startsWith(filePathPrefix)) {
			relativeFilePath = file.getAbsolutePath().replaceFirst(filePathPrefix, "");
			if (relativeFilePath.startsWith(FileUtils.fileSeparator)) {
				relativeFilePath = relativeFilePath.substring(1);
			}
		}
		
		PackageDeclaration packageDecl = this.cu.getPackage();
		if (packageDecl != null) {
			packageName = packageDecl.getName().toString();
		} else {
			packageName = UNKNOWN_PACKAGE;
		}
		
		primitives = new HashSet<String>();
		primitives.addAll(Arrays.asList(primTypes));
	} /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .CompilationUnit)
	 */
	
	/**
	 * Checks if a given object name passes the package filters set in
	 * constructor.
	 * 
	 * @param calledObject
	 *            the called object
	 * @return true, if successful
	 */
	public boolean checkFilters(final String calledObject) {
		String localCalledObject = calledObject;
		if (localCalledObject.startsWith("src.")) {
			localCalledObject = localCalledObject.substring(4);
		}
		
		String[] calledObjectParts = localCalledObject.split("\\.");
		String calledObjectName = calledObjectParts[calledObjectParts.length - 1];
		if (primitives.contains(calledObjectName)) {
			return false;
		}
		if (packageFilter.length > 0) {
			boolean filterPass = false;
			for (int i = 0; i < packageFilter.length; ++i) {
				if (localCalledObject.startsWith(packageFilter[i])) {
					filterPass = true;
				}
			}
			if (!filterPass) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void endVisit(final CompilationUnit node) {
		super.endVisit(node);
		for (PPAVisitor visitor : visitors) {
			if (methodStack.isEmpty()) {
				visitor.endVisit(this, cu, node, classStack.peek(), null);
			} else {
				visitor.endVisit(this, cu, node, classStack.peek(), methodStack.peek());
			}
		}
	}
	
	/**
	 * Gets all class contexts parsed so far.
	 * 
	 * @return the class contexts
	 */
	public Collection<JavaClassDefinition> getClassDefinitions() {
		return classContexts;
	}
	
	/**
	 * Gets the compilation unit derived from file given in constructor.
	 * 
	 * @return the compilation unit
	 */
	public CompilationUnit getCompilationUnit() {
		return cu;
	}
	
	public File getFile() {
		return file;
	}
	
	/**
	 * Gets the method contexts parsed so far.
	 * 
	 * @return the method contexts
	 */
	public Collection<JavaMethodDefinition> getMethodDefinitions() {
		return methodContexts;
	}
	
	public String getRelativeFilePath() {
		return relativeFilePath;
	}
	
	/**
	 * 
	 * @param node
	 */
	@Override
	public void postVisit(final ASTNode node) {
		super.postVisit(node);
		int currentLine = cu.getLineNumber(node.getStartPosition());
		for (PPAVisitor visitor : visitors) {
			if (classStack.isEmpty()) {
				visitor.postVisit(this, cu, node, null, null, currentLine);
			} else {
				if (methodStack.isEmpty()) {
					visitor.postVisit(this, cu, node, classStack.peek(), null, currentLine);
				} else {
					visitor.postVisit(this, cu, node, classStack.peek(), methodStack.peek(), currentLine);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#preVisit(org.eclipse.jdt.core.dom
	 * .ASTNode)
	 */
	@Override
	public void preVisit(final ASTNode node) {
		
		int currentLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength() - 1);
		
		if (!methodStack.isEmpty()) {
			if (methodStack.peek().getEndLine() < currentLine) {
				methodStack.pop();
			}
		}
		
		while ((!classStack.isEmpty()) && (cu.getLineNumber(node.getStartPosition()) > classStack.peek().getEndLine())) {
			classStack.pop();
		}
		
		if (node instanceof TypeDeclaration) {
			
			TypeDeclaration td = (TypeDeclaration) node;
			
			JavaClassDefinition parent = null;
			
			if (!classStack.isEmpty()) {
				//FOUND INNER CLASS: add parent to class definition
				parent = classStack.peek();
			}
			
			JavaClassDefinition classDef = new JavaClassDefinition(packageName + "." + td.getName().toString(),
					relativeFilePath, new DateTime(getFile().lastModified()), parent, currentLine, endLine, packageName);
			if (Logger.logDebug()) {
				Logger.debug("PPATypevisitor: Adding new class context with package name +`" + packageName
						+ "` and class name `" + td.getName().toString() + "`");
			}
			classStack.push(classDef);
			classContexts.add(classDef);
			
			Type superClassType = td.getSuperclassType();
			if (superClassType != null) {
				ITypeBinding superClassBinding = superClassType.resolveBinding();
				String superClassName = superClassBinding.getQualifiedName();
				if (superClassName.startsWith("src.")) {
					superClassName = superClassName.substring(4);
				}
				classStack.peek().setSuperclass(superClassName);
			}
			
		} else if (node instanceof AnonymousClassDeclaration) {
			
			if (classStack.isEmpty()) {
				if (Logger.logError()) {
					Logger.error("Found declaration of anonymous class outside a proper class in line" + currentLine
							+ ". Ignoring node!");
				}
				return;
			}
			
			int anonCount = classStack.peek().nextAnonCounter();
			if (!classStack.peek().getShortName().equals(classStack.peek().getShortName() + "$" + anonCount)) {
				
				String parentName = classStack.peek().getFullQualifiedName();
				if (parentName.contains("$")) {
					int index = parentName.lastIndexOf("$");
					parentName = parentName.substring(0, index);
				}
				
				JavaClassDefinition classDef = new JavaClassDefinition(parentName + "$" + anonCount, relativeFilePath,
						new DateTime(getFile().lastModified()), classStack.peek(), currentLine, endLine, packageName);
				classStack.push(classDef);
				classContexts.add(classDef);
			}
		} else if (node instanceof MethodDeclaration) {
			MethodDeclaration md = (MethodDeclaration) node;
			if (classStack.isEmpty()) {
				if (Logger.logError()) {
					Logger.error("Found declaration of method outside a proper class in line" + currentLine
							+ ". Ignoring node!");
				}
			} else {
				List<String> arguments = new ArrayList<String>();
				@SuppressWarnings("rawtypes") List args = md.parameters();
				@SuppressWarnings("rawtypes") Iterator iter = args.iterator();
				while (iter.hasNext()) {
					SingleVariableDeclaration dec = (SingleVariableDeclaration) iter.next();
					arguments.add(dec.getType().toString());
				}
				
				JavaMethodDefinition methodDef = classStack.peek().addMethod(md.getName().toString(), arguments,
						new DateTime(getFile().lastModified()), currentLine, endLine);
				if (!methodStack.isEmpty()) {
					if (Logger.logError()) {
						Logger.warn("Adding method definition to method stack while stack is not empty. This is not impossible but happens rarely!");
					}
				}
				methodStack.push(methodDef);
				methodContexts.add(methodDef);
			}
		}
		
		for (PPAVisitor visitor : visitors) {
			if (classStack.isEmpty()) {
				visitor.preVisit(this, cu, node, null, null, currentLine, endLine);
			} else {
				if (methodStack.isEmpty()) {
					visitor.preVisit(this, cu, node, classStack.peek(), null, currentLine, endLine);
				} else {
					visitor.preVisit(this, cu, node, classStack.peek(), methodStack.peek(), currentLine, endLine);
				}
			}
		}
		
	}
	
	/**
	 * Register visitor that will be notified when parsing.
	 * 
	 * @param visitor
	 *            the visitor
	 * @return true, if successful
	 */
	public boolean registerVisitor(final PPAVisitor visitor) {
		return visitors.add(visitor);
	}
}
