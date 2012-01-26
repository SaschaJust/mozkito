/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing_impl;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;

/**
 * @author just
 * 
 */
public class MoskitoTestingAgent implements ClassFileTransformer {
	
	private static Instrumentation instrumentation;
	
	/**
	 * @param agentArgs
	 * @param inst
	 */
	public static void agentmain(final String agentArgs,
	                             final Instrumentation inst) {
		premain(agentArgs, inst);
	}
	
	/**
	 * Programmatic hook to dynamically load javaagent at runtime.
	 */
	public static void initialize() {
		if (instrumentation == null) {
			MoskitoTestingAgentLoader.loadAgent();
		}
	}
	
	public static void premain(final String agentArgument,
	                           final Instrumentation instrumentation) {
		MoskitoTestingAgent.instrumentation = instrumentation;
		if (agentArgument != null) {
			final String[] args = agentArgument.split(",");
			final Set<String> argSet = new HashSet<String>(Arrays.asList(args));
			
			if (argSet.contains("time")) {
				System.out.println("Start at " + new Date());
				Runtime.getRuntime().addShutdownHook(new Thread() {
					
					@Override
					public void run() {
						System.out.println("Stop at " + new Date());
					}
				});
			}
		}
		instrumentation.addTransformer(new MoskitoTestingAgent());
	}
	
	private final MoskitoTestingInstrumenter instrumenter = new MoskitoTestingInstrumenter();
	
	/**
	 * @param name
	 * @param classBeingRedefined
	 * @param classfileBuffer
	 * @return the modified class file buffer
	 */
	private byte[] doClass(final String name,
	                       final Class<?> classBeingRedefined,
	                       byte[] classfileBuffer) {
		System.err.println("Processing class " + name);
		CtClass cl = null;
		final ClassPool classPool = ClassPool.getDefault();
		
		try {
			cl = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
			
			if (cl.isInterface() == false) {
				this.instrumenter.process(cl);
				classfileBuffer = cl.toBytecode();
			}
		} catch (final Exception e) {
			System.err.println("Could not instrument  " + name + ",  exception : " + e.getMessage());
		} finally {
			if (cl != null) {
				cl.detach();
			}
		}
		return classfileBuffer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader
	 * , java.lang.String, java.lang.Class, java.security.ProtectionDomain,
	 * byte[])
	 */
	@Override
	public byte[] transform(final ClassLoader classLoader,
	                        final String name,
	                        final Class<?> classBeingRedefined,
	                        final ProtectionDomain protectionDomain,
	                        final byte[] classfileBuffer) throws IllegalClassFormatException {
		System.err.println("Transform filter for: " + name);
		final int i = name.lastIndexOf('/');
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
		if (name.startsWith("sun/")
				|| name.startsWith("com/sun/")
				|| name.startsWith("org/omg/")
				|| name.startsWith("javax/")
				|| name.startsWith("sunw/")
				|| name.startsWith("java/")
				|| name.startsWith("org/w3c/dom/")
				|| name.startsWith("org/xml/sax/")
				|| name.startsWith("net/jini/")
				|| name.startsWith("org/eclipse/")
				|| name.startsWith("org/ccil/")
				|| (i < 0)) {
			//@formatter:on
			return classfileBuffer;
		} else {
			return doClass(name, classBeingRedefined, classfileBuffer);
		}
	}
	
}
