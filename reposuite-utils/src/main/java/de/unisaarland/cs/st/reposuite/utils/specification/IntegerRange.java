package de.unisaarland.cs.st.reposuite.utils.specification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Condition to guaranty an integer to be in a certain range.
 * <ul>
 * <li>If {@link IntegerRange#min()} is not specified, it is set to
 * {@link Integer#MIN_VALUE}.</li>
 * <li>If {@link IntegerRange#max()} is not specified, {@link Integer#MAX_VALUE}
 * is used.</li>
 * <li> {@link IntegerRange#value()} holds the specification/condition string,
 * i.e. the reason why this assumption is made/can be made.</li>
 * </ul>
 * 
 * This can be used only on PARAMETERs.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (value = { ElementType.PARAMETER })
public @interface IntegerRange {
	
	/**
	 * @return the max value the target variable/parameter must have.
	 */
	int max() default Integer.MAX_VALUE;
	
	/**
	 * @return the min value the target variable/parameter must have.
	 */
	int min() default Integer.MIN_VALUE;
	
	/**
	 * @return the corresponding specification/condition/assertion string.
	 */
	String value() default "";
}
