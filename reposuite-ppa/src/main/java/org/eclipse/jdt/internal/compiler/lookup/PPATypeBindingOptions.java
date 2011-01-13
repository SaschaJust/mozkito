package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.dom.Name;

import de.unisaarland.cs.st.reposuite.ppa.utils.PPAASTUtil;

public class PPATypeBindingOptions {
	
	public static PPATypeBindingOptions parseOptions(final Name name) {
		return new PPATypeBindingOptions(PPAASTUtil.isAnnotation(name));
	}
	
	private boolean isAnnotation;
	
	public PPATypeBindingOptions() {
		
	}
	
	public PPATypeBindingOptions(final boolean isAnnotation) {
		super();
		this.isAnnotation = isAnnotation;
	}
	
	public boolean isAnnotation() {
		return isAnnotation;
	}
	
	public void setAnnotation(final boolean isAnnotation) {
		this.isAnnotation = isAnnotation;
	}
	
	
	
}
