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
package org.eclipse.jdt.core.dom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PPAType;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TagBits;

import de.unisaarland.cs.st.reposuite.ppa.PPAIndexer;
import de.unisaarland.cs.st.reposuite.ppa.PPAOptions;
import de.unisaarland.cs.st.reposuite.ppa.TypeFact;
import de.unisaarland.cs.st.reposuite.ppa.ValidatorUtil;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAASTUtil;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPALoggerUtil;

public class PPABindingsUtil {
	
	public static final int PROBLEM_TYPE = -1;
	public static final int UNKNOWN_TYPE = 0;
	public static final int MISSING_TYPE = 1;
	public static final int MISSING_SUPER_TYPE = 2;
	public static final int FULL_TYPE = 3;
	
	private final static Logger logger = PPALoggerUtil
	.getLogger(PPABindingsUtil.class);
	
	public static boolean compatibleTypes(final ITypeBinding formalType,
			final ITypeBinding actualType) {
		boolean compatible = false;
		
		String actualTypeType = getTypeString(actualType);
		String formalTypeType = getTypeString(formalType);
		
		boolean areBooleans = formalTypeType.equals("boolean")
		&& actualTypeType.equals("boolean");
		boolean oneIsBoolean = !areBooleans
		&& (formalTypeType.equals("boolean") || actualTypeType
				.equals("boolean"));
		
		boolean areVoids = formalTypeType.equals("void")
		&& actualTypeType.equals("void");
		boolean oneIsVoid = !areVoids
		&& (formalTypeType.equals("void") || actualTypeType
				.equals("void"));
		
		int formalSafetyValue = getSafetyValue(formalType);
		int actualSafetyValue = getSafetyValue(actualType);
		compatible = formalType.isEqualTo(actualType);
		compatible = compatible || isUnknownType(formalType)
		|| isUnknownType(actualType);
		compatible = compatible
		|| (formalType.isPrimitive() && actualType.isPrimitive()
				&& !oneIsBoolean && !oneIsVoid);
		compatible = compatible
		|| (!formalType.isPrimitive() && actualType.isNullType());
		compatible = compatible
		|| ((formalSafetyValue < FULL_TYPE) && (actualSafetyValue < FULL_TYPE));
		compatible = compatible
		|| (!formalType.isPrimitive()
				&& !Modifier.isFinal(formalType.getModifiers())
				&& (formalSafetyValue == FULL_TYPE) && (actualSafetyValue < FULL_TYPE));
		compatible = compatible || actualType.isSubTypeCompatible(formalType);
		
		// TODO: Primitive with Reference Type -> enforce in the next one...
		// TODO: Formal known super type, but unknown actual...
		
		return compatible;
	}
	
	public static List<IMethodBinding> filterMethods(
			final List<IMethodBinding> methodBindings, final boolean filterUnknowns) {
		List<IMethodBinding> acceptableMethods = new ArrayList<IMethodBinding>();
		Set<String> keys = new HashSet<String>();
		
		for (IMethodBinding binding : methodBindings) {
			String key = getShortMethodSignature(binding);
			boolean acceptable = true;
			
			if (filterUnknowns) {
				acceptable = !getTypeString(binding.getReturnType()).equals(
						PPATypeRegistry.UNKNOWN_CLASS_FQN);
				
				if (acceptable) {
					int size = getArgsLength(binding);
					for (int i = 0; i < size; i++) {
						if (getTypeString(binding.getParameterTypes()[i])
								.equals(PPATypeRegistry.UNKNOWN_CLASS_FQN)) {
							acceptable = false;
							break;
						}
					}
				}
			}
			
			if (acceptable && !keys.contains(key)) {
				keys.add(key);
				acceptableMethods.add(binding);
			}
		}
		
		return acceptableMethods;
	}
	
	private static List<IMethodBinding> findAcceptableConstructors(
			final int numberParams, final ITypeBinding containerType) {
		
		List<IMethodBinding> acceptableMethods = new ArrayList<IMethodBinding>();
		
		if (!isUnknownType(containerType) && !isMissingType(containerType)) {
			for (IMethodBinding mBinding : containerType.getDeclaredMethods()) {
				if (mBinding.isConstructor()
						&& (getArgsLength(mBinding) == numberParams)) {
					acceptableMethods.add(mBinding);
				}
			}
		}
		
		return acceptableMethods;
	}
	
	public static List<IMethodBinding> findAcceptableConstructors(
			final ITypeBinding[] params, final ITypeBinding container) {
		List<IMethodBinding> acceptableMethods = new ArrayList<IMethodBinding>();
		int numberParams = params.length;
		List<IMethodBinding> tempMethods = findAcceptableConstructors(
				numberParams, container);
		
		for (IMethodBinding mBinding : tempMethods) {
			boolean valid = true;
			ITypeBinding[] formalParams = mBinding.getParameterTypes();
			for (int i = 0; i < numberParams; i++) {
				if (!compatibleTypes(formalParams[i], params[i])) {
					valid = false;
					break;
				}
			}
			if (valid) {
				acceptableMethods.add(mBinding);
			}
		}
		
		return acceptableMethods;
	}
	
