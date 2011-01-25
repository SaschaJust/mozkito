package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

@Entity
public class JavaMethodCall extends JavaElement implements Annotated{
	
	/**
	 * 
	 */
	private static final long     serialVersionUID  = -2885710604331995125L;
	
	private List<String>          signature;
	
	private String                calledPackageName = "<unknown>";
	
	private String                calledClassName   = "<unknown>";
	
	private JavaElementDefinition parent;
	
	public JavaMethodCall(final String fullQualifiedName, final String filePath, final DateTime timestamp,
			final int startLine, final int endLine, final List<String> signature, final JavaElementDefinition parent) {
		super(fullQualifiedName, filePath, timestamp, startLine, endLine);
		this.signature = new ArrayList<String>(signature);
		this.parent = parent;
		StringBuilder sb = new StringBuilder();
		sb.append(super.getFullQualifiedName());
		sb.append("(");
		if (!signature.isEmpty()) {
			sb.append(signature.get(0));
		}
		for (int i = 1; i < signature.size(); ++i) {
			sb.append(",");
			sb.append(signature.get(i));
		}
		sb.append(")");
		this.fullQualifiedName = sb.toString();
	}
	
	@Transient
	public String getCalledClassNameFullQualified() {
		return this.getFullQualifiedName();
	}
	
	@Transient
	public String getCalledClassNameShort() {
		return calledClassName;
	}
	
	public String getCalledPackageName() {
		return calledPackageName;
	}
	
	public JavaElementDefinition getParent() {
		return parent;
	}
	
	@ElementCollection
	public List<String> getSignature() {
		return signature;
	}
	
	@Override
	public Collection<Annotated> saveFirst() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("unused")
	private void setCalledClassName(final String calledClassName) {
		this.calledClassName = calledClassName;
	}
	
	@SuppressWarnings("unused")
	private void setCalledPackageName(final String calledPackageName) {
		this.calledPackageName = calledPackageName;
	}
	
	@SuppressWarnings("unused")
	private void setParent(final JavaElementDefinition parent) {
		this.parent = parent;
	}
	
	@SuppressWarnings("unused")
	private void setSignature(final List<String> signature) {
		this.signature = signature;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("JavaMethodCall [calledMethod=");
		sb.append(super.getFullQualifiedName());
		sb.append(", in File=");
		sb.append(super.getFilePath());
		sb.append(", in line=");
		sb.append(super.getEndLine());
		sb.append("]");
		return sb.toString();
	}
	
}
