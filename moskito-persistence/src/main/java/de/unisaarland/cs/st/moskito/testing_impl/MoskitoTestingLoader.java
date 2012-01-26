package de.unisaarland.cs.st.moskito.testing_impl;

import java.util.HashMap;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import net.ownhero.dev.kanuni.instrumentation.KanuniClassloader;

/**
 * 
 */

/**
 * @author just
 * 
 */
public class MoskitoTestingLoader extends ClassLoader {
	
	/**
	 * the {@link ClassPool} instance to manage classes loaded by javassist. For
	 * a list of packages loaded by the bootstrap classloader see documentation
	 * of {@link KanuniClassloader#loadClass(String)}.
	 */
	private static ClassPool                 classPool    = ClassPool.getDefault();
	private final MoskitoTestingInstrumenter instrumenter = new MoskitoTestingInstrumenter();
	private static Map<String, Class<?>>     cache        = new HashMap<String, Class<?>>();
	
	static {
		// in general, this should not be necessary, since
		// ClassPool.getDefault() should already do this. But in some scenarios
		// this might not work properly according to the javassist
		// documentation.
		classPool.appendSystemPath();
	}
	
	/**
	 * This constructor will never be called by the VM itself. If you call the
	 * constructor manually make sure that {@link KanuniClassloader} isn't the
	 * system's class loader. Otherwise this will cause a stack overflow.
	 * 
	 * The only reason that this constructor exists is for testing purpose. You
	 * can load single annotated classes and test the annotations under suspect.
	 */
	public MoskitoTestingLoader() {
		MoskitoTestingLoader.classPool.insertClassPath(new ClassClassPath(this.getClass()));
		MoskitoTestingLoader.classPool.insertClassPath(new LoaderClassPath(getSystemClassLoader()));
	}
	
	/**
	 * The constructor called when bootstrapping the VM and having the system
	 * bootloader set to {@link KanuniClassloader}.
	 * 
	 * @param arg0
	 *            the parent class loader
	 */
	public MoskitoTestingLoader(final ClassLoader arg0) {
		super(arg0);
		MoskitoTestingLoader.classPool.insertClassPath(new ClassClassPath(this.getClass()));
		try {
			MoskitoTestingLoader.classPool.insertClassPath(System.getProperty("java.class.path", "."));
		} catch (final NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		System.err.println("Loading class: " + name);
		final int i = name.lastIndexOf('.');
		//@formatter:off
		
		/* skip classes that may not be stubbed
		 * (taken from the JUnit exclude list)
		 * 
		 * sun.*
		 * com.sun.*
		 * org.omg.*
		 * javax.*
		 * sunw.*
		 * java.*
		 * org.w3c.dom.*
		 * org.xml.sax.*
		 * net.jini.*
		 */
		if (name.startsWith("sun.")
				|| name.startsWith("com.sun.")
				|| name.startsWith("org.omg.")
				|| name.startsWith("javax.")
				|| name.startsWith("sunw.")
				|| name.startsWith("java.")
				|| name.startsWith("org.w3c.dom.")
				|| name.startsWith("org.xml.sax.")
				|| name.startsWith("net.jini.")
				|| name.startsWith("org.eclipse.")
				|| name.startsWith("org.ccil.")
				|| (i < 0)) {
			//@formatter:on
			return getParent().loadClass(name);
		} else {
			try {
				if (cache.containsKey(name)) {
					return cache.get(name);
				}
				
				CtClass ctClass = classPool.get(name);
				
				if (i != -1) {
					final String pname = name.substring(0, i);
					if (getPackage(pname) == null) {
						try {
							definePackage(pname, null, null, null, null, null, null, null);
						} catch (final IllegalArgumentException e) {
							// ignore. maybe the package object for the same
							// name has been created just right away.
						}
					}
				}
				
				Class<?> c = null;
				// only instrument if assertions are enabled
				
				ctClass = this.instrumenter.process(ctClass);
				
				try {
					c = ctClass.toClass();
				} catch (final NullPointerException e) {
					System.err.println(">$>$>$>$>$");
					System.err.println("$$$$$$$$$$ " + ctClass.getName());
				}
				
				cache.put(name, c);
				return c;
			} catch (final NotFoundException e) {
				throw new ClassNotFoundException(e.getMessage(), e);
			} catch (final CannotCompileException e) {
				// in case of an compile error, try the parent bootloader
				return getParent().loadClass(name);
			}
		}
	}
}
