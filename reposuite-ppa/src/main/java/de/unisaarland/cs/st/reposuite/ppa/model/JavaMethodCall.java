package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Condition;

@Entity
public class JavaMethodCall extends JavaElement implements Annotated {
	
	/**
	 * 
	 */
	private static final long     serialVersionUID  = -2885710604331995125L;
	
	public static String composeFullQualifiedName(final String fullQualifiedName, final List<String> signature){
		StringBuilder sb = new StringBuilder();
		sb.append(fullQualifiedName);
		sb.append("(");
		if (!signature.isEmpty()) {
			sb.append(signature.get(0));
		}
		for (int i = 1; i < signature.size(); ++i) {
			sb.append(",");
			sb.append(signature.get(i));
		}
		sb.append(")");
		return sb.toString();
	}
	
	private List<String>          signature;
	
	private String                calledPackageName = "<unknown>";
	
	private String                calledClassName   = "<unknown>";
	
	@SuppressWarnings("unused")
	private JavaMethodCall() {
		super();
	}
	
	protected JavaMethodCall(final String fullQualifiedName, final List<String> signature) {
		super(fullQualifiedName);
		this.signature = new ArrayList<String>(signature);
		this.setFullQualifiedName(composeFullQualifiedName(fullQualifiedName, signature));
		int index = fullQualifiedName.lastIndexOf(".");
		Condition.check(index < fullQualifiedName.length(),
				"Could not determine called class name. Last index of `.` is not less than length of string: "
				+ fullQualifiedName);
		this.calledPackageName = fullQualifiedName.substring(0, index);
		this.calledClassName = fullQualifiedName.substring(index+1, fullQualifiedName.length());
		
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		JavaMethodCall other = (JavaMethodCall) obj;
		if (getSignature() == null) {
			if (other.getSignature() != null) {
				return false;
			}
		} else if (!getSignature().equals(other.getSignature())) {
			return false;
		}
		return true;
	}
	
	@Transient
	public String getCalledClassNameFullQualified() {
		return this.getFullQualifiedName();
	}
	
	@Transient
	public String getCalledClassNameShort() {
		return this.calledClassName;
	}
	
	public String getCalledPackageName() {
		return this.calledPackageName;
	}
	
	@ElementCollection
	public List<String> getSignature() {
		return this.signature;
	}
	
	@Override
	public Element getXMLRepresentation(final Document document) {
		Element thisElement = document.createElement("JavaMethodCall");
		
		Element nameElement = document.createElement("fullQualifiedName");
		Text textNode = document.createTextNode(this.getFullQualifiedName());
		nameElement.appendChild(textNode);
		thisElement.appendChild(nameElement);
		
		return thisElement;
	}
	
	@Override
	public Collection<Annotated> saveFirst() {
		return new HashSet<Annotated>();
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
	private void setSignature(final List<String> signature) {
		this.signature = signature;
	}
}
