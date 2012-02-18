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
package net.ownhero.dev.andama.settings;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.MinLength;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kanuni.conditions.MapCondition;
import net.ownhero.dev.kanuni.conditions.StringCondition;
import net.ownhero.dev.kanuni.instrumentation.KanuniInstrumenter;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class DynamicArgumentSet<T> extends ArgumentSet<T> {
	
	private final String moduleName;
	private final String providerGroupName;
	private final String name;
	
	/**
	 * @param argumentSet
	 * @param description
	 * @param requirements
	 * @throws ArgumentRegistrationException
	 */
	@NoneNull
	DynamicArgumentSet(final ArgumentSet<?> argumentSet, final String name, final String description,
	        final Requirement requirements, @MinLength (min = 2) final String moduleName,
	        @MinLength (min = 2) final String providerGroupName) throws ArgumentRegistrationException {
		super(argumentSet, description, requirements);
		
		try {
			this.name = name;
			this.moduleName = moduleName.toLowerCase();
			this.providerGroupName = providerGroupName.toLowerCase();
		} finally {
			Condition.notNull(this.name, "Name field");
			Condition.notNull(this.moduleName, "Module name field");
			Condition.notNull(this.providerGroupName, "Providergroup name field");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ArgumentSet#addArgument(net.ownhero.dev.andama.settings.Argument)
	 */
	@Override
	protected boolean addArgument(@NotNull final Argument<?> argument) {
		if (KanuniInstrumenter.assertionsEnabled()) {
			Condition.notNull(argument.getName(), "Argument name in %s.addArgument(Argument<?>)", getHandle());
		}
		
		try {
			if (!getSettings().frozen()) {
				final String newName = getFQName(argument);
				
				if (Logger.logDebug()) {
					Logger.debug("Setting argument name from '" + argument.getName() + "' to '" + newName + "'.");
				}
				
				argument.setName(newName);
				
				if (getSettings().hasSetting(argument.getName())) {
					// TODO Warn log
					return false;
				}
				
				if (getArguments().containsKey(argument.getName())) {
					// TODO Warn log
					return false;
				}
				
				getArguments().put(argument.getName(), argument);
				
				// tell settings who is responsible for this artifact
				getSettings().addArgumentMapping(argument.getName(), this);
				
				return true;
			} else {
				// TODO Warn log
				return false;
			}
		} finally {
			if (KanuniInstrumenter.assertionsEnabled()) {
				if (!getSettings().frozen()) {
					Condition.notNull(argument.getName(), "Argument name in %s.addArgument(Argument<?>)", getHandle());
					MapCondition.containsKey(getArguments(), argument.getName(),
					                         "Argument name in %s.addArgument(Argument<?>)", getHandle());
				}
			}
		}
	}
	
	/**
	 * @param argument
	 * @return
	 */
	private final String getFQName(@NotNull final Argument<?> argument) {
		if (KanuniInstrumenter.assertionsEnabled()) {
			Condition.notNull(getModuleName(), "Module name in %s.getFQName()", getHandle());
			Condition.notNull(getProviderGroupName(), "Providergroup name in %s.getFQName()", getHandle());
			Condition.notNull(argument.getName(), "Argument name in %s.getFQName()", getHandle());
		}
		
		final StringBuilder builder = new StringBuilder();
		final Throwable t = new Throwable();
		
		try {
			t.fillInStackTrace();
			
			String caller = null;
			for (final StackTraceElement element : t.getStackTrace()) {
				if (element.getMethodName().equals("init")
				        && !element.getClassName().startsWith("net.ownhero.dev.andama")) {
					caller = element.getClassName().toLowerCase();
					if (caller.contains(getProviderGroupName())) {
						caller = caller.replace(getProviderGroupName(), "");
					}
					
					if (caller.contains(getModuleName())) {
						caller = caller.replace(getModuleName(), "");
					}
				}
			}
			
			builder.append(getModuleName());
			builder.append('.').append(getProviderGroupName());
			builder.append('.').append(caller);
			builder.append('.').append(argument.getName().toLowerCase());
			
			return builder.toString();
		} finally {
			if (KanuniInstrumenter.assertionsEnabled()) {
				Condition.notNull(getModuleName(), "Module name in %s.getFQName()", getHandle());
				Condition.notNull(getProviderGroupName(), "Providergroup name in %s.getFQName()", getHandle());
				Condition.notNull(argument.getName(), "Argument name in %s.getFQName()", getHandle());
				Condition.notNull(builder, "Buidler in %s.getFQName()", getHandle());
				StringCondition.minLength(builder.toString(), getModuleName().length()
				                                  + getProviderGroupName().length() + argument.getName().length() + 4,
				                          "Buidler in %s.getFQName()", getHandle());
			}
		}
	}
	
	/**
	 * @return the moduleName
	 */
	public final String getModuleName() {
		if (KanuniInstrumenter.assertionsEnabled()) {
			Condition.notNull(this.moduleName, "Module name in %s.getModuleName()", getHandle());
		}
		
		return this.moduleName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ArgumentSet#getName()
	 */
	@Override
	public String getName() {
		final StringBuilder builder = new StringBuilder();
		
		if (KanuniInstrumenter.assertionsEnabled()) {
			Condition.notNull(getModuleName(), "Module name in %s.getName()", getHandle());
			Condition.notNull(getProviderGroupName(), "Providergroup name in %s.getName()", getHandle());
			StringCondition.minLength(getModuleName(), 2, "Module name in %s.getName()", getHandle());
			StringCondition.minLength(getProviderGroupName(), 2, "Providergroup name in %s.getName()", getHandle());
		}
		
		try {
			builder.append(Character.toUpperCase(getModuleName().charAt(0)));
			builder.append(getModuleName().substring(1));
			builder.append(Character.toUpperCase(getProviderGroupName().charAt(0)));
			builder.append(getProviderGroupName().substring(1));
			builder.append(this.name);
			
			return builder.toString();
		} finally {
			if (KanuniInstrumenter.assertionsEnabled()) {
				Condition.notNull(getModuleName(), "Module name in %s.getName()", getHandle());
				Condition.notNull(getProviderGroupName(), "Providergroup name in %s.getName()", getHandle());
				StringCondition.minLength(getModuleName(), 2, "Module name in %s.getName()", getHandle());
				StringCondition.minLength(getProviderGroupName(), 2, "Providergroup name in %s.getName()", getHandle());
				Condition.notNull(builder, "Builder in %s.getName()", getHandle());
				StringCondition.length(builder.toString(), getModuleName().length() + getProviderGroupName().length()
				        + this.name.length(), "Builder in %s.getName()", getHandle());
			}
		}
	}
	
	/**
	 * @return the providerGroupName
	 */
	public final String getProviderGroupName() {
		if (KanuniInstrumenter.assertionsEnabled()) {
			Condition.notNull(this.providerGroupName, "Providergroup name in %s.getProviderGroupName()", getHandle());
		}
		
		return this.providerGroupName;
	}
	
}
