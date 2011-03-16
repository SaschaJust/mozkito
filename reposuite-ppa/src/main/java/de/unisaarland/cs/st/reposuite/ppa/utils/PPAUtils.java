package de.unisaarland.cs.st.reposuite.ppa.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PPAASTParser;
import org.eclipse.jdt.core.dom.PPAEngine;
import org.eclipse.jdt.core.dom.PPATypeRegistry;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.internal.core.JavaProject;

import ca.mcgill.cs.swevo.ppa.PPAOptions;
import de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation.LineCover;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.ppa.visitors.PPAMethodCallVisitor;
import de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.DiffUtils;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;
import difflib.DeleteDelta;
import difflib.Delta;
import difflib.InsertDelta;

/**
 * The Class PPAUtils.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAUtils {
	
	private static class CopyThread extends Thread {
		
		private final IProject   project;
		private final File       file;
		private final String     packagename;
		private final String     filename;
		private final PPAOptions options;
		private CompilationUnit  cu;
		private IFile            iFile;
		
		public CopyThread(final IProject project, final File file, final String packagename, final String filename,
		        final PPAOptions options) {
			this.project = project;
			this.file = file;
			this.packagename = packagename;
			this.filename = filename;
			this.options = options;
		}
		
		public CompilationUnit getCompilationUnit() {
			return this.cu;
		}
		
		public IFile getIFile() {
			return this.iFile;
		}
		
		@Override
		public void run() {
			try {
				this.iFile = PPAResourceUtil.copyJavaSourceFile(this.project, this.file, this.packagename,
				                                                this.filename);
				if (this.iFile == null) {
					if (Logger.logError()) {
						Logger.error("Error while getting CU from PPA. Timeout copy to workspace exceeded.");
					}
					return;
				}
				this.cu = getCU(this.iFile, this.options);
			} catch (CoreException e) {
				if (Logger.logError()) {
					Logger.error("Could not import file into eclipse workspace", e);
				}
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error("Could not import file into eclipse workspace", e);
				}
			}
		}
		
	}
	
	/**
	 * Generate change operations. The generated change operations will be
	 * passed to the given visitors.
	 * 
	 * @param repository
	 *            the repository
	 * @param transaction
	 *            the transaction
	 * @param visitors
	 *            the visitors
	 */
	public static void generateChangeOperations(final Repository repository,
	                                            final RCSTransaction transaction,
	                                            final Collection<ChangeOperationVisitor> visitors) {
		
		Map<RCSRevision, String> changedFilePaths = new HashMap<RCSRevision, String>();
		
		Map<RCSRevision, CompilationUnit> oldRevs2CUs = new HashMap<RCSRevision, CompilationUnit>();
		Map<RCSRevision, CompilationUnit> newRevs2CUs = new HashMap<RCSRevision, CompilationUnit>();
		
		for (RCSRevision revision : transaction.getRevisions()) {
			String changedPath = revision.getChangedFile().getPath(transaction);
			if (!changedPath.endsWith(".java")) {
				if (Logger.logWarn()) {
					Logger.warn("Ignoring non-Java file: " + changedPath);
				}
				continue;
			}
			if (changedPath.startsWith("/")) {
				changedPath = changedPath.substring(1);
			}
			changedFilePaths.put(revision, changedPath);
		}
		
		RCSTransaction parentTransaction = transaction.getParent(transaction.getBranch());
		if (parentTransaction != null) {
			// get the old compilationUnits
			oldRevs2CUs = getCUsForTransaction(repository, parentTransaction, changedFilePaths);
		}
		newRevs2CUs = getCUsForTransaction(repository, transaction, changedFilePaths);
		
		for (RCSRevision revision : changedFilePaths.keySet()) {
			String changedPath = changedFilePaths.get(revision);

			switch (revision.getChangeType()) {
				case Added:
					JavaElementLocations newElems = PPAUtils.getJavaElementLocationsByCU(newRevs2CUs.get(revision),
					                                                                     changedPath, new String[0]);
					
					for (JavaElementLocation<JavaClassDefinition> classDef : newElems.getClassDefs(changedPath)) {
						JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, classDef, revision);
						for (ChangeOperationVisitor visitor : visitors) {
							visitor.visit(op);
						}
					}
					for (JavaElementLocation<JavaMethodDefinition> methDef : newElems.getMethodDefs(changedPath)) {
						JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, methDef, revision);
						for (ChangeOperationVisitor visitor : visitors) {
							visitor.visit(op);
						}
					}
					for (JavaElementLocation<JavaMethodCall> methCall : newElems.getMethodCalls(changedPath)) {
						JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, methCall, revision);
						for (ChangeOperationVisitor visitor : visitors) {
							visitor.visit(op);
						}
					}
					break;
				case Modified:
					generateChangeOperationsForModifiedFile(repository, transaction, revision,
					                                        oldRevs2CUs.get(revision), newRevs2CUs.get(revision),
					                                        changedPath, visitors);
					break;
				case Deleted:
					JavaElementLocations oldElems = PPAUtils.getJavaElementLocationsByCU(oldRevs2CUs.get(revision),
					                                                                     changedPath, new String[0]);
					
					for (JavaElementLocation<JavaClassDefinition> classDef : oldElems.getClassDefs(changedPath)) {
						JavaChangeOperation op = new JavaChangeOperation(ChangeType.Deleted, classDef, revision);
						for (ChangeOperationVisitor visitor : visitors) {
							visitor.visit(op);
						}
					}
					for (JavaElementLocation<JavaMethodDefinition> methDef : oldElems.getMethodDefs(changedPath)) {
						JavaChangeOperation op = new JavaChangeOperation(ChangeType.Deleted, methDef, revision);
						for (ChangeOperationVisitor visitor : visitors) {
							visitor.visit(op);
						}
					}
					for (JavaElementLocation<JavaMethodCall> methCall : oldElems.getMethodCalls(changedPath)) {
						JavaChangeOperation op = new JavaChangeOperation(ChangeType.Deleted, methCall, revision);
						for (ChangeOperationVisitor visitor : visitors) {
							visitor.visit(op);
						}
					}
					break;
			}
			
		}

	}
	
	protected static Map<RCSRevision, CompilationUnit> getCUsForTransaction(final Repository repository,
	                                                                        final RCSTransaction transaction,
	                                                                        Map<RCSRevision, String> changedFilePaths) {
		Map<RCSRevision, CompilationUnit> result = new HashMap<RCSRevision, CompilationUnit>();
		Map<File, RCSRevision> files2Revisions = new HashMap<File, RCSRevision>();
		
		File oldCheckoutFile = repository.checkoutPath("/", transaction.getId());
		if (!oldCheckoutFile.exists()) {
			if (Logger.logError()) {
				Logger.error("Could not access checkout directory: " + oldCheckoutFile.getAbsolutePath()
				        + ". Ignoring!");
			}
			return result;
		}
		
		List<File> filesToAnalyze = new LinkedList<File>();
		for (RCSRevision rev : changedFilePaths.keySet()) {
			String changedFileName = changedFilePaths.get(rev);
			File tmpFile = new File(oldCheckoutFile.getAbsolutePath() + FileUtils.fileSeparator + changedFileName);
			if (!tmpFile.exists()) {
				if (Logger.logDebug()) {
					Logger.debug("Could not find checked out file " + tmpFile.getAbsolutePath()
					        + " (might be added in next revision?)");
				}
				continue;
			}
			files2Revisions.put(tmpFile, rev);
			filesToAnalyze.add(tmpFile);
		}
		
		Map<File, CompilationUnit> cus = PPAUtils.getCUs(filesToAnalyze, new PPAOptions());
		for (File file : cus.keySet()) {
			result.put(files2Revisions.get(file), cus.get(file));
		}
		return result;
	}
	
	/**
	 * Gets the cU.
	 * 
	 * @param file
	 *            the file
	 * @param options
	 *            the options
	 * @return the cU
	 */
	public static CompilationUnit getCU(final File file,
	                                    final PPAOptions options) {
		return getCU(file, options, Thread.currentThread().getName());
	}
	
	/**
	 * Gets the cU.
	 * 
	 * @param file
	 *            the file
	 * @param options
	 *            the options
	 * @param requestName
	 *            the request name
	 * @return the cU
	 */
	public static CompilationUnit getCU(final File file,
	                                    final PPAOptions options,
	                                    final String requestName) {
		CompilationUnit cu = null;
		String fileName = file.getName();
		
		try {
			String packageName = getPackageFromFile(file);
			IJavaProject javaProject = getProject(requestName);
			
			CopyThread copyThread = new CopyThread(javaProject.getProject(), file, packageName, fileName, options);
			
			// IFile newFile =
			// PPAResourceUtil.copyJavaSourceFile(javaProject.getProject(),
			// file, packageName, fileName);
			
			copyThread.start();
			
			copyThread.join(120000);
			cu = copyThread.getCompilationUnit();
			if (cu == null) {
				IFile ifile = copyThread.getIFile();
				if (ifile == null) {
					if (Logger.logError()) {
						Logger.error("Error while getting IFile from PPA. Timeout copy to workspace exceeded.");
					}
				} else {
					if (Logger.logError()) {
						Logger.error("Error while getting CU from PPA. Timeout copy to workspace exceeded. Trying without PPA");
					}
					return getCUNoPPA(ifile);
				}
				return null;
			}
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error("Error while getting CU from PPA", e);
			}
		}
		
		return cu;
	}
	
	/**
	 * Gets the cU.
	 * 
	 * @param file
	 *            the file
	 * @param options
	 *            the options
	 * @return the cU
	 */
	public static CompilationUnit getCU(final IFile file,
	                                    final PPAOptions options) {
		CompilationUnit cu = null;
		try {
			ICompilationUnit icu = JavaCore.createCompilationUnitFrom(file);
			PPATypeRegistry registry = new PPATypeRegistry((JavaProject) JavaCore.create(icu.getUnderlyingResource()
			                                                                                .getProject()));
			ASTNode node = null;
			PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
			parser2.setStatementsRecovery(true);
			parser2.setResolveBindings(true);
			parser2.setSource(icu);
			node = parser2.createAST(null);
			PPAEngine ppaEngine = new PPAEngine(registry, options);
			
			cu = (CompilationUnit) node;
			
			ppaEngine.addUnitToProcess(cu);
			ppaEngine.doPPA();
			ppaEngine.reset();
		} catch (JavaModelException jme) {
			// Retry with the file version.
			if (Logger.logWarn()) {
				Logger.warn("Warning while getting CU from PPA");
			}
			if (Logger.logDebug()) {
				Logger.debug("Exception", jme);
			}
			cu = getCU(file.getLocation().toFile(), options);
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error("Error while getting CU from PPA", e);
			}
		}
		
		return cu;
	}
	
	/**
	 * Gets the cU no ppa.
	 * 
	 * @param file
	 *            the file
	 * @return the cU no ppa
	 */
	public static CompilationUnit getCUNoPPA(final IFile file) {
		CompilationUnit cu = null;
		try {
			ICompilationUnit icu = JavaCore.createCompilationUnitFrom(file);
			ASTNode node = null;
			PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
			parser2.setStatementsRecovery(true);
			parser2.setResolveBindings(true);
			parser2.setSource(icu);
			node = parser2.createAST(null);
			cu = (CompilationUnit) node;
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error("Error while getting CU without PPA", e);
			}
		}
		
		return cu;
	}
	
	/**
	 * Gets the c us.
	 * 
	 * @param files
	 *            the files
	 * @param options
	 *            the options
	 * @return the c us
	 */
	public static Map<File, CompilationUnit> getCUs(final List<File> files,
	                                                final PPAOptions options) {
		return getCUs(files, options, Thread.currentThread().getName());
	}
	
	/**
	 * Gets the c us.
	 * 
	 * @param files
	 *            the files
	 * @param options
	 *            the options
	 * @param requestName
	 *            the request name
	 * @return the c us
	 */
	public static Map<File, CompilationUnit> getCUs(final List<File> files,
	                                                final PPAOptions options,
	                                                final String requestName) {
		Map<File, CompilationUnit> cus = new HashMap<File, CompilationUnit>();
		Map<IFile, File> iFiles = new HashMap<IFile, File>();
		try {
			cleanupWorkspace();
		} catch (CoreException e1) {
			if (Logger.logWarn()) {
				Logger.warn(e1.getMessage(), e1);
			}
		}
		for (File file : files) {
			String fileName = file.getName();
			try {
				String packageName = getPackageFromFile(file);
				IJavaProject javaProject = getProject(requestName);
				IFile newFile = PPAResourceUtil.copyJavaSourceFile(javaProject.getProject(), file, packageName,
				                                                   fileName);
				iFiles.put(newFile, file);
			} catch (Exception e) {
				if (Logger.logError()) {
					Logger.error("Error while getting IFile from PPA", e);
				}
			}
		}
		
		for (IFile iFile : iFiles.keySet()) {
			if (Logger.logDebug()) {
				Logger.debug("Getting CU for file: " + iFile.getLocation().toOSString());
			}
			CompilationUnit cu = getCU(iFile, options);
			if (cu == null) {
				cu = getCUNoPPA(iFile);
			}
			cus.put(iFiles.get(iFile), cu);
		}

		return cus;
	}
	
	/**
	 * Gets the java element locations by file.
	 * 
	 * @param file
	 *            the file
	 * @param filePrefixPath
	 *            the file prefix path
	 * @param packageFilter
	 *            the package filter
	 * @return the java element locations by file
	 */
	public static JavaElementLocations getJavaElementLocationsByFile(final File file,
	                                                                 final String filePrefixPath,
	                                                                 final String[] packageFilter) {
		PPAMethodCallVisitor methodCallVisitor = new PPAMethodCallVisitor();
		PPAOptions ppaOptions = new PPAOptions();
		
		JavaElementCache elemCache = new JavaElementCache();
		
		if (!file.getAbsolutePath().startsWith(filePrefixPath)) {
			if (Logger.logError()) {
				Logger.error("File name does not start with specified filePrefixPath");
			}
		} else {
			
			CompilationUnit cu = getCU(file, ppaOptions);
			if (cu != null) {
				
				String relativePath = file.getAbsolutePath().substring(filePrefixPath.length());
				if (relativePath.startsWith(FileUtils.fileSeparator)) {
					relativePath = relativePath.substring(1);
				}
				
				PPATypeVisitor typeVisitor = new PPATypeVisitor(cu, relativePath, packageFilter, elemCache);
				typeVisitor.registerVisitor(methodCallVisitor);
				cu.accept(typeVisitor);
			} else {
				if (Logger.logError()) {
					Logger.error("Could not analyze file " + file.getAbsolutePath()
					        + ". CompilationUnit cannot be created. Skipping ... ");
				}
			}
		}
		return elemCache.getJavaElementLocations();
	}
	
	/**
	 * Gets the java element locations by file.
	 * 
	 * @param file
	 *            the file
	 * @param filePrefixPath
	 *            the file prefix path
	 * @param packageFilter
	 *            the package filter
	 * @return the java element locations by file
	 */
	public static JavaElementLocations getJavaElementLocationsByCU(final CompilationUnit cu,
	                                                               String relativePath,
	                                                               final String[] packageFilter) {
		PPAMethodCallVisitor methodCallVisitor = new PPAMethodCallVisitor();
		
		JavaElementCache elemCache = new JavaElementCache();
		
		if (cu != null) {
			PPATypeVisitor typeVisitor = new PPATypeVisitor(cu, relativePath, packageFilter, elemCache);
			typeVisitor.registerVisitor(methodCallVisitor);
			cu.accept(typeVisitor);
		} else {
			if (Logger.logError()) {
				Logger.error("Could not analyze file " + relativePath
				        + ". CompilationUnit cannot be created. Skipping ... ");
			}
		}
		return elemCache.getJavaElementLocations();
	}
	
	/**
	 * Gets the java method elements of all java files within
	 * <code>sourceDir</code>.
	 * 
	 * @param sourceDir
	 *            the source dir
	 * @param packageFilter
	 *            the package filter
	 * @return the java method elements
	 */
	public static JavaElementLocations getJavaElementLocationsByFile(final File sourceDir,
	                                                                 final String[] packageFilter) {
		
		try {
			Iterator<File> fileIterator = FileUtils.getFileIterator(sourceDir, new String[] { "java" }, true);
			return getJavaElementLocationsByFile(fileIterator, sourceDir.getAbsolutePath(), packageFilter);
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return new JavaElementLocations();
	}
	
	/**
	 * Gets the java method elements of all files covered by the iterator.
	 * 
	 * @param fileIterator
	 *            the file iterator
	 * @param filePrefixPath
	 *            the file prefix path
	 * @param packageFilter
	 *            the package filter
	 * @return the java method elements
	 */
	public static JavaElementLocations getJavaElementLocationsByFile(final Iterator<File> fileIterator,
	                                                                 final String filePrefixPath,
	                                                                 final String[] packageFilter) {
		PPAMethodCallVisitor methodCallVisitor = new PPAMethodCallVisitor();
		PPAOptions ppaOptions = new PPAOptions();
		
		JavaElementCache elemCache = new JavaElementCache();
		
		while (fileIterator.hasNext()) {
			File file = fileIterator.next();
			CompilationUnit cu = getCU(file, ppaOptions);
			if (!file.getAbsolutePath().startsWith(filePrefixPath)) {
				if (Logger.logError()) {
					Logger.error("File name does not start with specified filePrefixPath");
				}
				continue;
			}
			if (cu == null) {
				if (Logger.logError()) {
					Logger.error("Could not analyze file " + file.getAbsolutePath()
					        + ". CompilationUnit cannot be created. Skipping ... ");
				}
				continue;
			}

			String relativePath = file.getAbsolutePath().substring(filePrefixPath.length());
			if (relativePath.startsWith(FileUtils.fileSeparator)) {
				relativePath = relativePath.substring(1);
			}
			
			PPATypeVisitor typeVisitor = new PPATypeVisitor(cu, relativePath, packageFilter, elemCache);
			typeVisitor.registerVisitor(methodCallVisitor);
			cu.accept(typeVisitor);
		}
		
		return elemCache.getJavaElementLocations();
	}
	
	/**
	 * Gets the package from file.
	 * 
	 * @param file
	 *            the file
	 * @return the package from file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String getPackageFromFile(final File file) throws IOException {
		String packageName = "";
		PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
		parser2.setStatementsRecovery(true);
		parser2.setResolveBindings(true);
		parser2.setSource(PPAResourceUtil.getContent(file).toCharArray());
		CompilationUnit cu = (CompilationUnit) parser2.createAST(null);
		PackageDeclaration pDec = cu.getPackage();
		if (pDec != null) {
			packageName = pDec.getName().getFullyQualifiedName();
		}
		return packageName;
	}
	
	public static void cleanupWorkspace() throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.getRoot().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		workspace.getRoot().clearHistory(new NullProgressMonitor());
	}

	/**
	 * Gets the project.
	 * 
	 * @param name
	 *            the name
	 * @return the project
	 */
	private static IJavaProject getProject(final String name) {
		IJavaProject project = null;
		try {
			IProject javaProject = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
			if (!javaProject.exists()) {
				javaProject.create(new NullProgressMonitor());
				javaProject.open(IResource.BACKGROUND_REFRESH, new NullProgressMonitor());
				IProjectDescription description = javaProject.getDescription();
				String[] natures = description.getNatureIds();
				String[] newNatures = new String[natures.length + 1];
				System.arraycopy(natures, 0, newNatures, 0, natures.length);
				newNatures[natures.length] = JavaCore.NATURE_ID;
				description.setNatureIds(newNatures);
				javaProject.setDescription(description, new NullProgressMonitor());
				
				project = JavaCore.create(javaProject);
			} else {
				project = (IJavaProject) javaProject.getNature(JavaCore.NATURE_ID);
			}
			if (!javaProject.isOpen()) {
				javaProject.open(IResource.BACKGROUND_REFRESH, new NullProgressMonitor());
			}
			IFolder folder = javaProject.getFolder("src");
			if (!folder.exists()) {
				folder.create(true, true, new NullProgressMonitor());
			}
		} catch (CoreException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return project;
	}
	
	/**
	 * Generate change operations for modified file. The generated change
	 * operations will be passed to the given visitors.
	 * 
	 * @param repository
	 *            the repository
	 * @param revision
	 *            the revision
	 * @param visitors
	 *            the visitors
	 */
	@SuppressWarnings ({ "rawtypes", "unchecked" })
	@NoneNull
	protected static void generateChangeOperationsForModifiedFile(final Repository repository,
	                                                              final RCSTransaction transaction,
	                                                              final RCSRevision revision,
	                                                              final CompilationUnit oldCU,
	                                                              final CompilationUnit newCU,
	                                                              final String changedPath,
	                                                              final Collection<ChangeOperationVisitor> visitors) {
		
		JavaElementLocations oldElems = PPAUtils.getJavaElementLocationsByCU(oldCU, changedPath, new String[0]);
		JavaElementLocations newElems = PPAUtils.getJavaElementLocationsByCU(newCU, changedPath, new String[0]);
		
		// generate diff
		Collection<Delta> diff = repository.diff(changedPath, transaction.getParent(transaction.getBranch()).getId(),
		                                         transaction.getId());
		List<JavaChangeOperation> operations = new LinkedList<JavaChangeOperation>();
		for (Delta delta : diff) {
			
			if (delta instanceof DeleteDelta) {
				// corresponding elements to be removed
				HashSet<Integer> lines = DiffUtils.getLineNumbers(delta.getOriginal());
				for (JavaElementLocation javaElem : oldElems.getElements(changedPath)) {
					LineCover cover = javaElem.coversAnyLine(lines);
					switch (cover) {
						case DEFINITION:
						case DEF_AND_BODY:
							operations.add(new JavaChangeOperation(ChangeType.Deleted, javaElem, revision));
							break;
						case BODY:
							operations.add(new JavaChangeOperation(ChangeType.Modified, javaElem, revision));
							break;
					}
				}
			} else if (delta instanceof InsertDelta) {
				// corresponding elements to be added
				HashSet<Integer> lines = DiffUtils.getLineNumbers(delta.getRevised());
				for (JavaElementLocation javaElem : newElems.getElements(changedPath)) {
					LineCover cover = javaElem.coversAnyLine(lines);
					switch (cover) {
						case DEFINITION:
						case DEF_AND_BODY:
							operations.add(new JavaChangeOperation(ChangeType.Added, javaElem, revision));
							break;
						case BODY:
							operations.add(new JavaChangeOperation(ChangeType.Modified, javaElem, revision));
							break;
					}
				}
			} else {
				// make a collection diff
				HashSet<Integer> oldLines = DiffUtils.getLineNumbers(delta.getOriginal());
				HashSet<Integer> newLines = DiffUtils.getLineNumbers(delta.getRevised());
				
				Collection<JavaElementLocation<JavaElementDefinition>> modifiedDefCandidates = new HashSet<JavaElementLocation<JavaElementDefinition>>();
				
				Collection<JavaElementLocation<JavaElementDefinition>> addDefCandidates = new HashSet<JavaElementLocation<JavaElementDefinition>>();
				for (JavaElementLocation def : newElems.getElements(changedPath)) {
					LineCover cover = def.coversAnyLine(newLines);
					switch (cover) {
						case BODY:
							modifiedDefCandidates.add(def);
							break;
						case DEF_AND_BODY:
						case DEFINITION:
							addDefCandidates.add(def);
							break;
					}
				}
				Collection<JavaElementLocation<JavaElementDefinition>> removeDefCandidates = new HashSet<JavaElementLocation<JavaElementDefinition>>();
				for (JavaElementLocation def : oldElems.getElements(changedPath)) {
					LineCover cover = def.coversAnyLine(oldLines);
					switch (cover) {
						case BODY:
							modifiedDefCandidates.add(def);
							break;
						case DEF_AND_BODY:
						case DEFINITION:
							removeDefCandidates.add(def);
							break;
					}
				}
				
				Collection<JavaElementLocation<JavaElementDefinition>> defsToRemove = new HashSet<JavaElementLocation<JavaElementDefinition>>();
				Collection<JavaElementLocation<JavaElementDefinition>> defsToAdd = new HashSet<JavaElementLocation<JavaElementDefinition>>();
				
				defsToRemove.addAll(CollectionUtils.subtract(removeDefCandidates, addDefCandidates));
				defsToAdd.addAll(CollectionUtils.subtract(addDefCandidates, removeDefCandidates));
				
				Map<String, TreeSet<JavaElementLocation<JavaMethodCall>>> removeCallCandidates = new HashMap<String, TreeSet<JavaElementLocation<JavaMethodCall>>>();
				if (oldElems.getMethodCalls(changedPath) != null) {
					for (JavaElementLocation<JavaMethodCall> call : oldElems.getMethodCalls(changedPath)) {
						LineCover cover = call.coversAnyLine(oldLines);
						if (!cover.equals(LineCover.FALSE)) {
							if (!removeCallCandidates.containsKey(call.getElement().getFullQualifiedName())) {
								removeCallCandidates.put(call.getElement().getFullQualifiedName(),
								                         new TreeSet<JavaElementLocation<JavaMethodCall>>());
							}
							removeCallCandidates.get(call.getElement().getFullQualifiedName()).add(call);
						}
					}
				}
				Map<String, TreeSet<JavaElementLocation<JavaMethodCall>>> addCallCandidates = new HashMap<String, TreeSet<JavaElementLocation<JavaMethodCall>>>();
				if (newElems.getMethodCalls(changedPath) != null) {
					for (JavaElementLocation<JavaMethodCall> call : newElems.getMethodCalls(changedPath)) {
						LineCover cover = call.coversAnyLine(newLines);
						if (!cover.equals(LineCover.FALSE)) {
							if (!addCallCandidates.containsKey(call.getElement().getFullQualifiedName())) {
								addCallCandidates.put(call.getElement().getFullQualifiedName(),
								                      new TreeSet<JavaElementLocation<JavaMethodCall>>());
							}
							addCallCandidates.get(call.getElement().getFullQualifiedName()).add(call);
						}
					}
				}
				
				// if a line was changed: detect added and removed calls that
				// are most likely corresponding
				for (String addedCallId : addCallCandidates.keySet()) {
					if (removeCallCandidates.containsKey(addedCallId)) {
						TreeSet<JavaElementLocation<JavaMethodCall>> delSet = removeCallCandidates.get(addedCallId);
						TreeSet<JavaElementLocation<JavaMethodCall>> addSet = addCallCandidates.get(addedCallId);
						for (JavaElementLocation<JavaMethodCall> addedCall : new TreeSet<JavaElementLocation<JavaMethodCall>>(
						                                                                                                      addCallCandidates.get(addedCallId))) {
							JavaElementLocation<JavaMethodCall> ceiling = delSet.ceiling(addedCall);
							JavaElementLocation<JavaMethodCall> floor = delSet.floor(addedCall);
							if ((ceiling != null) || (floor != null)) {
								JavaElementLocation<JavaMethodCall> correspondent = null;
								if (ceiling == null) {
									correspondent = floor;
								} else if (floor == null) {
									correspondent = ceiling;
								} else {
									int floorDist = Math.abs(addedCall.getPosition() - floor.getPosition());
									int ceilingDist = Math.abs(addedCall.getPosition() - ceiling.getPosition());
									if (floorDist < ceilingDist) {
										correspondent = floor;
									} else {
										correspondent = ceiling;
									}
								}
								Condition.check(correspondent != null, "Must have found correspondent");
								if (correspondent == null) {
									if (Logger.logError()) {
										Logger.error("Must have found correspondent!");
									}
								}
								delSet.remove(correspondent);
								addSet.remove(addedCall);
							}
						}
					}
				}
				
				// method calls that are still present in their collections make
				// up the operations
				for (TreeSet<JavaElementLocation<JavaMethodCall>> methodCallsToDelete : removeCallCandidates.values()) {
					for (JavaElementLocation<JavaMethodCall> methodCall : methodCallsToDelete) {
						operations.add(new JavaChangeOperation(ChangeType.Deleted, methodCall, revision));
					}
				}
				for (TreeSet<JavaElementLocation<JavaMethodCall>> methodCallsToAdd : addCallCandidates.values()) {
					for (JavaElementLocation<JavaMethodCall> methodCall : methodCallsToAdd) {
						operations.add(new JavaChangeOperation(ChangeType.Added, methodCall, revision));
					}
				}
				for (JavaElementLocation<JavaElementDefinition> methodDefToDelete : defsToRemove) {
					operations.add(new JavaChangeOperation(ChangeType.Deleted, methodDefToDelete, revision));
				}
				for (JavaElementLocation<JavaElementDefinition> methodDefToAdd : defsToAdd) {
					operations.add(new JavaChangeOperation(ChangeType.Added, methodDefToAdd, revision));
				}
				for (JavaElementLocation<JavaElementDefinition> methodDefModified : modifiedDefCandidates) {
					operations.add(new JavaChangeOperation(ChangeType.Modified, methodDefModified, revision));
				}
			}
		}
		for (JavaChangeOperation op : operations) {
			for (ChangeOperationVisitor visitor : visitors) {
				visitor.visit(op);
			}
		}
	}
	
}
