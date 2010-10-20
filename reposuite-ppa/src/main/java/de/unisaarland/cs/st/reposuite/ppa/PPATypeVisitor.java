package de.unisaarland.cs.st.reposuite.ppa;

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

import ca.mcgill.cs.swevo.ppa.PPAOptions;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The Class PPATypeVisitor.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPATypeVisitor extends ASTVisitor {
	
	private static String[]        primTypes      = { "Class", "Character", "Byte", "Short", "Integer", "Long",
	        "Float", "Double", "Boolean", "Void", "String", "TestCase", "ClassMapper", "Thread", "ClassLoader",
	        "Color", "AbstractCollectionConverter", "ObjectTree" };
	
	private HashSet<ClassContext>  classContexts  = new HashSet<ClassContext>();
	private HashSet<MethodContext> methodContexts = new HashSet<MethodContext>();
	private Stack<ClassContext>    contextStack;
	protected CompilationUnit      cu;
	private File                   file;
	protected String[]             packageFilter;
	private String                 packageName;
	protected HashSet<String>      primitives;
	private Set<PPAVisitor>        visitors       = new HashSet<PPAVisitor>();
	
	/**
	 * Instantiates a new pPA type visitor.
	 * 
	 * @deprecated replaced by {@link #PPATypeVisitor(File,String[])}
	 * 
	 * @param checkoutDir
	 *            the checkout dir
	 * @param cu
	 *            the cu
	 * @param packageFilter
	 *            the package filter
	 */
	@Deprecated
	public PPATypeVisitor(File checkoutDir, CompilationUnit cu, String[] packageFilter) {
		this.packageFilter = packageFilter;
		file = null;
		this.cu = cu;
		init();
	}
	
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
	public PPATypeVisitor(File file, String[] packageFilter) throws CompilationUnitException {
		this.packageFilter = packageFilter;
		this.file = file;
		cu = MyPPAUtil.getCU(file, new PPAOptions());
		if (cu == null) {
			throw new CompilationUnitException("PPAUtil could not" + "fetch compilation unit from file `"
			        + file.getAbsolutePath() + "`. Skipping unit.");
		}
		init();
	}
	
	/**
	 * Checks if a gived object name passes the package filters set in
	 * constructor.
	 * 
	 * @param calledObject
	 *            the called object
	 * @return true, if successful
	 */
	public boolean checkFilters(String calledObject) {
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
		
		// TODO check if type in workspace
		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .CompilationUnit)
	 */
	@Override
	public void endVisit(CompilationUnit node) {
		super.endVisit(node);
		for (PPAVisitor visitor : visitors) {
			visitor.endVisit(cu, node, contextStack.peek());
		}
	}
	
	/**
	 * Gets all class contexts parsed so far.
	 * 
	 * @return the class contexts
	 */
	public Collection<ClassContext> getClassContexts() {
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
	
	/**
	 * Gets the method contexts parsed so far.
	 * 
	 * @return the method contexts
	 */
	public Collection<MethodContext> getMethodContexts() {
		return methodContexts;
	}
	
	/**
	 * Used by all constructors to setup appropriate fields.
	 */
	private void init() {
		contextStack = new Stack<ClassContext>();
		
		PackageDeclaration packageDecl = cu.getPackage();
		if (packageDecl != null) {
			packageName = packageDecl.getName().toString();
		} else {
			packageName = "UNKNOWN";
		}
		
		// int endLine = cu.getLineNumber(cu.getStartPosition() + cu.getLength()
		// - 1);
		// classStack.push(new ClassContext(getClassName(cu), null, 0,
		// endLine));
		
		primitives = new HashSet<String>();
		primitives.addAll(Arrays.asList(primTypes));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#postVisit(org.eclipse.jdt.core.dom
	 * .ASTNode)
	 */
	@Override
	public void postVisit(ASTNode node) {
		super.postVisit(node);
		for (PPAVisitor visitor : visitors) {
			if (contextStack.isEmpty()) {
				visitor.postVisit(cu, node, null);
			} else {
				visitor.postVisit(cu, node, contextStack.peek());
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
	public void preVisit(ASTNode node) {
		
		int currentLine = cu.getLineNumber(node.getStartPosition());
		if (!contextStack.isEmpty()) {
			contextStack.peek().removeMethod(currentLine);
		}
		while ((!contextStack.isEmpty())
		        && (cu.getLineNumber(node.getStartPosition()) > contextStack.peek().getEndLine())) {
			contextStack.pop();
		}
		
		if (node instanceof TypeDeclaration) {
			TypeDeclaration td = (TypeDeclaration) node;
			int endLine = cu.getLineNumber(td.getStartPosition() + td.getLength() - 1);
			if (contextStack.size() < 1) {
				ClassContext context = null;
				if (file != null) {
					context = new ClassContext(packageName + "." + td.getName().toString(), null, 0, endLine,
					        file.getAbsolutePath());
					if (RepoSuiteSettings.debug) {
						Logger.debug("PPATypevisitor: Adding new class context with package name +`" + packageName
						        + "` and class name `" + td.getName().toString() + "`");
					}
				} else {
					context = new ClassContext(packageName + "." + td.getName().toString(), null, 0, endLine, "");
				}
				contextStack.push(context);
				classContexts.add(context);
			} else if (!contextStack.peek().getName().equals(td.getName().toString())) {
				int line = cu.getLineNumber(td.getStartPosition());
				ClassContext context = null;
				if (file != null) {
					context = new ClassContext(contextStack.peek().getFullQualifiedName() + "."
					        + td.getName().toString(), contextStack.peek(), line, endLine, file.getAbsolutePath());
				} else {
					context = new ClassContext(contextStack.peek().getFullQualifiedName() + "."
					        + td.getName().toString(), contextStack.peek(), line, endLine, "");
				}
				contextStack.push(context);
				classContexts.add(context);
			}
			
			Type superClassType = td.getSuperclassType();
			if (superClassType != null) {
				ITypeBinding superClassBinding = superClassType.resolveBinding();
				String superClassName = superClassBinding.getQualifiedName();
				if (superClassName.startsWith("src.")) {
					superClassName = superClassName.substring(4);
				}
				contextStack.peek().setSuperclass(superClassName);
			}
		} else if (node instanceof AnonymousClassDeclaration) {
			AnonymousClassDeclaration acd = (AnonymousClassDeclaration) node;
			int line = cu.getLineNumber(acd.getStartPosition());
			if (contextStack.size() < 1) {
				Logger.error("Found declaration of anonymous class outside a proper class in line" + line
				        + ". Ignoring node!");
				return;
			}
			
			int anonCount = contextStack.peek().nextAnonCounter();
			if (!contextStack.peek().getName().equals(contextStack.peek().getName() + "$" + anonCount)) {
				int endLine = cu.getLineNumber(acd.getStartPosition() + acd.getLength());
				
				String parentName = contextStack.peek().getFullQualifiedName();
				if (parentName.contains("$")) {
					int index = parentName.lastIndexOf("$");
					parentName = parentName.substring(0, index);
				}
				if (file != null) {
					contextStack.push(new ClassContext(parentName + "$" + anonCount, contextStack.peek(), line,
					        endLine, file.getAbsolutePath()));
				} else {
					contextStack.push(new ClassContext(parentName + "$" + anonCount, contextStack.peek(), line,
					        endLine, ""));
				}
			}
		} else if (node instanceof MethodDeclaration) {
			MethodDeclaration md = (MethodDeclaration) node;
			int line = cu.getLineNumber(md.getStartPosition());
			if (contextStack.size() < 1) {
				Logger.error("Found declaration of method outside a proper class in line" + line + ". Ignoring node!");
				return;
			} else {
				int endLine = cu.getLineNumber(md.getStartPosition() + md.getLength());
				List<String> arguments = new ArrayList<String>();
				@SuppressWarnings("rawtypes") List args = md.parameters();
				@SuppressWarnings("rawtypes") Iterator iter = args.iterator();
				while (iter.hasNext()) {
					SingleVariableDeclaration dec = (SingleVariableDeclaration) iter.next();
					arguments.add(dec.getType().toString());
				}
				MethodContext mContext = contextStack.peek().addMethod(md.getName().toString(), arguments, line,
				        endLine);
				methodContexts.add(mContext);
				
			}
		}
		
		for (PPAVisitor visitor : visitors) {
			if (contextStack.isEmpty()) {
				visitor.preVisit(cu, node, null);
			} else {
				visitor.preVisit(cu, node, contextStack.peek());
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
	public boolean registerVisitor(PPAVisitor visitor) {
		return visitors.add(visitor);
	}
}
