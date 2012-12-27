package org.mozkito.issues.analysis.filedchanges;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.OutputFileArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.Tuple;

import org.mozkito.issues.analysis.IssuesAnalysis;
import org.mozkito.issues.tracker.model.History;
import org.mozkito.issues.tracker.model.HistoryElement;
import org.mozkito.issues.tracker.model.Report;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;

/**
 * Class to analyse how often field of issue reports get changed. This analysis was used to for the RSSE book.
 */
public class IssuesFieldChangeAnalysis implements IssuesAnalysis {
	
	private OutputFileArgument fieldFileArgument;
	private OutputFileArgument typeFileArgument;
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.issues.analysis.IssuesAnalysis#performAnalysis()
	 */
	@Override
	public void performAnalysis(final PersistenceUtil persistenceUtil) {
		
		final Map<String, Set<String>> fieldChanges = new HashMap<>();
		final Map<Tuple<?, ?>, Set<String>> typeChangesByType = new HashMap<>();
		
		final Criteria<Report> loadCriteria = persistenceUtil.createCriteria(Report.class);
		final List<Report> allReports = persistenceUtil.load(loadCriteria);
		for (final Report report : allReports) {
			final History reportHistory = report.getHistory();
			
			for (final HistoryElement reportHistoryElement : reportHistory.getElements()) {
				if (reportHistoryElement.contains("type")) {
					final Tuple<?, ?> tuple = reportHistoryElement.get("type");
					if (!typeChangesByType.containsKey(tuple)) {
						typeChangesByType.put(tuple, new HashSet<String>());
					}
					typeChangesByType.get(tuple).add(report.getId());
				}
				for (final String changedField : reportHistoryElement.getFields()) {
					if (!fieldChanges.containsKey(changedField)) {
						fieldChanges.put(changedField, new HashSet<String>());
					}
					fieldChanges.get(changedField).add(report.getId());
				}
			}
		}
		
		try {
			try (final BufferedWriter writer = new BufferedWriter(new FileWriter(this.fieldFileArgument.getValue()));) {
				StringBuilder sb = new StringBuilder();
				sb.append("FIELD,REPORT");
				sb.append(FileUtils.lineSeparator);
				writer.append(sb.toString());
				for (final Entry<String, Set<String>> entry : fieldChanges.entrySet()) {
					for (final String reportId : entry.getValue()) {
						sb = new StringBuilder();
						sb.append(entry.getKey());
						sb.append(",");
						sb.append(reportId);
						sb.append(FileUtils.lineSeparator);
						writer.write(sb.toString());
					}
				}
			}
			try (final BufferedWriter writer = new BufferedWriter(new FileWriter(this.typeFileArgument.getValue()));) {
				StringBuilder sb = new StringBuilder();
				sb.append("TYPE_CHANGE,REPORT");
				sb.append(FileUtils.lineSeparator);
				writer.append(sb.toString());
				for (final Entry<Tuple<?, ?>, Set<String>> entry : typeChangesByType.entrySet()) {
					for (final String reportId : entry.getValue()) {
						sb = new StringBuilder();
						sb.append(entry.getKey().getFirst());
						sb.append("->");
						sb.append(entry.getKey().getSecond());
						sb.append(",");
						sb.append(reportId);
						sb.append(FileUtils.lineSeparator);
						writer.write(sb.toString());
					}
				}
			}
		} catch (final IOException e) {
			throw new UnrecoverableError(e);
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.issues.analysis.IssuesAnalysis#setup(net.ownhero.dev.hiari.settings.Settings)
	 */
	@Override
	public void setup(final Settings settings) {
		final OutputFileArgument.Options fieldChangesFileOptions = new OutputFileArgument.Options(
		                                                                                          settings.getRoot(),
		                                                                                          "fieldChangesOut",
		                                                                                          "CSV file that to be created that will contain the field changes per report",
		                                                                                          null,
		                                                                                          Requirement.required,
		                                                                                          true);
		final OutputFileArgument.Options typeChangesFileOptions = new OutputFileArgument.Options(
		                                                                                         settings.getRoot(),
		                                                                                         "typeChangesOut",
		                                                                                         "Output file that will contain the kind of type changes per bug report.",
		                                                                                         null,
		                                                                                         Requirement.required,
		                                                                                         true);
		try {
			this.fieldFileArgument = ArgumentFactory.create(fieldChangesFileOptions);
			this.typeFileArgument = ArgumentFactory.create(typeChangesFileOptions);
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			new Shutdown(e.getMessage());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.issues.analysis.IssuesAnalysis#tearDown()
	 */
	@Override
	public void tearDown() {
		// nothing to do
	}
	
}
