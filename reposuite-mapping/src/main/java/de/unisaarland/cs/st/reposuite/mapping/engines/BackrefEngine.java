/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class BackrefEngine extends MappingEngine {
	
	/**
	 * @param settings
	 */
	public BackrefEngine(final MappingSettings settings) {
		super(settings);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		super.init();
	}
	
	@Override
	public void register(final MappingSettings settings,
	                     final MappingArguments arguments,
	                     final boolean isRequired) {
		// TODO Auto-generated method stub
		super.register(settings, arguments, isRequired);
	}
	
	@Override
	public void score(final RCSTransaction transaction,
	                  final Report report,
	                  final MapScore score) {
		// TODO Auto-generated method stub
		super.score(transaction, report, score);
	}
}
