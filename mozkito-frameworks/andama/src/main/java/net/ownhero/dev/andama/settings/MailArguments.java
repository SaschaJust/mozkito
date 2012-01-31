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
				put("mail.smtp.host", getArgument("mail.host").getValue());
				put("mail.transport.protocol", "smtp");
				put("mail.to", getArgument("mail.to").getValue());
				put("mail.subject", getArgument("mail.subject").getValue());
				put("mail.sender.name", getArgument("mail.sender.name").getValue());
				put("mail.sender.address", getArgument("mail.sender.address").getValue());
				put("mail.sender.host", getArgument("mail.sender.host").getValue());
				if (getArgument("mail.username").getValue() != null) {
					put("mail.username", getArgument("mail.username").getValue());
				}
				
				if (getArgument("mail.password").getValue() != null) {
					put("mail.password", getArgument("mail.password").getValue());
				}
				
			}
		};
		
		setCachedValue(properties);
		return true;
	}
	
}
