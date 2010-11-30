package de.unisaarland.cs.st.reposuite.utils.specification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Condition to guaranty an short to be in a certain range.
 * <ul>
 * <li>If {@link ShortRange#min()} is not specified, it is set to
 * {@link Short#MIN_VALUE}.</li>
 * <li>If {@link ShortRange#max()} is not specified, {@link Short#MAX_VALUE}
 * is used.</li>
 * <li> {@link ShortRange#value()} holds the specification/condition string,
 * i.e. the reason why this assumption is made/can be made.</li>
 * </ul>
 * 
 * This can be used only on PARAMETERs.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (value = { ElementType.PARAMETER })
public @interface ShortRange {
	
	/**
	 * @return the max value the target variable/parameter must have.
	 */
	int max() default Short.MAX_VALUE;
	
	/**
	 * @return the min value the target variable/parameter must have.
	 */
	int min() default Short.MIN_VALUE;
	
	/**
	 * @return the corresponding specification/condition/assertion string.
	 */
	String value() default "";
}
