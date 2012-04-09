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
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocationSet;
import de.unisaarland.cs.st.moskito.ppa.model.JavaTypeDefinition;

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
		this.elementCache = locationSet;
		
		this.relativeFilePath = relativeFilePath;
		
		final PackageDeclaration packageDecl = this.cu.getPackage();
		if (packageDecl != null) {
			this.packageName = packageDecl.getName().toString();
		} else {
			this.packageName = UNKNOWN_PACKAGE;
		}
		
		this.primitives = new HashSet<String>();
		this.primitives.addAll(Arrays.asList(primTypes));
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
		
		final String[] calledObjectParts = localCalledObject.split("\\.");
		final String calledObjectName = calledObjectParts[calledObjectParts.length - 1];
		if (this.primitives.contains(calledObjectName)) {
			return false;
		}
		if (this.packageFilter.length > 0) {
			boolean filterPass = false;
			for (int i = 0; i < this.packageFilter.length; ++i) {
				if (localCalledObject.startsWith(this.packageFilter[i])) {
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
		for (final PPAVisitor visitor : this.visitors) {
			if (this.methodStack.isEmpty()) {
				if (!this.classStack.isEmpty()) {
					visitor.endVisit(this, this.cu, node, this.classStack.peek(), null, this.elementCache);
				} else {
					visitor.endVisit(this, this.cu, node, null, null, this.elementCache);
					if (Logger.logDebug()) {
						Logger.debug("Found empty classStack on compilation unit end in file: " + this.relativeFilePath);
					}
				}
			} else {
				visitor.endVisit(this, this.cu, node, this.classStack.peek(), this.methodStack.peek(),
				                 this.elementCache);
			}
		}
	}
	
	/**
	 * Gets the compilation unit derived from file given in constructor.
	 * 
	 * @return the compilation unit
	 */
	public CompilationUnit getCompilationUnit() {
		return this.cu;
	}
	
	/**
	 * Gets the relative file path.
	 * 
	 * @return the relative file path
	 */
	public String getRelativeFilePath() {
		return this.relativeFilePath;
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
		final int currentLine = this.cu.getLineNumber(node.getStartPosition());
		for (final PPAVisitor visitor : this.visitors) {
			if (this.classStack.isEmpty()) {
				visitor.postVisit(this, this.cu, node, null, null, currentLine, this.elementCache);
			} else {
				if (this.methodStack.isEmpty()) {
					visitor.postVisit(this, this.cu, node, this.classStack.peek(), null, currentLine, this.elementCache);
				} else {
					visitor.postVisit(this, this.cu, node, this.classStack.peek(), this.methodStack.peek(),
					                  currentLine, this.elementCache);
				}
			}
		}
		this.previousNode = node;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#preVisit(org.eclipse.jdt.core.dom .ASTNode)
	 */
	@Override
	@NoneNull
	public void preVisit(final ASTNode node) {
		
		final int currentLine = this.cu.getLineNumber(node.getStartPosition());
		final int endLine = this.cu.getLineNumber((node.getStartPosition() + node.getLength()) - 1);
		
		while ((!this.methodStack.isEmpty()) && (this.methodStack.peek().getEndLine() < currentLine)) {
			this.methodStack.pop();
		}
		
		while ((!this.classStack.isEmpty()) && (currentLine > this.classStack.peek().getEndLine())) {
			this.classStack.pop();
		}
		
		// mark comment in definitions and handle them when checking for covered
		// lines
		if (node instanceof Comment) {
			final int startline = this.cu.getLineNumber(node.getStartPosition());
			final int endline = this.cu.getLineNumber(node.getStartPosition() + node.getLength());
			boolean markComment = true;
			if (node instanceof LineComment) {
				int previousLine = -1;
				if (this.previousNode != null) {
					previousLine = this.cu.getLineNumber(this.previousNode.getStartPosition()
					        + this.previousNode.getLength());
				}
				if (previousLine == startline) {
					markComment = false;
				}
			}
			if (markComment) {
				if (!this.methodStack.isEmpty()) {
					this.methodStack.peek().addCommentLines(startline, endline);
				}
				if (!this.classStack.isEmpty()) {
					this.classStack.peek().addCommentLines(startline, endline);
				}
			}
			
		} else if (node instanceof TypeDeclaration) {
			
			final TypeDeclaration td = (TypeDeclaration) node;
			
			int startLine = currentLine;
			boolean startLineShifted = false;
			@SuppressWarnings ("unchecked")
			final List<ASTNode> modifiers = (List<ASTNode>) td.getStructuralProperty(td.getModifiersProperty());
			for (final ASTNode modifier : modifiers) {
				if (modifier instanceof Modifier) {
					startLine = this.cu.getLineNumber(modifier.getStartPosition());
					startLineShifted = true;
					break;
				}
			}
			if (!startLineShifted) {
				final ASTNode nameNode = (ASTNode) td.getStructuralProperty(td.getNameProperty());
				startLine = this.cu.getLineNumber(nameNode.getStartPosition());
			}
			
			@SuppressWarnings ("unchecked")
			final List<ASTNode> bodyDeclarations = (List<ASTNode>) td.getStructuralProperty(td.getBodyDeclarationsProperty());
			// Condition.check(bodyDeclarations.size() > 0,
			// "A type declaration must have a body declaration");
			int bodyStartLine = startLine;
			if (bodyDeclarations.size() > 0) {
				final ASTNode bodyDeclaration = bodyDeclarations.get(0);
				bodyStartLine = this.cu.getLineNumber(bodyDeclaration.getStartPosition());
			}
			
			JavaElementLocation classDefLoc = null;
			if (td.isInterface()) {
				classDefLoc = this.elementCache.addInterfaceDefinition(this.packageName + "." + td.getName().toString(),
				                                                       this.relativeFilePath, startLine, endLine,
				                                                       td.getStartPosition(), bodyStartLine);
			} else {
				classDefLoc = this.elementCache.addClassDefinition(this.packageName + "." + td.getName().toString(),
				                                                   this.relativeFilePath, startLine, endLine,
				                                                   td.getStartPosition(), bodyStartLine);
			}
			Condition.notNull(classDefLoc,
			                  "Must be either a ClassDefinition or InterfaceDefinition location. Cannot be null");
			if (Logger.logDebug()) {
				Logger.debug("PPATypevisitor: Adding new class context with package name +`" + this.packageName
				        + "` and class name `" + td.getName().toString() + "`");
			}
			this.classStack.push(classDefLoc);
			
			final Type superClassType = td.getSuperclassType();
			final JavaTypeDefinition element = ((JavaTypeDefinition) this.classStack.peek().getElement());
			if ((superClassType != null)
			        && ((element.getSuperClassName() == null) || (element.getSuperClassName().equals("")))) {
				final ITypeBinding superClassBinding = superClassType.resolveBinding();
				if (superClassBinding != null) {
					String superClassName = superClassBinding.getQualifiedName();
					if (superClassName.startsWith("src.")) {
						superClassName = superClassName.substring(4);
					}
					element.setSuperClassName(superClassName);
				}
			}
			
		} else if (node instanceof AnonymousClassDeclaration) {
			
			if (this.classStack.isEmpty()) {
				if (Logger.logError()) {
					Logger.error("Found declaration of anonymous class outside a proper class in line" + currentLine
					        + ". Ignoring node!");
				}
				return;
			}
			
			final AnonymousClassDeclaration acd = (AnonymousClassDeclaration) node;
			@SuppressWarnings ("unchecked")
			final List<BodyDeclaration> bodyDeclarations = acd.bodyDeclarations();
			Condition.check(bodyDeclarations.size() > 0, "Found type declaration with " + bodyDeclarations.size()
			        + " body declarations!");
			
			int bodyStartLine = currentLine;
			if (bodyDeclarations.size() > 0) {
				bodyStartLine = this.cu.getLineNumber(bodyDeclarations.get(0).getStartPosition());
			}
			final int anonCount = ((JavaTypeDefinition) this.classStack.peek().getElement()).nextAnonCounter(this);
			if (!this.classStack.peek().getElement().getShortName()
			                    .equals(this.classStack.peek().getElement().getShortName() + "$" + anonCount)) {
				
				String parentName = this.classStack.peek().getElement().getFullQualifiedName();
				if (parentName.contains("$")) {
					final int index = parentName.lastIndexOf("$");
					parentName = parentName.substring(0, index);
				}
				
				final JavaElementLocation classDefLoc = this.elementCache.addAnonymousClassDefinition(((JavaTypeDefinition) this.classStack.peek()
				                                                                                                                           .getElement()),
				                                                                                      parentName
				                                                                                              + "$"
				                                                                                              + anonCount,
				                                                                                      this.relativeFilePath,
				                                                                                      currentLine,
				                                                                                      endLine,
				                                                                                      node.getStartPosition(),
				                                                                                      bodyStartLine);
				this.classStack.push(classDefLoc);
			}
		} else if (node instanceof MethodDeclaration) {
			final MethodDeclaration md = (MethodDeclaration) node;
			
			if (Logger.logTrace()) {
				Logger.trace("Found method declaration %s", md.getName());
			}
			
			int startLine = currentLine;
			
			boolean startLineShifted = false;
			boolean override = false;
			
			@SuppressWarnings ("unchecked")
			final List<ASTNode> modifiers = (List<ASTNode>) md.getStructuralProperty(md.getModifiersProperty());
			for (final ASTNode modifier : modifiers) {
				if ((modifier.getNodeType() == ASTNode.MODIFIER)
				        || (modifier.getNodeType() == ASTNode.MARKER_ANNOTATION)) {
					if (Logger.logTrace()) {
						Logger.trace("Processing Modifiers of method declaration %s. Startline will eb shifted.",
						             md.getName());
					}
					startLine = this.cu.getLineNumber(modifier.getStartPosition());
					startLineShifted = true;
					if ((modifier.getNodeType() == ASTNode.MARKER_ANNOTATION)) {
						final MarkerAnnotation annotation = (MarkerAnnotation) modifier;
						if (annotation.getTypeName().getFullyQualifiedName().toLowerCase().equals("java.lang.override")) {
							if (Logger.logTrace()) {
								Logger.trace("Found method declaration ");
								override = true;
							}
						}
					}
				}
			}
			if (!startLineShifted) {
				if (md.getReturnType2() != null) {
					startLine = this.cu.getLineNumber(md.getReturnType2().getStartPosition());
				}
			}
			if (Logger.logTrace()) {
				Logger.trace("Start line of method declaration %s detected on line: %d", md.getName(), startLine);
			}
			
			final Block body = md.getBody();
			int bodyStartLine = -1;
			if (body != null) {
				bodyStartLine = this.cu.getLineNumber(body.getStartPosition());
			}
			
			if (this.classStack.isEmpty()) {
				if (Logger.logError()) {
					Logger.error("Found declaration of method %s outside a proper class in line %d. Igoring node.",
					             md.getName(), currentLine);
				}
			} else {
				final List<String> arguments = new ArrayList<String>();
				@SuppressWarnings ("rawtypes")
				final List args = md.parameters();
				@SuppressWarnings ("rawtypes")
				final Iterator iter = args.iterator();
				while (iter.hasNext()) {
					final SingleVariableDeclaration dec = (SingleVariableDeclaration) iter.next();
					arguments.add(dec.getType().toString());
				}
				
				final JavaTypeDefinition parent = ((JavaTypeDefinition) this.classStack.peek().getElement());
				final JavaElementLocation methodDefLoc = this.elementCache.addMethodDefinition(parent.getFullQualifiedName(),
				                                                                               md.getName().toString(),
				                                                                               arguments,
				                                                                               getRelativeFilePath(),
				                                                                               startLine, endLine,
				                                                                               node.getStartPosition(),
				                                                                               bodyStartLine, override);
				
				if (!this.methodStack.isEmpty()) {
					if (Logger.logError()) {
						Logger.warn("Adding method definition %s to method stack while stack is not empty. This is not impossible but happens rarely. Filename: $s",
						            md.getName(), getRelativeFilePath());
					}
				}
				this.methodStack.push(methodDefLoc);
			}
		}
		
		if (Logger.logTrace()) {
			Logger.trace("Running external attached PPAVisitors");
		}
		for (final PPAVisitor visitor : this.visitors) {
			JavaElementLocation classPeek = null;
			JavaElementLocation methodPeek = null;
			if (!this.classStack.isEmpty()) {
				classPeek = this.classStack.peek();
			}
			if (!this.methodStack.isEmpty()) {
				methodPeek = this.methodStack.peek();
			}
			visitor.preVisit(this, this.cu, node, classPeek, methodPeek, currentLine, endLine, this.elementCache);
		}
	}
	
	/**
	 * Register all visitor.
	 * 
	 * @param visitors
	 *            the visitors
	 * @return true, if successful
	 */
	@NoneNull
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
	@NoneNull
	public boolean registerVisitor(final PPAVisitor visitor) {
		if (Logger.logTrace()) {
			Logger.trace("Registering PPAVisitor: %s", visitor.toString());
		}
		return this.visitors.add(visitor);
	}
}
