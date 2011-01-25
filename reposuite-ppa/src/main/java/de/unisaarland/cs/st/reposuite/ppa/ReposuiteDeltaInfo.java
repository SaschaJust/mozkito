package de.unisaarland.cs.st.reposuite.ppa;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.settings.DatabaseArguments;
import de.unisaarland.cs.st.reposuite.settings.LoggerArguments;
import de.unisaarland.cs.st.reposuite.settings.RepositoryArguments;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;

public class ReposuiteDeltaInfo {
	
	private final RepositoryArguments repoSettings;
	private final LoggerArguments     logSettings;
	private final DatabaseArguments   databaseSettings;
	private boolean                   shutdown;
	private final HibernateUtil             hibernateUtil;
	
	public ReposuiteDeltaInfo() {
		RepositorySettings settings = new RepositorySettings();
		repoSettings = settings.setRepositoryArg(true);
		databaseSettings = settings.setDatabaseArgs(false);
		logSettings = settings.setLoggerArg(true);
		settings.parseArguments();
		hibernateUtil = databaseSettings.getValue();
	}
	
	public void run() {
		
		Repository repository = repoSettings.getValue();
		

		//		if (!shutdown) {
		//			setup();
		//			if (!shutdown) {
		//				threadPool.execute();
		//			}
		//		}
		//		File tmpDir = new File("/Users/kim/Backup/reposuite-ppa/src/test/resources/DataStructure.java");
		//		CompilationUnit cu = PPAUtils.getCU(tmpDir, new PPAOptions());
		//		System.out.println(cu.toString());
		
		//		if (tmpDir.exists() && (tmpDir.isDirectory())) {
		//			System.out.println("Found file");
		//			Map<String, Collection<JavaElement>> elems = PPAUtils.getJavaMethodElementsByFile(tmpDir, new String[0],
		//					true);
		//			System.out.println("Found " + elems.size() + " files!");
		//			if (elems.containsKey("DataStructure.java")) {
		//				System.out.println("For file DataStructure.java: ");
		//				int classDefCount = 0;
		//				int methDefCount = 0;
		//				int methCallCount = 0;
		//				for (JavaElement elem : elems.get("DataStrcuture.java")) {
		//					if (elem instanceof JavaClassDefinition) {
		//						classDefCount += 1;
		//					} else if (elem instanceof JavaMethodDefinition) {
		//						methDefCount += 1;
		//					} else if (elem instanceof JavaMethodCall) {
		//						methCallCount += 1;
		//					}
		//				}
		//				System.out.println(classDefCount + " class definitions");
		//				System.out.println(methDefCount + " method definitions");
		//				System.out.println(methCallCount + " method calls");
		//			}
		//
		//		}
		
	}
}
