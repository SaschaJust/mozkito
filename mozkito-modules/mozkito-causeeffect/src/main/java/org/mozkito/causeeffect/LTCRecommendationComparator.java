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
package org.mozkito.causeeffect;

import java.util.Comparator;

import org.mozkito.causeeffect.LTCRecommendation.ChangeProperty;

/**
 * The Class LTCRecommendationComparator.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class LTCRecommendationComparator implements Comparator<LTCRecommendation> {
	
	/** The property. */
	private final ChangeProperty property;
	
	/**
	 * Instantiates a new lTC recommendation comparator.
	 * 
	 * @param property
	 *            the property
	 */
	public LTCRecommendationComparator(final ChangeProperty property) {
		this.property = property;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final LTCRecommendation o1,
	                   final LTCRecommendation o2) {
		if (o1.getConfidence(this.property) > o2.getConfidence(this.property)) {
			return -1;
		} else if (o1.getConfidence(this.property) < o2.getConfidence(this.property)) {
			return 1;
		} else {
			if (o1.getSupport(this.property) > o2.getSupport(this.property)) {
				return -1;
			} else if (o1.getSupport(this.property) < o2.getSupport(this.property)) {
				return 1;
			}
			return 0;
		}
	}
	
}
