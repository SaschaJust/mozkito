/**
 * 
 */
package de.unisaarland.cs.st.reposuite.toolchain;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.FileUtils.FileShutdownAction;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class CrashHandler extends ThreadGroup {
	
	private static final Properties                      mailProps = new Properties() {
		
		private static final long serialVersionUID = -4075576523389682827L;
		
		{
			put("mail.smtp.host",
			"mail.st.cs.uni-saarland.de");
			put("mail.transport.protocol", "smtp");
			put("mail.to",
			"project_reposuite@st.cs.uni-saarland.de");
			put("mail.subject", "RepoSuite Crash Report");
			put("mail.sender.name", "RepoSuite Client");
			put("mail.sender.address",
			"reposuite-crasher@st.cs.uni-saarland.de");
			put("mail.sender.host",
			"hg.st.cs.uni-saarland.de");
		}
	};
	
	private static Map<RepoSuiteToolchain, CrashHandler> handlers  = new HashMap<RepoSuiteToolchain, CrashHandler>();
	
	public static void init(final RepoSuiteToolchain toolchain) {
		if (!handlers.containsKey(toolchain)) {
			handlers.put(toolchain, new CrashHandler(toolchain));
		}
	}
	
	private RepoSuiteToolchain application = null;
	
	private static boolean     executed    = false;
	
	/**
	 * @param application
	 *            a {@link RepoSuiteToolchain} instance
	 */
	private CrashHandler(final RepoSuiteToolchain application) {
		super(RepoSuiteToolchain.class.getSimpleName());
		this.application = application;
		// this.previousHandler = Thread.getDefaultUncaughtExceptionHandler();
		application.setUncaughtExceptionHandler(this);
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	/**
	 * @return information about class loading
	 */
	private String getClassLoadingInformation() {
		ClassLoadingMXBean bean = ManagementFactory.getClassLoadingMXBean();
		StringBuilder builder = new StringBuilder();
		builder.append("Loaded classes: ");
		builder.append("total: ").append(bean.getTotalLoadedClassCount()).append(" ");
		builder.append("current: ").append(bean.getLoadedClassCount()).append(" ");
		builder.append("unloaded: ").append(bean.getUnloadedClassCount());
		builder.append(FileUtils.lineSeparator);
		return builder.toString();
	}
	
	/**
	 * @param e
	 *            the error/exception under inspection
	 * @return the complete crash report in one string
	 */
	private String getCrashReport(final Throwable e) {
		StringBuilder body = new StringBuilder();
		
		body.append("RepoSuite crashed. An automated, anonymous crash report will be send to help us fix the problem.");
		body.append(FileUtils.lineSeparator);
		body.append("This report does NOT contain any usernames or passwords.");
		body.append(FileUtils.lineSeparator);
		body.append(FileUtils.lineSeparator);
		
		try {
			body.append(">>> RepoSuite VersionInfo >>>");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
			body.append(getVersionInfo());
			body.append("<<< RepoSuite VersionInfo <<<");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
		} catch (Throwable t) {
			
		}
		
		try {
			body.append(">>> Crash Report >>>");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
			
			StringWriter stack = new StringWriter();
			PrintWriter writer = new PrintWriter(stack);
			e.printStackTrace(writer);
			
			// body.append(FileUtils.lineSeparator);
			// body.append(FileUtils.lineSeparator);
			body.append("Stacktrace:");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
			body.append(stack.toString());
			body.append(FileUtils.lineSeparator);
			
			if (e.getCause() != null) {
				Throwable cause = e.getCause();
				stack = new StringWriter();
				writer = new PrintWriter(stack);
				cause.printStackTrace(writer);
				
				body.append(FileUtils.lineSeparator);
				body.append(FileUtils.lineSeparator);
				body.append("Cause Stacktrace:");
				body.append(FileUtils.lineSeparator);
				body.append(FileUtils.lineSeparator);
				body.append(stack.toString());
				body.append(FileUtils.lineSeparator);
			}
			
			body.append("<<< Crash Report <<<");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
		} catch (Throwable t) {
			
		}
		
		try {
			body.append(">>> System Information >>>");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
			body.append(getSystemInformation());
			body.append(getClassLoadingInformation());
			body.append(getRuntimeInformation());
			body.append(FileUtils.lineSeparator);
			body.append("<<< System Information <<<");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
		} catch (Throwable t) {
			
		}
		
		try {
			body.append(">>> RepoSuite Setup >>>");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
			body.append(getRepoSuiteSettings());
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
			body.append("<<< RepoSuite Setup <<<");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
		} catch (Throwable t) {
			
		}
		try {
			body.append(">>> RepoSuite ToolInfo >>>");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
			body.append(getToolInformation());
			body.append("<<< RepoSuite ToolInfo <<<");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
		} catch (Throwable t) {
			
		}
		
		try {
			body.append(">>> Active Threads >>>");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
			body.append(getThreadInformation());
			body.append("<<< Active Threads <<<");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
		} catch (Throwable t) {
			
		}
		
		try {
			body.append(">>> Open Files >>>");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
			body.append(getOpenFilesInformation());
			body.append("<<< Open Files <<<");
			body.append(FileUtils.lineSeparator);
			body.append(FileUtils.lineSeparator);
		} catch (Throwable t) {
			
		}
		
		return body.toString();
	}
	
	private String getOpenFilesInformation() {
		Map<FileShutdownAction, Set<File>> openFiles = FileUtils.getManagedOpenFiles();
		StringBuilder sb = new StringBuilder();
		for (FileShutdownAction action : openFiles.keySet()) {
			for (File file : openFiles.get(action)) {
				sb.append(file.getAbsolutePath());
				sb.append(" (");
				sb.append(action.toString());
				sb.append("), ");
			}
		}
		return sb.toString();
	}
	
	/**
	 * @return the reposuite settings of the monitored application, if any
	 */
	protected String getRepoSuiteSettings() {
		return (this.application != null)
		? this.application.getSettings().toString()
		: "";
	}
	
	private String getRuntimeInformation() {
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		StringBuilder builder = new StringBuilder();
		builder.append("VM: ");
		builder.append(bean.getVmVendor()).append(" ");
		builder.append(bean.getVmName()).append(" ");
		builder.append(bean.getVmVersion());
		builder.append(FileUtils.lineSeparator);
		return builder.toString();
		
	}
	
	/**
	 * @return some system information
	 */
	private String getSystemInformation() {
		OperatingSystemMXBean systemMXBean = ManagementFactory.getOperatingSystemMXBean();
		StringBuilder builder = new StringBuilder();
		builder.append("Operating System: ");
		builder.append(systemMXBean.getName()).append(" ").append(systemMXBean.getVersion()).append(" ")
		.append(systemMXBean.getArch());
		builder.append(FileUtils.lineSeparator);
		return builder.toString();
	}
	
	/**
	 * @return information about running threads
	 */
	private String getThreadInformation() {
		ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
		while (root.getParent() != null) {
			root = root.getParent();
		}
		
		// Visit each thread group
		return visit(root, 0) + FileUtils.lineSeparator;
	}
	
	/**
	 * @return information provided by the {@link RepoSuiteToolchain}
	 */
	protected String getToolInformation() {
		return (this.application != null)
		? this.application.getSettings().getToolInformation()
		: "";
	}
	
	/**
	 * @return version info of RepoSuite
	 */
	private String getVersionInfo() {
		StringBuilder builder = new StringBuilder();
		String path = CrashHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		try {
			if (path.endsWith(".jar")) {
				JarFile jarFile = new JarFile(path);
				
				for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
					JarEntry current = e.nextElement();
					
					if (current.getName().endsWith(FileUtils.fileSeparator + "pom.xml")) {
						InputStream inputStream = CrashHandler.class.getResourceAsStream(FileUtils.fileSeparator
						                                                                 + current.getName());
						BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
						String line;
						boolean capturing = false;
						StringBuilder versionBuilder = new StringBuilder();
						Regex regex = new Regex("<version>({version}[^<]+)</version>");
						while (null != (line = reader.readLine())) {
							if (line.contains("<parent>")) {
								capturing = true;
							}
							if (capturing && line.contains("<version>")) {
								versionBuilder.append(line);
							}
							if (capturing && line.contains("</version>")) {
								versionBuilder.append(line);
								capturing = false;
								break;
							}
						}
						if (versionBuilder.length() > 0) {
							List<RegexGroup> list = regex.find(versionBuilder.toString());
							if ((list != null) && (list.size() > 1)) {
								builder.append("RepoSuite version: ");
								builder.append(list.get(1).getMatch().trim());
								builder.append(" (built ");
								builder.append(new DateTime(current.getTime(), DateTimeZone.UTC));
								builder.append(")");
								break;
							}
						}
					}
				}
			} else {
				// TODO this will not work for .class only files.
			}
		} catch (Throwable e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		
		if (builder.length() == 0) {
			builder.append("RepoSuite version: ");
			builder.append(" <version could not be determined>");
		}
		
		builder.append(FileUtils.lineSeparator);
		builder.append(FileUtils.lineSeparator);
		
		return builder.toString();
	}
	
	/**
	 * Sends the given report to the email address using the mail settings in
	 * mailProps
	 * 
	 * @param report
	 *            the report to be send
	 */
	private void sendReport(final String report) {
		if ((System.getProperty("disableCrashEmail") == null)
				|| (System.getProperty("disableCrashEmail").toLowerCase().equals("false"))) {
			try {
				Session session = Session.getDefaultInstance(CrashHandler.mailProps, null);
				Transport transport = session.getTransport();
				MimeMessage message = new MimeMessage(session);
				message.setSubject(CrashHandler.mailProps.getProperty("mail.subject"));
				message.addRecipient(Message.RecipientType.TO,
				                     new InternetAddress(CrashHandler.mailProps.getProperty("mail.to")));
				message.setFrom(new InternetAddress(CrashHandler.mailProps.getProperty("mail.sender.address"),
				                                    CrashHandler.mailProps.getProperty("mail.sender.name")));
				message.setSender(new InternetAddress(CrashHandler.mailProps.getProperty("mail.sender.address"),
				                                      CrashHandler.mailProps.getProperty("mail.sender.name")));
				message.setContent(report, "text/plain");
				transport.connect();
				transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
				transport.close();
			} catch (MessagingException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage());
				}
			} catch (UnsupportedEncodingException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang
	 * .Thread, java.lang.Throwable)
	 */
	@Override
	public synchronized void uncaughtException(final Thread arg0,
	                                           final Throwable arg1) {
		if (!CrashHandler.executed) {
			CrashHandler.executed = true;
			
			if ((arg1 == null) || (arg1 instanceof Shutdown)) {
				if (Logger.logInfo()) {
					Logger.info("Received shutdown notification from " + arg0.getName() + " with notice: "
					            + arg1.getMessage());
				}
			} else {
				if (Logger.logError()) {
					Logger.error("[[ " + arg1.getClass().getSimpleName() + " ]] Generating crash report.");
				}
				
				String crashReport = getCrashReport(arg1);
				
				if (Logger.logError()) {
					Logger.error(crashReport);
				}
				
				System.out.println(crashReport);
				sendReport(crashReport);
			}
			
			if (Logger.logInfo()) {
				Logger.info("Initiating shutdown.");
			}
			
			FileUtils.shutdown();
			
			if (this.application != null) {
				this.application.shutdown();
			}
		}
	}
	
	/**
	 * Used by {@link #getThreadInformation()} for a traversal search of
	 * {@link Thread}s/{@link ThreadGroup}s
	 * 
	 * @param group
	 * @param level
	 * @return
	 */
	private String visit(final ThreadGroup group,
	                     final int level) {
		// Get threads in `group'
		StringBuilder builder = new StringBuilder();
		int numThreads = group.activeCount();
		Thread[] threads = new Thread[numThreads * 2];
		numThreads = group.enumerate(threads, false);
		
		StringBuilder indent = new StringBuilder();
		for (int i = 0; i < level; ++i) {
			indent.append("  ");
		}
		
		// Enumerate each thread in `group'
		for (int i = 0; i < numThreads; i++) {
			// Get thread
			Thread thread = threads[i];
			builder.append(indent);
			builder.append("|-");
			builder.append(thread.getName()).append(" [");
			builder.append(thread.getClass().getSimpleName()).append("], ");
			builder.append(thread.getPriority()).append(", ");
			builder.append(thread.getState().name());
			builder.append(FileUtils.lineSeparator);
			for (StackTraceElement element : thread.getStackTrace()) {
				builder.append(indent);
				builder.append("| ");
				builder.append(element.toString());
				builder.append(FileUtils.lineSeparator);
			}
			// builder.append(FileUtils.lineSeparator);
		}
		
		// Get thread subgroups of `group'
		int numGroups = group.activeGroupCount();
		ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
		numGroups = group.enumerate(groups, false);
		
		// Recursively visit each subgroup
		for (int i = 0; i < numGroups; i++) {
			builder.append(indent);
			builder.append(visit(groups[i], level + 1));
		}
		
		return builder.toString();
	}
	
}
