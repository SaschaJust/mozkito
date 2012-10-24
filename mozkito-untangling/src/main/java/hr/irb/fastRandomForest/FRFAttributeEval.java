package hr.irb.fastRandomForest;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.AttributeEvaluator;
import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Instances;
import weka.core.RevisionUtils;

/**
 * Evaluate the merit of each attribute using a random forest.
 * 
 * @author Santi Villalba
 * @version $Id: FRFAttributeEval.java 49 2010-10-05 14:05:11Z vinaysethmohta $
 */
public class FRFAttributeEval extends ASEvaluation implements AttributeEvaluator {
	
	private static final long serialVersionUID = -4504270948574160991L;
	
	/** The feature importances. */
	private double[]          m_Importances;
	
	/** The prototype for the rf. */
	private FastRandomForest  m_frfProto       = new FastRandomForest();
	
	/** Constructor */
	public FRFAttributeEval() {
	}
	
	/**
	 * Constructor.
	 * 
	 * @param frfProto
	 *            the prototype for the random forest.
	 */
	public FRFAttributeEval(final FastRandomForest frfProto) {
		this.m_frfProto = frfProto;
	}
	
	/** {@inheritDoc} */
	@Override
	public void buildEvaluator(final Instances data) throws Exception {
		final FastRandomForest forest = (FastRandomForest) AbstractClassifier.makeCopy(this.m_frfProto);
		forest.buildClassifier(data);
		this.m_Importances = forest.getFeatureImportances();
	}
	
	/** {@inheritDoc} */
	@Override
	public double evaluateAttribute(final int attribute) throws Exception {
		return this.m_Importances[attribute];
	}
	
	@Override
	public Capabilities getCapabilities() {
		return this.m_frfProto.getCapabilities();
	}
	
	/** @return the prototype for the random forest */
	public FastRandomForest getFrfProto() {
		return this.m_frfProto;
	}
	
	@Override
	public String getRevision() {
		return RevisionUtils.extract("$Id: FRFAttributeEval.java 49 2010-10-05 14:05:11Z vinaysethmohta $");
	}
	
	/**
	 * @param frfProto
	 *            the prototype for the random forest
	 */
	public void setFrfProto(final FastRandomForest frfProto) {
		this.m_frfProto = frfProto;
	}
}
