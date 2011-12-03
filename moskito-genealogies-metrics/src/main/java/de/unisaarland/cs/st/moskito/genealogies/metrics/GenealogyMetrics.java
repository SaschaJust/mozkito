package de.unisaarland.cs.st.moskito.genealogies.metrics;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.model.AndamaPool;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogyArguments;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogySettings;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

public class GenealogyMetrics extends AndamaChain {
	
	private GenealogyArguments genealogyArgs;
	private AndamaPool         threadPool;
	private PersistenceUtil    persistenceUtil;
	private CoreChangeGenealogy genealogy;
	
	public GenealogyMetrics() {
		super(new GenealogySettings());
		GenealogySettings settings = (GenealogySettings) getSettings();
		this.threadPool = new AndamaPool(GenealogyMetrics.class.getSimpleName(), this);
		genealogyArgs = settings.setGenealogyArgs(true);
		settings.parseArguments();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
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
		genealogy = genealogyArgs.getValue();
		try {
			this.persistenceUtil = PersistenceManager.getUtil();
		} catch (UninitializedDatabaseException e1) {
			throw new UnrecoverableError(e1);
		}
	}
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
}