	public static List<IMethodBinding> findAcceptableMethods(final String name,
			final int numberParams, final ITypeBinding container) {
		List<IMethodBinding> acceptableMethods = new ArrayList<IMethodBinding>();
		
		if (!isUnknownType(container) && !isMissingType(container)) {
			ITypeBinding[] superTypes = getAllSuperTypes(container);
			for (IMethodBinding mBinding : container.getDeclaredMethods()) {
				if (mBinding.getName().equals(name)
						&& (getArgsLength(mBinding) == numberParams)) {
					acceptableMethods.add(mBinding);
				}
			}
			
			for (ITypeBinding superType : superTypes) {
				for (IMethodBinding mBinding : superType.getDeclaredMethods()) {
					if (mBinding.getName().equals(name)
							&& (getArgsLength(mBinding) == numberParams)
							&& !Modifier.isPrivate(mBinding.getModifiers())) {
						acceptableMethods.add(mBinding);
					}
				}
			}
		}
		
		return acceptableMethods;
	}
	
	public static List<IMethodBinding> findAcceptableMethods(final String name,
			final ITypeBinding[] params, final ITypeBinding container) {
		List<IMethodBinding> acceptableMethods = new ArrayList<IMethodBinding>();
		int numberParams = params.length;
		List<IMethodBinding> tempMethods = findAcceptableMethods(name,
				numberParams, container);
		
		for (IMethodBinding mBinding : tempMethods) {
			boolean valid = true;
			ITypeBinding[] formalParams = mBinding.getParameterTypes();
			for (int i = 0; i < numberParams; i++) {
				if (!compatibleTypes(formalParams[i], params[i])) {
					valid = false;
					break;
				}
			}
			if (valid) {
				acceptableMethods.add(mBinding);
			}
		}
		
		return acceptableMethods;
	}
	
	public static List<IMethodBinding> findAcceptableMethods(final String name,
			final ITypeBinding[] params, final ITypeBinding returnType,
			final ITypeBinding container) {
		List<IMethodBinding> acceptableMethods = new ArrayList<IMethodBinding>();
		List<IMethodBinding> tempMethods = findAcceptableMethods(name, params,
				container);
		
		for (IMethodBinding mBinding : tempMethods) {
			if (compatibleTypes(returnType, mBinding.getReturnType())) {
				acceptableMethods.add(mBinding);
			}
		}
		
		return acceptableMethods;
	}
	
	public static IVariableBinding findField(final ITypeBinding type, final String fieldName) {
		return findFieldHierarchy(type, fieldName);
	}
	
	public static IVariableBinding findFieldHierarchy(final ITypeBinding type,
			final String fieldName) {
		IVariableBinding fieldBinding = findFieldInType(type, fieldName);
		if (fieldBinding != null) {
			return fieldBinding;
		}
		ITypeBinding superClass = type.getSuperclass();
		if (superClass != null) {
			fieldBinding = findFieldHierarchy(superClass, fieldName);
			if (fieldBinding != null) {
				return fieldBinding;
			}
		}
		ITypeBinding[] interfaces = type.getInterfaces();
		if (interfaces != null) {
			for (int i = 0; i < interfaces.length; i++) {
				fieldBinding = findFieldHierarchy(interfaces[i], fieldName);
				if (fieldBinding != null) {
					return fieldBinding;
				}
			}
		}
		return null;
	}
	
