package de.unisaarland.cs.st.moskito.genealogies;

import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.model.AndamaPool;
import net.ownhero.dev.andama.settings.LoggerArguments;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogyArguments;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogySettings;

public class GenealogyToolChain extends AndamaChain {
	
	private AndamaPool              threadPool;
	private GenealogyArguments genealogyArgs;
	
	public GenealogyToolChain() {
		super(new GenealogySettings());
		
		this.threadPool = new AndamaPool(GenealogyToolChain.class.getSimpleName(), this);
		GenealogySettings settings = (GenealogySettings)getSettings();
		LoggerArguments loggerArg = settings.setLoggerArg(false);
		loggerArg.getValue();
		genealogyArgs = settings.setGenealogyArgs(true);
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
		CoreChangeGenealogy genealogy = genealogyArgs.getValue();
		new ChangeOperationReader(this.threadPool.getThreadGroup(), getSettings(), genealogy.getPersistenceUtil());
		new GenealogyNodePersister(this.threadPool.getThreadGroup(), getSettings(), genealogy);
		new GenealogyDependencyPersister(this.threadPool.getThreadGroup(), getSettings(), genealogy);
		
	}
}
