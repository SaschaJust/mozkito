/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils.specification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import de.unisaarland.cs.st.reposuite.utils.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteClassLoader extends ClassLoader {
	
	static final Package pakkage          = RepoSuiteClassLoader.class.getPackage();
	static final Package conditionPakkage = Condition.class.getPackage();
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		ClassLoader cl = new RepoSuiteClassLoader();
		Class<?> c;
		try {
			c = cl.loadClass(pakkage.getName() + ".Test");
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		try {
			CtClass cc = this.cp.get(name);
			return processAnnotations(cc).toClass();
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
		for (CtMethod method : cc.getDeclaredMethods()) {
			// fetch annotations
			
			MethodInfo minfo = method.getMethodInfo();
			AnnotationsAttribute attr = (AnnotationsAttribute) minfo.getAttribute(AnnotationsAttribute.visibleTag);
			if (attr != null) {
				Annotation an = attr.getAnnotation(pakkage.getName() + ".NotNull");
				if (an != null) {
					Set<String> memberNames = an.getMemberNames();
					for (String memberName : memberNames) {
						System.err.println(memberName + " : " + an.getMemberValue(memberName));
						try {
							method.insertBefore(conditionPakkage.getName() + ".Condition.notNull(i);");
						} catch (CannotCompileException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
			ParameterAnnotationsAttribute attributes = (ParameterAnnotationsAttribute) minfo
			        .getAttribute(ParameterAnnotationsAttribute.visibleTag);
			Annotation[][] annotations = attributes.getAnnotations();
			
			for (Annotation[] parameterAnnotations : annotations) {
				for (Annotation annotation : parameterAnnotations) {
					if (annotation.getTypeName().equals(pakkage.getName() + ".NotNull")) {
						try {
							
							method.insertBefore(conditionPakkage.getName() + ".Condition.notNull( " + "i" + " );");
						} catch (CannotCompileException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}
		}
		return cc;
	}
	
}
