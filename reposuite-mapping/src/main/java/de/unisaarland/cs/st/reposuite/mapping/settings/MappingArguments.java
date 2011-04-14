/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.settings;

import de.unisaarland.cs.st.reposuite.settings.DoubleArgument;
import de.unisaarland.cs.st.reposuite.settings.ListArgument;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet;
import de.unisaarland.cs.st.reposuite.settings.URIArgument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MappingArguments extends RepoSuiteArgumentSet {
	
	/**
	 * @param isRequired 
	 * @param mappingSettings 
	 * 
	 */
	public MappingArguments(final MappingSettings settings, final boolean isRequired) {
		super();
		
		addArgument(new URIArgument(settings, "mapping.config.regexFile",
		                            "URI to file containing the regular expressions used to map the IDs.", null,
		                            isRequired));
		addArgument(new DoubleArgument(settings, "mapping.score.AuthorEquality",
		                               "Score for equal authors in transaction and report comments.", "0.2", isRequired));
		addArgument(new DoubleArgument(settings, "mapping.score.AuthorInequality",
		                               "Score for not equal authors in transaction and report comments.", "-0.8",
		                               isRequired));
		addArgument(new DoubleArgument(settings, "mapping.score.ReportCreatedAfterTransaction",
		                               "Score in case the report was created after the transaction.", "-100",
		                               isRequired));
		addArgument(new DoubleArgument(
		                               settings,
		                               "mapping.score.ReportResolvedWithinWindow",
		                               "Score in case the report was resolved within the specified time window after the transaction.",
		                               "2.0", isRequired));
		addArgument(new ListArgument(
		                             settings,
		                             "mapping.window.ReportResolvedAfterTransaction",
		                             "Time window for the 'mapping.score.ReportResolvedWithinWindow' setting in format '[+-]XXd XXh XXm XXs'.",
		                             "-0d 0h 10m 0s,+0d 2h 0m 0s", isRequired));
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
