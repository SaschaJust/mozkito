/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
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
package de.unisaarland.cs.st.moskito.mapping.engines;

import static net.ownhero.dev.ioda.StringUtils.truncate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.ClassLoadingError;
import net.ownhero.dev.andama.exceptions.NoSuchConstructorError;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import de.unisaarland.cs.st.moskito.mapping.storages.LuceneStorage;
import de.unisaarland.cs.st.moskito.mapping.storages.MappingStorage;

// TODO: Auto-generated Javadoc
/**
 * The Class SearchEngine.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class SearchEngine extends MappingEngine {
	
	/** The storage. */
	private LuceneStorage          storage;
	
	/** The language. */
	private String                 language;
	
	/** The min tokens. */
	private Long                   minTokens;
	
	/** The language argument. */
	private StringArgument         languageArgument;
	
	/** The language option. */
	private StringArgument.Options languageOption;
	
	/** The min tokens argument. */
	private LongArgument           minTokensArgument;
	
	/** The min tokens option. */
	private LongArgument.Options   minTokensOption;
	
	/**
	 * Builds the query.
	 * 
	 * @param queryString
	 *            the query string
	 * @param queryParser
	 *            the query parser
	 * @return the query
	 */
	protected Query buildQuery(final String queryString,
	                           final QueryParser queryParser) {
		Query query = null;
		final String modifiedQuery = queryString.replaceAll("[^a-zA-Z0-9]", " "); //$NON-NLS-1$ //$NON-NLS-2$
		
		if (modifiedQuery.replaceAll("[^a-zA-Z0-9]", "").length() < 8) { //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		
		try {
			// FIXME remove truncate and fix query string accordingly
			query = queryParser.parse(truncate(modifiedQuery));
			
			final Set<Term> terms = new HashSet<Term>();
			query.extractTerms(terms);
			
			if (terms.size() < getMinTokens()) {
				return null;
			}
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		
		return query;
	}
	
	/**
	 * Gets the language.
	 * 
	 * @return the language
	 */
	private final String getLanguage() {
		// PRECONDITIONS
		
		try {
			return this.language;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.language, "Field '%s' in '%s'.", "language", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the language argument.
	 * 
	 * @return the languageArgument
	 */
	private final StringArgument getLanguageArgument() {
		// PRECONDITIONS
		
		try {
			return this.languageArgument;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.languageArgument, "Field '%s' in '%s'.", "languageArgument", //$NON-NLS-1$ //$NON-NLS-2$
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the language option.
	 * 
	 * @return the languageOption
	 */
	private final StringArgument.Options getLanguageOption() {
		// PRECONDITIONS
		
		try {
			return this.languageOption;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.languageOption, "Field '%s' in '%s'.", "languageOption", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the min tokens.
	 * 
	 * @return the minTokens
	 */
	private final Long getMinTokens() {
		// PRECONDITIONS
		
		try {
			return this.minTokens;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.minTokens, "Field '%s' in '%s'.", "minTokens", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the min tokens argument.
	 * 
	 * @return the minTokensArgument
	 */
	private final LongArgument getMinTokensArgument() {
		// PRECONDITIONS
		
		try {
			return this.minTokensArgument;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.minTokensArgument, "Field '%s' in '%s'.", "minTokensArgument", //$NON-NLS-1$ //$NON-NLS-2$
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the min tokens option.
	 * 
	 * @return the minTokensOption
	 */
	private final LongArgument.Options getMinTokensOption() {
		// PRECONDITIONS
		
		try {
			return this.minTokensOption;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.minTokensOption, "Field '%s' in '%s'.", "minTokensOption", //$NON-NLS-1$ //$NON-NLS-2$
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the storage.
	 * 
	 * @return the storage
	 */
	public final LuceneStorage getStorage() {
		// PRECONDITIONS
		
		try {
			return this.storage;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.storage, "Field '%s' in '%s'.", "storage", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#init()
	 */
	@Override
	public void init() {
		// PRECONDITIONS
		Condition.notNull(this.minTokensOption, "Field '%s' in '%s'.", "minTokensOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		Condition.notNull(this.languageOption, "Field '%s' in '%s'.", "languageOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			setMinTokensArgument(getSettings().getArgument(getMinTokensOption()));
			Condition.notNull(this.minTokensArgument, "Field '%s' in '%s'.", "minTokensArgument", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			setMinTokens(getMinTokensArgument().getValue());
			
			setLanguageArgument(getSettings().getArgument(getLanguageOption()));
			Condition.notNull(this.languageArgument, "Field '%s' in '%s'.", "languageArgument", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			setLanguage(getLanguageArgument().getValue());
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.language, "Field '%s' in '%s'.", "language", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.minTokens, "Field '%s' in '%s'.", "minTokens", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#provide(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public ArgumentSet<?, ?> provide(final ArgumentSet<?, ?> root) throws net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException,
	                                                              ArgumentSetRegistrationException,
	                                                              SettingsParseError {
		// PRECONDITIONS
		setSettings(root.getSettings());
		Condition.notNull(getSettings(), "Field '%s' in '%s'.", "settings", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		// request the mapping.engines anchor
		final ArgumentSet<?, ?> anchor = super.getAnchor(getSettings());
		
		try {
			
			setMinTokensOption(new LongArgument.Options(anchor, "minTokens", //$NON-NLS-1$
			                                            Messages.getString("SearchEngine.minTokensDescription"), 3l, //$NON-NLS-1$
			                                            Requirement.contains(getOptions(getSettings()),
			                                                                 getClass().getSimpleName())));
			setLanguageOption(new StringArgument.Options(
			                                             anchor,
			                                             "language", Messages.getString("SearchEngine.languageDescription"), //$NON-NLS-1$ //$NON-NLS-2$
			                                             "en:English", Requirement.contains(getOptions(getSettings()), getClass().getSimpleName()))); //$NON-NLS-1$
			
			return anchor;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(getSettings(), "Field '%s' in '%s'.", "settings", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.minTokensOption, "Field '%s' in '%s'.", "minTokensOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.languageOption, "Field '%s' in '%s'.", "languageOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(anchor, "Field '%s' in '%s'.", "anchor", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#provideStorage
	 * (de.unisaarland.cs.st.moskito.mapping.storages.MappingStorage)
	 */
	@Override
	public void provideStorage(final MappingStorage storage) {
		super.provideStorage(storage);
		this.storage = getStorage(LuceneStorage.class);
		
		if (storage != null) {
			final String value = getLanguage();
			final String[] split = value.split(":"); //$NON-NLS-1$
			Class<?> clazz = null;
			Constructor<?> constructor = null;
			final String className = "org.apache.lucene.analysis." + split[0] + "." + split[1] + "Analyzer"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			try {
				if (this.storage.getAnalyzer() == null) {
					clazz = Class.forName(className);
					constructor = clazz.getConstructor(Version.class);
					final Analyzer newInstance = (Analyzer) constructor.newInstance(Version.LUCENE_31);
					this.storage.setAnalyzer(newInstance);
				}
			} catch (final ClassNotFoundException e) {
				throw new ClassLoadingError(e, className);
			} catch (final SecurityException e) {
				throw new UnrecoverableError(e);
			} catch (final NoSuchMethodException e) {
				throw new NoSuchConstructorError(e, Version.class);
			} catch (final IllegalArgumentException e) {
				throw new UnrecoverableError(e);
			} catch (final InstantiationException e) {
				throw new net.ownhero.dev.andama.exceptions.InstantiationError(e, clazz, constructor, Version.LUCENE_31);
			} catch (final IllegalAccessException e) {
				throw new UnrecoverableError(e);
			} catch (final InvocationTargetException e) {
				throw new UnrecoverableError(e);
			}
		}
	}
	
	/**
	 * Sets the language.
	 * 
	 * @param language
	 *            the language to set
	 */
	private final void setLanguage(final String language) {
		// PRECONDITIONS
		Condition.notNull(language, "Argument '%s' in '%s'.", "language", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.language = language;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.language, language,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Sets the language argument.
	 * 
	 * @param languageArgument
	 *            the languageArgument to set
	 */
	private final void setLanguageArgument(final StringArgument languageArgument) {
		// PRECONDITIONS
		Condition.notNull(languageArgument, "Argument '%s' in '%s'.", "languageArgument", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.languageArgument = languageArgument;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.languageArgument, languageArgument,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Sets the language option.
	 * 
	 * @param languageOption
	 *            the languageOption to set
	 */
	private final void setLanguageOption(final StringArgument.Options languageOption) {
		// PRECONDITIONS
		Condition.notNull(languageOption, "Argument '%s' in '%s'.", "languageOption", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.languageOption = languageOption;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.languageOption, languageOption,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Sets the min tokens.
	 * 
	 * @param minTokens
	 *            the minTokens to set
	 */
	private final void setMinTokens(final Long minTokens) {
		// PRECONDITIONS
		Condition.notNull(minTokens, "Argument '%s' in '%s'.", "minTokens", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.minTokens = minTokens;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.minTokens, minTokens,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Sets the min tokens argument.
	 * 
	 * @param minTokensArgument
	 *            the minTokensArgument to set
	 */
	private final void setMinTokensArgument(final LongArgument minTokensArgument) {
		// PRECONDITIONS
		Condition.notNull(minTokensArgument, "Argument '%s' in '%s'.", "minTokensArgument", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.minTokensArgument = minTokensArgument;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.minTokensArgument, minTokensArgument,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Sets the min tokens option.
	 * 
	 * @param options
	 *            the minTokensOption to set
	 */
	private final void setMinTokensOption(final net.ownhero.dev.hiari.settings.LongArgument.Options options) {
		// PRECONDITIONS
		Condition.notNull(options, "Argument '%s' in '%s'.", "minTokensOption", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.minTokensOption = options;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.minTokensOption, options,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine# storageDependency()
	 */
	@Override
	public Set<Class<? extends MappingStorage>> storageDependency() {
		return new HashSet<Class<? extends MappingStorage>>() {
			
			private static final long serialVersionUID = 8360079171674014391L;
			
			{
				add(LuceneStorage.class);
			}
		};
	}
}
