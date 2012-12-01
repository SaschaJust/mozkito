/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
/**
 * 
 */
package org.mozkito.issues.tracker.model.comparators;

import java.util.Comparator;

import org.mozkito.issues.tracker.model.Comment;

/**
 * The Class CommentComparator.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CommentComparator implements Comparator<Comment> {
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final Comment o1,
	                   final Comment o2) {
		return o1.compareTo(o2);
	}
	
}
