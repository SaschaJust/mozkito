package de.unisaarland.cs.st.reposuite.callgraph;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.dom.CompilationUnit;

import ca.mcgill.cs.swevo.ppa.PPAOptions;
import de.unisaarland.cs.st.reposuite.callgraph.model.CallGraph;
import de.unisaarland.cs.st.reposuite.callgraph.visitor.CallGraphPPAVisitor;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAUtils;
import de.unisaarland.cs.st.reposuite.ppa.visitors.PPATypeVisitor;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.settings.DirectoryArgument;
import de.unisaarland.cs.st.reposuite.settings.ListArgument;
import de.unisaarland.cs.st.reposuite.settings.OutputFileArgument;
import de.unisaarland.cs.st.reposuite.settings.RepositoryArguments;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.settings.StringArgument;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;


public class CallGraphToolChain extends RepoSuiteToolchain {
	
	private final StringArgument    transactionArg;
	private final DirectoryArgument dirArg;
	private final OutputFileArgument   outArg;
	private final RepositoryArguments repoSettings;
	private Repository                repository = null;
	private final ListArgument              packageFilterArg;
	
	public CallGraphToolChain() {
		super(new RepositorySettings());
		RepositorySettings settings = (RepositorySettings) getSettings();
		
		this.repoSettings = settings.setRepositoryArg(false);
		transactionArg = new StringArgument(super.getSettings(), "transaction.id",
		                                    "The transaction id to create the call graph for.", null, false);
		dirArg = new DirectoryArgument(
		                               getSettings(),
		                               "source.directory",
		                               "(Only used when "+transactionArg.getName()+" not set) Use files from from.directory to build the call graph on.",
		                               null, false, false);
		packageFilterArg = new ListArgument(
		                                    getSettings(),
		                                    "package.filter",
		                                    "A white list of package names to be considered. Entities not mathings any of these packages will be ignores",
		                                    "", false);
		
		outArg = new OutputFileArgument(getSettings(), "output", "File to store the serialized CallGraph in.", null,
		                                true, true);
		getSettings().parseArguments();
	}
	
	@Override
	public void run() {
		if (repository != null) {
			runRepository();
		}
		File sourcedir = dirArg.getValue();
		runDirectory(sourcedir);
	}
	
	private void runDirectory(final File sourcedir) {
		Collection<File> files = FileUtils.listFiles(sourcedir, new String[] { "java" }, true);
		HashSet<String> packageFilter = packageFilterArg.getValue();
		JavaElementCache elemCache = new JavaElementCache();
		
		Map<File, CompilationUnit> compilationUnits = PPAUtils.getCUs(files, new PPAOptions());
		
		CallGraph callGraph = new CallGraph();
		
		for(Entry<File, CompilationUnit> cuEntry : compilationUnits.entrySet()){
			String relativePath = cuEntry.getKey().getAbsolutePath();
			if(!relativePath.startsWith(sourcedir.getAbsolutePath())){
				throw new UnrecoverableError("CU file must start with sourceDir path!");
			}
			relativePath = relativePath.substring(sourcedir.getAbsolutePath().length());
			PPATypeVisitor typeVisitor = new PPATypeVisitor(cuEntry.getValue(), relativePath, packageFilter.toArray(new String[packageFilter.size()]), elemCache);
			CallGraphPPAVisitor callGraphPPAVisitor = new CallGraphPPAVisitor(callGraph, true, relativePath, elemCache);
			typeVisitor.registerVisitor(callGraphPPAVisitor);
			cuEntry.getValue().accept(typeVisitor);
		}
		File outFile = outArg.getValue();
		callGraph.serialize(outFile);
	}
	
	private void runRepository() {
		String transactionId = transactionArg.getValue();
		File checkoutPath = repository.checkoutPath("/", transactionId);
		runDirectory(checkoutPath);
	}
	
	@Override
	public void setup() {
		if (transactionArg.getValue() != null) {
			repository = repoSettings.getValue();
		}
	}
	
	@Override
	public void shutdown() {
		
	}
	
}