	public static IVariableBinding findFieldInType(final ITypeBinding type,
			final String fieldName) {
		IVariableBinding fieldBinding = null;
		IVariableBinding[] fields = type.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			IVariableBinding field = fields[i];
			if (field.getName().equals(fieldName)) {
				fieldBinding = field;
				break;
			}
		}
		return fieldBinding;
	}
	
	public static void fixConstructor(final ClassInstanceCreation cic,
			final PPATypeRegistry typeRegistry, final PPADefaultBindingResolver resolver,
			final PPAIndexer indexer, final PPAEngine engine, final boolean skipSuperMissing,
			final boolean filterUnknowns) {
		IMethodBinding oldBinding = cic.resolveConstructorBinding();
		ITypeBinding unknownType = typeRegistry.getUnknownBinding(resolver);
		ITypeBinding[] paramTypes = PPABindingsUtil.getParamTypes(cic,
				unknownType);
		ITypeBinding containerType = null;
		
		if (oldBinding == null) {
			if (cic.getAnonymousClassDeclaration() != null) {
				// XXX Case when this is an anonymous class that is not resolved.
				containerType = cic.getType().resolveBinding();
			}
		} else {
			containerType = oldBinding.getDeclaringClass();
		}
		
		// XXX This is to prevent the problem of null container type. it should
		// not happen.
		if (containerType == null) {
			logger.info("This container type was null: " + cic.toString());
			containerType = unknownType;
		}
		
		List<IMethodBinding> acceptableMethods = new ArrayList<IMethodBinding>();
		
		if (!skipSuperMissing
				|| (PPABindingsUtil.getSafetyValue(containerType) == PPABindingsUtil.FULL_TYPE)) {
			acceptableMethods = PPABindingsUtil.filterMethods(PPABindingsUtil
					.findAcceptableConstructors(paramTypes, containerType),
					filterUnknowns);
		}
		
		if (acceptableMethods.size() == 1) {
			IMethodBinding newBinding = acceptableMethods.get(0);
			if ((oldBinding == null) || !newBinding.isEqualTo(oldBinding)) {
				resolver.fixClassInstanceCreation(cic, newBinding);
				reportNewConstructorBinding(indexer, engine, unknownType, cic,
						paramTypes, newBinding);
			}
		} else {
			IMethodBinding newBinding = typeRegistry.createConstructor(
					containerType, paramTypes, resolver);
			resolver.fixClassInstanceCreation(cic, newBinding);
		}
	}
	
	public static void fixMethod(final SimpleName name,
			final ITypeBinding expectedReturnType, final PPATypeRegistry typeRegistry,
			final PPADefaultBindingResolver resolver, final PPAIndexer indexer,
			final PPAEngine ppaEngine) {
		fixMethod(name, expectedReturnType, typeRegistry, resolver, indexer,
				ppaEngine, true, false);
	}
	
	public static void fixMethod(final SimpleName name,
			final ITypeBinding expectedReturnType, final PPATypeRegistry typeRegistry,
			final PPADefaultBindingResolver resolver, final PPAIndexer indexer,
			final PPAEngine ppaEngine, final boolean skipSuperMissing,
			final boolean filterUnknowns) {
		ASTNode parent = name.getParent();
		String methodName = name.getFullyQualifiedName();
		ITypeBinding unknownType = typeRegistry.getUnknownBinding(resolver);
		IMethodBinding oldBinding = null;
		ITypeBinding[] paramTypes = null;
		ITypeBinding containerType = null;
		ASTNode container = null;
		
		if (parent instanceof MethodInvocation) {
			MethodInvocation mi = (MethodInvocation) parent;
			container = PPAASTUtil.getContainer(mi);
			if (container == null) {
				return;
			}
			paramTypes = PPABindingsUtil.getParamTypes(mi.arguments(),
					unknownType);
			containerType = PPABindingsUtil.getTypeBinding(container);
			oldBinding = mi.resolveMethodBinding();
		}
		
		if (parent instanceof SuperMethodInvocation) {
			SuperMethodInvocation smi = (SuperMethodInvocation) parent;
			container = PPAASTUtil.getSpecificParentType(parent,
					ASTNode.TYPE_DECLARATION);
			if (container != null) {
				TypeDeclaration td = (TypeDeclaration) container;
				container = td.getSuperclassType();
			}
			
			if (container != null) {
				paramTypes = PPABindingsUtil.getParamTypes(smi.arguments(),
						unknownType);
				containerType = PPABindingsUtil.getTypeBinding(container);
				oldBinding = smi.resolveMethodBinding();
			}
		}
		
		if (container != null) {
			// XXX This is to prevent the problem of null container type. it
			// should not happen.
			if (containerType == null) {
				logger.info("This container type was null: "
						+ name.getFullyQualifiedName() + " : "
						+ container.toString());
				containerType = PPABindingsUtil.getTypeBinding(container);
				containerType = unknownType;
			}
			
			List<IMethodBinding> acceptableMethods = new ArrayList<IMethodBinding>();
			
			if (!skipSuperMissing
					|| (PPABindingsUtil.getSafetyValue(containerType) == PPABindingsUtil.FULL_TYPE)) {
				if (expectedReturnType == null) {
					acceptableMethods = PPABindingsUtil.filterMethods(
							PPABindingsUtil.findAcceptableMethods(methodName,
									paramTypes, containerType), filterUnknowns);
				} else {
					acceptableMethods = PPABindingsUtil.filterMethods(
							PPABindingsUtil.findAcceptableMethods(methodName,
									paramTypes, expectedReturnType,
									containerType), filterUnknowns);
				}
			}
			
			if (acceptableMethods.size() == 1) {
				IMethodBinding newBinding = acceptableMethods.get(0);
				
				if ((oldBinding == null) || !newBinding.isEqualTo(oldBinding)) {
					resolver.fixMessageSend(name, newBinding);
					reportNewMethodBinding(indexer, ppaEngine, unknownType,
							parent, paramTypes, newBinding);
				}
			} else {
				ITypeBinding newContainerType = PPABindingsUtil
				.getFirstFieldContainerMissingSuperType(container);
				ITypeBinding newReturnType = expectedReturnType == null ? unknownType
						: expectedReturnType;
				if (newContainerType == null) {
					newContainerType = containerType;
				}
				
				IMethodBinding newBinding = typeRegistry.createMethodBinding(
						methodName, newContainerType, newReturnType,
						paramTypes, resolver);
				resolver.fixMessageSend(name, newBinding);
			}
			
		}
	}
	
	public static void fixMethod(final SimpleName name, final PPATypeRegistry typeRegistry,
			final PPADefaultBindingResolver resolver, final PPAIndexer indexer,
			final PPAEngine ppaEngine) {
		fixMethod(name, null, typeRegistry, resolver, indexer, ppaEngine);
	}
	
	@SuppressWarnings("unchecked")
	public static ITypeBinding[] getAllSuperTypes(final ITypeBinding type) {
		Set superTypes = new HashSet();
		getAllSuperTypes(type, superTypes);
		superTypes.remove(type);
		return (ITypeBinding[]) superTypes.toArray(new ITypeBinding[superTypes
		                                                            .size()]);
	}
	
	@SuppressWarnings("unchecked")
	private static void getAllSuperTypes(final ITypeBinding type, final Set superTypes) {
		if (superTypes.contains(type)) {
			// Possible because of interfaces
			return;
		} else {
			superTypes.add(type);
			// Interfaces first.
			ITypeBinding[] interfaces = type.getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				getAllSuperTypes(interfaces[i], superTypes);
			}
			// Super class in second.
			ITypeBinding superClass = type.getSuperclass();
			if (superClass != null) {
				getAllSuperTypes(superClass, superTypes);
			}
		}
		
	}
	
	public static int getArgsLength(final IMethodBinding mBinding) {
		ITypeBinding[] args = mBinding.getParameterTypes();
		return args == null ? 0 : args.length;
	}
	
	public static char[][] getArrayFromName(final String name) {
		ValidatorUtil.validateEmpty(name, "name", true);
		String[] fragments = name.split("\\.");
		int size = fragments.length;
		char[][] packages = new char[size][];
		
		for (int i = 0; i < size; i++) {
			packages[i] = fragments[i].toCharArray();
		}
		
		return packages;
	}
	
	public static String getBindingText(final IBinding binding) {
		String bindingText = "";
		if (binding != null) {
			if (binding instanceof IMethodBinding) {
				IMethodBinding mBinding = (IMethodBinding) binding;
				bindingText = "MBinding: "
					+ PPABindingsUtil.getFullMethodSignature(mBinding);
			} else if (binding instanceof IVariableBinding) {
				IVariableBinding vBinding = (IVariableBinding) binding;
				if (vBinding.isField()) {
					String type = "nil";
					if (vBinding.getType() != null) {
						type = getTypeString(vBinding.getType());
					}
					
					String decType = "nil";
					if (vBinding.getDeclaringClass() != null) {
						decType = getTypeString(vBinding.getDeclaringClass());
					}
					bindingText = "FBinding: " + type + " " + decType + ":"
					+ vBinding.getName();
				} else {
					String type = "nil";
					if (vBinding.getType() != null) {
						type = getTypeString(vBinding.getType());
					}
					bindingText = "VBinding: " + type + " "
					+ vBinding.getName();
				}
			} else if (binding instanceof ITypeBinding) {
				ITypeBinding typeBinding = (ITypeBinding) binding;
				bindingText = "TBinding: " + typeBinding.getName();
			} else if (binding instanceof IPackageBinding) {
				IPackageBinding pBinding = (IPackageBinding) binding;
				bindingText = "PBinding: " + pBinding.getName();
			}
		}
		return bindingText;
	}
	
	
	
	// For testing
	public static ASTNode getCU(final ICompilationUnit icu, final PPATypeRegistry registry)
	throws JavaModelException {
		ASTNode node = null;
		PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
		parser2.setStatementsRecovery(true);
		// parser2.setBindingsRecovery(true);
		parser2.setResolveBindings(true);
		parser2.setSource(icu);
		node = parser2.createAST(null);
		PPAEngine ppaEngine = new PPAEngine(registry, new PPAOptions());
		
		CompilationUnit cu = (CompilationUnit) node;
		
		ppaEngine.addUnitToProcess(cu);
		ppaEngine.doPPA();
		ppaEngine.reset();
		return node;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param container
	 * @return The type in the hierarchy of the container that has a missing
	 *         declaration. Can be the container itself for FieldAccess and
	 *         Name. Null if the type of a name cannot be resolved.
	 */
	public static ITypeBinding getFirstFieldContainerMissingSuperType(
			ASTNode container) {
		ITypeBinding superTypeBinding = null;
		if (container == null) {
			superTypeBinding = null;
		} else if (container instanceof TypeDeclaration) {
			TypeDeclaration tDeclaration = (TypeDeclaration) container;
			superTypeBinding = getFirstMissingSuperType(tDeclaration
					.resolveBinding());
		} else if (container instanceof AnonymousClassDeclaration) {
			AnonymousClassDeclaration aDeclaration = (AnonymousClassDeclaration) container;
			superTypeBinding = aDeclaration.resolveBinding();
			if (!isMissingType(superTypeBinding)) {
				superTypeBinding = getFirstMissingSuperType(superTypeBinding);
			}
		} else if (container instanceof Expression) {
			Expression expression = (Expression) container;
			superTypeBinding = expression.resolveTypeBinding();
			
			if (getSafetyValue(superTypeBinding) > PPABindingsUtil.MISSING_TYPE) {
				superTypeBinding = getFirstMissingSuperType(superTypeBinding);
			}
		}
		
		if ((superTypeBinding == null)
				&& ((container instanceof TypeDeclaration) || (container instanceof AnonymousClassDeclaration))) {
			container = PPAASTUtil.getFieldContainer(container, true, false);
			if (container != null) {
				superTypeBinding = getFirstFieldContainerMissingSuperType(container);
			}
		}
		
		return superTypeBinding;
	}
	
	/**
	 * <p>
	 * Depth-first search to find the first missing interface of a type.
	 * </p>
	 * 
	 * @param typeBinding
	 *            A type which is neither unknown nor missing.
	 * @return The first interface of this type in a depth-first search that is
	 *         missing.
	 */
	public static ITypeBinding getFirstMissingInterface(final ITypeBinding typeBinding) {
		ITypeBinding missingSuperInterface = null;
		
		if (!isProblemType(typeBinding) && !isUnknownType(typeBinding)
				&& !isMissingType(typeBinding)) {
			for (ITypeBinding superInterface : typeBinding.getInterfaces()) {
				if (isMissingType(superInterface)) {
					missingSuperInterface = superInterface;
					break;
				} else {
					missingSuperInterface = getFirstMissingInterface(superInterface);
					if (missingSuperInterface != null) {
						break;
					}
				}
			}
		}
		
		return missingSuperInterface;
	}
	
	/**
	 * <p>
	 * Depth-first search to find the first missing superclass of a type.
	 * </p>
	 * 
	 * @param typeBinding
	 *            A type which is neither unknown nor missing.
	 * @return The first class of this type in a depth-first search that is
	 *         missing.
	 */
	public static ITypeBinding getFirstMissingSuperClass(
			final ITypeBinding typeBinding) {
		ITypeBinding missingSuperClass = null;
		
		if (!isProblemType(typeBinding) && !isUnknownType(typeBinding)
				&& !isMissingType(typeBinding)) {
			ITypeBinding superType = typeBinding.getSuperclass();
			if (isMissingType(superType)) {
				missingSuperClass = superType;
			} else {
				missingSuperClass = getFirstMissingSuperClass(superType);
			}
		}
		
		return missingSuperClass;
	}
	
	public static ITypeBinding getFirstMissingSuperType(final ITypeBinding typeBinding) {
		ITypeBinding missingSuperType = null;
		
		if (!isProblemType(typeBinding) && !isUnknownType(typeBinding)
				&& !isMissingType(typeBinding)) {
			missingSuperType = getFirstMissingSuperClass(typeBinding);
			// look for interfaces;
			if (missingSuperType == null) {
				missingSuperType = getFirstMissingInterface(typeBinding);
			}
		}
		
		return missingSuperType;
	}
	
	public static String getFQN(final String packageName, final String name) {
		String fqn = name;
		if (ValidatorUtil.validateEmpty(packageName, "package", false)) {
			fqn = packageName + "." + name;
		}
		
		return fqn;
	}
	
	public static String getFullMethodSignature(final IMethodBinding methodBinding) {
		StringBuffer buffer = new StringBuffer();
		try {
			int numArgs = getArgsLength(methodBinding);
			ITypeBinding[] params = methodBinding.getParameterTypes();
			ITypeBinding returnType = methodBinding.getReturnType();
			String name = methodBinding.getName();
			buffer.append(getTypeString(returnType));
			buffer.append(" ");
			buffer.append(getTypeString(methodBinding.getDeclaringClass()));
			buffer.append(":");
			buffer.append(name);
			buffer.append("(");
			
			for (int i = 0; i < numArgs; i++) {
				buffer.append(getTypeString(params[i]));
				if (i < numArgs - 1) {
					buffer.append(",");
				}
			}
			
			buffer.append(")");
		} catch (Exception e) {
			logger.error("Error while getting method signature", e);
		}
		return buffer.toString();
	}
	
	public static org.eclipse.jdt.internal.compiler.lookup.TypeBinding getInternalTypeBinding(
			final ITypeBinding typeBinding) {
		org.eclipse.jdt.internal.compiler.lookup.TypeBinding internalTBinding = null;
		
		if (typeBinding instanceof TypeBinding) {
			TypeBinding tBinding = (TypeBinding) typeBinding;
			internalTBinding = tBinding.binding;
		}
		
		return internalTBinding;
	}
	
	public static String getPackage(final String name) {
		ValidatorUtil.validateEmpty(name, "name", true);
		String packageName = null;
		
		int index = name.lastIndexOf(".");
		if ((index != PROBLEM_TYPE) && (index != (name.length() - 1))) {
			packageName = name.substring(0, index);
		}
		
		return packageName;
	}
	
	public static String[] getPackageArray(final String packageName) {
		String[] packages = null;
		if (ValidatorUtil.validateEmpty(packageName, "packageName", false)) {
			packages = packageName.split("\\.");
		}
		
		return packages;
	}
	
	@SuppressWarnings("unchecked")
	private static ITypeBinding[] getParamTypes(final ClassInstanceCreation cic,
			final ITypeBinding unknownBinding) {
		List args = cic.arguments();
		int size = args.size();
		ITypeBinding[] typeBindings = new ITypeBinding[args.size()];
		
		for (int i = 0; i < size; i++) {
			ASTNode node = (ASTNode) args.get(i);
			ITypeBinding tempBinding = PPABindingsUtil.getTypeBinding(node);
			if (tempBinding == null) {
				tempBinding = unknownBinding;
			}
			typeBindings[i] = tempBinding;
		}
		
		return typeBindings;
	}
	
	@SuppressWarnings("unchecked")
	public static ITypeBinding[] getParamTypes(final List args,
			final ITypeBinding unknownBinding) {
		int size = args.size();
		ITypeBinding[] typeBindings = new ITypeBinding[args.size()];
		
		for (int i = 0; i < size; i++) {
			ASTNode node = (ASTNode) args.get(i);
			ITypeBinding tempBinding = PPABindingsUtil.getTypeBinding(node);
			if (tempBinding == null) {
				tempBinding = unknownBinding;
			}
			typeBindings[i] = tempBinding;
		}
		
		return typeBindings;
	}
	
	public static PPADefaultBindingResolver getResolver(final AST ast) {
		PPADefaultBindingResolver returnResolver = null;
		BindingResolver resolver = ast.getBindingResolver();
		if (resolver instanceof PPADefaultBindingResolver) {
			returnResolver = (PPADefaultBindingResolver) resolver;
		}
		return returnResolver;
	}
	
	public static int getSafetyValue(final ITypeBinding binding) {
		if (isProblemType(binding)) {
			return PROBLEM_TYPE;
		} else if (isUnknownType(binding)) {
			return UNKNOWN_TYPE;
		} else if (isMissingType(binding)) {
			return MISSING_TYPE;
		} else if (isSuperMissingType(binding)) {
			return MISSING_SUPER_TYPE;
		} else if (isFullType(binding)) {
			return FULL_TYPE;
		} else {
			// We've got a problem;
			assert false;
			return -100;
		}
	}
	
	public static String getShortMethodSignature(final IMethodBinding methodBinding) {
		StringBuffer buffer = new StringBuffer();
		int numArgs = getArgsLength(methodBinding);
		ITypeBinding[] params = methodBinding.getParameterTypes();
		ITypeBinding returnType = methodBinding.getReturnType();
		String name = methodBinding.getName();
		
		buffer.append(getTypeString(returnType));
		buffer.append(" ");
		buffer.append(name);
		buffer.append("(");
		
		for (int i = 0; i < numArgs; i++) {
			buffer.append(getTypeString(params[i]));
			if (i < numArgs - 1) {
				buffer.append(",");
			}
		}
		
		buffer.append(")");
		
		return buffer.toString();
	}
	
	public static String getSimpleName(final String name) {
		ValidatorUtil.validateEmpty(name, "name", true);
		String simpleName = name;
		int index = name.lastIndexOf(".");
		if ((index != -1) && (index != (name.length() - 1))) {
			simpleName = name.substring(index + 1);
		}
		
		return simpleName;
	}
	
	public static ITypeBinding getTypeBinding(final ASTNode node) {
		ITypeBinding typeBinding = null;
		
		if (node == null) {
			typeBinding = null;
		} else if (node instanceof TypeDeclaration) {
			TypeDeclaration typeDec = (TypeDeclaration) node;
			typeBinding = typeDec.resolveBinding();
		} else if (node instanceof AnonymousClassDeclaration) {
			AnonymousClassDeclaration anon = (AnonymousClassDeclaration) node;
			typeBinding = anon.resolveBinding();
		} else if (node instanceof EnumDeclaration) {
			EnumDeclaration enumD = (EnumDeclaration) node;
			typeBinding = enumD.resolveBinding();
		} else if (node instanceof Name) {
			Name name = (Name) node;
			IBinding binding = name.resolveBinding();
			if (binding instanceof IVariableBinding) {
				// XXX This is to avoid the nasty .getDeclaringClass in
				// qualified name reference.
				IVariableBinding varBinding = (IVariableBinding) binding;
				typeBinding = varBinding.getType();
			} else if (binding instanceof IMethodBinding) {
				IMethodBinding methodBinding = (IMethodBinding) binding;
				typeBinding = methodBinding.getReturnType();
			} else {
				typeBinding = name.resolveTypeBinding();
			}
		} else if (node instanceof Expression) {
			Expression exp = (Expression) node;
			typeBinding = exp.resolveTypeBinding();
		} else if (node instanceof Type) {
			Type typeNode = (Type) node;
			typeBinding = typeNode.resolveBinding();
		}
		
		return typeBinding;
	}
	
	public static String getTypeString(final ITypeBinding binding) {
		if (binding == null) {
			return "nil";
		} else {
			return binding.getQualifiedName();
		}
	}
	
	public static boolean isComplexName(final String name) {
		ValidatorUtil.validateEmpty(name, "name", true);
		return name.contains(".");
	}
	
	public static boolean isConventionalClassName(final String name) {
		String simpleName = getSimpleName(name);
		boolean isConventional = Character.isUpperCase(simpleName.charAt(0));
		isConventional = isConventional
		&& !simpleName.toUpperCase().equals(simpleName);
		return isConventional;
	}
	
	public static boolean isEquivalent(final IBinding binding1, final IBinding binding2) {
		boolean isEquivalent = binding1.getKey().equals(binding2.getKey());
		if (!isEquivalent) {
			if ((binding1 instanceof IVariableBinding)
					&& (binding2 instanceof IVariableBinding)) {
				isEquivalent = isEquivalent((IVariableBinding) binding1,
						(IVariableBinding) binding2);
			} else if ((binding1 instanceof IMethodBinding)
					&& (binding2 instanceof IMethodBinding)) {
				isEquivalent = isEquivalent((IMethodBinding) binding1,
						(IMethodBinding) binding2);
			}
		}
		return isEquivalent;
	}
	
	public static boolean isEquivalent(final IMethodBinding binding1,
			final IMethodBinding binding2) {
		boolean isEquivalent = false;
		
		if (binding1.getName().equals(binding2.getName())
				&& (getArgsLength(binding1) == getArgsLength(binding2))) {
			isEquivalent = binding1.getReturnType().getKey().equals(
					binding2.getReturnType().getKey())
					|| binding1.getDeclaringClass().getKey().equals(
							binding2.getDeclaringClass().getKey());
		}
		
		return isEquivalent;
	}
	
	public static boolean isEquivalent(final IVariableBinding binding1,
			final IVariableBinding binding2) {
		boolean isEquivalent = false;
		
		if (binding1.getName().equals(binding2.getName()) && binding1.isField()
				&& binding2.isField()) {
			isEquivalent = binding1.getType().getKey().equals(
					binding2.getType().getKey());
			
			if (!isEquivalent) {
				ITypeBinding dClass1 = binding1.getDeclaringClass();
				ITypeBinding dClass2 = binding2.getDeclaringClass();
				if (((dClass1 == null) && (dClass2 != null))
						|| ((dClass1 != null) && (dClass2 == null))) {
					isEquivalent = false;
				} else {
					isEquivalent = ((dClass1 == null) && (dClass2 == null))
					|| dClass1.getKey().equals(dClass2.getKey());
				}
			}
		}
		
		return isEquivalent;
	}
	
	public static boolean isFullType(final ITypeBinding typeBinding) {
		return !isProblemType(typeBinding) && !isUnknownType(typeBinding)
		&& !isMissingType(typeBinding)
		&& (getFirstMissingSuperType(typeBinding) == null);
	}
	
	/**
	 * 
	 * @param typeBinding
	 * @return True if the type declaration is missing (unknown is a missing
	 *         type too).
	 */
	public static boolean isMissingType(final ITypeBinding typeBinding) {
		org.eclipse.jdt.internal.compiler.lookup.TypeBinding binding = null;
		if (typeBinding instanceof TypeBinding) {
			TypeBinding tb = (TypeBinding) typeBinding;
			binding = tb.binding;
			
		}
		
		return (binding instanceof PPAType);
	}
	
	public static boolean isMissingType(
			final org.eclipse.jdt.internal.compiler.lookup.TypeBinding typeBinding) {
		return (typeBinding instanceof PPAType);
	}
	
	public static boolean isNullType(
			final org.eclipse.jdt.internal.compiler.lookup.TypeBinding typeBinding) {
		boolean isNull = false;
		
		if (typeBinding.isBaseType()) {
			BaseTypeBinding base = (BaseTypeBinding) typeBinding;
			isNull = new String(base.simpleName).equals("null");
		}
		
		return isNull;
	}
	
	public static boolean isPrimitiveName(final String name) {
		return Arrays.binarySearch(PPATypeRegistry.PRIMITIVES, name) > PROBLEM_TYPE;
	}
	
	public static boolean isProblemType(final ITypeBinding typeBinding) {
		org.eclipse.jdt.internal.compiler.lookup.TypeBinding binding = null;
		if (typeBinding instanceof TypeBinding) {
			TypeBinding tb = (TypeBinding) typeBinding;
			binding = tb.binding;
			
		}
		
		return isProblemType(binding);
	}
	
	public static boolean isProblemType(
			final org.eclipse.jdt.internal.compiler.lookup.TypeBinding typeBinding) {
		return (typeBinding == null)
		|| (typeBinding instanceof ProblemReferenceBinding)
		|| ((typeBinding.tagBits & TagBits.HasMissingType) != 0);
	}
	
	/**
	 * 
	 * @param binding1
	 * @param binding2
	 * @return true if binding1 is safer than binding2
	 */
	public static boolean isSafer(final ITypeBinding binding1, final ITypeBinding binding2) {
		return getSafetyValue(binding1) > getSafetyValue(binding2);
	}
	
	public static boolean isStarImport(final String packageName) {
		ValidatorUtil.validateEmpty(packageName, "packageName", true);
		return packageName.endsWith("*");
	}
	
	public static boolean isSuperMissingType(final ITypeBinding typeBinding) {
		return getFirstMissingSuperType(typeBinding) != null;
	}
	
	public static boolean isUnknownType(final ITypeBinding typeBinding) {
		boolean isUnknown = false;
		org.eclipse.jdt.internal.compiler.lookup.TypeBinding binding = null;
		if (typeBinding instanceof TypeBinding) {
			TypeBinding tb = (TypeBinding) typeBinding;
			binding = tb.binding;
			
		}
		if (binding != null) {
			isUnknown = isUnknownType(binding);
		}
		
		return isUnknown;
	}
	
	public static boolean isUnknownType(
			final org.eclipse.jdt.internal.compiler.lookup.TypeBinding typeBinding) {
		
		return PPATypeRegistry.UNKNOWN_CLASS_FQN.equals(typeBinding.toString());
	}
	
	@SuppressWarnings("unchecked")
	private static void reportNewConstructorBinding(final PPAIndexer indexer,
			final PPAEngine engine, final ITypeBinding unknownType,
			final ClassInstanceCreation cic, final ITypeBinding[] paramTypes,
			final IMethodBinding newBinding) {
		TypeFact tFact = null;
		
		// Report params type.
		ITypeBinding[] newParams = newBinding.getParameterTypes();
		List args = cic.arguments();
		for (int i = 0; i < paramTypes.length; i++) {
			ASTNode arg = (ASTNode) args.get(i);
			if (indexer.isIndexable(arg)) {
				tFact = new TypeFact(indexer.getMainIndex(arg), paramTypes[i],
						TypeFact.UNKNOWN, newParams[i], TypeFact.SUBTYPE,
						TypeFact.METHOD_STRATEGY);
				engine.reportTypeFact(tFact);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void reportNewMethodBinding(final PPAIndexer indexer,
			final PPAEngine ppaEngine, final ITypeBinding unknownType, final ASTNode method,
			final ITypeBinding[] previousParamTypes, final IMethodBinding newBinding) {
		// Report return type.
		TypeFact tFact = new TypeFact(indexer.getMainIndex(method),
				unknownType, TypeFact.UNKNOWN, newBinding.getReturnType(),
				TypeFact.EQUALS, TypeFact.METHOD_STRATEGY);
		ppaEngine.reportTypeFact(tFact);
		
		// Report params type.
		ITypeBinding[] newParams = newBinding.getParameterTypes();
		List args = MethodInvocationUtil.getArguments(method);
		for (int i = 0; i < previousParamTypes.length; i++) {
			ASTNode arg = (ASTNode) args.get(i);
			if (indexer.isIndexable(arg)) {
				tFact = new TypeFact(indexer.getMainIndex(arg),
						previousParamTypes[i], TypeFact.UNKNOWN, newParams[i],
						TypeFact.SUBTYPE, TypeFact.METHOD_STRATEGY);
				ppaEngine.reportTypeFact(tFact);
			}
		}
	}
}
