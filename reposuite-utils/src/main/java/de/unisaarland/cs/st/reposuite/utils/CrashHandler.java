/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
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

import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	
	private static final Properties mailProps = new Properties() {
		                                          
		                                          private static final long serialVersionUID = -4075576523389682827L;
		                                          
		                                          {
			                                          put("mail.smtp.host", "mail.own-hero.net");
			                                          put("mail.transport.protocol", "smtp");
			                                          put("mail.to", "methos@own-hero.net");
			                                          put("mail.subject", "RepoSuite Crash Report");
			                                          put("mail.sender.name", "RepoSuite Client");
			                                          put("mail.sender.address",
			                                                  "reposuite-crasher@st.cs.uni-saarland.de");
			                                          put("mail.sender.host", "hg.st.cs.uni-saarland.de");
		                                          }
	                                          };
	
	private static String getClassLoadingInformation() {
		ClassLoadingMXBean bean = ManagementFactory.getClassLoadingMXBean();
		StringBuilder builder = new StringBuilder();
		builder.append("Loaded classes: ");
		builder.append("total: ").append(bean.getTotalLoadedClassCount()).append(" ");
		builder.append("current: ").append(bean.getLoadedClassCount()).append(" ");
		builder.append("unloaded: ").append(bean.getUnloadedClassCount());
		builder.append(FileUtils.lineSeparator);
		return builder.toString();
	}
	
	private static String getRuntimeInformation() {
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		StringBuilder builder = new StringBuilder();
		builder.append("VM: ");
		builder.append(bean.getVmVendor()).append(" ");
		builder.append(bean.getVmName()).append(" ");
		builder.append(bean.getVmVersion());
		builder.append(FileUtils.lineSeparator);
		return builder.toString();
		
	}
	
	private static String getSystemInformation() {
		OperatingSystemMXBean systemMXBean = ManagementFactory.getOperatingSystemMXBean();
		StringBuilder builder = new StringBuilder();
		builder.append("Operating System: ");
		builder.append(systemMXBean.getName()).append(" ").append(systemMXBean.getVersion()).append(" ")
		        .append(systemMXBean.getArch());
		builder.append(FileUtils.lineSeparator);
		return builder.toString();
	}
	
	private static String getThreadInformation() {
		ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
		while (root.getParent() != null) {
			root = root.getParent();
		}
		
		// Visit each thread group
		return visit(root, 0) + FileUtils.lineSeparator;
	}
	
	private static String getVersionInfo() {
		StringBuilder builder = new StringBuilder();
		String path = CrashHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		try {
			if (path.endsWith(".jar")) {
				JarFile jarFile = new JarFile(path);
				
				for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
					JarEntry current = e.nextElement();
					
					if (current.getName().endsWith(FileUtils.fileSeparator + "pom.xml")) {
						System.err.println(">>> " + current.getName());
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
							}
						}
					}
				}
			} else {
				// TODO
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
	
	public static void init(final RepoSuiteToolchain toolchain) {
		new CrashHandler(toolchain);
	}
	
	private static void sendReport(final String report) {
		try {
			Session session = Session.getDefaultInstance(mailProps, null);
			Transport transport = session.getTransport();
			MimeMessage message = new MimeMessage(session);
			message.setSubject(mailProps.getProperty("mail.subject"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailProps.getProperty("mail.to")));
			message.setFrom(new InternetAddress(mailProps.getProperty("mail.sender.address"), mailProps
			        .getProperty("mail.sender.name")));
			message.setSender(new InternetAddress(mailProps.getProperty("mail.sender.address"), mailProps
			        .getProperty("mail.sender.name")));
			message.setContent(report, "text/plain");
			transport.connect();
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			transport.close();
		} catch (MessagingException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (UnsupportedEncodingException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
	}
	
	private static String visit(final ThreadGroup group, final int level) {
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
	
	private final Thread.UncaughtExceptionHandler previousHandler;
	
	private final RepoSuiteToolchain              application;
	
	private CrashHandler(final RepoSuiteToolchain application) {
		this.application = application;
		this.previousHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	private String getCrashReport(final Throwable e) {
		StringBuilder body = new StringBuilder();
		
		body.append("RepoSuite crashed. An automated, anonymous crash report will be send to help us fix the problem.");
		body.append(FileUtils.lineSeparator);
		body.append("This report does NOT contain any usernames or passwords.");
		body.append(FileUtils.lineSeparator);
		body.append(FileUtils.lineSeparator);
		
		body.append(">>> RepoSuite VersionInfo >>>");
		body.append(FileUtils.lineSeparator);
		body.append(FileUtils.lineSeparator);
		body.append(getVersionInfo());
		body.append("<<< RepoSuite VersionInfo <<<");
		body.append(FileUtils.lineSeparator);
		body.append(FileUtils.lineSeparator);
		
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
		
		body.append(">>> RepoSuite Setup >>>");
		body.append(FileUtils.lineSeparator);
		body.append(FileUtils.lineSeparator);
		body.append(this.application.getSettings().toString());
		body.append(FileUtils.lineSeparator);
		body.append(FileUtils.lineSeparator);
		body.append("<<< RepoSuite Setup <<<");
		body.append(FileUtils.lineSeparator);
		body.append(FileUtils.lineSeparator);
		
		body.append(">>> RepoSuite ToolInfo >>>");
		body.append(FileUtils.lineSeparator);
		body.append(FileUtils.lineSeparator);
		body.append(this.application.getSettings().getToolInformation());
		body.append("<<< RepoSuite ToolInfo <<<");
		body.append(FileUtils.lineSeparator);
		body.append(FileUtils.lineSeparator);
		
		body.append(">>> Active Threads >>>");
		body.append(FileUtils.lineSeparator);
		body.append(FileUtils.lineSeparator);
		body.append(getThreadInformation());
		body.append("<<< Active Threads <<<");
		body.append(FileUtils.lineSeparator);
		body.append(FileUtils.lineSeparator);
		
		return body.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang
	 * .Thread, java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(final Thread arg0, final Throwable arg1) {
		String crashReport = getCrashReport(arg1);
		System.out.println(crashReport);
		sendReport(crashReport);
		this.application.shutdown();
		this.previousHandler.uncaughtException(arg0, arg1);
	}
	
}
