/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing_impl;

/**
 * 
 */

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import sun.tools.attach.LinuxVirtualMachine;
import sun.tools.attach.WindowsVirtualMachine;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.sun.tools.attach.spi.AttachProvider;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MoskitoTestingAgentLoader {
	
	private static final String[]       paths           = new String[] { "de/unisaarland/cs/st/moskito/testing_impl",
	        "com/sun/tools", "sun/tools"               };
	
	private static final AttachProvider ATTACH_PROVIDER = new AttachProvider() {
		                                                    
		                                                    @Override
		                                                    public VirtualMachine attachVirtualMachine(final String id) {
			                                                    return null;
		                                                    }
		                                                    
		                                                    @Override
		                                                    public List<VirtualMachineDescriptor> listVirtualMachines() {
			                                                    return null;
		                                                    }
		                                                    
		                                                    @Override
		                                                    public String name() {
			                                                    return null;
		                                                    }
		                                                    
		                                                    @Override
		                                                    public String type() {
			                                                    return null;
		                                                    }
	                                                    };
	
	/**
	 * @param source
	 * @param target
	 * @throws IOException
	 */
	private static void add(final File source,
	                        final JarOutputStream target) throws IOException {
		BufferedInputStream in = null;
		try {
			if (source.isDirectory()) {
				String name = source.getPath().replace("\\", "/");
				
				if (!name.isEmpty()) {
					if (!name.endsWith("/")) {
						name += "/";
					}
					final JarEntry entry = new JarEntry(name);
					entry.setTime(source.lastModified());
					target.putNextEntry(entry);
					target.closeEntry();
				}
				
				for (final File nestedFile : source.listFiles()) {
					add(nestedFile, target);
				}
				
				return;
			}
			
			final JarEntry entry = new JarEntry(source.getPath().replace("\\", "/"));
			entry.setTime(source.lastModified());
			target.putNextEntry(entry);
			in = new BufferedInputStream(new FileInputStream(source));
			
			final byte[] buffer = new byte[1024];
			
			while (true) {
				final int count = in.read(buffer);
				if (count == -1) {
					break;
				}
				target.write(buffer, 0, count);
			}
			target.closeEntry();
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	/**
	 * @param file
	 * @param baseDirectory
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void appendAgentJarFromClassFiles(final JarOutputStream agentJarFile,
	                                                 final File file,
	                                                 final File baseDirectory) throws FileNotFoundException,
	                                                                          IOException {
		add(baseDirectory, agentJarFile);
	}
	
	/**
	 * @param file
	 * @param jarFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void appendAgentJarFromJar(final JarOutputStream agentJarFile,
	                                          final File file,
	                                          final String packagePath,
	                                          final JarFile jarFile) throws FileNotFoundException, IOException {
		for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
			final JarEntry current = e.nextElement();
			
			if ((current.getName().startsWith(packagePath)) && current.getName().endsWith(".class")) {
				agentJarFile.putNextEntry(current);
				jarFile.getInputStream(current);
				
				final BufferedInputStream in = new BufferedInputStream(jarFile.getInputStream(current));
				
				final byte[] buffer = new byte[1024];
				
				while (true) {
					final int count = in.read(buffer);
					if (count == -1) {
						break;
					}
					agentJarFile.write(buffer, 0, count);
				}
				
				in.close();
				agentJarFile.closeEntry();
			}
		}
	}
	
	/**
	 * @return
	 */
	private static Manifest createManifest() {
		final Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		manifest.getMainAttributes().put(new Attributes.Name("Agent-Class"),
		                                 MoskitoTestingAgent.class.getCanonicalName());
		manifest.getMainAttributes().put(new Attributes.Name("Premain-Class"),
		                                 MoskitoTestingAgent.class.getCanonicalName());
		
		return manifest;
	}
	
	/**
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static String getAgentJar() throws FileNotFoundException, IOException, ClassNotFoundException {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		if (classLoader == null) {
			throw new ClassNotFoundException("Can't get class loader.");
		}
		
		final File tempJar = File.createTempFile("moskito-testing-", ".jar");
		// tempJar.deleteOnExit();
		final JarOutputStream agentJarFile = new JarOutputStream(new FileOutputStream(tempJar), createManifest());
		
		for (final String path : paths) {
			final Enumeration<URL> resources = classLoader.getResources(path);
			while (resources.hasMoreElements()) {
				final URL url = resources.nextElement();
				String internalPath = url.getPath();
				
				if (internalPath.startsWith("file:")) {
					internalPath = internalPath.substring(5);
				}
				
				if (internalPath.contains("!")) {
					internalPath = internalPath.substring(0, internalPath.indexOf('!'));
				}
				
				System.err.println("Internal " + path + ": " + internalPath);
				
				if (internalPath.endsWith(".jar")) {
					appendAgentJarFromJar(agentJarFile, tempJar, path, new JarFile(internalPath));
				} else {
					// class files
					final File directory = new File(internalPath);
					appendAgentJarFromClassFiles(agentJarFile, tempJar, directory);
				}
				break;
			}
		}
		
		agentJarFile.flush();
		agentJarFile.close();
		
		return tempJar.getAbsolutePath();
	}
	
	/**
	 * Gets the recursive directories.
	 * 
	 * @param baseDirectory
	 *            the base directory
	 * @return the recursive directories
	 */
	public static List<File> getRecursiveDirectories(final File baseDirectory) {
		final List<File> list = new LinkedList<File>();
		
		for (final String subDirectoryPath : baseDirectory.list()) {
			final File subDirectory = new File(baseDirectory.getAbsolutePath() + System.getProperty("file.separator")
			        + subDirectoryPath);
			if (subDirectory.isDirectory() && subDirectory.canExecute() && subDirectory.canRead()) {
				list.add(subDirectory);
				list.addAll(getRecursiveDirectories(subDirectory));
			}
		}
		
		return list;
	}
	
	/**
	 * @param pid
	 * @return
	 */
	private static VirtualMachine getVirtualMachineImplementationFromEmbeddedOnes(final String pid) {
		try {
			if (File.separatorChar == '\\') {
				return new WindowsVirtualMachine(ATTACH_PROVIDER, pid);
			} else {
				return new LinuxVirtualMachine(ATTACH_PROVIDER, pid);
			}
		} catch (final AttachNotSupportedException e) {
			throw new RuntimeException(e);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} catch (final UnsatisfiedLinkError ignore) {
			// noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
			throw new IllegalStateException(
			                                "Unable to load Java agent; please add lib/tools.jar from your JDK to the classpath");
		}
	}
	
	/**
	 * 
	 */
	public static void loadAgent() {
		final String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
		final int p = nameOfRunningVM.indexOf('@');
		final String pid = nameOfRunningVM.substring(0, p);
		
		String jarFilePath;
		
		try {
			jarFilePath = getAgentJar();
			System.err.println("Using agent: " + jarFilePath);
			VirtualMachine vm = null;
			
			if (AttachProvider.providers().isEmpty()) {
				vm = getVirtualMachineImplementationFromEmbeddedOnes(pid);
			} else {
				vm = VirtualMachine.attach(pid);
			}
			vm.loadAgent(jarFilePath, "");
			vm.detach();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
}
