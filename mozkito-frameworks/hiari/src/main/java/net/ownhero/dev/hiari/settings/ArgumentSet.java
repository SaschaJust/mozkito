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
package net.ownhero.dev.hiari.settings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class ArgumentSet.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ArgumentSet<T, X extends ArgumentSetOptions<T, ? extends ArgumentSet<T, ?>>> implements IArgument<T, X> {
	
	/** The arguments. */
	private final HashMap<String, Argument<?, ? extends IOptions<?, IArgument<?, ?>>>>    arguments    = new HashMap<String, Argument<?, ? extends IOptions<?, IArgument<?, ?>>>>();
	/** The argument sets. */
	private final HashMap<String, ArgumentSet<?, ? extends IOptions<?, IArgument<?, ?>>>> argumentSets = new HashMap<String, ArgumentSet<?, ? extends IOptions<?, IArgument<?, ?>>>>();
	/** The name. */
	private final String                                                                  name;
	
	/** The description. */
	private final String                                                                  description;
	
	/** The requirements. */
	private final Requirement                                                             requirements;
	
	/** The settings. */
	private final ISettings                                                               settings;
	
	/** The cached value. */
	private T                                                                             cachedValue  = null;
	
	/** The argument set. */
	private final ArgumentSet<?, ?>                                                       argumentSet;
	
	private X                                                                             configurator;
	
	private boolean                                                                       initialized  = false;
	
	/**
	 * Instantiates a new argument set (for root argument set only).
	 * 
	 * @param settings
	 *            the settings
	 * @param description
	 *            the description
	 */
	@Deprecated
	ArgumentSet(final ISettings settings, final String name, final String description) {
		this.name = name;
		this.settings = settings;
		this.description = description;
		this.requirements = Requirement.required;
		this.argumentSet = null;
		this.configurator = null;
	}
	
	/**
	 * @throws ArgumentSetRegistrationException
	 * 
	 */
	@SuppressWarnings ("unchecked")
	ArgumentSet(final @NotNull X options) throws ArgumentSetRegistrationException {
		// PRECONDITIONS
		
		try {
			this.name = options.getName();
			this.description = options.getDescription();
			this.argumentSet = options.getArgumentSet();
			this.settings = options.getArgumentSet().getSettings();
			this.requirements = options.getRequirements();
			this.configurator = options;
			
			if (!this.argumentSet.addArgument((ArgumentSet<?, ? extends IOptions<?, IArgument<?, ?>>>) this)) {
				throw new ArgumentSetRegistrationException("Could not register argument set " + getHandle() + ".",
				                                           this, options);
			}
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.argumentSet, "Field '%s' in %s.", "argumentSet", getHandle());
			Condition.notNull(this.name, "Field '%s' in %s.", "name", getHandle());
			Condition.notNull(this.description, "Field '%s' in %s.", "description", getHandle());
			Condition.notNull(this.settings, "Field '%s' in %s.", "settings", getHandle());
			Condition.notNull(this.requirements, "Field '%s' in %s.", "requirements", getHandle());
			Condition.notNull(this.configurator, "Field '%s' in %s.", "configurator", getHandle());
		}
	}
	
	/**
	 * Call this method to add an argument to the set of arguments. But be aware that you have to set all arguments
	 * before adding it to the MinerSettings!
	 * 
	 * @param argument
	 *            MinerArgument to be added
	 * @return <code>true</code> if the argument could be added. <code>false</code> otherwise.
	 */
	boolean addArgument(@NotNull final Argument<?, ? extends IOptions<?, IArgument<?, ?>>> argument) {
		if (argument.getName().contains(".")) {
			if (Logger.logWarn()) {
				Logger.warn("Argument tags may never contain '.'. Cannot register argument: " + argument.getName()
				        + ":" + argument.getHandle());
			}
			// names may not contain the tag of the set
			return false;
		}
		
		if (this.arguments.containsKey(argument.getName())) {
			// TODO Warn log
			return false;
		}
		
		this.arguments.put(argument.getName(), argument);
		
		// tell settings who is responsible for this artifact
		if (!((Settings) getSettings()).addArgumentMapping(argument.getTag(), this)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Adds the argument.
	 * 
	 * @param argument
	 *            the argument
	 * @return true, if successful
	 */
	private final boolean addArgument(@NotNull final ArgumentSet<?, ? extends IOptions<?, IArgument<?, ?>>> argument) {
		if (getSettings().hasSetting(argument.getTag())) {
			// TODO Warn log
			return false;
		}
		
		if (this.argumentSets.containsKey(argument.getName())) {
			// TODO Warn log
			return false;
		}
		
		this.argumentSets.put(argument.getName(), argument);
		
		return true;
		
	}
	
	/**
	 * Compare to.
	 * 
	 * @param arg0
	 *            the arg0
	 * @return the int
	 */
	@Override
	public final int compareTo(final IArgument<?, ?> arg0) {
		if (this == arg0) {
			return 0;
		}
		
		final Set<IOptions<?, ?>> dependencies = getDependencies();
		
		if (dependencies.contains(arg0.getOptions())) {
			return 1;
		} else if (dependencies.contains(this)) {
			return 0;
		} else {
			int ret = -1;
			for (final IOptions<?, ?> argX : dependencies) {
				ret = argX.compareTo(arg0.getOptions());
				if (ret != 0) {
					return ret;
				}
			}
			return ret;
		}
	}
	
	/**
	 * Gets the argument.
	 * 
	 * @param name
	 *            the name
	 * @return the argument
	 */
	public final IArgument<?, ?> getArgument(final String tag) {
		for (final IArgument<?, ?> argument : getArguments().values()) {
			if (argument.getTag().equals(tag)) {
				return argument;
			}
		}
		return null;
	}
	
	/**
	 * Return the arguments held within the set.
	 * 
	 * @return the arguments
	 */
	private final Map<String, Argument<?, ? extends IOptions<?, IArgument<?, ?>>>> getArguments() {
		return this.arguments;
	}
	
	/**
	 * Gets the argument sets.
	 * 
	 * @return the argument sets
	 */
	Map<String, ArgumentSet<?, ? extends IOptions<?, IArgument<?, ?>>>> getArgumentSets() {
		return this.argumentSets;
	}
	
	/**
	 * Gets the cached value.
	 * 
	 * @return the cached value
	 */
	protected final T getCachedValue() {
		return this.cachedValue;
	}
	
	/**
	 * Gets the children.
	 * 
	 * @return the children
	 */
	public Collection<IArgument<?, ? extends IOptions<?, IArgument<?, ?>>>> getChildren() {
		final LinkedList<IArgument<?, ? extends IOptions<?, IArgument<?, ?>>>> list = new LinkedList<IArgument<?, ? extends IOptions<?, IArgument<?, ?>>>>();
		list.addAll(getArguments().values());
		list.addAll(getArgumentSets().values());
		return list;
	}
	
	/**
	 * Gets the dependencies.
	 * 
	 * @return the dependees
	 */
	@Override
	public final Set<IOptions<?, ?>> getDependencies() {
		return this.requirements.getDependencies();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getDescription()
	 */
	@Override
	public final String getDescription() {
		return this.description;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getHandle()
	 */
	@Override
	public final String getHandle() {
		return getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String getHelpString() {
		return getHelpString(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#toString(int)
	 */
	public String getHelpString(final int indentation) {
		int maxWidth = 0;
		for (final IArgument<?, ? extends IOptions<?, IArgument<?, ?>>> argument : getChildren()) {
			final Tuple<Integer, Integer> tuple = argument.getKeyValueSpan();
			if (tuple.getFirst() > maxWidth) {
				maxWidth = tuple.getFirst();
			}
		}
		
		return getHelpString(maxWidth, indentation);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getHelpString(int, int)
	 */
	@Override
	public String getHelpString(final int keyWidth,
	                            final int indentation) {
		// PRECONDITIONS
		
		try {
			final StringBuilder builder = new StringBuilder();
			
			final StringBuilder indent = new StringBuilder();
			final StringBuilder header = new StringBuilder();
			
			for (int i = 0, j = 0; i < indentation; ++i, ++j) {
				indent.append("| ");
				if ((j + 1) < indentation) {
					header.append("| ");
				}
			}
			
			builder.append(header).append("|-[ ").append(getName()).append(" ]").append(FileUtils.lineSeparator);
			builder.append(header).append("| `-Description: ").append(getDescription());
			builder.append(required()
			                         ? " (Required, due to " + getRequirements() + ")"
			                         : "");
			
			for (final IArgument<?, ? extends IOptions<?, IArgument<?, ?>>> argument : getChildren()) {
				builder.append(FileUtils.lineSeparator).append(indent)
				       .append(argument.getHelpString(keyWidth, indentation + 1));
			}
			
			return builder.toString();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getKeyValueSpan()
	 */
	@Override
	public Tuple<Integer, Integer> getKeyValueSpan() {
		final Tuple<Integer, Integer> tuple = new Tuple<Integer, Integer>(0, 0);
		
		for (final IArgument<?, ?> arg : getChildren()) {
			final Tuple<Integer, Integer> span = arg.getKeyValueSpan();
			
			if (span.getFirst() > tuple.getFirst()) {
				tuple.setFirst(span.getFirst());
			}
			
			if (span.getSecond() > tuple.getSecond()) {
				tuple.setSecond(span.getSecond());
			}
		}
		
		return new Tuple<Integer, Integer>(tuple.getFirst(), tuple.getSecond());
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getOptions()
	 */
	@Override
	public X getOptions() {
		// PRECONDITIONS
		
		try {
			return this.configurator;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getParent()
	 */
	@Override
	public ArgumentSet<?, ?> getParent() {
		// PRECONDITIONS
		
		try {
			return this.argumentSet;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getRequirements()
	 */
	@Override
	public Requirement getRequirements() {
		return this.requirements;
	}
	
	/**
	 * Gets the settings.
	 * 
	 * @return the settings
	 */
	@Override
	public final ISettings getSettings() {
		return this.settings;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.IArgument#getParent()
	 */
	@Override
	public String getTag() {
		final StringBuilder builder = new StringBuilder();
		final LinkedList<String> list = new LinkedList<String>();
		ArgumentSet<?, ?> parent = getParent();
		
		do {
			list.add(parent.getName());
		} while ((parent = parent.getParent()) != null);
		
		final Iterator<String> iterator = list.descendingIterator();
		
		while (iterator.hasNext()) {
			if (builder.length() > 0) {
				builder.append('.');
			}
			
			builder.append(iterator.next());
		}
		
		if (builder.length() > 0) {
			builder.append('.');
		}
		builder.append(getName());
		
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getValue()
	 */
	@Override
	public final T getValue() {
		return this.getCachedValue();
	}
	
	/**
	 * Inits the.
	 * 
	 * @return true, if successful
	 */
	protected boolean init() {
		// TODO
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#parse()
	 */
	@Override
	public void parse() throws SettingsParseError {
		LinkedList<IArgument<?, ?>> list = new LinkedList<IArgument<?, ?>>(this.arguments.values());
		list.addAll(this.argumentSets.values());
		LinkedList<IArgument<?, ?>> list2 = new LinkedList<IArgument<?, ?>>();
		
		while (!list.isEmpty()) {
			for (final IArgument<?, ?> argument : list) {
				argument.parse();
				
				if (argument.getRequirements().getRequiredDependencies() != null) {
					list2.add(argument);
				}
			}
			
			if (list2.isEmpty()) {
				break;
			} else if (list.size() == list2.size()) {
				for (final IArgument<?, ?> argument : list) {
					if (argument.required()) {
						throw new SettingsParseError(
						                             "Could not resolved dependencies. Arguments have unresolved dependencies: ",
						                             list2.iterator().next());
						
					} else {
						if (Logger.logWarn()) {
							Logger.warn("Skipping: " + argument);
						}
					}
				}
				break;
			} else {
				list = list2;
				list2 = new LinkedList<IArgument<?, ?>>();
			}
		}
		
		boolean initResult = false;
		initResult = init();
		
		if (!initResult) {
			throw new SettingsParseError("Could not initialize '" + getName()
			        + "'. Please see error earlier error messages, refer to the argument help information, "
			        + "or review the init() method of the corresponding " + getHandle() + ".", this);
		}
		
		this.initialized = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#required()
	 */
	@Override
	public boolean required() {
		return getRequirements().required();
	}
	
	/**
	 * Sets the cached value.
	 * 
	 * @param cachedValue
	 *            the new cached value
	 */
	protected final void setCachedValue(final T cachedValue) {
		this.initialized = true;
		this.cachedValue = cachedValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final Tuple<Integer, Integer> span = getKeyValueSpan();
		return toString(span.getFirst(), span.getSecond());
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#toString(int)
	 */
	@Override
	public String toString(final int keyWidth,
	                       final int valueWidth) {
		final StringBuilder builder = new StringBuilder();
		
		if (!this.initialized && (this instanceof Settings.RootArgumentSet)) {
			builder.append(getSettings().getHandle())
			       .append(" are uninitialized. You might wanna call help (-Dhelp) and display the helpString instead.")
			       .append(FileUtils.lineSeparator);
		}
		
		builder.append("[").append(getName()).append("] ");
		
		if (!this.initialized) {
			builder.append("  <## NOT INITIALIZED ##>  ");
		}
		
		builder.append(getDescription());
		
		for (final IArgument<?, ?> arg : getChildren()) {
			builder.append(FileUtils.lineSeparator).append(arg.toString(keyWidth, valueWidth));
		}
		
		return builder.toString();
	}
	
}
