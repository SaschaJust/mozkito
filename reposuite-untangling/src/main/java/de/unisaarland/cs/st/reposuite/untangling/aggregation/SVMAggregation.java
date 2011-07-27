package de.unisaarland.cs.st.reposuite.untangling.aggregation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import weka.classifiers.functions.LibSVM;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClustering;
import de.unisaarland.cs.st.reposuite.untangling.Untangling;
import de.unisaarland.cs.st.reposuite.untangling.blob.AtomicTransaction;


public class SVMAggregation extends UntanglingScoreAggregation {
	
	private static final double  TRAIN_FRACTION      = .5;
	private boolean              trained             = false;
	private final Untangling    untangling;
	private ArrayList<Attribute> attributes          = new ArrayList<Attribute>();
	private final Attribute      confidenceAttribute = new Attribute("confidence");
	private LibSVM               model               = new LibSVM();
	private Instances            trainingInstances;
	
	public SVMAggregation(final Untangling untangling) {
		super();
		this.untangling = untangling;
		if (System.getProperty("svmModel") != null) {
			File serialFile = new File(System.getProperty("svmModel"));
			if (serialFile.exists()) {
				try {
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(serialFile));
					this.model = (LibSVM) in.readObject();
					this.trained = true;
				} catch (FileNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				} catch (IOException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				} catch (ClassNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				}
				
			}
		}
	}
	
	@Override
	public double aggregate(final List<Double> values) {
		Condition.check(trained, "You must train a model before using it,");
		Condition.check((values.size() + 1) == attributes.size(),
				"The given set of values differ from the trained attribute's dimension");
		
		Instance instance = new DenseInstance(values.size() + 1);
		for (int j = 0; j < values.size(); ++j) {
			instance.setValue(attributes.get(j), values.get(j));
		}
		
		instance.setDataset(trainingInstances);
		try {
			return model.distributionForInstance(instance)[0];
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return MultilevelClustering.IGNORE_SCORE;
		}
	}
	
	@Override
	public String getInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Type: " + SVMAggregation.class.getSimpleName());
		sb.append(FileUtils.lineSeparator);
		sb.append(model.toString());
		return sb.toString();
	}
	
	public boolean train(final Set<AtomicTransaction> transactionSet) {
		
		if (trained) {
			return true;
		}
		
		Condition
		.check(!transactionSet.isEmpty(), "The transactionSet to train linear regression on must be not empty");
		
		Map<SampleType, List<List<Double>>> samples = super.getSamples(transactionSet, TRAIN_FRACTION, untangling);
		List<List<Double>> trainValues = new LinkedList<List<Double>>();
		for (List<Double> value : samples.get(SampleType.POSITIVE)) {
			List<Double> valueCopy = new ArrayList<Double>(value);
			valueCopy.add(1d);
			trainValues.add(valueCopy);
		}
		for (List<Double> value : samples.get(SampleType.NEGATIVE)) {
			List<Double> valueCopy = new ArrayList<Double>(value);
			valueCopy.add(0d);
			trainValues.add(valueCopy);
		}
		
		List<String> scoreVisitorNames = untangling.getScoreVisitorNames();
		attributes = new ArrayList<Attribute>(scoreVisitorNames.size());
		
		// Declare attributes
		for (String scoreVisitorName : scoreVisitorNames) {
			attributes.add(new Attribute(scoreVisitorName));
		}
		attributes.add(confidenceAttribute);
		
		//create an empty training set
		trainingInstances = new Instances("TrainingSet", attributes, trainValues.size());
		trainingInstances.setClassIndex(attributes.size() - 1);
		
		//set the training values within the weka training set
		for (int i = 0; i < trainValues.size(); ++i) {
			List<Double> instanceValues = trainValues.get(i);
			
			Condition.check(
					instanceValues.size() == attributes.size(),
					"InstanceValues and attributes must have equal dimensions: dum(instanceValues)="
							+ instanceValues.size() + ", dim(attributes)=" + attributes.size());
			
			Instance instance = new DenseInstance(instanceValues.size());
			for (int j = 0; j < instanceValues.size(); ++j) {
				instance.setValue(attributes.get(j), instanceValues.get(j));
			}
			// add the instance
			trainingInstances.add(instance);
		}
		try {
			model.buildClassifier(trainingInstances);
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		}
		
		//TODO optimize the parameters of libSVM
		//		GridSearch gs = new GridSearch();
		//		gs.setClassifier(model);
		//		gs.setXProperty(value)
		
		//serialize model for later use
		File serialFile = new File("svmModel.ser");
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serialFile));
			out.writeObject(model);
			out.close();
			if (Logger.logInfo()) {
				Logger.info("Wrote trained model to file: " + serialFile.getAbsolutePath());
			}
		} catch (FileNotFoundException e1) {
			if (Logger.logError()) {
				Logger.error(e1.getMessage(), e1);
			}
		} catch (IOException e1) {
			if (Logger.logError()) {
				Logger.error(e1.getMessage(), e1);
			}
		}
		
		trained = true;
		return trained;
		
	}
	
}
