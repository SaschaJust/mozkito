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
	 * 
	 */
	public MailArguments(final AndamaSettings settings, final boolean isRequired) {
		super(settings, "Used configure mailer arguments for the crash reporter.", isRequired);
		
		addArgument(new StringArgument(settings, "mail.host", "The hostname of the mail server",
		                               "mail.st.cs.uni-saarland.de", isRequired));
		addArgument(new StringArgument(settings, "mail.to", "The recipient of the crash mail",
		                               "project_reposuite@st.cs.uni-saarland.de", isRequired));
		addArgument(new StringArgument(settings, "mail.subject", "The subject of the crash mail",
		                               "Application Crash Report", isRequired));
		addArgument(new StringArgument(settings, "mail.sender.name", "The name of the sender of the crash mail",
		                               "Andama Application", isRequired));
		addArgument(new StringArgument(settings, "mail.sender.address", "The address of the sender of the crash mail",
		                               "andama-crasher@st.cs.uni-saarland.de", isRequired));
		addArgument(new StringArgument(settings, "mail.username", "The smtp login username", null,
		                               settings.getSetting("mail.password") != null));
		addArgument(new MaskedStringArgument(settings, "mail.password", "The smtp login password", null, false));
		
		try {
			addArgument(new StringArgument(settings, "mail.sender.host", "The hostname the crash mail is sent from",
			                               InetAddress.getLocalHost().getHostName(), isRequired));
		} catch (final UnknownHostException e) {
			addArgument(new StringArgument(settings, "mail.sender.host", "The hostname the crash mail is sent from",
			                               "localhost", isRequired));
		}
	}
	
	@Override
	protected boolean init() {
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
