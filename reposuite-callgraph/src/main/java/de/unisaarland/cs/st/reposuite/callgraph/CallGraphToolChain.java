/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.callgraph;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import net.ownhero.dev.andama.settings.DirectoryArgument;
import net.ownhero.dev.andama.settings.ListArgument;
import net.ownhero.dev.andama.settings.OutputFileArgument;
import net.ownhero.dev.andama.settings.StringArgument;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import org.eclipse.jdt.core.dom.CompilationUnit;

import ca.mcgill.cs.swevo.ppa.PPAOptions;
import de.unisaarland.cs.st.reposuite.callgraph.model.CallGraph;
import de.unisaarland.cs.st.reposuite.callgraph.visitor.CallGraphPPAVisitor;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocationSet;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAUtils;
import de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.settings.RepositoryArguments;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;

public class CallGraphToolChain {
	
	private final StringArgument      transactionArg;
	private final DirectoryArgument   dirArg;
	private final OutputFileArgument  outArg;
	private final RepositoryArguments repoSettings;
	private Repository                repository = null;
	private final ListArgument        packageFilterArg;
	private File                      sourceDir;
	private final DirectoryArgument   cacheDirArg;
	private final String              transactionId;
	
	public CallGraphToolChain() {
		RepositorySettings settings = new RepositorySettings();
		
		this.repoSettings = settings.setRepositoryArg(true);
		this.transactionArg = new StringArgument(settings, "transaction.id",
		                                         "The transaction id to create the call graph for.", null, false);
		this.dirArg = new DirectoryArgument(
		                                    settings,
		                                    "source.directory",
		                                    "(Only used when "
		                                            + this.transactionArg.getName()
		                                            + " not set) Use files from from.directory to build the call graph on.",
		                                    null, false, false);
		this.packageFilterArg = new ListArgument(
		                                         settings,
		                                         "package.filter",
		                                         "A white list of package names to be considered. Entities not mathings any of these packages will be ignores",
		                                         "", false);
		
		this.cacheDirArg = new DirectoryArgument(
		                                         settings,
		                                         "cache.dir",
		                                         "Directory containing call graphs using the name converntion <transaction_id>.cg",
		                                         null, false, false);
		
		this.outArg = new OutputFileArgument(settings, "output", "File to store the serialized CallGraph in.", null,
		                                     true, true);
		settings.parseArguments();
		
		this.transactionId = this.transactionArg.getValue();
		
		if (this.transactionId != null) {
			this.repository = this.repoSettings.getValue();
			this.sourceDir = this.repository.checkoutPath("/", this.transactionId);
			if (this.sourceDir == null) {
				throw new UnrecoverableError("Could not checkout transaction " + this.transactionId
				        + " from repository " + this.repository.getUri().toString() + ". See errors above.");
			}
		} else {
			this.sourceDir = this.dirArg.getValue();
		}
	}
	
	public void run() {
		String[] fileExtensions = { "java" };
		Collection<File> files = FileUtils.listFiles(this.sourceDir, fileExtensions, true);
		HashSet<String> packageFilter = this.packageFilterArg.getValue();
		JavaElementLocationSet elemCache = new JavaElementLocationSet();
		
		Map<File, CompilationUnit> compilationUnits = PPAUtils.getCUs(files, new PPAOptions());
		
		File cacheDir = this.cacheDirArg.getValue();
		
		// generate the call graph
		CallGraph callGraph = null;
		if ((cacheDir != null) && (cacheDir.exists()) && (cacheDir.isDirectory()) && (cacheDir.canRead())
		        && (this.transactionId != null)) {
			File serialFile = new File(cacheDir.getAbsolutePath() + FileUtils.fileSeparator + this.transactionId
			        + ".cg");
			if (serialFile.exists()) {
				callGraph = CallGraph.unserialize(serialFile);
			}
		}
		if (callGraph == null) {
			callGraph = new CallGraph();
			for (Entry<File, CompilationUnit> cuEntry : compilationUnits.entrySet()) {
				String relativePath = cuEntry.getKey().getAbsolutePath();
				if (!relativePath.startsWith(this.sourceDir.getAbsolutePath())) {
					throw new UnrecoverableError("CU file must start with sourceDir path!");
				}
				relativePath = relativePath.substring(this.sourceDir.getAbsolutePath().length());
				PPATypeVisitor typeVisitor = new PPATypeVisitor(
				                                                cuEntry.getValue(),
				                                                relativePath,
				                                                packageFilter.toArray(new String[packageFilter.size()]),
				                                                elemCache);
				CallGraphPPAVisitor callGraphPPAVisitor = new CallGraphPPAVisitor(callGraph, true, relativePath,
				                                                                  elemCache);
				typeVisitor.registerVisitor(callGraphPPAVisitor);
				cuEntry.getValue().accept(typeVisitor);
			}
		}
		if (Logger.logInfo()) {
			StringBuilder sb = new StringBuilder();
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
		File outFile = this.outArg.getValue();
		callGraph.serialize(outFile);
	}
}
