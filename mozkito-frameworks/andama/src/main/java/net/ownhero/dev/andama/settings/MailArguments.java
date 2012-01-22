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
	
	private final AndamaSettings settings;
	
	/**
	 * 
	 */
	public MailArguments(final AndamaSettings settings, final boolean isRequired) {
		this.settings = settings;
		
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
		} catch (UnknownHostException e) {
			addArgument(new StringArgument(settings, "mail.sender.host", "The hostname the crash mail is sent from",
					"localhost", isRequired));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentSet#getValue()
	 */
	@Override
	public Properties getValue() {
		return new Properties() {
			
			private static final long serialVersionUID = -4075576523389682827L;
			
			{
				put("mail.smtp.host", MailArguments.this.settings.getSetting("mail.host"));
				put("mail.transport.protocol", "smtp");
				put("mail.to", MailArguments.this.settings.getSetting("mail.to"));
				put("mail.subject", MailArguments.this.settings.getSetting("mail.subject"));
				put("mail.sender.name", MailArguments.this.settings.getSetting("mail.sender.name"));
				put("mail.sender.address", MailArguments.this.settings.getSetting("mail.sender.address"));
				put("mail.sender.host", MailArguments.this.settings.getSetting("mail.sender.host"));
				if (MailArguments.this.settings.getSetting("mail.username") != null) {
					put("mail.username", MailArguments.this.settings.getSetting("mail.username"));
				}
				
				if (MailArguments.this.settings.getSetting("mail.password") != null) {
					put("mail.password", MailArguments.this.settings.getSetting("mail.password"));
				}
				
			}
		};
	}
	
}
