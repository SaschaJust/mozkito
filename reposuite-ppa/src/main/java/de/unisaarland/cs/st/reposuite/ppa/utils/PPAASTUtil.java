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
package de.unisaarland.cs.st.reposuite.ppa.utils;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PPABindingsUtil;
import org.eclipse.jdt.core.dom.PPATypeRegistry;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WhileStatement;

public class PPAASTUtil {
	
	private static final String OBJECT_FQN = "java.lang.Object";
	
	/**
	 * 
	 * @param mi
	 * @return True if this is a final method from Object
	 */
	private static boolean checkObjectFinalMethod(final MethodInvocation mi) {
		boolean isObjectFinal = false;
		IMethodBinding mb = mi.resolveMethodBinding();
		
		if (mb != null) {
			isObjectFinal = Modifier.isFinal(mb.getModifiers())
			&& mb.getDeclaringClass().getQualifiedName().equals(
					OBJECT_FQN);
		}
		
		return isObjectFinal;
	}
	
	/**
	 * XXX When changing getContainer, getMethodContainer, and
	 * isMethodContainer, do not forget to change getTypeBinding
	 * 
	 * @param mi
	 * @return
	 */
	public static ASTNode getContainer(final MethodInvocation mi) {
		ASTNode container = mi.getExpression();
		if (container == null) {
			container = PPAASTUtil.getMethodContainer(mi);
		}
		return container;
	}
	
	/**
	 * <p>
	 * Convenience method to exclude self and/or name.
	 * </p>
	 * 
	 * @param node
	 * @param includeSelf
	 * @return
	 */
	public static ASTNode getFieldContainer(final ASTNode node,
			final boolean includeSpecial, final boolean includeSelf) {
		ASTNode container = null;
		
		// XXX Special case: if you want the container of the first qualifier in
		// a qualified name,
		// You want the type declaration (or whatever is the top-level parent),
		// but not the
		// qualified name.
		if (!includeSelf && includeSpecial) {
			ASTNode parent = node.getParent();
			if (parent instanceof QualifiedName) {
				QualifiedName qName = (QualifiedName) parent;
				if (qName.getQualifier().equals(node)) {
					return getFieldContainer(parent, includeSpecial,
							includeSelf);
				}
			}
		}
		
		if (node == null) {
			container = null;
		} else if (includeSelf && isFieldContainer(node, includeSpecial)) {
			if (node instanceof QualifiedName) {
				container = ((QualifiedName) node).getQualifier();
			} else if (node instanceof SuperFieldAccess) {
				SuperFieldAccess sFieldAccess = (SuperFieldAccess) node;
				if (sFieldAccess.getQualifier() != null) {
					container = sFieldAccess.getQualifier();
				} else {
					container = getFieldContainer(node.getParent(),
							includeSpecial, true);
				}
			} else if (node instanceof FieldAccess) {
				container = ((FieldAccess) node).getExpression();
			} else {
				container = node;
			}
		} else {
			container = getFieldContainer(node.getParent(), includeSpecial,
					true);
		}
		
		return container;
	}
	
	/**
	 * 
	 * @param name
	 * @return The root FQN of a name.
	 */
	public static String getFQNFromAnyName(final Name name) {
		String fqn = null;
		ASTNode node = name;
		
		do {
			Name tempName = (Name) node;
			fqn = tempName.getFullyQualifiedName();
			node = tempName.getParent();
		} while ((node != null) && (node instanceof Name));
		
		return fqn;
	}
	
	public static ASTNode getMethodContainer(final ASTNode node) {
		ASTNode container = null;
		
		if (node == null) {
			container = null;
		} else if (isMethodContainer(node)) {
			container = node;
		} else {
			container = getMethodContainer(node.getParent());
		}
		
		return container;
	}
	
	public static String getNameFromAnon(final AnonymousClassDeclaration anon) {
		String name = PPATypeRegistry.UNKNOWN_CLASS_FQN;
		ASTNode parent = anon.getParent();
		if (parent instanceof ClassInstanceCreation) {
			ClassInstanceCreation cic = (ClassInstanceCreation) parent;
			Type type = cic.getType();
			if (type instanceof SimpleType) {
				name = ((SimpleType) type).getName().getFullyQualifiedName();
			} else if (type instanceof QualifiedType) {
				name = ((QualifiedType) type).getName().getFullyQualifiedName();
			}
			
		}
		// Parent could also be enum constant declaration, although this is a
		// lot less interesting
		// for PPA than anon.
		
		return name;
	}
	
	public static String getPackageName(final CompilationUnit cu) {
		String packageName = null;
		PackageDeclaration pDeclaration = cu.getPackage();
		
		if (pDeclaration != null) {
			packageName = pDeclaration.getName().getFullyQualifiedName();
		}
		
		return packageName;
	}
	
	/**
	 * <p>
	 * Returns the name + its qualifier if any. Does not return the suffix of
	 * the FQN.
	 * </p>
	 * <ol>
	 * <li>A : returns A</li>
	 * <li>A.B : returns A</li>
	 * <li>C.A : return C.A</li>
	 * <li>C.A.B : returns C.A</li>
	 * </ol>
	 * 
	 * @param name
	 * @return
	 */
	public static String getQualifierPlusName(final SimpleName name) {
		String qualifierPlusName = name.getFullyQualifiedName();
		
		ASTNode parent = name.getParent();
		if ((parent != null) && (parent instanceof QualifiedName)) {
			QualifiedName qName = (QualifiedName) parent;
			if (qName.getName().equals(name)) {
				qualifierPlusName = qName.getFullyQualifiedName();
			}
		}
		return qualifierPlusName;
	}
	
