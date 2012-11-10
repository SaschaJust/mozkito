/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.mappings.strategies;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.mappings.model.Composite;

/**
 * The Class DummyStrategy.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DummyStrategy extends Strategy {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends ArgumentSetOptions<DummyStrategy, ArgumentSet<DummyStrategy, Options>> {
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, DummyStrategy.TAG, DummyStrategy.DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public DummyStrategy init() {
			// PRECONDITIONS
			
			try {
				return new DummyStrategy();
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
		                                                                                    SettingsParseError {
			// PRECONDITIONS
			
			try {
				return new HashMap<String, IOptions<?, ?>>();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The Constant TAG. */
	private final static String TAG         = "dummy";                               //$NON-NLS-1$
	                                                                                  
	/** The Constant DESCRIPTION. */
	private final static String DESCRIPTION = "Dummy strategy for testing purposes."; //$NON-NLS-1$
	                                                                                  
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			return DummyStrategy.DESCRIPTION;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.strategies.Strategy#map(org.mozkito.mappings.model.Composite)
	 */
	@Override
	public Composite map(final Composite composite) {
		// PRECONDITIONS
		
		try {
			return composite.addStrategy(this, true);
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
