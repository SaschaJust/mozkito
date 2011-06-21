/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.changecouplings;

import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain;

/**
 * The Class ChangeCouplings.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeCouplings extends RepoSuiteToolchain {
	
	public ChangeCouplings() {
		super(new RepositorySettings());
	}
	
	@Override
	public void setup() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
}
