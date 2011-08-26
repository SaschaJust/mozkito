/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.mapping.mappable;

import java.util.HashSet;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MappableReport extends MappableEntity {
	
	/**
     * 
     */
	private static final long serialVersionUID = 1097712059403322470L;
	private Report            report;
	
	public MappableReport(Report report) {
		this.setReport(report);
	}
	
	public Report getReport() {
		return report;
	}
	
	public void setReport(Report report) {
		this.report = report;
	}
	
	@Override
	public Class<?> getBaseType() {
		return Report.class;
	}
	
	public String getBody() {
		return report.getDescription();
	}
	
	@Override
	public Object get(FieldKey key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object get(FieldKey key, int index) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("serial")
	@Override
	public Set<FieldKey> supported() {
		// TODO complete this
		return new HashSet<FieldKey>() {
			
			{
				add(FieldKey.ID);
			}
		};
	}
	
	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}
}
