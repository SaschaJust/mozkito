/**
 * 
 */
package net.ownhero.dev.andama.settings;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MailArguments extends AndamaArgumentSet<Properties> {
	
	/**
	 * @param argumentSet
	 * @param isRequired
	 */
	public MailArguments(final AndamaArgumentSet<?> argumentSet, final boolean isRequired) {
		super(argumentSet, "Used configure mailer arguments for the crash reporter.", isRequired);
		
		addArgument(new StringArgument(argumentSet, "mail.host", "The hostname of the mail server",
		                               "mail.st.cs.uni-saarland.de", isRequired));
		addArgument(new StringArgument(argumentSet, "mail.to", "The recipient of the crash mail",
		                               "project_reposuite@st.cs.uni-saarland.de", isRequired));
		addArgument(new StringArgument(argumentSet, "mail.subject", "The subject of the crash mail",
		                               "Application Crash Report", isRequired));
		addArgument(new StringArgument(argumentSet, "mail.sender.name", "The name of the sender of the crash mail",
		                               "Andama Application", isRequired));
		addArgument(new StringArgument(argumentSet, "mail.sender.address",
		                               "The address of the sender of the crash mail",
		                               "andama-crasher@st.cs.uni-saarland.de", isRequired));
		addArgument(new StringArgument(argumentSet, "mail.username", "The smtp login username", null,
		                               argumentSet.getSettings().getSetting("mail.password") != null));
		addArgument(new MaskedStringArgument(argumentSet, "mail.password", "The smtp login password", null, false));
		
		try {
			addArgument(new StringArgument(argumentSet, "mail.sender.host", "The hostname the crash mail is sent from",
			                               InetAddress.getLocalHost().getHostName(), isRequired));
		} catch (final UnknownHostException e) {
			addArgument(new StringArgument(argumentSet, "mail.sender.host", "The hostname the crash mail is sent from",
			                               "localhost", isRequired));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentSet#init()
	 */
	@Override
	protected boolean init() {
		if (!isInitialized()) {
			synchronized (this) {
				if (!isInitialized()) {
					Properties properties = new Properties() {
						
						private static final long serialVersionUID = -4075576523389682827L;
						
						{
							put("mail.smtp.host", getSettings().getSetting("mail.host"));
							put("mail.transport.protocol", "smtp");
							put("mail.to", getSettings().getSetting("mail.to"));
							put("mail.subject", getSettings().getSetting("mail.subject"));
							put("mail.sender.name", getSettings().getSetting("mail.sender.name"));
							put("mail.sender.address", getSettings().getSetting("mail.sender.address"));
							put("mail.sender.host", getSettings().getSetting("mail.sender.host"));
							if (getSettings().getSetting("mail.username") != null) {
								put("mail.username", getSettings().getSetting("mail.username"));
							}
							
							if (getSettings().getSetting("mail.password") != null) {
								put("mail.password", getSettings().getSetting("mail.password"));
							}
							
						}
					};
					
					setCachedValue(properties);
					return true;
				}
			}
		}
		return true;
	}
	
}
