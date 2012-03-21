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
package de.unisaarland.cs.st.moskito.callgraph;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.OutputFileArgument;
import net.ownhero.dev.hiari.settings.SetArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import org.eclipse.jdt.core.dom.CompilationUnit;

import ca.mcgill.cs.swevo.ppa.PPAOptions;
import de.unisaarland.cs.st.moskito.callgraph.model.CallGraph;
import de.unisaarland.cs.st.moskito.callgraph.visitor.CallGraphPPAVisitor;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementFactory;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocationSet;
import de.unisaarland.cs.st.moskito.ppa.utils.PPAUtils;
import de.unisaarland.cs.st.moskito.ppa.visitors.PPATypeVisitor;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.settings.DatabaseOptions;
import de.unisaarland.cs.st.moskito.settings.RepositoryOptions;

public class CallGraphToolChain {
	
	private Repository                                 repository = null;
	private final SetArgument                          packageFilterArgument;
	private File                                       sourceDir;
	private final String                               transactionId;
	private ArgumentSet<Repository, RepositoryOptions> repositoryArguments;
	private StringArgument                             transactionArgument;
	private DirectoryArgument                          sourceDirArgument;
	private DirectoryArgument                          cacheDirArgument;
	private OutputFileArgument                         outArgument;
	
	public CallGraphToolChain(final Settings settings) {
		
		try {
			
			final DatabaseOptions databaseOptions = new DatabaseOptions(settings.getRoot(), Requirement.required, "rcs");
			ArgumentSetFactory.create(databaseOptions);
			final RepositoryOptions repositoryOptions = new RepositoryOptions(settings.getRoot(), Requirement.required,
			                                                                  databaseOptions);
			this.repositoryArguments = ArgumentSetFactory.create(repositoryOptions);
			
			final StringArgument.Options transactionIdOptions = new StringArgument.Options(
			                                                                               settings.getRoot(),
			                                                                               "transactionId",
			                                                                               "The transaction id to create the call graph for.",
			                                                                               null, Requirement.optional);
			this.transactionArgument = ArgumentFactory.create(transactionIdOptions);
			
			this.sourceDirArgument = ArgumentFactory.create(new DirectoryArgument.Options(
			                                                                              settings.getRoot(),
			                                                                              "sourceDirectory",
			                                                                              "Use files from from.directory to build the call graph on. (Only used when "
			                                                                                      + this.transactionArgument.getTag()
			                                                                                      + " not set)",
			                                                                              null,
			                                                                              Requirement.unset(transactionIdOptions),
			                                                                              false));
			
			this.packageFilterArgument = ArgumentFactory.create(new SetArgument.Options(
			                                                                            settings.getRoot(),
			                                                                            "packageFilter",
			                                                                            "A white list of package names to be considered. Entities not mathings any of these packages will be ignores.",
			                                                                            new HashSet<String>(),
			                                                                            Requirement.optional));
			
			this.cacheDirArgument = ArgumentFactory.create(new DirectoryArgument.Options(
			                                                                             settings.getRoot(),
			                                                                             "cacheDir",
			                                                                             "Directory containing call graphs using the name converntion <transaction_id>.cg.",
			                                                                             null, Requirement.optional,
			                                                                             false));
			
			this.outArgument = ArgumentFactory.create(new OutputFileArgument.Options(
			                                                                         settings.getRoot(),
			                                                                         "output",
			                                                                         "File to store the serialized CallGraph in.",
			                                                                         null, Requirement.required, true));
			
			this.transactionId = this.transactionArgument.getValue();
			
			if (this.transactionId != null) {
				this.repository = this.repositoryArguments.getValue();
				this.sourceDir = this.repository.checkoutPath("/", this.transactionId);
				if (this.sourceDir == null) {
					throw new UnrecoverableError("Could not checkout transaction " + this.transactionId
					        + " from repository " + this.repository.getUri().toString() + ". See errors above.");
				}
			} else {
				this.sourceDir = this.sourceDirArgument.getValue();
			}
		} catch (final SettingsParseError e) {
			throw new UnrecoverableError(e);
		} catch (final ArgumentSetRegistrationException e) {
			throw new UnrecoverableError(e);
		} catch (final ArgumentRegistrationException e) {
			throw new UnrecoverableError(e);
		} finally {
			
		}
	}
	
	public void run() {
		final String[] fileExtensions = { "java" };
		final Collection<File> files = FileUtils.listFiles(this.sourceDir, fileExtensions, true);
		final HashSet<String> packageFilter = this.packageFilterArgument.getValue();
		
		final JavaElementFactory elementFactory = new JavaElementFactory();
		
		final JavaElementLocationSet elemCache = new JavaElementLocationSet(elementFactory);
		
		final Map<File, CompilationUnit> compilationUnits = PPAUtils.getCUs(files, new PPAOptions());
		
		final File cacheDir = this.cacheDirArgument.getValue();
		
		// generate the call graph
		CallGraph callGraph = null;
		if ((cacheDir != null) && (cacheDir.exists()) && (cacheDir.isDirectory()) && (cacheDir.canRead())
		        && (this.transactionId != null)) {
			final File serialFile = new File(cacheDir.getAbsolutePath() + FileUtils.fileSeparator + this.transactionId
			        + ".cg");
			if (serialFile.exists()) {
				callGraph = CallGraph.unserialize(serialFile);
			}
		}
		if (callGraph == null) {
			callGraph = new CallGraph();
			for (final Entry<File, CompilationUnit> cuEntry : compilationUnits.entrySet()) {
				String relativePath = cuEntry.getKey().getAbsolutePath();
				if (!relativePath.startsWith(this.sourceDir.getAbsolutePath())) {
					throw new UnrecoverableError("CU file must start with sourceDir path!");
				}
				relativePath = relativePath.substring(this.sourceDir.getAbsolutePath().length());
				final PPATypeVisitor typeVisitor = new PPATypeVisitor(
				                                                      cuEntry.getValue(),
				                                                      relativePath,
				                                                      packageFilter.toArray(new String[packageFilter.size()]),
				                                                      elemCache);
				final CallGraphPPAVisitor callGraphPPAVisitor = new CallGraphPPAVisitor(callGraph, true, relativePath,
				                                                                        elemCache);
				typeVisitor.registerVisitor(callGraphPPAVisitor);
				cuEntry.getValue().accept(typeVisitor);
			}
		}
		if (Logger.logInfo()) {
			final StringBuilder sb = new StringBuilder();
			sb.append("Generated call graph with:");
			sb.append(FileUtils.lineSeparator);
			sb.append("\t");
			sb.append(callGraph.getVertexCount());
			sb.append(" vertices");
			sb.append(FileUtils.lineSeparator);
			sb.append("\t");
			sb.append(callGraph.getEdgeCount());
			sb.append(" edges");
			Logger.info(sb.toString());
			
		}
		final File outFile = this.outArgument.getValue();
		callGraph.serialize(outFile);
	}
}
