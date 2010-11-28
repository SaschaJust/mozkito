package de.unisaarland.cs.st.reposuite.utils.specification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Condition to guaranty an double to be in a certain range.
 * <ul>
 * <li>If {@link DoubleRange#min()} is not specified, it is set to
 * {@link Double#MIN_VALUE}.</li>
 * <li>If {@link DoubleRange#max()} is not specified, {@link Double#MAX_VALUE}
 * is used.</li>
 * <li> {@link DoubleRange#value()} holds the specification/condition string,
 * i.e. the reason why this assumption is made/can be made.</li>
 * </ul>
 * 
 * This can be used only on PARAMETERs.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (value = { ElementType.PARAMETER })
public @interface DoubleRange {
	
	/**
	 * @return the max value the target variable/parameter must have.
	 */
	double max() default Double.MAX_VALUE;
	
	/**
	 * @return the min value the target variable/parameter must have.
	 */
	double min() default Double.MIN_VALUE;
	
	/**
	 * @return the corresponding specification/condition/assertion string.
	 */
	String value() default "";
}
