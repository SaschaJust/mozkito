/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.selectors;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.StringArgument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ReportRegexSelector extends MappingSelector {
	
	private String pattern = "\\p{XDigit}{7,}";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.mapping.selectors.MappingSelector#
	 * getDescription()
	 */
	@Override
	public String getDescription() {
		return "Looks up all regular matches of the specified pattern and returns possible (report) candidates from the database.";
	}
	
	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return this.pattern;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.mapping.register.Registered#init()
	 */
	@Override
	public void init() {
		super.init();
		setPattern((String) getSettings().getSetting(getOptionName("pattern")).getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.selectors.MappingSelector#parse
	 * (java.lang.Object)
	 */
	@Override
	public <T extends MappableEntity> List<T> parse(MappableEntity element, Class<T> targetType) {
		List<RCSTransaction> list = new LinkedList<RCSTransaction>();
		try {
			List<String> ids = new LinkedList<String>();
			Regex regex = new Regex(this.pattern);
			PersistenceUtil util;
			
			util = PersistenceManager.getUtil();
			
			Criteria<RCSTransaction> criteria = util.createCriteria(RCSTransaction.class);
			
			for (Comment comment : element.getComments()) {
				List<List<RegexGroup>> findAll = regex.findAll(comment.getMessage());
				
				if (findAll != null) {
					for (List<RegexGroup> match : findAll) {
						
						ids.add(match.get(0).getMatch());
					}
				}
			}
			criteria.in("id", ids);
			list = util.load(criteria);
			
		} catch (UninitializedDatabaseException e) {
			throw new Shutdown(e.getMessage(), e);
		}
		
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.register.Registered#register(de
	 * .unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments,
	 * boolean)
	 */
	@Override
	public void register(final MappingSettings settings, final MappingArguments arguments, final boolean isRequired) {
		super.register(settings, arguments, isRequired);
		arguments.addArgument(new StringArgument(settings, getOptionName("pattern"),
		        "Pattern of report ids to scan for.", "\\p{XDigit}{7,}", isRequired));
	}
	
	/**
	 * @param pattern
	 *            the pattern to set
	 */
	public void setPattern(final String pattern) {
		this.pattern = pattern;
	}
	
}
