package de.unisaarland.cs.st.reposuite.mapping.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.IdClass;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.mapping.elements.MapId;
import de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@IdClass(MapId.class)
public class MapScore implements Annotated, Comparable<MapScore> {
	
	private static final long  serialVersionUID = -8606759070008468513L;
	
	List<MappingEngineFeature> features         = new LinkedList<MappingEngineFeature>();
	double                     totalConfidence  = 0.0d;
	private MappableEntity     element1;
	
	private MappableEntity     element2;
	
	private String             id1;
	private String             class1;
	
	private String             id2;
	private String             class2;
	
	public String getId1() {
		return id1;
	}
	
	public void setId1(String id1) {
		this.id1 = id1;
	}
	
	public String getClass1() {
		return class1;
	}
	
	public void setClass1(String class1) {
		this.class1 = class1;
	}
	
	public String getId2() {
		return id2;
	}
	
	public void setId2(String id2) {
		this.id2 = id2;
	}
	
	public String getClass2() {
		return class2;
	}
	
	public void setClass2(String class2) {
		this.class2 = class2;
	}
	
	@Transient
	public MappableEntity getElement1() {
		if (element1 != null) {
			return element1;
		} else {
			try {
				Class<?> clazz = Class.forName(getClass1());
				return (MappableEntity) PersistenceManager.getUtil().loadById(getId1(), clazz);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UninitializedDatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public void setElement1(MappableEntity element1) {
		this.element1 = element1;
		setClass1(element1.getClass().getCanonicalName());
		setId1(fetchId(element1));
	}
	
	@Transient
	public MappableEntity getElement2() {
		if (element2 != null) {
			return element2;
		} else {
			try {
				Class<?> clazz = Class.forName(getClass2());
				return (MappableEntity) PersistenceManager.getUtil().loadById(getId2(), clazz);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UninitializedDatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public void setElement2(MappableEntity element2) {
		this.element2 = element2;
		setClass2(element2.getClass().getCanonicalName());
		setId2(fetchId(element2));
	}
	
	private String fetchId(Object o) {
		try {
			Method method = o.getClass().getMethod("getId", new Class<?>[0]);
			return (String) method.invoke(o, new Object[0]);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return null;
	}
	
	/**
	 * @param transaction
	 * @param report
	 */
	public MapScore(final MappableEntity element1, final MappableEntity element2) {
		setElement1(element1);
		setElement2(element2);
	}
	
	/**
	 * @param confidence
	 * @param transactionFieldName
	 * @param relevantString
	 * @param mappingEngine
	 */
	@Transient
	public void addFeature(final double confidence, @NotNull @NotEmpty final String transactionFieldName,
	        @NotNull @NotEmpty final String transactionFieldContent,
	        @NotNull @NotEmpty final String transactionSubstring, @NotNull @NotEmpty final String reportFieldName,
	        @NotNull @NotEmpty final String reportFieldContent, @NotNull @NotEmpty final String reportSubstring,
	        @NotNull final Class<? extends MappingEngine> mappingEngine) {
		this.totalConfidence += confidence;
		this.features.add(new MappingEngineFeature(confidence, transactionFieldName, transactionSubstring,
		        reportFieldName, reportSubstring, mappingEngine));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final MapScore arg0) {
		return Double.compare(this.totalConfidence, arg0.totalConfidence);
	}
	
	/**
	 * @return the features
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	public List<MappingEngineFeature> getFeatures() {
		return this.features;
	}
	
	/**
	 * @return a set of {@link MappingEngine} classes that were used for this
	 *         scoring
	 */
	@Transient
	public Set<Class<? extends MappingEngine>> getScoringEngines() {
		HashSet<Class<? extends MappingEngine>> engines = new HashSet<Class<? extends MappingEngine>>();
		
		for (MappingEngineFeature feature : this.features) {
			engines.add(feature.getEngine());
		}
		
		return engines;
	}
	
	/**
	 * @return the totalConfidence
	 */
	@Basic
	public double getTotalConfidence() {
		return this.totalConfidence;
	}
	
	/**
	 * @param features
	 *            the features to set
	 */
	public void setFeatures(final List<MappingEngineFeature> features) {
		this.features = features;
	}
	
	/**
	 * @param totalConfidence
	 *            the totalConfidence to set
	 */
	public void setTotalConfidence(final double totalConfidence) {
		this.totalConfidence = totalConfidence;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MapScore [totalConfidence=");
		builder.append(getTotalConfidence());
		builder.append(", element1=");
		builder.append(getElement1());
		builder.append(", element2=");
		builder.append(getElement2());
		builder.append(", features=");
		builder.append(JavaUtils.collectionToString(getFeatures()));
		builder.append("]");
		return builder.toString();
	}
	
}
