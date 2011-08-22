/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.mappable;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;

/**
 * @author just
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
	
	@Override
	public String getBodyText() {
		return report.getDescription();
	}
}
