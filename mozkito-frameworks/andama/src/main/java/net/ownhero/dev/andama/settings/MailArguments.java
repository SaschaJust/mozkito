/**
 * 
 */
package net.ownhero.dev.andama.settings;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import net.ownhero.dev.andama.settings.dependencies.IsSet;
import net.ownhero.dev.andama.settings.dependencies.Optional;
import net.ownhero.dev.andama.settings.dependencies.Required;
import net.ownhero.dev.andama.settings.dependencies.Requirement;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MailArguments extends AndamaArgumentSet<Properties> {
	
	private final StringArgument       username;
	private final MaskedStringArgument password;
	private final StringArgument       host;
	private final StringArgument       to;
	private final StringArgument       subject;
	private final StringArgument       senderName;
	private final StringArgument       senderAddress;
	
	/**
	 * @param argumentSet
	 * @param isRequired
	 */
	public MailArguments(final AndamaArgumentSet<?> argumentSet, final Requirement requirements) {
		super(argumentSet, "Used configure mailer arguments for the crash reporter.", requirements);
		
		this.host = new StringArgument(argumentSet, "mail.host", "The hostname of the mail server",
		                               "mail.st.cs.uni-saarland.de", new Required());
		this.to = new StringArgument(argumentSet, "mail.to", "The recipient of the crash mail",
		                             "project_reposuite@st.cs.uni-saarland.de", new Required());
		this.subject = new StringArgument(argumentSet, "mail.subject", "The subject of the crash mail",
		                                  "Application Crash Report", new Required());
		this.senderName = new StringArgument(argumentSet, "mail.sender.name",
		                                     "The name of the sender of the crash mail", "Andama Application",
		                                     new Required());
		this.senderAddress = new StringArgument(argumentSet, "mail.sender.address",
		                                        "The address of the sender of the crash mail",
		                                        "andama-crasher@st.cs.uni-saarland.de", new Required());
		this.password = new MaskedStringArgument(argumentSet, "mail.password", "The smtp login password", null,
		                                         new Optional());
		this.username = new StringArgument(argumentSet, "mail.username", "The smtp login username", null,
		                                   new IsSet(this.username));
		try {
			addArgument(new StringArgument(argumentSet, "mail.sender.host", "The hostname the crash mail is sent from",
			                               InetAddress.getLocalHost().getHostName(), new Required()));
		} catch (final UnknownHostException e) {
			addArgument(new StringArgument(argumentSet, "mail.sender.host", "The hostname the crash mail is sent from",
			                               "localhost", new Required()));
		}
	}
	
	/**
	 * @return the host
	 */
	public final StringArgument getHost() {
		return this.host;
	}
	
	/**
	 * @return the password
	 */
	public final MaskedStringArgument getPassword() {
		return this.password;
	}
	
	/**
	 * @return the senderAddress
	 */
	public final StringArgument getSenderAddress() {
		return this.senderAddress;
	}
	
	/**
	 * @return the senderName
	 */
	public final StringArgument getSenderName() {
		return this.senderName;
	}
	
	/**
	 * @return the subject
	 */
	public final StringArgument getSubject() {
		return this.subject;
	}
	
	/**
	 * @return the to
	 */
	public final StringArgument getTo() {
		return this.to;
	}
	
	/**
	 * @return the username
	 */
	public final StringArgument getUsername() {
		return this.username;
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
