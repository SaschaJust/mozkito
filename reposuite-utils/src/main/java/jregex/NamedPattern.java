/**
 * 
 */
package jregex;

import java.util.Set;

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
	
	public String getGroupName(final int index) {
		assert (index > 0);
		assert (index < this.memregs);
		
		@SuppressWarnings ("unchecked") Set<String> keySet = this.namedGroupMap.keySet();
		for (String groupName : keySet) {
			if (this.namedGroupMap.get(groupName).equals(index)) {
				return groupName;
			}
		}
		return "";
		
	}
}
