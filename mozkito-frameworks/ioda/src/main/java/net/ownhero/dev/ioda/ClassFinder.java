/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package net.ownhero.dev.ioda;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
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
import java.util.jar.JarInputStream;

import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Searches for classes matching certain criteria in the classpath or the current jar file.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ClassFinder {
	
	/** The Constant externalResources. */
	private static final Set<URL> EXTERNAL_RESOURCES = new HashSet<>();
	
	/**
	 * Adds the external resource.
	 * 
	 * @param resource
	 *            the resource
	 * @return true, if successful
	 */
	public static final synchronized boolean addExternalResource(final URL resource) {
		return EXTERNAL_RESOURCES.add(resource);
	}
	
	/**
	 * Extending.
	 * 
	 * @param baseClass
	 *            the base class
	 * @param superClass
	 *            the super class
	 * @return true, if successful
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
	 * Gets the all classes.
	 * 
	 * @param pakkage
	 *            the pakkage
	 * @param modifiers
	 *            an Integer value representing properties the class shall not have (e.g. private, interface, abstract).
	 *            See {@link Modifier} for details.
	 * @return the all classes
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws WrongClassSearchMethodException
	 *             the wrong class search method exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Collection<Class<?>> getAllClasses(@NotNull final Package pakkage,
	                                                 final Integer modifiers) throws ClassNotFoundException,
	                                                                         WrongClassSearchMethodException,
	                                                                         IOException {
		final List<String> pathList = new LinkedList<String>();
		
		final String classPaths = System.getProperty("java.class.path");
		final String[] split = classPaths.split(System.getProperty("path.separator"));
		for (final String classPath : split) {
			pathList.add(classPath);
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
		
		for (final URL resource : EXTERNAL_RESOURCES) {
			if (resource.getPath().toLowerCase().endsWith(".jar")) {
				discoveredClasses.addAll(getClassesFromJarResource(thePackage, resource, modifiers));
			} else if (resource.getPath().toLowerCase().endsWith(".class")) {
				if (Logger.logError()) {
					Logger.error("Loading classes from external resources outside a JAR is currently not supported.");
				}
			}
		}
		
		return discoveredClasses;
	}
	
	/**
	 * Gets the all class names.
	 * 
	 * @param classPath
	 *            the class path
	 * @return the all class names
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Set<String> getAllClassNames(final String classPath) throws IOException {
		final HashSet<String> classNames = new HashSet<String>();
		
		final List<String> pathList = new LinkedList<String>();
		
		final String classPaths = classPath == null
		                                           ? System.getProperty("java.class.path")
		                                           : classPath;
		final String[] split = classPaths.split(System.getProperty("path.separator"));
		for (final String cp : split) {
			pathList.add(cp);
		}
		
		for (final String cp : pathList) {
			if (cp.endsWith(".jar")) {
				classNames.addAll(getClassNamesFromJarFile(cp));
			} else {
				classNames.addAll(getClassNamesFromClassPath(cp));
			}
		}
		
		for (final URL resource : EXTERNAL_RESOURCES) {
			if (resource.getPath().toLowerCase().endsWith(".jar")) {
				classNames.addAll(getClassNamesFromJarResource(resource));
			} else if (resource.getPath().toLowerCase().endsWith(".class")) {
				if (Logger.logError()) {
					Logger.error("Loading classes from external resources outside a JAR is currently not supported.");
				}
			}
		}
		
		return classNames;
	}
	
	/**
	 * This class loads all classes in the specified package that can be found in the classpath and checks if they are
	 * derived from the supplied super class. If this is the case, they are added to a collection which will be returned
	 * at the end of the method.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param pakkage
	 *            the package where the traversal search takes place (recursive), not null
	 * @param superClass
	 *            the class all returned classes have to be derived from
	 * @param modifiers
	 *            an Integer value representing properties the class shall not have (e.g. private, interface, abstract).
	 *            See {@link Modifier} for details.
	 * @return a collection of classes matching the above conditions
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws WrongClassSearchMethodException
	 *             the wrong class search method exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
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
				}
				aClass = tmpClass;
				
				if (aClass.equals(superClass)) {
					classList.add((Class<? extends T>) discovered);
					break;
				}
				
			} while (true);
		}
		
		return classList;
	}
	
	/**
	 * Finds all classes in the current classpath that are contained in the given package (from packageName). Only
	 * .class files are inspected!
	 * 
	 * @param packageName
	 *            the name of the package the classes have to be contained in
	 * @param modifiers
	 *            the modifiers
	 * @return a collection of all classes from the supplied package
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws WrongClassSearchMethodException
	 *             the wrong class search method exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
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
	 * Scans through the given JAR file and finds all class objects for a given package name.
	 * 
	 * @param packageName
	 *            the name of the package the classes have to be contained in
	 * @param filePath
	 *            the file path
	 * @param modifiers
	 *            the modifiers
	 * @return a collection of all classes from the supplied package
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws WrongClassSearchMethodException
	 *             the wrong class search method exception
	 */
	public static Collection<Class<?>> getClassesFromJarFile(final String packageName,
	                                                         final String filePath,
	                                                         final Integer modifiers) throws ClassNotFoundException,
	                                                                                 WrongClassSearchMethodException {
		try {
			final URL resource = new File(filePath).toURI().toURL();
			
			return getClassesFromJarResource(packageName, resource, modifiers);
		} catch (final MalformedURLException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new ClassNotFoundException();
		}
	}
	
	/**
	 * Gets the classes from jar resource.
	 * 
	 * @param packageName
	 *            the package name
	 * @param resource
	 *            the resource
	 * @param modifiers
	 *            the modifiers
	 * @return the classes from jar resource
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws WrongClassSearchMethodException
	 *             the wrong class search method exception
	 */
	public static Collection<Class<?>> getClassesFromJarResource(final String packageName,
	                                                             final URL resource,
	                                                             final Integer modifiers) throws ClassNotFoundException,
	                                                                                     WrongClassSearchMethodException {
		final Collection<Class<?>> classes = new HashSet<Class<?>>();
		JarInputStream inputStream = null;
		
		final String path = packageName.replace(".", FileUtils.fileSeparator) + FileUtils.fileSeparator;
		
		try {
			inputStream = new JarInputStream(resource.openStream());
			JarEntry current = null;
			boolean retryFromFile = false;
			
			JARENTRIES: while ((current = inputStream.getNextJarEntry()) != null) {
				final String currentName = current.getName();
				if (!current.isDirectory()) {
					
					if (currentName.toLowerCase().endsWith(".jar")) {
						if (Logger.logError()) {
							Logger.error("JAR in JAR is not supported yet. Found archive: " + currentName);
						}
						retryFromFile = true;
						break JARENTRIES;
					} else if ((current.getName().length() > path.length())
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
			}
			
			if (retryFromFile) {
				final File randomFile = FileUtils.createRandomFile(FileShutdownAction.DELETE);
				final OutputStream outputStream = new FileOutputStream(randomFile);
				IOUtils.copy(resource.openStream(), outputStream);
				
				outputStream.close();
				JarFile jFile = null;
				
				try {
					jFile = new JarFile(randomFile);
					
					final Enumeration<JarEntry> entries = jFile.entries();
					while (entries.hasMoreElements()) {
						final JarEntry element = entries.nextElement();
						
						if (!element.isDirectory()) {
							final String currentName = element.getName();
							
							if (currentName.toLowerCase().endsWith(".jar")) {
								final InputStream inputStream2 = jFile.getInputStream(element);
								final File randomFile2 = FileUtils.createRandomFile(FileShutdownAction.DELETE);
								final OutputStream outputStream2 = new FileOutputStream(randomFile2);
								IOUtils.copy(inputStream2, outputStream2);
								
								classes.addAll(getClassesFromJarFile(packageName, randomFile2.getAbsolutePath(),
								                                     modifiers));
								randomFile2.delete();
							} else if ((element.getName().length() > path.length())
							        && element.getName().substring(0, path.length()).equals(path)
							        && element.getName().endsWith(".class")) {
								final Class<?> class1 = Class.forName(element.getName()
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
					}
				} catch (final IOException e) {
					if (Logger.logWarn()) {
						Logger.warn("Skipping invalid JAR resource `" + randomFile + "`: " + e.getMessage());
					}
				} finally {
					if (jFile != null) {
						try {
							jFile.close();
						} catch (final IOException ignore) {
							// ignore
						}
					}
				}
			}
		} catch (final IOException e) {
			if (Logger.logWarn()) {
				Logger.warn("Skipping invalid JAR resource `" + resource.toString() + "`: " + e.getMessage());
			}
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (final IOException ignore) {
					// ignore
				}
			}
		}
		
		return classes;
	}
	
	/**
	 * Finds all classes in a package that implement a given interface.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param pakkage
	 *            the package that contains the classes
	 * @param theInterface
	 *            the interface the classes must implement
	 * @param modifiers
	 *            the modifiers
	 * @return a list of all classes fitting the search criteria
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws WrongClassSearchMethodException
	 *             the wrong class search method exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
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
	 * Gets the class name.
	 * 
	 * @return the class name
	 */
	public static String getClassName() {
		return ClassFinder.class.getSimpleName();
	}
	
	/**
	 * Gets the class names from class path.
	 * 
	 * @param cp
	 *            the cp
	 * @return the class names from class path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static Collection<? extends String> getClassNamesFromClassPath(final String cp) throws IOException {
		final Set<String> classNames = new HashSet<String>();
		
		final Iterator<File> iterator = FileUtils.findFiles(new File(cp), new IOFileFilter() {
			
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
	 * Gets the class names from jar file.
	 * 
	 * @param filePath
	 *            the file path
	 * @return the class names from jar file
	 */
	private static Set<String> getClassNamesFromJarFile(final String filePath) {
		try {
			final URL resource = new File(filePath).toURI().toURL();
			return getClassNamesFromJarResource(resource);
		} catch (final MalformedURLException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return new HashSet<>();
		}
	}
	
	/**
	 * Gets the class names from jar resource.
	 * 
	 * @param resource
	 *            the resource
	 * @return the class names from jar resource
	 */
	private static Set<String> getClassNamesFromJarResource(final URL resource) {
		// PRECONDITIONS
		
		JarInputStream inputStream = null;
		final Set<String> classNames = new HashSet<String>();
		
		try {
			try {
				inputStream = new JarInputStream(resource.openStream());
				JarEntry current = null;
				
				while ((current = inputStream.getNextJarEntry()) != null) {
					final String currentName = current.getName();
					if (!current.isDirectory()) {
						if (currentName.toLowerCase().endsWith(".jar")) {
							if (Logger.logError()) {
								Logger.error("JAR in JAR is not supported yet. Found archive: " + currentName);
							}
						} else if (currentName.endsWith(".class")) {
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
				}
			} catch (final IOException e) {
				if (Logger.logWarn()) {
					Logger.warn("Skipping invalid JAR file `" + resource.toString() + "`: " + e.getMessage());
				}
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (final IOException ignore) {
						// ignore
					}
				}
			}
			
			return classNames;
		} finally {
			// POSTCONDITIONS
		}
	}
}
