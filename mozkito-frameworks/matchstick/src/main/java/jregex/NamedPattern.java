/**
 * 
 */
package jregex;

import java.util.Set;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class NamedPattern extends Pattern {
	
	private static final long serialVersionUID = 658555765816034510L;
	
	public NamedPattern(final String pattern) {
		super(pattern, 0);
	}
	
	public NamedPattern(final String pattern, final int flags) {
		super(pattern, flags);
	}
	
	public String getGroupName(@NotNegative final int index) {
		Condition.notNull(this.memregs,
		                  "Requesting memory register for groups when no groups are active violates setup.");
		CompareCondition.less(index, this.memregs,
		                      "Index of group has to be less then the number of active memory registers.");
		
		@SuppressWarnings ("unchecked")
		Set<String> keySet = this.namedGroupMap.keySet();
		for (String groupName : keySet) {
			if (this.namedGroupMap.get(groupName).equals(index)) {
				return groupName;
			}
		}
		return "";
		
	}
}
