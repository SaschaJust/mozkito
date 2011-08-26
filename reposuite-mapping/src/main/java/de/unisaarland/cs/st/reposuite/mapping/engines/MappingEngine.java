package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.register.Registered;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Expression;
import de.unisaarland.cs.st.reposuite.mapping.storages.MappingStorage;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class MappingEngine extends Registered {
	
	public static final String unused          = "(unused)";
	public static final String unknown         = "(unknown)";
	public static final String defaultNegative = "-1";
	public static final String defaultPositive = "1";
	
	private final boolean      registered      = false;
	private final boolean      initialized     = false;
	
	/**
	 * @param score
	 * @param confidence
	 * @param fromFieldName
	 * @param fromFieldContent
	 * @param fromSubstring
	 * @param toFieldName
	 * @param toFieldContent
	 * @param toSubstring
	 */
	
	public void addFeature(@NotNull final MapScore score, final double confidence,
	        @NotNull @NotEmpty final String fromFieldName, final Object fromFieldContent, final Object fromSubstring,
	        @NotNull @NotEmpty final String toFieldName, final Object toFieldContent, final Object toSubstring) {
		score.addFeature(confidence, truncate(fromFieldName),
		        truncate(fromFieldContent != null ? fromFieldContent.toString() : unused),
		        truncate(fromSubstring != null ? fromSubstring.toString()
		                : truncate(fromFieldContent != null ? fromFieldContent.toString() : unused)),
		        truncate(toFieldName), truncate(toFieldContent != null ? toFieldContent.toString() : unused),
		        truncate(toSubstring != null ? toSubstring.toString()
		                : truncate(toFieldContent != null ? toFieldContent.toString() : unused)), getClass());
	}
	
	public abstract Expression supported();
	
	/**
	 * @param transaction
	 * @param report
	 * @param score
	 */
	@NoneNull
	public abstract void score(final MappableEntity from, final MappableEntity to, final MapScore score);
	
	/**
	 * @return
	 */
	@Override
	public Set<Class<? extends MappingStorage>> storageDependency() {
		return new HashSet<Class<? extends MappingStorage>>();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingEngine [class=");
		builder.append(this.getClass().getSimpleName());
		builder.append("registered=");
		builder.append(this.registered);
		builder.append(", initialized=");
		builder.append(this.initialized);
		builder.append("]");
		return builder.toString();
	}
	
}
