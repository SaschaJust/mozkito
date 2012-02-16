/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package de.unisaarland.cs.st.moskito.bugs.tracker.sourceforge;

import java.util.Set;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Severity;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.AttachmentEntry;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.persistence.model.Person;


/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class SourceForgeParser implements Parser {
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getAssignedTo()
	 */
	@Override
	public Person getAssignedTo() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getAttachment(int)
	 */
	@Override
	public AttachmentEntry getAttachment(int index) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getCategory()
	 */
	@Override
	public String getCategory() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getComment(int)
	 */
	@Override
	public Comment getComment(int index) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getComments()
	 */
	@Override
	public Set<Comment> getComments() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getComponent()
	 */
	@Override
	public String getComponent() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getCreationTimestamp()
	 */
	@Override
	public DateTime getCreationTimestamp() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getHistoryElement(int)
	 */
	@Override
	public HistoryElement getHistoryElement(int index) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getHistoryLength()
	 */
	@Override
	public int getHistoryLength() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return 0;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getId()
	 */
	@Override
	public Long getId() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getNumberOfAttachments()
	 */
	@Override
	public int getNumberOfAttachments() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return 0;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getNumberOfComments()
	 */
	@Override
	public int getNumberOfComments() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return 0;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getPriority()
	 */
	@Override
	public Priority getPriority() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getProduct()
	 */
	@Override
	public String getProduct() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getResolution()
	 */
	@Override
	public Resolution getResolution() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getResolutionTimestamp()
	 */
	@Override
	public DateTime getResolutionTimestamp() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getResolver()
	 */
	@Override
	public Person getResolver() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSeverity()
	 */
	@Override
	public Severity getSeverity() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSiblings()
	 */
	@Override
	public Set<Long> getSiblings() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getStatus()
	 */
	@Override
	public Status getStatus() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSubject()
	 */
	@Override
	public String getSubject() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSubmitter()
	 */
	@Override
	public Person getSubmitter() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSummary()
	 */
	@Override
	public String getSummary() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getType()
	 */
	@Override
	public Type getType() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getVersion()
	 */
	@Override
	public String getVersion() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setTracker(de.unisaarland.cs.st.moskito.bugs.tracker.Tracker)
	 */
	@Override
	public void setTracker(Tracker tracker) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS	
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setXMLReport(de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport)
	 */
	@Override
	public void setXMLReport(XmlReport report) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS	
		}
	}
	
}
