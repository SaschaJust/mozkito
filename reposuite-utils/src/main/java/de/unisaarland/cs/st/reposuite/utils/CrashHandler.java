/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		body.append(">>> Crash Report >>>");
		body.append(FileUtils.lineSeparator);
		body.append(FileUtils.lineSeparator);
		// try {
		// PackageInfo pi =
		// this.application.getPackageManager().getPackageInfo(this.application.getPackageName(),
		// 0);
		// body.append("Package Name: ").append(pi.packageName).append("\n");
		// body.append("Package Version: ").append(pi.versionCode).append("\n");
		// body.append("Phone Model: ").append(android.os.Build.MODEL).append("\n");
		// body.append("Phone Manufacturer: ").append(android.os.Build.MANUFACTURER).append("\n");
		// body.append("Android Version:").append(android.os.Build.VERSION.RELEASE).append("\n");
		// } catch (NameNotFoundException e1) {
		// }
		
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
