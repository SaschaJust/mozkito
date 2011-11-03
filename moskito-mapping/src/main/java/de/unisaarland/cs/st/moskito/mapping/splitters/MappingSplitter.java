/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.mapping.splitters;

import java.util.List;

import de.unisaarland.cs.st.moskito.mapping.register.Registered;
import de.unisaarland.cs.st.moskito.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class MappingSplitter extends Registered {
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.register.Registered#getDescription
	 * ()
	 */
	@Override
	public abstract String getDescription();
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.register.Registered#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return isEnabled("mapping.splitters", this.getClass().getSimpleName());
	}
	
	public abstract List<Annotated> process();
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingSplitter [settings=");
		builder.append(getSettings());
		builder.append(", initialized=");
		builder.append(isInitialized());
		builder.append(", registered=");
		builder.append(isRegistered());
		builder.append("]");
		return builder.toString();
	}
	
}
