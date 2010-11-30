package de.unisaarland.cs.st.reposuite.utils.specification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Condition to guaranty an long to be in a certain range.
 * <ul>
 * <li>If {@link LongRange#min()} is not specified, it is set to
 * {@link Long#MIN_VALUE}.</li>
 * <li>If {@link LongRange#max()} is not specified, {@link Long#MAX_VALUE} is
 * used.</li>
 * <li> {@link LongRange#value()} holds the specification/condition string, i.e.
 * the reason why this assumption is made/can be made.</li>
 * </ul>
 * 
 * This can be used only on PARAMETERs.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (value = { ElementType.PARAMETER })
public @interface LongRange {
	
	/**
	 * @return the max value the target variable/parameter must have.
	 */
	long max() default Long.MAX_VALUE;
	
	/**
	 * @return the min value the target variable/parameter must have.
	 */
	long min() default Long.MIN_VALUE;
	
	/**
	 * @return the corresponding specification/condition/assertion string.
	 */
	String value() default "";
}
