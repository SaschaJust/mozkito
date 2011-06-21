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
package de.unisaarland.cs.st.reposuite.clustering;


public class BaseCluster<T> extends Cluster<T> {
	
	public BaseCluster(final T t1, final T t2, final double score) {
		super(new VirtualCluster<T>(t1), new VirtualCluster<T>(t2), score);
	}
	
}
