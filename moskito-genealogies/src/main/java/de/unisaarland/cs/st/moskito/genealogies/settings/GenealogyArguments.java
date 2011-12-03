package de.unisaarland.cs.st.moskito.genealogies.settings;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.settings.DirectoryArgument;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.genealogies.core.ChangeGenealogyUtils;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;


public class GenealogyArguments extends AndamaArgumentSet {
	
	private DirectoryArgument graphDBArg;
	private DatabaseArguments dbArgs;
	
	public GenealogyArguments(AndamaSettings settings, boolean isRequired,String unit) {
		super();
		graphDBArg = new DirectoryArgument(settings, "genealogy.graphdb",
				"Directory in which to store the GraphDB (if exists, load graphDB from this dir)", null, true, true);
		dbArgs = new DatabaseArguments(settings, isRequired, unit);
	}
	
	@Override
	public CoreChangeGenealogy getValue() {
		
		if (!this.dbArgs.getValue()) {
			if (Logger.logError()) {
				Logger.error("Could not connect to database!");
			}
			
			throw new Shutdown();
		}
		PersistenceUtil persistenceUtil;
		try {
			persistenceUtil = PersistenceManager.getUtil();
		} catch (UninitializedDatabaseException e1) {
			throw new UnrecoverableError(e1);
		}
		return ChangeGenealogyUtils.readFromDB(graphDBArg.getValue(), persistenceUtil);
	}
	
}
