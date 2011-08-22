/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.selectors;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableReport;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.settings.StringArgument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TransactionRegexSelector extends MappingSelector {
	
	private String pattern = "\\d{2,}";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.mapping.selectors.MappingSelector#
	 * getDescription()
	 */
	@Override
	public String getDescription() {
		return "Looks up all regular matches of the specified pattern and returns possible (transaction) candidates from the database.";
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
	@SuppressWarnings("unchecked")
	@Override
	public <T extends MappableEntity> List<T> parse(MappableEntity element, Class<T> targetType) {
		List<T> list = new LinkedList<T>();
		try {
			List<Long> ids = new LinkedList<Long>();
			Regex regex = new Regex(this.pattern);
			PersistenceUtil util;
			
			util = PersistenceManager.getUtil();
			
			Criteria<Report> criteria = util.createCriteria(Report.class);
			
			List<List<RegexGroup>> findAll = regex.findAll(element.getBodyText());
			
			if (findAll != null) {
				for (List<RegexGroup> match : findAll) {
					
					ids.add(Long.parseLong(match.get(0).getMatch()));
				}
			}
			criteria.in("id", ids);
			List<Report> loadedList = util.load(criteria);
			
			list.addAll(CollectionUtils.collect(loadedList, new Transformer() {
				
				@Override
				public MappableReport transform(Object input) {
					return new MappableReport((Report) input);
				}
			}));
			
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
		        "Pattern of transaction ids to scan for.", "\\d{2,}", isRequired));
	}
	
	/**
	 * @param pattern
	 *            the pattern to set
	 */
	public void setPattern(final String pattern) {
		this.pattern = pattern;
	}
	
}
