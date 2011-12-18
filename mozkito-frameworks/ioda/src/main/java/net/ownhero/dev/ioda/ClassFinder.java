package net.ownhero.dev.ioda;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Searches for classes matching certain criteria in the classpath or the
 * current jar file.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ClassFinder {
	
	/**
	 * @param baseClass
	 * @param superClass
	 * @return
	 */
	@NoneNull
	public static boolean extending(final Class<?> baseClass,
	                                final Class<?> superClass) {
		Class<?> c = null;
		while ((c = baseClass.getSuperclass()) != null) {
			if (c == superClass) {
				return true;
			}
		}
		return superClass.isAssignableFrom(baseClass);
		
	}
	
	/**
	 * @param pakkage
	 * @return
	 * @throws IOException
	 * @throws WrongClassSearchMethodException
	 * @throws ClassNotFoundException
	 */
	@Deprecated
	public static Collection<Class<?>> getAllClasses(@NotNull final Package pakkage) throws ClassNotFoundException,
	                                                                                WrongClassSearchMethodException,
	                                                                                IOException {
		return getAllClasses(pakkage, null);
	}
	
	/**
	 * @param pakkage
	 * @param modifiers
	 *            an Integer value representing properties the class shall not
	 *            have (e.g. private, interface, abstract). See {@link Modifier}
	 *            for details.
	 * @return
	 * @throws ClassNotFoundException
	 * @throws WrongClassSearchMethodException
	 * @throws IOException
	 */
	public static Collection<Class<?>> getAllClasses(@NotNull final Package pakkage,
	                                                 final Integer modifiers) throws ClassNotFoundException,
	                                                                         WrongClassSearchMethodException,
	                                                                         IOException {
		List<String> pathList = new LinkedList<String>();
		
		String classPaths = System.getProperty("java.class.path");
		String[] split = classPaths.split(System.getProperty("path.separator"));
		for (String classPath : split) {
			pathList.add(classPath);
		}
		classPaths = System.getProperty("reposuiteClassLookup");
		if (classPaths != null) {
			split = classPaths.split(System.getProperty("path.separator"));
			for (String classPath : split) {
				pathList.add(classPath);
			}
		}
		
		Collection<Class<?>> discoveredClasses = new HashSet<Class<?>>();
		String thePackage = pakkage.getName();
		
		for (String classPath : pathList) {
			if (classPath.endsWith(".jar")) {
				discoveredClasses.addAll(getClassesFromJarFile(thePackage, classPath, modifiers));
			} else {
				discoveredClasses.addAll(getClassesFromClasspath(thePackage, modifiers));
			}
		}
		return discoveredClasses;
	}
	
	/**
	 * @param <T>
	 * @param pakkage
	 * @param superClass
	 * @return
	 * @throws ClassNotFoundException
	 * @throws WrongClassSearchMethodException
	 * @throws IOException
	 * @Deprecated use
	 *             {@link ClassFinder#getClassesExtendingClass(Package, Class, Integer)}
	 *             instead
	 */
	@Deprecated
	public static <T> Collection<Class<? extends T>> getClassesExtendingClass(final Package pakkage,
	                                                                          final Class<T> superClass) throws ClassNotFoundException,
	                                                                                                    WrongClassSearchMethodException,
	                                                                                                    IOException {
		return getClassesExtendingClass(pakkage, superClass, null);
	}
	
	/**
	 * This class loads all classes in the specified package that can be found
	 * in the classpath and checks if they are derived from the supplied super
	 * class. If this is the case, they are added to a collection which will be
	 * returned at the end of the method.
	 * 
	 * @param pakkage
	 *            the package where the traversal search takes place
	 *            (recursive), not null
	 * @param superClass
	 *            the class all returned classes have to be derived from
	 * @param modifiers
	 *            an Integer value representing properties the class shall not
	 *            have (e.g. private, interface, abstract). See {@link Modifier}
	 *            for details.
	 * @return a collection of classes matching the above conditions
	 * @throws ClassNotFoundException
	 * @throws WrongClassSearchMethodException
	 * @throws IOException
	 */
	@SuppressWarnings ("unchecked")
	@NoneNull
	public static <T> Collection<Class<? extends T>> getClassesExtendingClass(final Package pakkage,
	                                                                          final Class<T> superClass,
	                                                                          final Integer modifiers) throws ClassNotFoundException,
	                                                                                                  WrongClassSearchMethodException,
	                                                                                                  IOException {
		Collection<Class<?>> discoveredClasses = ClassFinder.getAllClasses(pakkage, modifiers);
		
		Collection<Class<? extends T>> classList = new HashSet<Class<? extends T>>();
		for (Class<?> discovered : discoveredClasses) {
			Class<?> aClass = discovered;
			do {
				Class<?> tmpClass = aClass.getSuperclass();
				if (tmpClass == null) {
					// Object case
					break;
				} else {
					aClass = tmpClass;
				}
				
				if (aClass.equals(superClass)) {
					classList.add((Class<? extends T>) discovered);
					break;
				}
				
			} while (true);
		}
		
		return classList;
	}
	
	/**
	 * @param packageName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws WrongClassSearchMethodException
	 * @throws IOException
	 * @deprecated
	 */
	@Deprecated
	public static Collection<Class<?>> getClassesFromClasspath(final String packageName) throws ClassNotFoundException,
	                                                                                    WrongClassSearchMethodException,
	                                                                                    IOException {
		return getClassesFromClasspath(packageName, null);
	}
	
	/**
	 * Finds all classes in the current classpath that are contained in the
	 * given package (from packageName). Only .class files are inspected!
	 * 
	 * @param packageName
	 *            the name of the package the classes have to be contained in
	 * @param modifiers
	 * @return a collection of all classes from the supplied package
	 * @throws ClassNotFoundException
	 * @throws WrongClassSearchMethodException
	 * @throws IOException
	 */
	public static Collection<Class<?>> getClassesFromClasspath(final String packageName,
	                                                           final Integer modifiers) throws ClassNotFoundException,
	                                                                                   WrongClassSearchMethodException,
	                                                                                   IOException {
		// This will hold a list of directories matching the packageName. There
		// may
		// be more than one if a package is split over multiple jars/paths
		Collection<File> directories = new HashSet<File>();
		
		// String filePath =
		// ClassFinder.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String path = packageName.replaceAll("\\.", FileUtils.fileSeparator);
		
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			
			if (classLoader == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			
			// Ask for all resources for the path
			Enumeration<URL> resources = classLoader.getResources(path);
			while (resources.hasMoreElements()) {
				String internalPath = resources.nextElement().getPath();
				if (internalPath.contains("!")) {
					internalPath = internalPath.substring(0, internalPath.indexOf("!"));
				}
				
				if (!internalPath.endsWith(".jar")) {
					File directory = new File(URLDecoder.decode(internalPath, "UTF-8"));
					directories.add(directory);
					directories.addAll(FileUtils.getRecursiveDirectories(directory));
				}
			}
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(packageName
			        + " does not appear to be a valid package (Null pointer exception)");
		} catch (UnsupportedEncodingException encex) {
			throw new ClassNotFoundException(packageName
			        + " does not appear to be a valid package (Unsupported encoding)");
		}
		
		Collection<Class<?>> classes = new HashSet<Class<?>>();
		
		// For every directory identified capture all the .class files
		for (File directory : directories) {
			if (directory.exists()) {
				// Get the list of the files contained in the package
				String[] files = directory.list();
				for (String file : files) {
					// we are only interested in .class files
					if (file.endsWith(".class")) {
						// removes the .class extension
						int index = directory.getAbsolutePath().indexOf(path);
						String absolutePackageName = directory.getAbsolutePath().substring(index)
						                                      .replaceAll(FileUtils.fileSeparator, ".");
						Class<?> class1 = Class.forName(absolutePackageName + '.'
						        + file.substring(0, file.length() - 6));
						if (modifiers != null) {
							if ((class1.getModifiers() & modifiers) == 0) {
								classes.add(class1);
							}
						} else {
							classes.add(class1);
						}
					}
				}
			} else {
				throw new ClassNotFoundException(packageName + " (" + directory.getPath()
				        + ") does not appear to be a valid package");
			}
		}
		return classes;
	}
	
	/**
	 * @param packageName
	 * @param filePath
	 * @return
	 * @throws ClassNotFoundException
	 * @throws WrongClassSearchMethodException
	 */
	@Deprecated
	public static Collection<Class<?>> getClassesFromJarFile(final String packageName,
	                                                         final String filePath) throws ClassNotFoundException,
	                                                                               WrongClassSearchMethodException {
		return getClassesFromJarFile(packageName, filePath, null);
	}
	
	/**
	 * Scans through the given JAR file and finds all class objects for a given
	 * package name
	 * 
	 * @param packageName
	 *            the name of the package the classes have to be contained in
	 * @param modifiers
	 * @return a collection of all classes from the supplied package
	 * @throws ClassNotFoundException
	 * @throws WrongClassSearchMethodException
	 * @throws IOException
	 */
	public static Collection<Class<?>> getClassesFromJarFile(final String packageName,
	                                                         final String filePath,
	                                                         final Integer modifiers) throws ClassNotFoundException,
	                                                                                 WrongClassSearchMethodException {
		// String filePath =
		// ClassFinder.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		
		Collection<Class<?>> classes = new HashSet<Class<?>>();
		
		// determine the full qualified pathname string from the package name by
		// replacing all '.' with OS file separators.
		String path = packageName.replaceAll("\\.", FileUtils.fileSeparator) + FileUtils.fileSeparator;
		
		try {
			JarFile currentFile = new JarFile(filePath);
			
			/*
			 * step through all elements in the jar file and check if there
			 * exists a class file in given package. If so, load it and add it
			 * to the collection.
			 */
			for (Enumeration<JarEntry> e = currentFile.entries(); e.hasMoreElements();) {
				JarEntry current = e.nextElement();
				
				if ((current.getName().length() > path.length())
				        && current.getName().substring(0, path.length()).equals(path)
				        && current.getName().endsWith(".class")) {
					Class<?> class1 = Class.forName(current.getName()
					                                       .replaceAll(StringEscapeUtils.escapeJava(FileUtils.fileSeparator),
					                                                   ".").replace(".class", ""));
					if (modifiers != null) {
						if ((class1.getModifiers() & modifiers) == 0) {
							classes.add(class1);
						}
					} else {
						classes.add(class1);
					}
				}
			}
		} catch (IOException e) {
			if (Logger.logWarn()) {
				Logger.warn("Skipping invalid JAR file `" + filePath + "`: " + e.getMessage());
			}
		}
		
		return classes;
	}
	
	/**
	 * @param pakkage
	 * @param theInterface
	 * @return
	 * @throws ClassNotFoundException
	 * @throws WrongClassSearchMethodException
	 * @throws IOException
	 */
	@Deprecated
	public static <T> Collection<Class<T>> getClassesOfInterface(final Package pakkage,
	                                                             final Class<T> theInterface) throws ClassNotFoundException,
	                                                                                         WrongClassSearchMethodException,
	                                                                                         IOException {
		return getClassesOfInterface(pakkage, theInterface, null);
	}
	
	/**
	 * Finds all classes in a package that implement a given interface
	 * 
	 * @param pakkage
	 *            the package that contains the classes
	 * @param theInterface
	 *            the interface the classes must implement
	 * @return a list of all classes fitting the search criteria
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws WrongClassSearchMethodException
	 */
	@SuppressWarnings ("unchecked")
	@NoneNull
	public static <T> Collection<Class<T>> getClassesOfInterface(final Package pakkage,
	                                                             final Class<T> theInterface,
	                                                             final Integer modifiers) throws ClassNotFoundException,
	                                                                                     WrongClassSearchMethodException,
	                                                                                     IOException {
		Collection<Class<?>> discoveredClasses = ClassFinder.getAllClasses(pakkage, modifiers);
		
		Collection<Class<T>> classList = new HashSet<Class<T>>();
		for (Class<?> discovered : discoveredClasses) {
			Class<?> aClass = discovered;
			Class<T> matchingClass = null;
			
			do {
				if (Logger.logTrace()) {
					Logger.trace("Checking Class: " + aClass.getName());
				}
				
				if (discovered.equals(theInterface)) {
					break;
				}
				
				Class<?>[] classInterfaces = aClass.getInterfaces();
				
				for (Class<?> classInterface : classInterfaces) {
					if (classInterface == theInterface) {
						if (Logger.logTrace()) {
							Logger.trace("Class implements the " + theInterface.getSimpleName() + " interface: "
							        + aClass.getName());
						}
						matchingClass = (Class<T>) discovered;
						break;
					}
				}
				
				if (matchingClass == null) {
					if (Logger.logTrace()) {
						Logger.trace("Class doesn't implement the " + theInterface.getSimpleName() + " interface: "
						        + aClass.getName() + ", checking its superclasses");
					}
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
						if (Logger.logTrace()) {
							Logger.trace("Checking superclass : " + aClass.getName() + " for noarg constructor");
						}
						Constructor<?>[] cons = aClass.getConstructors();
						for (Constructor<?> con : cons) {
							if (con.getTypeParameters().length == 0) {
								// it will work
								if (Logger.logTrace()) {
									Logger.trace("Superclass : " + aClass.getName() + " has a noarg constructor");
								}
								noargCons = true;
							}
						}
					} while (!noargCons && !aClass.equals(Object.class));
				} else {
					// match
					if (Logger.logTrace()) {
						Logger.trace("Adding Class: " + matchingClass);
					}
					classList.add(matchingClass);
				}
			} while ((matchingClass == null) && !aClass.equals(Object.class));
		}
		return classList;
	}
	
	/**
	 * @return
	 */
	public static String getHandle() {
		return ClassFinder.class.getSimpleName();
	}
}
