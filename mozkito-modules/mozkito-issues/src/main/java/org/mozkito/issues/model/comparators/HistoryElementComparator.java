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
package org.mozkito.issues.model.comparators;

import java.io.Serializable;
import java.util.Comparator;

import org.mozkito.issues.model.HistoryElement;

/**
 * The Class HistoryElementComparator.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class HistoryElementComparator implements Serializable, Comparator<HistoryElement> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2988635832288340124L;
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final HistoryElement arg0,
	                   final HistoryElement arg1) {
		return arg0.compareTo(arg1);
	}
	
}
