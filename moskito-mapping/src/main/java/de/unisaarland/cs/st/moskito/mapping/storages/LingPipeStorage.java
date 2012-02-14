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
package de.unisaarland.cs.st.moskito.mapping.storages;

import net.ownhero.dev.andama.settings.DynamicArgumentSet;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class LingPipeStorage extends MappingStorage {
	
	/**
	 * 
	 */
	public LingPipeStorage() {
	}
	
	@Override
	public void afterParse() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.register.Registered#getDescription ()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#initSettings(net.ownhero.dev.andama.settings.
	 * DynamicArgumentSet)
	 */
	@Override
	public boolean initSettings(final DynamicArgumentSet<Boolean> set) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
