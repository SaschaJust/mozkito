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
package de.unisaarland.cs.st.moskito.ppa.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.unisaarland.cs.st.moskito.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocationSet;

/**
 * The Class PPATypeVisitor.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPATypeVisitor extends ASTVisitor {
	
	/** The UNKNOW n_ package. */
	public static String                     UNKNOWN_PACKAGE  = "<UNKNOWN>";
	
	/** The prim types. */
	private static String[]                  primTypes        = { "Class", "Character", "Byte", "Short", "Integer",
	        "Long", "Float", "Double", "Boolean", "Void", "String", "TestCase", "ClassMapper", "Thread", "ClassLoader",
	        "Color", "AbstractCollectionConverter", "ObjectTree" };
	
	/** The class stack. */
	private final Stack<JavaElementLocation> classStack       = new Stack<JavaElementLocation>();
	
	/** The method stack. */
	private final Stack<JavaElementLocation> methodStack      = new Stack<JavaElementLocation>();
	
	/** The cu. */
	protected CompilationUnit                cu;
	
	/** The package filter. */
	protected String[]                       packageFilter;
	
	/** The package name. */
	private String                           packageName;
	
	/** The primitives. */
	protected HashSet<String>                primitives;
	
	/** The visitors. */
	private final Set<PPAVisitor>            visitors         = new HashSet<PPAVisitor>();
	
	/** The relative file path. */
	private String                           relativeFilePath = "";
	
	/** The element cache. */
	private final JavaElementLocationSet     elementCache;
	
	/** The previous node. */
	private ASTNode                          previousNode     = null;
	
	/**
	 * Instantiates a new pPA type visitor.
	 * 
	 * @param cu
	 *            the cu
	 * @param file
	 *            the file
	 * @param filePathPrefix
	 *            the file path prefix
	 * @param packageFilter
	 *            the package filter
	 * @param locationSet
	 *            the element cache
	 */
	@NoneNull
	public PPATypeVisitor(final CompilationUnit cu, final String relativeFilePath, final String[] packageFilter,
	        final JavaElementLocationSet locationSet) {
		this.packageFilter = packageFilter;
		this.cu = cu;
		elementCache = locationSet;
		
		this.relativeFilePath = relativeFilePath;
		
		PackageDeclaration packageDecl = this.cu.getPackage();
		if (packageDecl != null) {
			packageName = packageDecl.getName().toString();
		} else {
			packageName = UNKNOWN_PACKAGE;
		}
		
		primitives = new HashSet<String>();
		primitives.addAll(Arrays.asList(primTypes));
	}
	
	/**
	 * Checks if a given object name passes the package filters set in constructor.
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
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom .CompilationUnit)
	 */
	@Override
	public void endVisit(final CompilationUnit node) {
		super.endVisit(node);
		for (PPAVisitor visitor : visitors) {
			if (methodStack.isEmpty()) {
				if (!classStack.isEmpty()) {
					visitor.endVisit(this, cu, node, classStack.peek(), null, elementCache);
				} else {
					visitor.endVisit(this, cu, node, null, null, elementCache);
					if (Logger.logDebug()) {
						Logger.debug("Found empty classStack on compilation unit end in file: " + relativeFilePath);
					}
				}
			} else {
				visitor.endVisit(this, cu, node, classStack.peek(), methodStack.peek(), elementCache);
			}
		}
	}
	
	/**
	 * Gets the compilation unit derived from file given in constructor.
	 * 
	 * @return the compilation unit
	 */
	public CompilationUnit getCompilationUnit() {
		return cu;
	}
	
	/**
	 * Gets the relative file path.
	 * 
	 * @return the relative file path
	 */
	public String getRelativeFilePath() {
		return relativeFilePath;
	}
	
	/**
	 * Post visit.
	 * 
	 * @param node
	 *            the node
	 */
	@Override
	public void postVisit(final ASTNode node) {
		super.postVisit(node);
		int currentLine = cu.getLineNumber(node.getStartPosition());
		for (PPAVisitor visitor : visitors) {
			if (classStack.isEmpty()) {
				visitor.postVisit(this, cu, node, null, null, currentLine, elementCache);
			} else {
				if (methodStack.isEmpty()) {
					visitor.postVisit(this, cu, node, classStack.peek(), null, currentLine, elementCache);
				} else {
					visitor.postVisit(this, cu, node, classStack.peek(), methodStack.peek(), currentLine, elementCache);
				}
			}
		}
		previousNode = node;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#preVisit(org.eclipse.jdt.core.dom .ASTNode)
	 */
	@Override
	public void preVisit(final ASTNode node) {
		
		int currentLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength() - 1);
		
		while ((!methodStack.isEmpty()) && (methodStack.peek().getEndLine() < currentLine)) {
			methodStack.pop();
		}
		
		while ((!classStack.isEmpty()) && (currentLine > classStack.peek().getEndLine())) {
			classStack.pop();
		}
		
		// mark comment in definitions and handle them when checking for covered
		// lines
		if (node instanceof Comment) {
			int startline = cu.getLineNumber(node.getStartPosition());
			int endline = cu.getLineNumber(node.getStartPosition() + node.getLength());
			boolean markComment = true;
			if (node instanceof LineComment) {
				int previousLine = -1;
				if (previousNode != null) {
					previousLine = cu.getLineNumber(previousNode.getStartPosition() + previousNode.getLength());
				}
				if (previousLine == startline) {
					markComment = false;
				}
			}
			if (markComment) {
				if (!methodStack.isEmpty()) {
					methodStack.peek().addCommentLines(startline, endline);
				}
				if (!classStack.isEmpty()) {
					classStack.peek().addCommentLines(startline, endline);
				}
			}
			
		} else if (node instanceof TypeDeclaration) {
			
			TypeDeclaration td = (TypeDeclaration) node;
			
			int startLine = currentLine;
			boolean startLineShifted = false;
			@SuppressWarnings ("unchecked")
			List<ASTNode> modifiers = (List<ASTNode>) td.getStructuralProperty(td.getModifiersProperty());
			for (ASTNode modifier : modifiers) {
				if (modifier instanceof Modifier) {
					startLine = cu.getLineNumber(modifier.getStartPosition());
					startLineShifted = true;
					break;
				}
			}
			if (!startLineShifted) {
				ASTNode nameNode = (ASTNode) td.getStructuralProperty(td.getNameProperty());
				startLine = cu.getLineNumber(nameNode.getStartPosition());
			}
			
			@SuppressWarnings ("unchecked")
			List<ASTNode> bodyDeclarations = (List<ASTNode>) td.getStructuralProperty(td.getBodyDeclarationsProperty());
			// Condition.check(bodyDeclarations.size() > 0,
			// "A type declaration must have a body declaration");
			int bodyStartLine = startLine;
			if (bodyDeclarations.size() > 0) {
				ASTNode bodyDeclaration = bodyDeclarations.get(0);
				bodyStartLine = cu.getLineNumber(bodyDeclaration.getStartPosition());
			}
			JavaElementLocation classDefLoc = elementCache.addClassDefinition(packageName + "."
			                                                                          + td.getName().toString(),
			                                                                  relativeFilePath, startLine, endLine,
			                                                                  td.getStartPosition(),
			                                                                  bodyStartLine);
			
			if (Logger.logDebug()) {
				Logger.debug("PPATypevisitor: Adding new class context with package name +`" + packageName
				        + "` and class name `" + td.getName().toString() + "`");
			}
			classStack.push(classDefLoc);
			
			Type superClassType = td.getSuperclassType();
			JavaClassDefinition element = ((JavaClassDefinition) classStack.peek().getElement());
			if ((superClassType != null)
			        && ((element.getSuperClassName() == null) || (element.getSuperClassName().equals("")))) {
				ITypeBinding superClassBinding = superClassType.resolveBinding();
				if (superClassBinding != null) {
					String superClassName = superClassBinding.getQualifiedName();
					if (superClassName.startsWith("src.")) {
						superClassName = superClassName.substring(4);
					}
					element.setSuperClassName(superClassName);
				}
			}
			
		} else if (node instanceof AnonymousClassDeclaration) {
			
			if (classStack.isEmpty()) {
				if (Logger.logError()) {
					Logger.error("Found declaration of anonymous class outside a proper class in line" + currentLine
					        + ". Ignoring node!");
				}
				return;
			}
			
			AnonymousClassDeclaration acd = (AnonymousClassDeclaration) node;
			@SuppressWarnings ("unchecked")
			List<BodyDeclaration> bodyDeclarations = acd.bodyDeclarations();
			Condition.check(bodyDeclarations.size() > 0, "Found type declaration with " + bodyDeclarations.size()
			        + " body declarations!");
			
			int bodyStartLine = currentLine;
			if (bodyDeclarations.size() > 0) {
				bodyStartLine = cu.getLineNumber(bodyDeclarations.get(0).getStartPosition());
			}
			int anonCount = ((JavaClassDefinition) classStack.peek().getElement()).nextAnonCounter(this);
			if (!classStack.peek().getElement().getShortName()
			               .equals(classStack.peek().getElement().getShortName() + "$" + anonCount)) {
				
				String parentName = classStack.peek().getElement().getFullQualifiedName();
				if (parentName.contains("$")) {
					int index = parentName.lastIndexOf("$");
					parentName = parentName.substring(0, index);
				}
				
				JavaElementLocation classDefLoc = elementCache.addAnonymousClassDefinition(((JavaClassDefinition) classStack.peek()
				                                                                                                            .getElement()),
				                                                                           parentName + "$" + anonCount,
				                                                                           relativeFilePath,
				                                                                           currentLine, endLine,
				                                                                           node.getStartPosition(),
				                                                                           bodyStartLine);
				classStack.push(classDefLoc);
			}
		} else if (node instanceof MethodDeclaration) {
			MethodDeclaration md = (MethodDeclaration) node;
			
			int startLine = currentLine;
			
			boolean startLineShifted = false;
			@SuppressWarnings ("unchecked")
			List<ASTNode> modifiers = (List<ASTNode>) md.getStructuralProperty(md.getModifiersProperty());
			for (ASTNode modifier : modifiers) {
				if (modifier instanceof Modifier) {
					startLine = cu.getLineNumber(modifier.getStartPosition());
					startLineShifted = true;
					break;
				}
			}
			if (!startLineShifted) {
				if (md.getReturnType2() != null) {
					startLine = cu.getLineNumber(md.getReturnType2().getStartPosition());
				}
			}
			
			Block body = md.getBody();
			int bodyStartLine = -1;
			if (body != null) {
				bodyStartLine = cu.getLineNumber(body.getStartPosition());
			}
			
			if (classStack.isEmpty()) {
				if (Logger.logError()) {
					Logger.error("Found declaration of method outside a proper class in line" + currentLine
					        + ". Ignoring node!");
				}
			} else {
				List<String> arguments = new ArrayList<String>();
				@SuppressWarnings ("rawtypes")
				List args = md.parameters();
				@SuppressWarnings ("rawtypes")
				Iterator iter = args.iterator();
				while (iter.hasNext()) {
					SingleVariableDeclaration dec = (SingleVariableDeclaration) iter.next();
					arguments.add(dec.getType().toString());
				}
				
				JavaClassDefinition parent = ((JavaClassDefinition) classStack.peek().getElement());
				JavaElementLocation methodDefLoc = elementCache.addMethodDefinition(parent.getFullQualifiedName(),
				                                                                    md.getName().toString(), arguments,
				                                                                    getRelativeFilePath(), startLine,
				                                                                    endLine, node.getStartPosition(),
				                                                                    bodyStartLine);
				
				if (!methodStack.isEmpty()) {
					if (Logger.logError()) {
						Logger.warn("Adding method definition to method stack while stack is not empty. This is not impossible but happens rarely!");
					}
				}
				methodStack.push(methodDefLoc);
			}
		}
		
		for (PPAVisitor visitor : visitors) {
			if (classStack.isEmpty()) {
				visitor.preVisit(this, cu, node, null, null, currentLine, endLine, elementCache);
			} else {
				if (methodStack.isEmpty()) {
					visitor.preVisit(this, cu, node, classStack.peek(), null, currentLine, endLine, elementCache);
				} else {
					visitor.preVisit(this, cu, node, classStack.peek(), methodStack.peek(), currentLine, endLine,
					                 elementCache);
				}
			}
		}
		
	}
	
	/**
	 * Register all visitor.
	 * 
	 * @param visitors
	 *            the visitors
	 * @return true, if successful
	 */
	public boolean registerAllVisitor(final Collection<PPAVisitor> visitors) {
		return this.visitors.addAll(visitors);
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
