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

import org.mozkito.issues.model.Report;

/**
 * The Class ReportComparator.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ReportComparator implements Serializable, Comparator<Report> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 962491433315191888L;
	
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
	public int compare(final Report o1,
	                   final Report o2) {
		return o1.compareTo(o2);
	}
	
}
