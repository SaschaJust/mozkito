/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

package causeeffect;

import java.util.LinkedList;
import java.util.List;

import org.mozkito.causeeffect.AG_EF_FormulaGenearator;
import org.mozkito.causeeffect.EF_FormulaGenearator;
import org.mozkito.causeeffect.EF_and_FormulaGenearator;
import org.mozkito.causeeffect.EFandEF_FormulaGenearator;
import org.mozkito.causeeffect.LTCExperiment;
import org.mozkito.causeeffect.LTCFormulaFactory;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.core.TransactionChangeGenealogy;
import org.mozkito.genealogies.settings.GenealogyOptions;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.persistence.RCSPersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.versions.collections.TransactionSet;
import org.mozkito.versions.collections.TransactionSet.TransactionSetOrder;
import org.mozkito.versions.model.Branch;
import org.mozkito.versions.model.Transaction;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.DoubleArgument;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class Main.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class Main {
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		try {
			final Settings settings = new Settings();
			final DatabaseOptions databaseOptions = new DatabaseOptions(settings.getRoot(), Requirement.required, "codeanalysis");
			final PersistenceUtil persistenceUtil = ArgumentSetFactory.create(databaseOptions).getValue();
			final GenealogyOptions genealogyOptions = new GenealogyOptions(settings.getRoot(), Requirement.required,
			                                                               databaseOptions);
			
			final BooleanArgument innerRulesOpt = ArgumentFactory.create(new BooleanArgument.Options(
			                                                                                         settings.getRoot(),
			                                                                                         "includeInnerRules",
			                                                                                         "Select TRUE if you want to inlcude inner transaction rules into recommendations.",
			                                                                                         false,
			                                                                                         Requirement.required));
			
			final LongArgument minSupportOpt = ArgumentFactory.create(new LongArgument.Options(
			                                                                                   settings.getRoot(),
			                                                                                   "minSupport",
			                                                                                   "The minimal support value a LTC rule must have to be selected as recommendation.",
			                                                                                   3l, Requirement.required));
			
			final LongArgument formulaExpiryOpt = ArgumentFactory.create(new LongArgument.Options(
			                                                                                      settings.getRoot(),
			                                                                                      "formulaExpiry",
			                                                                                      "The maximal number of days a formula is supported backwards.",
			                                                                                      30l,
			                                                                                      Requirement.required));
			
			final LongArgument timeWindowOpt = ArgumentFactory.create(new LongArgument.Options(
			                                                                                   settings.getRoot(),
			                                                                                   "timeWindowLength",
			                                                                                   "The maximal number of days in which a LTC must evakate to true.",
			                                                                                   null,
			                                                                                   Requirement.required));
			
			final LongArgument numRecomOpt = ArgumentFactory.create(new LongArgument.Options(
			                                                                                 settings.getRoot(),
			                                                                                 "numRecommendations",
			                                                                                 "The maximal number of recommendations to be reported.",
			                                                                                 3l, Requirement.required));
			
			final DoubleArgument minConfidenceOpt = ArgumentFactory.create(new DoubleArgument.Options(
			                                                                                          settings.getRoot(),
			                                                                                          "minConfidence",
			                                                                                          "The minimal confidence value a LTC rule must have to be selected as recommendation.",
			                                                                                          0.5d,
			                                                                                          Requirement.required));
			
			final StringArgument branchNameOptions = ArgumentFactory.create(new StringArgument.Options(
			                                                                                           settings.getRoot(),
			                                                                                           "branch",
			                                                                                           "Use transactions of this branch to generate LTCs for. Make sure that this matches the tranactions in the genealogy graph.",
			                                                                                           Branch.MASTER_BRANCH_NAME,
			                                                                                           Requirement.required));
			
			final ArgumentSet<CoreChangeGenealogy, GenealogyOptions> genealogyArgument = ArgumentSetFactory.create(genealogyOptions);
			final CoreChangeGenealogy coreChangeGenealogy = genealogyArgument.getValue();
			final TransactionChangeGenealogy transactionLayer = coreChangeGenealogy.getTransactionLayer();
			
			final LTCFormulaFactory formulaFactory = new LTCFormulaFactory();
			formulaFactory.register(new EF_FormulaGenearator());
			formulaFactory.register(new EF_and_FormulaGenearator());
			formulaFactory.register(new EFandEF_FormulaGenearator());
			formulaFactory.register(new AG_EF_FormulaGenearator());
			
			final LTCExperiment experiment = new LTCExperiment(transactionLayer, formulaFactory,
			                                                   minSupportOpt.getValue().intValue(),
			                                                   minConfidenceOpt.getValue().doubleValue(),
			                                                   formulaExpiryOpt.getValue().intValue(),
			                                                   timeWindowOpt.getValue().intValue(),
			                                                   numRecomOpt.getValue().intValue());
			
			final Branch masterBranch = persistenceUtil.loadById(branchNameOptions.getValue(), Branch.class);
			if (masterBranch == null) {
				final List<String> branchNames = new LinkedList<>();
				for (final Branch branch : persistenceUtil.load(persistenceUtil.createCriteria(Branch.class))) {
					branchNames.add(branch.getName());
				}
				if (Logger.logError()) {
					Logger.error("Could not find a branch with name %s. Cannot create genealogy graph. Temrinating. Possible branch names are: %s.",
					             branchNameOptions.getValue(), JavaUtils.collectionToString(branchNames));
				}
				throw new Shutdown();
			}
			
			final TransactionSet masterTransactions = RCSPersistenceUtil.getTransactions(persistenceUtil, masterBranch,
			                                                                             TransactionSetOrder.ASC);
			final List<Transaction> transactions = new LinkedList<>();
			for (final Transaction t : masterTransactions) {
				if (transactionLayer.containsVertex(t)) {
					transactions.add(t);
				}
			}
			
			if (Logger.logDebug()) {
				Logger.debug("%d out of %d transactions found in genealogy graph.", transactions.size(),
				             masterTransactions.size());
			}
			
			final int trainSize = new Double(transactions.size() * 0.1).intValue();
			
			if (Logger.logInfo()) {
				Logger.info("Training set contains %d entities.", trainSize);
			}
			
			final List<Transaction> trainList = transactions.subList(0, trainSize);
			final List<Transaction> testList = transactions.subList(trainSize, transactions.size());
			
			experiment.run(trainList, testList, innerRulesOpt.getValue());
			
			if (Logger.logInfo()) {
				Logger.info("All done. Cerio.");
			}
		} catch (final Shutdown e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		} catch (final SettingsParseError e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		} catch (ArgumentRegistrationException | ArgumentSetRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			
		}
	}
}
