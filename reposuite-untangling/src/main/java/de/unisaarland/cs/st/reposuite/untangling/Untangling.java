package de.unisaarland.cs.st.reposuite.untangling;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.reposuite.clustering.MaxCollapseVisitor;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClustering;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringCollapseVisitor;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.settings.BooleanArgument;
import de.unisaarland.cs.st.reposuite.settings.DatabaseArguments;
import de.unisaarland.cs.st.reposuite.settings.DirectoryArgument;
import de.unisaarland.cs.st.reposuite.settings.InputFileArgument;
import de.unisaarland.cs.st.reposuite.settings.LongArgument;
import de.unisaarland.cs.st.reposuite.settings.RepositoryArguments;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.settings.StringArgument;
import de.unisaarland.cs.st.reposuite.untangling.voters.CallGraphVoter;

public class Untangling {
	
	@NoneNull
	public static Set<Set<JavaChangeOperation>> untangle(final Set<JavaChangeOperation> blob,
	                                                     final int numClusters,
	                                                     final List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors,
	                                                     final MultilevelClusteringCollapseVisitor<JavaChangeOperation> collapseVisitor) {
		@SuppressWarnings ("unused")
		Set<Set<JavaChangeOperation>> result = new HashSet<Set<JavaChangeOperation>>();
		
		MultilevelClustering<JavaChangeOperation> clustering = new MultilevelClustering<JavaChangeOperation>(
		                                                                                                     blob,
		                                                                                                     scoreVisitors,
		                                                                                                     collapseVisitor);
		
		return clustering.getPartitions(numClusters);
	}
	
	private final RepositoryArguments                                   repositoryArg;
	private final DirectoryArgument                                     callgraphArg;
	private final StringArgument                                        transactionArg;
	
	private final InputFileArgument                                     blobArg;
	private List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors;
	private final BooleanArgument                                       useCallGraph;
	private final DatabaseArguments                                     databaseArgs;
	private final LongArgument                                          numPartitionArg;
	
	public Untangling() {
		RepositorySettings settings = new RepositorySettings();
		
		repositoryArg = settings.setRepositoryArg(true);
		databaseArgs = settings.setDatabaseArgs(true);
		callgraphArg = new DirectoryArgument(
		                                     settings,
		                                     "callgraph.eclipse",
		                                     "Home directory of the reposuite callgraph applcation (must contain ./eclipse executable).",
		                                     null, true, false);
		
		blobArg = new InputFileArgument(
		                                settings,
		                                "blob.xml",
		                                "XML file containing change operations to be considered as a single change blob. (This option will ignore the databse arguments!)",
		                                null, false);
		
		transactionArg = new StringArgument(
		                                    settings,
		                                    "transaction.id",
		                                    "The transaction id identifying the transaction to be untangled. (If argument '"
		                                            + blobArg.getName()
		                                            + "' is provided, this transaction id will be used to untangle the blob, if necessary).",
		                                    null, true);
		
		useCallGraph = new BooleanArgument(settings, "vote.callgraph", "Use call graph voter when untangling", "true",
		                                   false);
		
		numPartitionArg = new LongArgument(settings, "num.partitions",
		                                   "Specifies the number of partitions to be generated.", null, true);
		
		settings.parseArguments();
	}
	
	public void run() {
		
		List<String> eclipseArgs = new LinkedList<String>();
		
		eclipseArgs.add(" -Drepository.uri=" + repositoryArg.getRepoDirArg().getValue().toString());
		eclipseArgs.add(" -Drepository.password" + repositoryArg.getPassArg().getValue());
		eclipseArgs.add(" -Drepository.type" + repositoryArg.getRepoTypeArg().getValue());
		eclipseArgs.add(" -Drepository.user" + repositoryArg.getUserArg().getValue());
		
		scoreVisitors = new LinkedList<MultilevelClusteringScoreVisitor<JavaChangeOperation>>();
		
		// add call graph visitor
		if (useCallGraph.getValue()) {
			scoreVisitors.add(new CallGraphVoter(callgraphArg.getValue(),
			                                     eclipseArgs.toArray(new String[eclipseArgs.size()]),
			                                     transactionArg.getValue()));
		}
		
		// TODO add change coupling visitor
		// TODO add test impact visitor
		// TODO add yana's change rule visitor
		// TODO add semdiff visitor
		
		File blobXML = blobArg.getValue();
		
		Set<Set<JavaChangeOperation>> partition = new HashSet<Set<JavaChangeOperation>>();
		
		if (blobXML != null) {
			// TODO read and convert blob.
			Set<JavaChangeOperation> blob = new HashSet<JavaChangeOperation>();
			partition = untangle(blob, numPartitionArg.getValue().intValue(), scoreVisitors,
			                     new MaxCollapseVisitor<JavaChangeOperation>());
		} else {
			if (!databaseArgs.getValue()) {
				throw new UnrecoverableError("Could not connect to specified database using specified credentials.");
			}
			// TODO get method change operations from DB.
			Set<JavaChangeOperation> blob = new HashSet<JavaChangeOperation>();
			partition = untangle(blob, numPartitionArg.getValue().intValue(), scoreVisitors,
			                     new MaxCollapseVisitor<JavaChangeOperation>());
		}
		
		// TODO think of a clever was to report partition
	}
}
