/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils.specification;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationDefaultAttribute;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import de.unisaarland.cs.st.reposuite.exceptions.WrongClassSearchMethodException;
import de.unisaarland.cs.st.reposuite.utils.ClassFinder;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;

/**
 * 
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteClassLoader extends ClassLoader {
	
	static final Package             pakkage                = RepoSuiteClassLoader.class.getPackage();
	static final Package             conditionPakkage       = Condition.class.getPackage();
	
	static final Map<String, String> parameterAnnotations   = new HashMap<String, String>();
	static final Map<String, String> methodAnnotations      = new HashMap<String, String>();
	static final Map<String, String> constructorAnnotations = new HashMap<String, String>();
	
	private static boolean           assertionsEnabled      = false;
	
	private final LinkedList<String> insertions             = new LinkedList<String>();
	
	static {
		try {
			assert (false);
		} catch (AssertionError e) {
			assertionsEnabled = true;
			System.err.println("Switched to development mode: Specification checks enabled.");
		}
		
		// load all annotations in `pakkage`
		// foreach annotation
		// -- check @target
		// -- put to corresponding map(s)
		
		try {
			Collection<Class<?>> annotationClasses = ClassFinder.getAllClasses(pakkage);
			for (Class<?> c : annotationClasses) {
				if (c.isAnnotation()) {
					Class<java.lang.annotation.Annotation> aC = (Class<java.lang.annotation.Annotation>) c;
					ConditionPattern pattern = aC.getAnnotation(ConditionPattern.class);
					Target target = aC.getAnnotation(Target.class);
					
					if ((target != null) && (pattern != null)) {
						for (ElementType t : target.value()) {
							switch (t) {
								case PARAMETER:
									parameterAnnotations.put(aC.getCanonicalName(), pattern.value());
									break;
								case METHOD:
									methodAnnotations.put(aC.getCanonicalName(), pattern.value());
									break;
								case CONSTRUCTOR:
									constructorAnnotations.put(aC.getCanonicalName(), pattern.value());
									break;
								default:
									// TODO error
							}
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongClassSearchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		ClassLoader cl = new RepoSuiteClassLoader();
		Class<?> c;
		try {
			c = cl.loadClass("de.unisaarland.cs.st.reposuite.utils" + ".Test");
			Object instance = c.newInstance();
			Method method = c.getMethod("test", Integer.class);
			method.invoke(instance, (Integer) null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
	}
	
	ClassPool cp = ClassPool.getDefault();
	
	/**
	 * 
	 */
	public RepoSuiteClassLoader() {
		this.cp.insertClassPath(new ClassClassPath(this.getClass()));
		this.cp.insertClassPath(new LoaderClassPath(getSystemClassLoader()));
	}
	
	/**
	 * @param parent
	 */
	public RepoSuiteClassLoader(final ClassLoader parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}
	
	private String getAnnotationFormatString(final Annotation annotation,
	                                         final String string) {
		LinkedList<Object> arguments = new LinkedList<Object>();
		Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
		Matcher matcher = pattern.matcher(string);
		while (matcher.find()) {
			String memberName = matcher.group(1);
			Object memberValue = annotation.getMemberValue(memberName);
			if (memberValue == null) {
				// determine default value
				CtClass ctClass;
				try {
					ctClass = this.cp.get(annotation.getTypeName());
					MethodInfo info = ctClass.getDeclaredMethod(memberName).getMethodInfo();
					AnnotationDefaultAttribute ada = (AnnotationDefaultAttribute) info.getAttribute(AnnotationDefaultAttribute.tag);
					memberValue = ada.getDefaultValue();
				} catch (NotFoundException e) {
					e.printStackTrace();
				}
			}
			arguments.add(memberValue);
		}
		if (!arguments.isEmpty()) {
			System.err.println("formatting :" + string.replaceAll("\\$\\{[^}]+\\}", "%s") + " with "
			        + JavaUtils.collectionToString(arguments));
			return String.format(string.replaceAll("\\$\\{[^}]+\\}", "%s"), arguments.toArray()).toString()
			             .replaceAll("\\#\\{target\\}", "%s");
		} else {
			return string;
		}
		
	}
	
	private String getConditionString(final String formatString,
	                                  final Object... arguments) {
		return Condition.class.getCanonicalName() + "." + new Formatter().format(formatString, arguments) + ";";
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		try {
			CtClass cc = this.cp.get(name);
			return assertionsEnabled
			                        ? processAnnotations(cc).toClass()
			                        : cc.toClass();
		} catch (NotFoundException e) {
			throw new ClassNotFoundException(e.getMessage(), e);
		} catch (CannotCompileException e) {
			throw new ClassNotFoundException(e.getMessage(), e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	@Override
	protected synchronized Class<?> loadClass(final String name,
	                                          final boolean resolve) throws ClassNotFoundException {
		Class<?> c = loadClass(name);
		if (resolve) {
			resolveClass(c);
		}
		return c;
	}
	
	/**
	 * @param cc
	 * @return
	 */
	private CtClass processAnnotations(final CtClass cc) {
		// 1. PROCESS METHODS/CONSTRUCTORS
		// 2. `- PROCESS PARAMETERS
		
		for (CtMethod method : cc.getDeclaredMethods()) {
			processMethodAnnotations(method);
			String insertionString = null;
			System.err.println("Inserting at beginning of " + method.getName() + ": "
			        + JavaUtils.collectionToString(this.insertions));
			for (ListIterator<String> it = this.insertions.listIterator(this.insertions.size()); it.hasPrevious();) {
				try {
					insertionString = it.previous();
					System.err.println("Inserting before: " + insertionString);
					method.insertBefore(insertionString);
				} catch (CannotCompileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.insertions.clear();
		}
		
		for (CtConstructor constructor : cc.getDeclaredConstructors()) {
			processConstructorAnnotations(constructor);
			String insertionString = null;
			for (ListIterator<String> it = this.insertions.listIterator(this.insertions.size()); it.hasPrevious(); insertionString = it.previous()) {
				try {
					constructor.insertBefore(insertionString);
				} catch (CannotCompileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.insertions.clear();
		}
		return cc;
	}
	
	private void processConstructorAnnotations(final CtConstructor constructor) {
		// TODO Auto-generated method stub
		
	}
	
	private void processConstructorParameterAnnotations(final CtConstructor constructor,
	                                                    final Annotation[][] annotations) {
		
	}
	
	private void processMethodAnnotations(final CtMethod method) {
		MethodInfo minfo = method.getMethodInfo();
		AnnotationsAttribute attr = (AnnotationsAttribute) minfo.getAttribute(AnnotationsAttribute.visibleTag);
		if (attr != null) {
			Annotation[] annotations = attr.getAnnotations();
			for (Annotation annotation : annotations) {
				// Annotation an = attr.getAnnotation(pakkage.getName() +
				// ".NotNull");
				System.err.println("Checking if " + annotation.getTypeName() + " is known in "
				        + JavaUtils.mapToString(methodAnnotations));
				if (methodAnnotations.containsKey(annotation.getTypeName())) {
					// @SuppressWarnings ("unchecked")
					// Set<String> memberNames =
					// annotation.getMemberNames();
					// for (String memberName : memberNames) {
					// System.err.println(memberName + " : " +
					// an.getMemberValue(memberName));
					// try {
					// method.insertBefore(conditionPakkage.getName() +
					// ".Condition.notNull(i);");
					// } catch (CannotCompileException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					// }
					// RETURN
					
					// FIELD
					
					// ELSE
					@SuppressWarnings ("unchecked")
					Set<String> memberNames = annotation.getMemberNames();
					if ((memberNames != null) && !memberNames.isEmpty()) {
						// e.g. @NotNull(parameter="i")
						// e.g. @Field (...)
						String formatString = getAnnotationFormatString(annotation,
						                                                methodAnnotations.get(annotation.getTypeName()));
						String conditionString = getConditionString(formatString);
						this.insertions.add(conditionString);
					} else {
						// e.g. @NoneNull
						String formatString = getAnnotationFormatString(annotation,
						                                                methodAnnotations.get(annotation.getTypeName()));
						String conditionString = getConditionString(formatString);
						this.insertions.add(conditionString);
						
					}
				}
			}
		}
		
		ParameterAnnotationsAttribute attributes = (ParameterAnnotationsAttribute) minfo.getAttribute(ParameterAnnotationsAttribute.visibleTag);
		if (attributes != null) {
			Annotation[][] annotations = attributes.getAnnotations();
			
			processMethodParameterAnnotations(method, annotations);
		}
	}
	
	private void processMethodParameterAnnotations(final CtMethod method,
	                                               final Annotation[][] annotations) {
		int i = 1;
		for (Annotation[] pAnnotations : annotations) {
			for (Annotation annotation : pAnnotations) {
				if (RepoSuiteClassLoader.parameterAnnotations.containsKey(annotation.getTypeName())) {
					String formatString = getAnnotationFormatString(annotation,
					                                                parameterAnnotations.get(annotation.getTypeName()));
					String conditionString = getConditionString(formatString, "$" + i);
					this.insertions.add(conditionString);
					
				}
			}
			++i;
		}
	}
}
