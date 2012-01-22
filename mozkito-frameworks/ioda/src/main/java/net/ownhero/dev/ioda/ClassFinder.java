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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.io.filefilter.IOFileFilter;
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
		final List<String> pathList = new LinkedList<String>();
		
		String classPaths = System.getProperty("java.class.path");
		String[] split = classPaths.split(System.getProperty("path.separator"));
		for (final String classPath : split) {
			pathList.add(classPath);
		}
		classPaths = System.getProperty("reposuiteClassLookup");
		if (classPaths != null) {
			split = classPaths.split(System.getProperty("path.separator"));
			for (final String classPath : split) {
				pathList.add(classPath);
			}
		}
		
		final Collection<Class<?>> discoveredClasses = new HashSet<Class<?>>();
		final String thePackage = pakkage.getName();
		
		for (final String classPath : pathList) {
			if (classPath.endsWith(".jar")) {
				discoveredClasses.addAll(getClassesFromJarFile(thePackage, classPath, modifiers));
			} else {
				discoveredClasses.addAll(getClassesFromClasspath(thePackage, modifiers));
			}
		}
		return discoveredClasses;
	}
	
	/**
	 * @param classPath
	 * @return
	 * @throws IOException
	 */
	public static Set<String> getAllClassNames(final String classPath) throws IOException {
		final HashSet<String> classNames = new HashSet<String>();
		
		final List<String> pathList = new LinkedList<String>();
		
		String classPaths = classPath == null
		                                     ? System.getProperty("java.class.path")
		                                     : classPath;
		String[] split = classPaths.split(System.getProperty("path.separator"));
		for (final String cp : split) {
			pathList.add(cp);
		}
		classPaths = System.getProperty("reposuiteClassLookup");
		if (classPaths != null) {
			split = classPaths.split(System.getProperty("path.separator"));
			for (final String cp : split) {
				pathList.add(cp);
			}
		}
		
		new HashSet<Class<?>>();
		
		for (final String cp : pathList) {
			if (cp.endsWith(".jar")) {
				classNames.addAll(getClassNamesFromJarFile(cp));
			} else {
				classNames.addAll(getClassNamesFromClassPath(cp));
			}
		}
		return classNames;
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
		final Collection<Class<?>> discoveredClasses = ClassFinder.getAllClasses(pakkage, modifiers);
		
		final Collection<Class<? extends T>> classList = new HashSet<Class<? extends T>>();
		for (final Class<?> discovered : discoveredClasses) {
			Class<?> aClass = discovered;
			do {
				final Class<?> tmpClass = aClass.getSuperclass();
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
		final Collection<File> directories = new HashSet<File>();
		
		// String filePath =
		// ClassFinder.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		final String path = packageName.replaceAll("\\.", FileUtils.fileSeparator);
		
		try {
			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			
			if (classLoader == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			
			// Ask for all resources for the path
			final Enumeration<URL> resources = classLoader.getResources(path);
			while (resources.hasMoreElements()) {
				String internalPath = resources.nextElement().getPath();
				if (internalPath.contains("!")) {
					internalPath = internalPath.substring(0, internalPath.indexOf("!"));
				}
				
				if (!internalPath.endsWith(".jar")) {
					final File directory = new File(URLDecoder.decode(internalPath, "UTF-8"));
					directories.add(directory);
					directories.addAll(FileUtils.getRecursiveDirectories(directory));
				}
			}
		} catch (final NullPointerException x) {
			throw new ClassNotFoundException(packageName
			        + " does not appear to be a valid package (Null pointer exception)");
		} catch (final UnsupportedEncodingException encex) {
			throw new ClassNotFoundException(packageName
			        + " does not appear to be a valid package (Unsupported encoding)");
		}
		
		final Collection<Class<?>> classes = new HashSet<Class<?>>();
		
		// For every directory identified capture all the .class files
		for (final File directory : directories) {
			if (directory.exists()) {
				// Get the list of the files contained in the package
				final String[] files = directory.list();
				for (final String file : files) {
					// we are only interested in .class files
					if (file.endsWith(".class")) {
						// removes the .class extension
						final int index = directory.getAbsolutePath().indexOf(path);
						final String absolutePackageName = directory.getAbsolutePath().substring(index)
						                                            .replaceAll(FileUtils.fileSeparator, ".");
						final Class<?> class1 = Class.forName(absolutePackageName + '.'
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
		
		final Collection<Class<?>> classes = new HashSet<Class<?>>();
		
		// determine the full qualified pathname string from the package name by
		// replacing all '.' with OS file separators.
		final String path = packageName.replaceAll("\\.", FileUtils.fileSeparator) + FileUtils.fileSeparator;
		
		try {
			final JarFile currentFile = new JarFile(filePath);
			
			/*
			 * step through all elements in the jar file and check if there
			 * exists a class file in given package. If so, load it and add it
			 * to the collection.
			 */
			for (final Enumeration<JarEntry> e = currentFile.entries(); e.hasMoreElements();) {
				final JarEntry current = e.nextElement();
				
				if ((current.getName().length() > path.length())
				        && current.getName().substring(0, path.length()).equals(path)
				        && current.getName().endsWith(".class")) {
					final Class<?> class1 = Class.forName(current.getName()
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
		} catch (final IOException e) {
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
		final Collection<Class<?>> discoveredClasses = ClassFinder.getAllClasses(pakkage, modifiers);
		
		final Collection<Class<T>> classList = new HashSet<Class<T>>();
		for (final Class<?> discovered : discoveredClasses) {
			Class<?> aClass = discovered;
			Class<T> matchingClass = null;
			
			do {
				if (Logger.logTrace()) {
					Logger.trace("Checking Class: " + aClass.getName());
				}
				
				if (discovered.equals(theInterface)) {
					break;
				}
				
				final Class<?>[] classInterfaces = aClass.getInterfaces();
				
				for (final Class<?> classInterface : classInterfaces) {
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
						final Constructor<?>[] cons = aClass.getConstructors();
						for (final Constructor<?> con : cons) {
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
	 * @param cp
	 * @return
	 * @throws IOException
	 */
	private static Collection<? extends String> getClassNamesFromClassPath(final String cp) throws IOException {
		Set<String> classNames = new HashSet<String>();
		
		Iterator<File> iterator = FileUtils.findFiles(new File(cp), new IOFileFilter() {
			
			@Override
			public boolean accept(final File file) {
				return file.getName().endsWith(".class");
			}
			
			@Override
			public boolean accept(final File dir,
			                      final String name) {
				return name.endsWith(".class");
			}
		});
		
		while (iterator.hasNext()) {
			String path = iterator.next().getAbsolutePath();
			if (path.startsWith(cp)) {
				path = path.substring(cp.length());
			}
			path = path.replaceAll(StringEscapeUtils.escapeJava(FileUtils.fileSeparator), ".");
			path = path.replaceAll("\\$[^.]+", "");
			if (path.endsWith(".class")) {
				path = path.substring(0, path.length() - ".class".length());
			}
			path = path.replaceFirst("^\\.+", "");
			classNames.add(path);
		}
		return classNames;
	}
	
	/**
	 * @param filePath
	 * @return
	 */
	private static Set<String> getClassNamesFromJarFile(final String filePath) {
		Set<String> classNames = new HashSet<String>();
		try {
			final JarFile currentFile = new JarFile(filePath);
			
			/*
			 * step through all elements in the jar file and check if there
			 * exists a class file in given package. If so, load it and add it
			 * to the collection.
			 */
			for (final Enumeration<JarEntry> e = currentFile.entries(); e.hasMoreElements();) {
				final JarEntry current = e.nextElement();
				
				if (current.getName().endsWith(".class")) {
					String path = current.getName();
					path = path.replaceAll(StringEscapeUtils.escapeJava(FileUtils.fileSeparator), ".");
					path = path.replaceAll("\\$[^.]+", "");
					if (path.endsWith(".class")) {
						path = path.substring(0, path.length() - ".class".length());
					}
					path = path.replaceFirst("^\\.+", "");
					classNames.add(path);
				}
			}
		} catch (final IOException e) {
			if (Logger.logWarn()) {
				Logger.warn("Skipping invalid JAR file `" + filePath + "`: " + e.getMessage(), e);
			}
		}
		return classNames;
	}
	
	/**
	 * @return
	 */
	public static String getHandle() {
		return ClassFinder.class.getSimpleName();
	}
}
