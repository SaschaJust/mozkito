package de.unisaarland.cs.st.reposuite.ppa.visitors;

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

import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;

/**
 * The Class PPATypeVisitor.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPATypeVisitor extends ASTVisitor {
	
	public static String                                      UNKNOWN_PACKAGE  = "<UNKNOWN>";
	private static String[]                                   primTypes        = { "Class", "Character", "Byte",
		"Short", "Integer", "Long", "Float", "Double", "Boolean", "Void", "String", "TestCase", "ClassMapper",
		"Thread", "ClassLoader", "Color", "AbstractCollectionConverter", "ObjectTree" };
	private final Stack<Tuple<JavaClassDefinition, Integer>>  classStack       = new Stack<Tuple<JavaClassDefinition, Integer>>();
	private final Stack<Tuple<JavaMethodDefinition, Integer>> methodStack      = new Stack<Tuple<JavaMethodDefinition, Integer>>();
	protected CompilationUnit                                 cu;
	private final File                                        file;
	protected String[]                                        packageFilter;
	private String                                            packageName;
	protected HashSet<String>                                 primitives;
	private final Set<PPAVisitor>                             visitors         = new HashSet<PPAVisitor>();
	private String                                            relativeFilePath = "";
	private final JavaElementCache                            elementCache;
	
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
	        final String[] packageFilter, final JavaElementCache elementCache) {
		this.packageFilter = packageFilter;
		this.file = file;
		this.cu = cu;
		this.elementCache = elementCache;
		
		if (file.getAbsolutePath().startsWith(filePathPrefix)) {
			this.relativeFilePath = file.getAbsolutePath().replaceFirst(filePathPrefix, "");
			if (this.relativeFilePath.startsWith(FileUtils.fileSeparator)) {
				this.relativeFilePath = this.relativeFilePath.substring(1);
			}
		}
		
		PackageDeclaration packageDecl = this.cu.getPackage();
		if (packageDecl != null) {
			this.packageName = packageDecl.getName().toString();
		} else {
			this.packageName = UNKNOWN_PACKAGE;
		}
		
		this.primitives = new HashSet<String>();
		this.primitives.addAll(Arrays.asList(primTypes));
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
	
	@Override
	public void endVisit(final CompilationUnit node) {
		super.endVisit(node);
		for (PPAVisitor visitor : this.visitors) {
			if (this.methodStack.isEmpty()) {
				visitor.endVisit(this, this.cu, node, this.classStack.peek(), null, this.elementCache);
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
	
	public File getFile() {
		return this.file;
	}
	
	public String getRelativeFilePath() {
		return this.relativeFilePath;
	}
	
	/**
	 * 
	 * @param node
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
		
		int currentLine = this.cu.getLineNumber(node.getStartPosition());
		int endLine = this.cu.getLineNumber(node.getStartPosition() + node.getLength() - 1);
		
		if (!this.methodStack.isEmpty()) {
			if (this.methodStack.peek().getSecond() < currentLine) {
				this.methodStack.pop();
			}
		}
		
		while ((!this.classStack.isEmpty())
				&& (this.cu.getLineNumber(node.getStartPosition()) > this.classStack.peek().getSecond())) {
			this.classStack.pop();
		}
		
		if (node instanceof TypeDeclaration) {
			
			TypeDeclaration td = (TypeDeclaration) node;
			
			JavaClassDefinition parent = null;
			
			if (!this.classStack.isEmpty()) {
				//FOUND INNER CLASS: add parent to class definition
				parent = this.classStack.peek().getFirst();
			}
			
			JavaClassDefinition classDef = this.elementCache.getClassDefinition(this.packageName + "."
					+ td.getName().toString(), this.relativeFilePath, parent, currentLine, endLine,
					td.getStartPosition(), this.packageName);
			if (Logger.logDebug()) {
				Logger.debug("PPATypevisitor: Adding new class context with package name +`" + this.packageName
						+ "` and class name `" + td.getName().toString() + "`");
			}
			this.classStack.push(new Tuple<JavaClassDefinition, Integer>(classDef, endLine));
			
			Type superClassType = td.getSuperclassType();
			if (superClassType != null) {
				ITypeBinding superClassBinding = superClassType.resolveBinding();
				if (superClassBinding != null) {
					String superClassName = superClassBinding.getQualifiedName();
					if (superClassName.startsWith("src.")) {
						superClassName = superClassName.substring(4);
					}
					this.classStack.peek().getFirst().setSuperClassName(superClassName);
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
			
			int anonCount = this.classStack.peek().getFirst().nextAnonCounter();
			if (!this.classStack.peek().getFirst().getShortName()
					.equals(this.classStack.peek().getFirst().getShortName() + "$" + anonCount)) {
				
				String parentName = this.classStack.peek().getFirst().getFullQualifiedName();
				if (parentName.contains("$")) {
					int index = parentName.lastIndexOf("$");
					parentName = parentName.substring(0, index);
				}
				
				JavaClassDefinition classDef = this.elementCache.getClassDefinition(parentName + "$" + anonCount,
						this.relativeFilePath, this.classStack.peek().getFirst(), currentLine, node.getStartPosition(),
						endLine, this.packageName);
				this.classStack.push(new Tuple<JavaClassDefinition, Integer>(classDef, endLine));
			}
		} else if (node instanceof MethodDeclaration) {
			MethodDeclaration md = (MethodDeclaration) node;
			
			//only consider method signatures!
			int bodyStartLine = this.cu.getLineNumber(md.getBody().getStartPosition());
			if (endLine > bodyStartLine) {
				endLine = bodyStartLine;
			}
			
			if (this.classStack.isEmpty()) {
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
				
				JavaMethodDefinition methodDef = this.elementCache.getMethodDefinition(md.getName().toString(),
						arguments,
						this.getRelativeFilePath(), this.classStack.peek().getFirst(), currentLine, endLine,
						node.getStartPosition());
				
				this.classStack.peek().getFirst().addMethod(methodDef);
				if (!this.methodStack.isEmpty()) {
					if (Logger.logError()) {
						Logger.warn("Adding method definition to method stack while stack is not empty. This is not impossible but happens rarely!");
					}
				}
				this.methodStack.push(new Tuple<JavaMethodDefinition, Integer>(methodDef, endLine));
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