	public static ASTNode getSpecificParentType(final ASTNode node, final int type) {
		ASTNode specificParent = null;
		
		if (node != null) {
			if (node.getNodeType() == type) {
				specificParent = node;
			} else {
				specificParent = getSpecificParentType(node.getParent(), type);
			}
		}
		
		return specificParent;
	}
	
	/**
	 * 
	 * @param name
	 * @return True if this name represents an annotation.
	 */
	public static boolean isAnnotation(final Name name) {
		boolean isAnnotation = false;
		
		if (name != null) {
			ASTNode node = name.getParent();
			if ((node != null) && (node instanceof Annotation)) {
				isAnnotation = ((Annotation) node).getTypeName().equals(name);
			}
		}
		return isAnnotation;
	}
	
	public static boolean isFieldContainer(final ASTNode node, final boolean includeSpecial) {
		return (node != null)
		&& ((node instanceof TypeDeclaration)
				|| (node instanceof AnonymousClassDeclaration)
				|| (node instanceof FieldAccess)
				|| (includeSpecial && (node instanceof Name)) || (includeSpecial && (node instanceof SuperFieldAccess)));
	}
	
	public static boolean isIndicationOfField(final List<Name> nodes) {
		boolean isIndicationOfField = false;
		
		for (Name node : nodes) {
			if (isIndicationOfField(node)) {
				isIndicationOfField = true;
				break;
			}
		}
		
		return isIndicationOfField;
	}
	
	public static boolean isIndicationOfField(Name node) {
		boolean isField = false;
		
		ASTNode parent = node.getParent();
		
		// The thing here is that if the parent is a qualified name, go one
		// level up.
		// If the parent is still a qualified name, then, it means we were in
		// the middle, so this is
		// not necessarily a field.
		if (parent instanceof QualifiedName) {
			QualifiedName qName = (QualifiedName) parent;
			if (qName.getName().equals(node)) {
				node = (Name) parent;
				parent = parent.getParent();
			}
		}
		
		if (parent instanceof ArrayAccess) {
			isField = true;
			// } else if (parent instanceof ArrayCreation) {
			// isField = true;
		} else if (parent instanceof ArrayInitializer) {
			isField = true;
		} else if (parent instanceof Assignment) {
			isField = true;
		} else if (parent instanceof CastExpression) {
			CastExpression cExpression = (CastExpression) parent;
			isField = cExpression.getExpression().equals(node);
		} else if (parent instanceof ConditionalExpression) {
			isField = true;
		} else if (parent instanceof InfixExpression) {
			isField = true;
		} else if (parent instanceof WhileStatement) {
			isField = true;
		} else if (parent instanceof DoStatement) {
			isField = true;
		} else if (parent instanceof ForStatement) {
			ForStatement forStatement = (ForStatement) parent;
			isField = forStatement.getExpression().equals(node);
		} else if (parent instanceof PrefixExpression) {
			isField = true;
		} else if (parent instanceof PostfixExpression) {
			isField = true;
		} else if (parent instanceof ReturnStatement) {
			isField = true;
		} else if (parent instanceof InstanceofExpression) {
			InstanceofExpression ioe = (InstanceofExpression) parent;
			isField = ioe.getLeftOperand().equals(node);
		} else if (parent instanceof FieldAccess) {
			isField = true;
		} else if (parent instanceof SuperFieldAccess) {
			isField = true;
		} else if (parent instanceof MethodInvocation) {
			MethodInvocation mi = (MethodInvocation) parent;
			for (Object object : mi.arguments()) {
				if (node == object) {
					isField = true;
					break;
				}
			}
		} else if (parent instanceof ClassInstanceCreation) {
			ClassInstanceCreation cic = (ClassInstanceCreation) parent;
			for (Object object : cic.arguments()) {
				if (node == object) {
					isField = true;
					break;
				}
			}
		} else if (parent instanceof VariableDeclarationFragment) {
			isField = true;
		}
		
		return isField;
	}
	
	public static boolean isMethodContainer(final ASTNode node) {
		return (node != null)
		&& ((node instanceof TypeDeclaration)
				|| (node instanceof AnonymousClassDeclaration) || (node instanceof EnumDeclaration));
	}
	
	/**
	 * Return true if 1) this is a method invocation and 2) one of the parameter
	 * is not full.
	 * 
	 * Always return false if this is a method invocation from Object and that
	 * is final.
	 * 
	 * @param node
	 * @return
	 */
	public static boolean isUnsafeMethod(final SimpleName node) {
		boolean isUnsafe = false;
		ASTNode parent = node.getParent();
		if (parent instanceof MethodInvocation) {
			MethodInvocation mi = (MethodInvocation) parent;
			if (mi.getName() == node) {
				
				if (!checkObjectFinalMethod(mi)) {
					for (Object arg : mi.arguments()) {
						Expression exp = (Expression) arg;
						if (PPABindingsUtil.getSafetyValue(PPABindingsUtil
								.getTypeBinding(exp)) < PPABindingsUtil.FULL_TYPE) {
							isUnsafe = true;
							break;
						}
					}
					
					if (!isUnsafe) {
						ITypeBinding containerType = PPABindingsUtil
						.getTypeBinding(getContainer(mi));
						isUnsafe = PPABindingsUtil
						.getSafetyValue(containerType) < PPABindingsUtil.FULL_TYPE;
					}
				}
			}
		}
		
		return isUnsafe;
	}
	
}
