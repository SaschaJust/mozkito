/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.callgraph;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ca.mcgill.cs.swevo.ppa.PPAOptions;
import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.ListArgument;
import net.ownhero.dev.hiari.settings.OutputFileArgument;
import net.ownhero.dev.hiari.settings.SetArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import org.eclipse.jdt.core.dom.CompilationUnit;

import org.mozkito.callgraph.model.CallGraph;
import org.mozkito.callgraph.visitor.CallGraphPPAVisitor;
import org.mozkito.codeanalysis.model.JavaElementFactory;
import org.mozkito.codeanalysis.model.JavaElementLocationSet;
import org.mozkito.codeanalysis.utils.PPAUtils;
import org.mozkito.codeanalysis.visitors.PPATypeVisitor;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.versions.Repository;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.settings.RepositoryOptions;

/**
 * The Class CallGraphToolChain.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class CallGraphToolChain {
	
	/** The repository. */
	private Repository                                 repository = null;
	
	/** The package filter argument. */
	private final SetArgument                          packageFilterArgument;
	
	/** The source dir. */
	private File                                       sourceDir;
	
	/** The transaction id. */
	private final String                               changeSetId;
	
	/** The repository arguments. */
	private ArgumentSet<Repository, RepositoryOptions> repositoryArguments;
	
	/** The transaction argument. */
	private StringArgument                             transactionArgument;
	
	/** The source dir argument. */
	private DirectoryArgument                          sourceDirArgument;
	
	/** The cache dir argument. */
	private DirectoryArgument                          cacheDirArgument;
	
	/** The out argument. */
	private OutputFileArgument                         outArgument;
	
	/** The negative filename list argument. */
	private ListArgument                               negativeFilenameListArgument;
	
	/**
	 * Instantiates a new call graph tool chain.
	 * 
	 * @param settings
	 *            the settings
	 */
	public CallGraphToolChain(final Settings settings) {
		
		try {
			
			final DatabaseOptions databaseOptions = new DatabaseOptions(settings.getRoot(), Requirement.required,
			                                                            "codeanalysis");
			ArgumentSetFactory.create(databaseOptions);
			final RepositoryOptions repositoryOptions = new RepositoryOptions(settings.getRoot(), Requirement.required,
			                                                                  databaseOptions);
			this.repositoryArguments = ArgumentSetFactory.create(repositoryOptions);
			
			final StringArgument.Options changeSetIdOptions = new StringArgument.Options(
			                                                                             settings.getRoot(),
			                                                                             "changeSetId",
			                                                                             "The transaction id to create the call graph for.",
			                                                                             null, Requirement.optional);
			this.transactionArgument = ArgumentFactory.create(changeSetIdOptions);
			
			this.sourceDirArgument = ArgumentFactory.create(new DirectoryArgument.Options(
			                                                                              settings.getRoot(),
			                                                                              "sourceDirectory",
			                                                                              "Use files from from.directory to build the call graph on. (Only used when "
			                                                                                      + this.transactionArgument.getTag()
			                                                                                      + " not set)",
			                                                                              null,
			                                                                              Requirement.unset(changeSetIdOptions),
			                                                                              false));
			
			this.packageFilterArgument = ArgumentFactory.create(new SetArgument.Options(
			                                                                            settings.getRoot(),
			                                                                            "packageFilter",
			                                                                            "A white list of package names to be considered. Entities not mathings any of these packages will be ignores.",
			                                                                            new HashSet<String>(),
			                                                                            Requirement.required));
			
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
			
			this.negativeFilenameListArgument = ArgumentFactory.create(new ListArgument.Options(
			                                                                                    settings.getRoot(),
			                                                                                    "negativeFileFilter",
			                                                                                    "Ignore source files whose file name ends of one of these strings. (entries are separated using ',')",
			                                                                                    new ArrayList<String>(0),
			                                                                                    Requirement.optional));
			
			if (settings.helpRequested()) {
				if (Logger.logAlways()) {
					Logger.always(settings.getHelpString());
				}
				
				throw new Shutdown();
			}
			
			this.changeSetId = this.transactionArgument.getValue();
			
			if (this.changeSetId != null) {
				this.repository = this.repositoryArguments.getValue();
				try {
					this.sourceDir = this.repository.checkoutPath("/", this.changeSetId);
				} catch (final RepositoryOperationException e) {
					throw new UnrecoverableError(
					                             String.format("Could not checkout transaction %s from repository %s. See errors above.",
					                                           this.changeSetId, this.repository.getUri().toString()));
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
		}
	}
	
	/**
	 * Run.
	 */
	public void run() {
		final String[] fileExtensions = { "java" };
		final Collection<File> files = FileUtils.listFiles(this.sourceDir, fileExtensions, true);
		final HashSet<String> packageFilter = this.packageFilterArgument.getValue();
		
		final JavaElementFactory elementFactory = new JavaElementFactory();
		
		final JavaElementLocationSet elemCache = new JavaElementLocationSet(elementFactory);
		
		final List<String> negativeList = this.negativeFilenameListArgument.getValue();
		if ((negativeList != null) && (!negativeList.isEmpty())) {
			final List<File> filesToIgnore = new ArrayList<File>(files.size());
			for (final File f : files) {
				for (final String p : negativeList) {
					if (f.getAbsolutePath().endsWith(p)) {
						filesToIgnore.add(f);
					}
				}
			}
			files.removeAll(filesToIgnore);
		}
		
		final Map<File, CompilationUnit> compilationUnits = PPAUtils.getCUs(files, new PPAOptions());
		
		final File cacheDir = this.cacheDirArgument.getValue();
		
		// generate the call graph
		CallGraph callGraph = null;
		if ((cacheDir != null) && (cacheDir.exists()) && (cacheDir.isDirectory()) && (cacheDir.canRead())
		        && (this.changeSetId != null)) {
			final File serialFile = new File(cacheDir.getAbsolutePath() + FileUtils.fileSeparator + this.changeSetId
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
