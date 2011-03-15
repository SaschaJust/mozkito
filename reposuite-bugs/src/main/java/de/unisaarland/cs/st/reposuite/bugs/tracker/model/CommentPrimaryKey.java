/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Embeddable;

import net.ownhero.dev.kanuni.annotations.simple.Positive;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;

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
	 * used by hibernate
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
	
	@Override
	public Collection<Annotated> saveFirst() {
		return null;
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
