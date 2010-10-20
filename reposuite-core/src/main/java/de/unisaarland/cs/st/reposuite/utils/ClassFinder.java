package de.unisaarland.cs.st.reposuite.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ClassFinder {
	
	public static Collection<Class<?>> getClassesForPackage(String pckgname) throws ClassNotFoundException {
		// This will hold a list of directories matching the pckgname. There may
		// be more than one if a package is split over multiple jars/paths
		ArrayList<File> directories = new ArrayList<File>();
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			String path = pckgname.replace('.', '/');
			// Ask for all resources for the path
			Enumeration<URL> resources = cld.getResources(path);
			while (resources.hasMoreElements()) {
				directories.add(new File(URLDecoder.decode(resources.nextElement().getPath(), "UTF-8")));
			}
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(pckgname
			        + " does not appear to be a valid package (Null pointer exception)");
		} catch (UnsupportedEncodingException encex) {
			throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Unsupported encoding)");
		} catch (IOException ioex) {
			throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname);
		}
		
		Collection<Class<?>> classes = new Vector<Class<?>>();
		// For every directory identified capture all the .class files
		for (File directory : directories) {
			if (directory.exists()) {
				// Get the list of the files contained in the package
				String[] files = directory.list();
				for (String file : files) {
					// we are only interested in .class files
					if (file.endsWith(".class")) {
						// removes the .class extension
						classes.add(Class.forName(pckgname + '.' + file.substring(0, file.length() - 6)));
					}
				}
			} else {
				throw new ClassNotFoundException(pckgname + " (" + directory.getPath()
				        + ") does not appear to be a valid package");
			}
		}
		return classes;
	}
	
	public static Collection<Class<?>> getClassesFromFileJarFile(String pckgname, String baseDirPath)
	        throws ClassNotFoundException {
		if (!baseDirPath.endsWith(".jar")) {
			return null;
		}
		
		Collection<Class<?>> classes = new Vector<Class<?>>();
		String path = pckgname.replaceAll("\\.", System.getProperty("file.separator"))
		        + System.getProperty("file.separator");
		
		try {
			JarFile currentFile = new JarFile(baseDirPath);
			for (Enumeration<JarEntry> e = currentFile.entries(); e.hasMoreElements();) {
				JarEntry current = e.nextElement();
				if ((current.getName().length() > path.length())
				        && current.getName().substring(0, path.length()).equals(path)
				        && current.getName().endsWith(".class")) {
					classes.add(Class.forName(current.getName()
					        .replaceAll(StringEscapeUtils.escapeJava(System.getProperty("file.separator")), ".")
					        .replace(".class", "")));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return classes;
	}
	
	public static List<Class<?>> getClassessOfInterface(Package pakkage, Class<?> theInterface)
	        throws ClassNotFoundException {
		assert (pakkage != null);
		assert (theInterface != null);
		
		String classPath = ClassFinder.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		Collection<Class<?>> discoveredClasses = null;
		String thePackage = pakkage.getName();
		
		if (classPath.endsWith(".jar")) {
			discoveredClasses = getClassesFromFileJarFile(thePackage, classPath);
		} else {
			discoveredClasses = getClassesForPackage(thePackage);
		}
		
		List<Class<?>> classList = new ArrayList<Class<?>>();
		for (Class<?> discovered : discoveredClasses) {
			Class<?> aClass = discovered;
			Class<?> matchingClass = null;
			do {
				
				Logger.getLogger(ClassFinder.class).trace("Checking Class: " + aClass.getName());
				if (discovered.equals(theInterface)) {
					break;
				}
				Class<?>[] classInterfaces = aClass.getInterfaces();
				
				for (int j = 0; j < classInterfaces.length; j++) {
					if (classInterfaces[j] == theInterface) {
						Logger.getLogger(ClassFinder.class).trace(
						        "Class implements the " + theInterface.getSimpleName() + " interface: "
						                + aClass.getName());
						matchingClass = discovered;
						break;
					}
				}
				
				if (matchingClass == null) {
					Logger.getLogger(ClassFinder.class).trace(
					        "Class doesn't implement the " + theInterface.getSimpleName() + " interface: "
					                + aClass.getName() + ", checking its superclasses");
					// find noarg constructable objects in the hierarchy
					boolean noargCons = false;
					do {
						aClass = aClass.getSuperclass();
						if (aClass == null) {
							aClass = Object.class;
							break;
						}
						// check for noarg constructor, required for
						// reflective instantiation
						Logger.getLogger(ClassFinder.class).trace(
						        "Checking superclass : " + aClass.getName() + " for noarg constructor");
						Constructor<?>[] cons = aClass.getConstructors();
						for (int j = 0; j < cons.length; j++) {
							if (cons[j].getTypeParameters().length == 0) {
								// it will work
								Logger.getLogger(ClassFinder.class).trace(
								        "Superclass : " + aClass.getName() + " has a noarg constructor");
								noargCons = true;
							}
						}
					} while (!noargCons && !aClass.equals(Object.class));
				} else {
					// match
					Logger.getLogger(ClassFinder.class).trace("Adding Class: " + matchingClass);
					classList.add(matchingClass);
				}
			} while ((matchingClass == null) && !aClass.equals(Object.class));
		}
		return classList;
	}
}
