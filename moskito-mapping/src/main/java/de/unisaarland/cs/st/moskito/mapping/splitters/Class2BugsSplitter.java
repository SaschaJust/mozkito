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
package de.unisaarland.cs.st.moskito.mapping.splitters;

import java.util.List;

import net.ownhero.dev.hiari.settings.DynamicArgumentSet;
import de.unisaarland.cs.st.moskito.persistence.Annotated;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class Class2BugsSplitter.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class Class2BugsSplitter extends MappingSplitter {
	
	/**
	 * After parse.
	 */
	@Override
	public void afterParse() {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.splitters.MappingSplitter#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Inits the settings.
	 *
	 * @param set the set
	 * @return true, if successful
	 * @throws ArgumentRegistrationException the argument registration exception
	 */
	@Override
	public boolean initSettings(final DynamicArgumentSet<Boolean> set) throws net.ownhero.dev.hiari.settings.registerable.ArgumentRegistrationException {
		// TODO Auto-generated method stub
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.splitters.MappingSplitter#process(de.unisaarland.cs.st.moskito.persistence.PersistenceUtil)
	 */
	@Override
	public List<Annotated> process(final PersistenceUtil util) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
