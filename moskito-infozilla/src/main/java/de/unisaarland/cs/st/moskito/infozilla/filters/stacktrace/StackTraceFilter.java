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
/**
 * 
 */
package de.unisaarland.cs.st.moskito.infozilla.filters.stacktrace;

import java.util.List;

import net.ownhero.dev.hiari.settings.DynamicArgumentSet;
import net.ownhero.dev.hiari.settings.registerable.ArgumentRegistrationException;
import de.unisaarland.cs.st.moskito.infozilla.filters.InfozillaFilter;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class StackTraceFilter extends InfozillaFilter {
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#afterParse()
	 */
	@Override
	public void afterParse() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.infozilla.filters.InfozillaFilter#getOutputText()
	 */
	@Override
	public String getOutputText() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#initSettings(net.ownhero.dev.andama.settings.
	 * DynamicArgumentSet)
	 */
	@Override
	public boolean initSettings(final DynamicArgumentSet<Boolean> set) throws ArgumentRegistrationException {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.infozilla.filters.InfozillaFilter#runFilter(java .lang.String)
	 */
	@Override
	public List<?> runFilter(final String inputText) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
