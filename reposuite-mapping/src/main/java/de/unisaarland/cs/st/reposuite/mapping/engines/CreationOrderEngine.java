package de.unisaarland.cs.st.reposuite.mapping.engines;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.requirements.And;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Atom;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Expression;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Index;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.settings.DoubleArgument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class CreationOrderEngine extends MappingEngine {
	
	private double scoreReportCreatedAfterTransaction = -1d;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Scores negative if the report was created after the transaction was committed.";
	}
	
	/**
	 * @return the scoreReportCreatedAfterTransaction
	 */
	public double getScoreReportCreatedAfterTransaction() {
		return this.scoreReportCreatedAfterTransaction;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		setScoreReportCreatedAfterTransaction((Double) getSettings().getSetting(
		        "mapping.score.ReportCreatedAfterTransaction").getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#register
	 * (de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments,
	 * boolean)
	 */
	@Override
	public void register(final MappingSettings settings, final MappingArguments arguments, final boolean isRequired) {
		super.register(settings, arguments, isRequired);
		arguments.addArgument(new DoubleArgument(settings, "mapping.score.ReportCreatedAfterTransaction",
		        "Score in case the report was created after the transaction.", "-1", isRequired));
	}
	
	/**
	 * @param scoreReportCreatedAfterTransaction
	 *            the scoreReportCreatedAfterTransaction to set
	 */
	public void setScoreReportCreatedAfterTransaction(final double scoreReportCreatedAfterTransaction) {
		this.scoreReportCreatedAfterTransaction = scoreReportCreatedAfterTransaction;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new Atom(Index.ONE, FieldKey.CREATION_TIMESTAMP), new Atom(Index.OTHER,
		        FieldKey.CREATION_TIMESTAMP));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.reposuite.mapping.model.MapScore)
	 */
	@Override
	public void score(MappableEntity from, MappableEntity to, MapScore score) {
		if (((DateTime) from.get(FieldKey.CREATION_TIMESTAMP))
		        .isBefore(((DateTime) to.get(FieldKey.CREATION_TIMESTAMP)))) {
			score.addFeature(getScoreReportCreatedAfterTransaction(), FieldKey.CREATION_TIMESTAMP.name(),
			        ((DateTime) from.get(FieldKey.CREATION_TIMESTAMP)).toString(),
			        ((DateTime) from.get(FieldKey.CREATION_TIMESTAMP)).toString(), FieldKey.CREATION_TIMESTAMP.name(),
			        ((DateTime) to.get(FieldKey.CREATION_TIMESTAMP)).toString(),
			        ((DateTime) to.get(FieldKey.CREATION_TIMESTAMP)).toString(), this.getClass());
		}
	}
	
}
