package de.unisaarland.cs.st.reposuite.utils.specification;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Condition to guaranty an integer to be in a certain range.
 * <ul>
 * <li>If {@link Range#min()} is not specified, it is set to
 * {@link Integer#MIN_VALUE}.</li>
 * <li>If {@link Range#max()} is not specified, {@link Integer#MAX_VALUE}
 * is used.</li>
 * <li> {@link Range#value()} holds the specification/condition string,
 * i.e. the reason why this assumption is made/can be made.</li>
 * </ul>
 * 
 * This can be used only on PARAMETERs.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@ConditionPattern ("range(($w) #{target}, ${min}, ${max}, ${spec})")
@Target (value = { ElementType.PARAMETER })
public @interface Range {
	
	/**
	 * @return the max value the target variable/parameter must have.
	 */
	int max();
	
	/**
	 * @return the min value the target variable/parameter must have.
	 */
	int min();
	
	/**
	 * @return the corresponding specification/condition/assertion string.
	 */
	String spec() default "";
}
