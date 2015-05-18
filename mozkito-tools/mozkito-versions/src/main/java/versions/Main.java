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
/**
 * 
 */
package versions;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.GraphBuilder;
import org.mozkito.RepositoryToolchain;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.versions.Repository;
import org.mozkito.versions.elements.RevDependencyGraph;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.git.GitRepository;

/**
 * The Class Main.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Main {
	
	static {
		KanuniAgent.initialize();
	}
	
	/** The Constant moduleName. */
	private static final String MODULE_NAME = getModuleName();
	
	/**
	 * Gets the module name.
	 * 
	 * @return the module name
	 */
	public static String getModuleName() {
		final StringBuilder builder = new StringBuilder();
		builder.append(Main.class.getPackage().getName());
		builder.append('.');
		builder.append(Main.class.getSimpleName());
		return builder.toString();
	}
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		try {
			final Settings settings = new Settings();
			final RepositoryToolchain repoToolChain = new RepositoryToolchain(settings);
			repoToolChain.setName(repoToolChain.getClass().getSimpleName());
			repoToolChain.start();
			repoToolChain.join();
			
			if (Logger.logInfo()) {
				Logger.info("%s: %s finished. Starting GraphToolChain ...", MODULE_NAME, repoToolChain.getClass()
				                                                                                      .getSimpleName());
			}
			
			final Thread graphBuilderThread = new Thread(new GraphBuilder(repoToolChain.getRepository()
			                                                                           .getMainBranchName(),
			                                                              repoToolChain.getRepository()
			                                                                           .getRevDependencyGraph(),
			                                                              repoToolChain.getVersionArchive(),
			                                                              repoToolChain.getPersistenceUtil()));
			graphBuilderThread.setName(GraphBuilder.class.getSimpleName());
			graphBuilderThread.start();
			graphBuilderThread.join();
			
			if (Logger.logInfo()) {
				Logger.info("%s: All done. Cerio!", MODULE_NAME);
			}
		} catch (InterruptedException | SettingsParseError | RepositoryOperationException | UnrecoverableError e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (final Shutdown shutdown) {
			System.err.println("Shutdown requested"
			        + (shutdown.getMessage() != null
			                                        ? " (" + shutdown.getMessage() + ")"
			                                        : "") + ".");
		}
	}
	
	public static void mainTest(final String[] args) {
		try {
			final Repository repository = new GitRepository(new PersonFactory());
			repository.setup(new URI(args[0]), new File(args[1]), args[2]);
			final RevDependencyGraph graph = repository.getRevDependencyGraph();
			
			System.out.println("digraph repograph {");
			for (final String hash : graph.getBranchTransactions(args[2])) {
				for (final String mergeParentHash : graph.getMergeParents(hash)) {
					System.out.println(String.format("%s -> %s;", mergeParentHash, hash));
				}
				System.out.println(String.format("%s -> %s;", graph.getBranchParent(hash), hash));
			}
			
			System.out.println("}");
		} catch (final RepositoryOperationException | URISyntaxException e) {
			e.printStackTrace(System.err);
		}
		
	}
	
}
