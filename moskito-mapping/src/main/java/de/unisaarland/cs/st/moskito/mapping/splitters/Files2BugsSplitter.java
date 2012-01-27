/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
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
package de.unisaarland.cs.st.moskito.mapping.splitters;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.model.File2Bugs;
import de.unisaarland.cs.st.moskito.persistence.Annotated;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Files2BugsSplitter extends MappingSplitter {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.splitters.MappingSplitter#
	 * getDescription()
	 */
	@Override
	public String getDescription() {
		return "Creates a table that puts files into relation to their bugs.";
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.splitters.MappingSplitter#process ()
	 */
	@Override
	public List<Annotated> process(final PersistenceUtil util) {
		final List<Annotated> ret = new LinkedList<Annotated>();
		
		@SuppressWarnings ("unchecked")
		final List<Object[]> result = util.executeNativeSelectQuery(manager.getNativeQuery(util, "files2bugs"));
		Criteria<RCSFile> fileCriteria;
		Criteria<Report> reportCriteria;
		long fileid = -1, tmp = -1, bugid = -1;
		RCSFile file = null;
		final Set<Report> reports = new HashSet<Report>();
		
		for (final Object[] entries : result) {
			tmp = (Long) entries[0];
			bugid = (Long) entries[1];
			
			if (tmp != fileid) {
				if (!reports.isEmpty()) {
					ret.add(new File2Bugs(file, reports));
					reports.clear();
				}
				
				fileid = tmp;
				fileCriteria = util.createCriteria(RCSFile.class).eq("generatedId", fileid);
				file = util.load(fileCriteria).iterator().next();
			}
			
			reportCriteria = util.createCriteria(Report.class).eq("id", bugid);
			reports.addAll(util.load(reportCriteria));
		}
		
		if (!reports.isEmpty()) {
			ret.add(new File2Bugs(file, reports));
			reports.clear();
		}
		
		return ret;
	}
	
}
