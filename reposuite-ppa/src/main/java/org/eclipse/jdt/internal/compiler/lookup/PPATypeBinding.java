/*******************************************************************************
 * PPA - Partial Program Analysis for Java
 * Copyright (C) 2008 Barthelemy Dagenais
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 3 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library. If not, see 
 * <http://www.gnu.org/licenses/lgpl-3.0.txt>
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.PPATypeRegistry;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;

public class PPATypeBinding extends ReferenceBinding implements PPAType {

	protected ReferenceBinding superclass;
	protected List<ReferenceBinding> superInterfaces = new ArrayList<ReferenceBinding>();
	protected List<FieldBinding> fields = new ArrayList<FieldBinding>();
	protected List<MethodBinding> methods = new ArrayList<MethodBinding>();
	protected List<ReferenceBinding> memberTypes = new ArrayList<ReferenceBinding>();
	protected List<TypeVariableBinding> typeVariables = new ArrayList<TypeVariableBinding>();
	protected boolean isUnknown = false;
	protected boolean isAnnotation = false;
	

	public PPATypeBinding(final char[][] compoundName, final PackageBinding fBinding, final PPATypeBindingOptions options) {
		this.compoundName = compoundName;
		this.fPackage = fBinding;
		this.sourceName = compoundName[compoundName.length - 1];
		isUnknown = toString().equals(PPATypeRegistry.UNKNOWN_CLASS_FQN);
		isAnnotation = options.isAnnotation();
	}

	public void addConstructor(final MethodBinding binding) {
		methods.add(binding);
	}

	public void addUnknownConstructor(final MethodBinding mBinding) {
		int num = mBinding.parameters != null ? mBinding.parameters.length : 0;
		if (getUnknownConstructor(mBinding.selector, num) == null) {
			methods.add(mBinding);
		}
	}

	@Override
	public FieldBinding[] availableFields() {
		return fields();
	}

	@Override
	public MethodBinding[] availableMethods() {
		return methods();
	}

	// public void addUnknownMethod(MethodBinding mBinding) {
	// int num = mBinding.parameters != null ? mBinding.parameters.length : 0;
	// if (getUnknownMethod(mBinding.selector, num) == null) {
	// methods.add(mBinding);
	// }
	// }

	@Override
	public boolean canBeInstantiated() {
		return super.canBeInstantiated();
	}

	@Override
	public TypeBinding closestMatch() {
		return super.closestMatch();
	}

	@Override
	public char[] computeGenericTypeSignature(final TypeVariableBinding[] typeVariables) {
		return super.computeGenericTypeSignature(typeVariables);
	}

	@Override
	public void computeId() {
		super.computeId();
	}

	@Override
	public char[] computeUniqueKey(final boolean isLeaf) {
		return super.computeUniqueKey(isLeaf);
	}

	@Override
	public char[] constantPoolName() {
		return super.constantPoolName();
	}

	@Override
	public String debugName() {
		return super.debugName();
	}

	@Override
	public boolean detectAnnotationCycle() {
		return super.detectAnnotationCycle();
	}

	@Override
	public int enumConstantCount() {
		return super.enumConstantCount();
	}

	@Override
	public int fieldCount() {
		return fields.size();
	}

	@Override
	public FieldBinding[] fields() {
		return fields.toArray(new FieldBinding[0]);
	}

	@Override
	public AnnotationBinding[] getAnnotations() {
		return super.getAnnotations();
	}

	@Override
	public long getAnnotationTagBits() {
		return super.getAnnotationTagBits();
	}

	@Override
	public MethodBinding getExactConstructor(final TypeBinding[] argumentTypes) {
		return super.getExactConstructor(argumentTypes);
	}

	@Override
	public MethodBinding getExactMethod(final char[] selector, final TypeBinding[] argumentTypes,
			final CompilationUnitScope refScope) {
		return super.getExactMethod(selector, argumentTypes, refScope);
	}

	@Override
	public FieldBinding getField(final char[] fieldName, final boolean needResolve) {
		FieldBinding fBinding = null;

		for (FieldBinding temp : fields) {
			if (new String(temp.name).equals(new String(fieldName))) {
				fBinding = temp;
				break;
			}
		}

		// For the unknown type, just return anything that is needed.
		if (isUnknown) {
			fBinding = new FieldBinding(fieldName, this, ClassFileConstants.AccPublic, this, null);
		}

		return fBinding;
	}

	public List<FieldBinding> getFieldsList() {
		return fields;
	}

	@Override
	public char[] getFileName() {
		return super.getFileName();
	}

	@Override
	public ReferenceBinding getMemberType(final char[] typeName) {
		return super.getMemberType(typeName);
	}

	public List<ReferenceBinding> getMemberTypesList() {
		return memberTypes;
	}

	@Override
	public MethodBinding[] getMethods(final char[] selector) {
		return super.getMethods(selector);
	}

	public List<MethodBinding> getMethodsList() {
		return methods;
	}

	@Override
	public PackageBinding getPackage() {
		return super.getPackage();
	}

	public List<ReferenceBinding> getSuperInterfacesList() {
		return superInterfaces;
	}

	@Override
	public TypeVariableBinding getTypeVariable(final char[] variableName) {
		return super.getTypeVariable(variableName);
	}

	public List<TypeVariableBinding> getTypeVariablesList() {
		return typeVariables;
	}

	public MethodBinding getUnknownConstructor(final char[] name, final int numberOfParams) {
		return getUnknownMethod(name, numberOfParams);
	}

	public MethodBinding getUnknownMethod(final char[] name, final int numberOfParams) {
		MethodBinding mBinding = null;
		String sName = new String(name);
		for (MethodBinding tempBinding : methods) {
			if (sName.equals(new String(tempBinding.selector))
					&& (tempBinding.parameters.length == numberOfParams)) {
				boolean allUnknowns = true;
				for (int i = 0; i < numberOfParams; i++) {
					if (!PPATypeRegistry.UNKNOWN_CLASS_FQN.equals(tempBinding.parameters[i]
							.toString())) {
						allUnknowns = false;
						break;
					}
				}

				if (allUnknowns) {
					mBinding = tempBinding;
					break;
				}
			}
		}

		return mBinding;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean hasIncompatibleSuperType(final ReferenceBinding otherType) {
		return super.hasIncompatibleSuperType(otherType);
	}

	@Override
	public boolean hasMemberTypes() {
		return super.hasMemberTypes();
	}

	@Override
	public boolean implementsInterface(final ReferenceBinding anInterface, final boolean searchHierarchy) {
		return super.implementsInterface(anInterface, searchHierarchy);
	}

	@Override
	public boolean isAnnotationType() {
		return isAnnotation || super.isAnnotationType();
	}

	@Override
	public boolean isClass() {
		return super.isClass();
	}

	@Override
	public boolean isCompatibleWith(final TypeBinding otherType) {
		return super.isCompatibleWith(otherType);
	}

	@Override
	public boolean isEnum() {
		return super.isEnum();
	}

	@Override
	public boolean isHierarchyBeingConnected() {
		return super.isHierarchyBeingConnected();
	}

	@Override
	public boolean isInterface() {
		return super.isInterface();
	}

	@Override
	public boolean isSuperclassOf(final ReferenceBinding otherType) {
		return super.isSuperclassOf(otherType);
	}

	@Override
	public boolean isThrowable() {
		return super.isThrowable();
	}

	@Override
	public boolean isUncheckedException(final boolean includeSupertype) {
		return super.isUncheckedException(includeSupertype);
	}

	@Override
	public ReferenceBinding[] memberTypes() {
		return memberTypes.toArray(new ReferenceBinding[0]);
	}

	@Override
	public MethodBinding[] methods() {
		return methods.toArray(new MethodBinding[0]);
	}

	@Override
	public char[] qualifiedSourceName() {
		return super.qualifiedSourceName();
	}

	@Override
	public char[] readableName() {
		return super.readableName();
	}

	@Override
	public AnnotationHolder retrieveAnnotationHolder(final Binding binding, final boolean forceInitialization) {
		return super.retrieveAnnotationHolder(binding, forceInitialization);
	}

	@Override
	public void setAnnotations(final AnnotationBinding[] annotations) {
		super.setAnnotations(annotations);
	}

	public void setSuperclass(final ReferenceBinding superclass) {
		this.superclass = superclass;
	}

	@Override
	public char[] shortReadableName() {
		return super.shortReadableName();
	}

	@Override
	public char[] signature() {
		return super.signature();
	}

	@Override
	public char[] sourceName() {
		return super.sourceName();
	}

	@Override
	public ReferenceBinding superclass() {
		return superclass;
	}

	@Override
	public ReferenceBinding[] superInterfaces() {
		return superInterfaces.toArray(new ReferenceBinding[0]);
	}

	@Override
	public ReferenceBinding[] syntheticEnclosingInstanceTypes() {
		return super.syntheticEnclosingInstanceTypes();
	}

	@Override
	public SyntheticArgumentBinding[] syntheticOuterLocalVariables() {
		return super.syntheticOuterLocalVariables();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (char[] shortName : compoundName) {
			buffer.append(shortName);
			buffer.append(".");
		}

		String toString = buffer.toString();
		int length = toString.length();
		if (length > 0) {
			toString = toString.substring(0, length - 1);
		}

		return toString;
	}

}
