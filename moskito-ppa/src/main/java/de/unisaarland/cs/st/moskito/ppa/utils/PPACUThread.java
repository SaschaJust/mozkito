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
package de.unisaarland.cs.st.moskito.ppa.utils;

import net.ownhero.dev.kisa.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PPAASTParser;
import org.eclipse.jdt.core.dom.PPAEngine;
import org.eclipse.jdt.core.dom.PPATypeRegistry;
import org.eclipse.jdt.internal.core.JavaProject;

import ca.mcgill.cs.swevo.ppa.PPAOptions;

/**
 * The Class PPACUThread.
 * 
 * @author Kim Herzig <kim@cs.uni-saarland.de>
 */
public class PPACUThread implements Runnable {
	
	/** The file. */
	private final IFile      file;
	
	/** The options. */
	private final PPAOptions options;
	
	/** The cu. */
	private CompilationUnit  cu = null;
	
	/**
	 * Instantiates a new pPACU thread.
	 * 
	 * @param file
	 *            the file
	 * @param options
	 *            the options
	 */
	public PPACUThread(final IFile file, final PPAOptions options) {
		this.file = file;
		this.options = options;
	}
	
	/**
	 * Gets the cU.
	 * 
	 * @return the cU
	 */
	public CompilationUnit getCU() {
		return cu;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// ICompilationUnit icu = JavaCore.createCompilationUnitFrom(file);
		// PPATypeRegistry registry;
		// try {
		// registry = new PPATypeRegistry((JavaProject)
		// JavaCore.create(icu.getUnderlyingResource().getProject()));
		// } catch (JavaModelException e) {
		// if (Logger.logWarn()) {
		// Logger.warn("Warning while getting CU from PPA");
		// }
		// if (Logger.logDebug()) {
		// Logger.debug("Exception", e);
		// }
		// return;
		// }
		// ASTNode node = null;
		// PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
		// parser2.setStatementsRecovery(true);
		// parser2.setResolveBindings(true);
		// parser2.setSource(icu);
		// node = parser2.createAST(null);s
		// PPAEngine ppaEngine = new PPAEngine(registry, options);
		//
		// cu = (CompilationUnit) node;
		//
		// ppaEngine.addUnitToProcess(cu);
		// ppaEngine.doPPA();
		// ppaEngine.reset();
		
		try {
			ICompilationUnit icu = JavaCore.createCompilationUnitFrom(file);
			PPATypeRegistry registry = new PPATypeRegistry((JavaProject) JavaCore.create(icu.getUnderlyingResource()
			                                                                                .getProject()));
			ASTNode node = null;
			PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
			parser2.setStatementsRecovery(true);
			parser2.setResolveBindings(true);
			parser2.setSource(icu);
			node = parser2.createAST(false, new NullProgressMonitor());
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
			
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error("Error while getting CU from PPA", e);
			}
		}
	}
	
}
