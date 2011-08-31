/**
 * 
 */
package net.ownhero.dev.andama.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.utils.AndamaUtils;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class AndamaCrashHandler extends ThreadGroup {
	
	private static final Properties                     mailProps = new Properties() {
		                                                              
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
	
	private static Map<AndamaChain, AndamaCrashHandler> handlers  = new HashMap<AndamaChain, AndamaCrashHandler>();
	
	public static void init(final AndamaChain toolchain) {
		if (!handlers.containsKey(toolchain)) {
			handlers.put(toolchain, new AndamaCrashHandler(toolchain));
		}
	}
	
	private AndamaChain    application = null;
	
	private static boolean executed    = false;
	
	/**
	 * @param application
	 *            a {@link AndamaChain} instance
	 */
	private AndamaCrashHandler(final AndamaChain application) {
		super(AndamaChain.class.getSimpleName());
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
		builder.append(AndamaUtils.lineSeparator);
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
		body.append(AndamaUtils.lineSeparator);
		body.append("This report does NOT contain any usernames or passwords.");
		body.append(AndamaUtils.lineSeparator);
		body.append(AndamaUtils.lineSeparator);
		
		try {
			body.append(">>> Crash Report >>>");
			body.append(AndamaUtils.lineSeparator);
			body.append(AndamaUtils.lineSeparator);
			
			StringWriter stack = new StringWriter();
			PrintWriter writer = new PrintWriter(stack);
			e.printStackTrace(writer);
			
			// body.append(AndamaUtils.lineSeparator);
			// body.append(AndamaUtils.lineSeparator);
			body.append("Stacktrace:");
			body.append(AndamaUtils.lineSeparator);
			body.append(AndamaUtils.lineSeparator);
			body.append(stack.toString());
			body.append(AndamaUtils.lineSeparator);
			
			Throwable t = e.getCause();
			while (t != null) {
				stack = new StringWriter();
				writer = new PrintWriter(stack);
				t.printStackTrace(writer);
				
				body.append(AndamaUtils.lineSeparator);
				body.append("Cause Stacktrace:");
				body.append(AndamaUtils.lineSeparator);
				body.append(AndamaUtils.lineSeparator);
				body.append(stack.toString());
				body.append(AndamaUtils.lineSeparator);
				
				t = t.getCause();
			}
			
			body.append("<<< Crash Report <<<");
			body.append(AndamaUtils.lineSeparator);
			body.append(AndamaUtils.lineSeparator);
		} catch (Throwable t) {
			
		}
		
		try {
			body.append(">>> System Information >>>");
			body.append(AndamaUtils.lineSeparator);
			body.append(AndamaUtils.lineSeparator);
			body.append(getSystemInformation());
			body.append(getClassLoadingInformation());
			body.append(getRuntimeInformation());
			body.append(AndamaUtils.lineSeparator);
			body.append("<<< System Information <<<");
			body.append(AndamaUtils.lineSeparator);
			body.append(AndamaUtils.lineSeparator);
		} catch (Throwable t) {
			
		}
		
		try {
			body.append(">>> RepoSuite Setup >>>");
			body.append(AndamaUtils.lineSeparator);
			body.append(AndamaUtils.lineSeparator);
			body.append(getRepoSuiteSettings());
			body.append(AndamaUtils.lineSeparator);
			body.append(AndamaUtils.lineSeparator);
			body.append("<<< RepoSuite Setup <<<");
			body.append(AndamaUtils.lineSeparator);
			body.append(AndamaUtils.lineSeparator);
		} catch (Throwable t) {
			
		}
		try {
			body.append(">>> RepoSuite ToolInfo >>>");
			body.append(AndamaUtils.lineSeparator);
			body.append(AndamaUtils.lineSeparator);
			body.append(getToolInformation());
			body.append("<<< RepoSuite ToolInfo <<<");
			body.append(AndamaUtils.lineSeparator);
			body.append(AndamaUtils.lineSeparator);
		} catch (Throwable t) {
			
		}
		
		try {
			body.append(">>> Active Threads >>>");
			body.append(AndamaUtils.lineSeparator);
			body.append(AndamaUtils.lineSeparator);
			body.append(getThreadInformation());
			body.append("<<< Active Threads <<<");
			body.append(AndamaUtils.lineSeparator);
			body.append(AndamaUtils.lineSeparator);
		} catch (Throwable t) {
			
		}
		
		return body.toString();
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
		builder.append(AndamaUtils.lineSeparator);
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
		builder.append(AndamaUtils.lineSeparator);
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
		return visit(root, 0) + AndamaUtils.lineSeparator;
	}
	
	/**
	 * @return information provided by the {@link AndamaChain}
	 */
	protected String getToolInformation() {
		return (this.application != null)
		                                 ? this.application.getSettings().getToolInformation()
		                                 : "";
	}
	
	/**
	 * Sends the given report to the email address using the mail settings in
	 * mailProps
	 * 
	 * @param report
	 *            the report to be send
	 */
	private void sendReport(final String report) {
		if (this.application.getSettings().isCrashEmailDisabled()) {
			try {
				Session session = Session.getDefaultInstance(AndamaCrashHandler.mailProps, null);
				Transport transport = session.getTransport();
				MimeMessage message = new MimeMessage(session);
				message.setSubject(AndamaCrashHandler.mailProps.getProperty("mail.subject"));
				message.addRecipient(Message.RecipientType.TO,
				                     new InternetAddress(AndamaCrashHandler.mailProps.getProperty("mail.to")));
				message.setFrom(new InternetAddress(AndamaCrashHandler.mailProps.getProperty("mail.sender.address"),
				                                    AndamaCrashHandler.mailProps.getProperty("mail.sender.name")));
				message.setSender(new InternetAddress(AndamaCrashHandler.mailProps.getProperty("mail.sender.address"),
				                                      AndamaCrashHandler.mailProps.getProperty("mail.sender.name")));
				message.setContent(report, "text/plain");
				transport.connect();
				transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
				transport.close();
			} catch (MessagingException e) {
				if (Logger.logWarn()) {
					Logger.warn(e.getMessage(), e);
				}
			} catch (UnsupportedEncodingException e) {
				if (Logger.logWarn()) {
					Logger.warn(e.getMessage(), e);
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
		if (!AndamaCrashHandler.executed) {
			AndamaCrashHandler.executed = true;
			
			if ((arg1 == null) || (arg1 instanceof Shutdown)) {
				
				if (Logger.logInfo()) {
					Logger.info("Received shutdown notification from " + arg0.getName() + " with notice: "
					        + arg1.getMessage());
				}
			} else {
				
				if (Logger.logError()) {
					Logger.error("[[ " + arg1.getClass().getSimpleName() + " ]] Generating crash report.");
					Logger.error(arg1.getMessage());
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
			
			if (this.application != null) {
				this.application.shutdown();
			}
			
			// TODO wait and kill unresponsive threads
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
			builder.append(AndamaUtils.lineSeparator);
			for (StackTraceElement element : thread.getStackTrace()) {
				builder.append(indent);
				builder.append("| ");
				builder.append(element.toString());
				builder.append(AndamaUtils.lineSeparator);
			}
			// builder.append(AndamaUtils.lineSeparator);
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
