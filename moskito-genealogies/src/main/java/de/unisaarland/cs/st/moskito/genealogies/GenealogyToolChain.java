package de.unisaarland.cs.st.moskito.genealogies;

import java.io.File;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.model.AndamaPool;
import net.ownhero.dev.andama.settings.DirectoryArgument;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.genealogies.core.ChangeGenealogyUtils;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.PPAToolChain;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

public class GenealogyToolChain extends AndamaChain {
	
	private final DirectoryArgument   graphDBArg;
	private final DatabaseArguments   databaseArgs;
	private PersistenceUtil           persistenceUtil;
	private AndamaPool              threadPool;
	
	public GenealogyToolChain() {
		super(new RepositorySettings());
		
		this.threadPool = new AndamaPool(PPAToolChain.class.getSimpleName(), this);
		RepositorySettings settings = (RepositorySettings) getSettings();
		settings.setLoggerArg(false);
		databaseArgs = settings.setDatabaseArgs(true, "ppa");
		
		graphDBArg = new DirectoryArgument(settings, "genealogy.graphdb",
				"Directory in which to store the GraphDB (if exists, load graphDB from this dir)", null, true, true);
		
		
		settings.parseArguments();
	}
	
	@Override
	public void run() {
		
		setup();
		this.threadPool.execute();
		
		if (Logger.logInfo()) {
			Logger.info("Terminating.");
		}
	}
	
	@Override
	public void setup() {
		if (!this.databaseArgs.getValue()) {
			if (Logger.logError()) {
				Logger.error("Could not connect to database!");
			}
			
			throw new Shutdown();
		}
		
		File graphDBDir = graphDBArg.getValue();
		CoreChangeGenealogy genealogy = ChangeGenealogyUtils.readFromDB(graphDBDir);
		
		try {
			this.persistenceUtil = PersistenceManager.getUtil();
		} catch (UninitializedDatabaseException e1) {
			throw new UnrecoverableError(e1);
		}
		
		new GenealogyReader(this.threadPool.getThreadGroup(), getSettings(), persistenceUtil);
		new GenealogyNodePersister(this.threadPool.getThreadGroup(), getSettings(), genealogy);
		new GenealogyDependencyPersister(this.threadPool.getThreadGroup(), getSettings(), genealogy);
		
	}
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
}
