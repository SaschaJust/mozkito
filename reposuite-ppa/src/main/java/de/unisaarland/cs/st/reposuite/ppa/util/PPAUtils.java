package de.unisaarland.cs.st.reposuite.ppa.util;

import java.util.List;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class PPAUtils {
	
	/**
	 * Utility method that builds a full qualified class.
	 * 
	 * @param parentName
	 *            the parent name
	 * @param name
	 *            the name
	 * @param params
	 *            the params
	 * @return the method name
	 */
	public static String getMethodName(String parentName, String name, List<String> params) {
		StringBuilder ss = new StringBuilder();
		ss.append(parentName);
		ss.append(".");
		ss.append(name);
		ss.append("(");
		if (params.size() > 0) {
			ss.append(params.get(0));
		}
		for (int i = 1; i < params.size(); ++i) {
			ss.append(",");
			ss.append(params.get(i));
		}
		ss.append(")");
		return ss.toString();
	}
	
}
