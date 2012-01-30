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
package de.unisaarland.cs.st.moskito.bugs.tracker.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

import net.ownhero.dev.kanuni.annotations.simple.Positive;
import de.unisaarland.cs.st.moskito.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Embeddable
public class CommentPrimaryKey implements Annotated, Serializable {
	
	private long              reportId         = -1l;
	
	private int               commentId        = -1;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2568891675198367976L;
	
	/**
	 * used by PersistenceUtil
	 */
	protected CommentPrimaryKey() {
	}
	
	/**
	 * @param reportId
	 * @param commentId
	 */
	CommentPrimaryKey(final long reportId, @Positive final int commentId) {
		setReportId(reportId);
		setCommentId(commentId);
	}
	
	/**
	 * @return the commentId
	 */
	protected int getCommentId() {
		return this.commentId;
	}
	
	/**
	 * @return the reportId
	 */
	protected long getReportId() {
		return this.reportId;
	}
	
	/**
	 * @param commentId
	 *            the commentId to set
	 */
	protected void setCommentId(final int commentId) {
		this.commentId = commentId;
	}
	
	/**
	 * @param reportId
	 *            the reportId to set
	 */
	protected void setReportId(final long reportId) {
		this.reportId = reportId;
	}
	
}
