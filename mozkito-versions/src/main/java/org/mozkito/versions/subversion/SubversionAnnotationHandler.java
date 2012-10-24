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
package org.mozkito.versions.subversion;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.mozkito.versions.elements.AnnotationEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNAnnotateHandler;


/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SubversionAnnotationHandler implements ISVNAnnotateHandler {
	
	private final List<AnnotationEntry> list = new LinkedList<AnnotationEntry>();
	
	/**
	 * @return the resulting list of {@link AnnotationEntry}s
	 */
	public List<AnnotationEntry> getResults() {
		return this.list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNAnnotateHandler#handleEOF()
	 */
	@Override
	public void handleEOF() {
		// nothing to do here
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNAnnotateHandler#handleLine(java.util.Date, long, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void handleLine(Date date,
	                       long revision,
	                       String author,
	                       String line) throws SVNException {
		this.list.add(new AnnotationEntry(revision + "", author, new DateTime(date), line));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNAnnotateHandler#handleLine(java.util.Date, long, java.lang.String,
	 * java.lang.String, java.util.Date, long, java.lang.String, java.lang.String, int)
	 */
	@Override
	public void handleLine(Date date,
	                       long revision,
	                       String author,
	                       String line,
	                       Date mergedDate,
	                       long mergedRevision,
	                       String mergedAuthor,
	                       String mergedPath,
	                       int lineNumber) throws SVNException {
		if (revision > mergedRevision) {
			this.list.add(new AnnotationEntry(revision + "", author, new DateTime(date), line));
		} else {
			this.list.add(new AnnotationEntry(mergedRevision + "", mergedAuthor, new DateTime(mergedDate), line,
			                                  mergedPath));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tmatesoft.svn.core.wc.ISVNAnnotateHandler#handleRevision(java.util .Date, long, java.lang.String,
	 * java.io.File)
	 */
	@Override
	public boolean handleRevision(Date date,
	                              long revision,
	                              String author,
	                              File contents) throws SVNException {
		return false;
	}
}
