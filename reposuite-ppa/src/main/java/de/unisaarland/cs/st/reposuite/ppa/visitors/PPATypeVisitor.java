package de.unisaarland.cs.st.reposuite.ppa.visitors;

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

import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocationSet;

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
		
		PackageDeclaration packageDecl = this.cu.getPackage();
		if (packageDecl != null) {
			this.packageName = packageDecl.getName().toString();
		} else {
			this.packageName = UNKNOWN_PACKAGE;
		}
		
		this.primitives = new HashSet<String>();
		this.primitives.addAll(Arrays.asList(primTypes));
	}
	
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
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .CompilationUnit)
	 */
	@Override
	public void endVisit(final CompilationUnit node) {
		super.endVisit(node);
		for (PPAVisitor visitor : this.visitors) {
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
		int currentLine = this.cu.getLineNumber(node.getStartPosition());
		for (PPAVisitor visitor : this.visitors) {
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
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#preVisit(org.eclipse.jdt.core.dom
	 * .ASTNode)
	 */
	@Override
	public void preVisit(final ASTNode node) {
		
		int currentLine = this.cu.getLineNumber(node.getStartPosition());
		int endLine = this.cu.getLineNumber(node.getStartPosition() + node.getLength() - 1);
		
		while ((!this.methodStack.isEmpty()) && (this.methodStack.peek().getEndLine() < currentLine)) {
			this.methodStack.pop();
		}
		
		while ((!this.classStack.isEmpty()) && (currentLine > this.classStack.peek().getEndLine())) {
			this.classStack.pop();
		}
		
		// mark comment in definitions and handle them when checking for covered
		// lines
		if (node instanceof Comment) {
			int startline = this.cu.getLineNumber(node.getStartPosition());
			int endline = this.cu.getLineNumber(node.getStartPosition() + node.getLength());
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
			
			TypeDeclaration td = (TypeDeclaration) node;
			
			int startLine = currentLine;
			boolean startLineShifted = false;
			@SuppressWarnings ("unchecked")
			List<ASTNode> modifiers = (List<ASTNode>) td.getStructuralProperty(td.getModifiersProperty());
			for (ASTNode modifier : modifiers) {
				if (modifier instanceof Modifier) {
					startLine = this.cu.getLineNumber(modifier.getStartPosition());
					startLineShifted = true;
					break;
				}
			}
			if (!startLineShifted) {
				ASTNode nameNode = (ASTNode) td.getStructuralProperty(td.getNameProperty());
				startLine = this.cu.getLineNumber(nameNode.getStartPosition());
			}
			
			@SuppressWarnings ("unchecked")
			List<ASTNode> bodyDeclarations = (List<ASTNode>) td.getStructuralProperty(td.getBodyDeclarationsProperty());
			// Condition.check(bodyDeclarations.size() > 0,
			// "A type declaration must have a body declaration");
			int bodyStartLine = startLine;
			if (bodyDeclarations.size() > 0) {
				ASTNode bodyDeclaration = bodyDeclarations.get(0);
				bodyStartLine = this.cu.getLineNumber(bodyDeclaration.getStartPosition());
			}
			JavaElementLocation classDefLoc = this.elementCache.addClassDefinition(this.packageName + "."
			                                                                               + td.getName().toString(),
			                                                                       this.relativeFilePath, startLine,
			                                                                       endLine, td.getStartPosition(),
			                                                                       bodyStartLine);
			
			if (Logger.logDebug()) {
				Logger.debug("PPATypevisitor: Adding new class context with package name +`" + this.packageName
				        + "` and class name `" + td.getName().toString() + "`");
			}
			this.classStack.push(classDefLoc);
			
			Type superClassType = td.getSuperclassType();
			if (superClassType != null) {
				ITypeBinding superClassBinding = superClassType.resolveBinding();
				if (superClassBinding != null) {
					String superClassName = superClassBinding.getQualifiedName();
					if (superClassName.startsWith("src.")) {
						superClassName = superClassName.substring(4);
					}
					((JavaClassDefinition) this.classStack.peek().getElement()).setSuperClassName(superClassName);
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
			
			AnonymousClassDeclaration acd = (AnonymousClassDeclaration) node;
			@SuppressWarnings ("unchecked")
			List<BodyDeclaration> bodyDeclarations = acd.bodyDeclarations();
			Condition.check(bodyDeclarations.size() > 0, "Found type declaration with " + bodyDeclarations.size()
			        + " body declarations!");
			
			int bodyStartLine = currentLine;
			if (bodyDeclarations.size() > 0) {
				bodyStartLine = this.cu.getLineNumber(bodyDeclarations.get(0).getStartPosition());
			}
			int anonCount = ((JavaClassDefinition) this.classStack.peek().getElement()).nextAnonCounter(this);
			if (!this.classStack.peek().getElement().getShortName()
			                    .equals(this.classStack.peek().getElement().getShortName() + "$" + anonCount)) {
				
				String parentName = this.classStack.peek().getElement().getFullQualifiedName();
				if (parentName.contains("$")) {
					int index = parentName.lastIndexOf("$");
					parentName = parentName.substring(0, index);
				}
				
				JavaElementLocation classDefLoc = this.elementCache.addAnonymousClassDefinition(((JavaClassDefinition) this.classStack.peek()
				                                                                                                                      .getElement()),
				                                                                                parentName + "$"
				                                                                                        + anonCount,
				                                                                                this.relativeFilePath,
				                                                                                currentLine,
				                                                                                endLine,
				                                                                                node.getStartPosition(),
				                                                                                bodyStartLine);
				this.classStack.push(classDefLoc);
			}
		} else if (node instanceof MethodDeclaration) {
			MethodDeclaration md = (MethodDeclaration) node;
			
			int startLine = currentLine;
			
			boolean startLineShifted = false;
			@SuppressWarnings ("unchecked")
			List<ASTNode> modifiers = (List<ASTNode>) md.getStructuralProperty(md.getModifiersProperty());
			for (ASTNode modifier : modifiers) {
				if (modifier instanceof Modifier) {
					startLine = this.cu.getLineNumber(modifier.getStartPosition());
					startLineShifted = true;
					break;
				}
			}
			if (!startLineShifted) {
				if (md.getReturnType2() != null) {
					startLine = this.cu.getLineNumber(md.getReturnType2().getStartPosition());
				}
			}
			
			Block body = md.getBody();
			int bodyStartLine = -1;
			if (body != null) {
				bodyStartLine = this.cu.getLineNumber(body.getStartPosition());
			}
			
			if (this.classStack.isEmpty()) {
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
				
				JavaClassDefinition parent = ((JavaClassDefinition) this.classStack.peek().getElement());
				JavaElementLocation methodDefLoc = this.elementCache.addMethodDefinition(parent.getFullQualifiedName(),
				                                                                         md.getName().toString(),
				                                                                         arguments,
				                                                                         this.getRelativeFilePath(),
				                                                                         startLine, endLine,
				                                                                         node.getStartPosition(),
				                                                                         bodyStartLine);
				
				if (!this.methodStack.isEmpty()) {
					if (Logger.logError()) {
						Logger.warn("Adding method definition to method stack while stack is not empty. This is not impossible but happens rarely!");
					}
				}
				this.methodStack.push(methodDefLoc);
			}
		}
		
		for (PPAVisitor visitor : this.visitors) {
			if (this.classStack.isEmpty()) {
				visitor.preVisit(this, this.cu, node, null, null, currentLine, endLine, this.elementCache);
			} else {
				if (this.methodStack.isEmpty()) {
					visitor.preVisit(this, this.cu, node, this.classStack.peek(), null, currentLine, endLine,
					                 this.elementCache);
				} else {
					visitor.preVisit(this, this.cu, node, this.classStack.peek(), this.methodStack.peek(), currentLine,
					                 endLine, this.elementCache);
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
		return this.visitors.add(visitor);
	}
}
