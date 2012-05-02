/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.HostArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.PortArgument;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.ProxyConfig;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringUtils;

/**
 * The Class MantisOptions.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class ProxyOptions extends ArgumentSetOptions<ProxyConfig, ArgumentSet<ProxyConfig, ProxyOptions>> {
	
	/** The host options. */
	private HostArgument.Options      hostOptions;
	
	/** The port options. */
	private PortArgument.Options      portOptions;
	
	/** The username options. */
	private StringArgument.Options    usernameOptions;
	
	/** The password options. */
	private StringArgument.Options    passwordOptions;
	
	/** The internal options. */
	private BooleanArgument.Options   internalOptions;
	
	/** The cache dir options. */
	private DirectoryArgument.Options cacheDirOptions;
	
	/**
	 * Instantiates a new mantis options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirement
	 *            the requirement
	 */
	@NoneNull
	public ProxyOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirement) {
		super(argumentSet, "proxy", "Arguments to configure HTTP/S proxy usage.", requirement);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init(java.util.Map)
	 */
	@Override
	@NoneNull
	public ProxyConfig init() {
		// PRECONDITIONS
		
		try {
			final BooleanArgument internalArgument = getSettings().getArgument(this.internalOptions);
			final HostArgument hostArgument = getSettings().getArgument(this.hostOptions);
			final PortArgument portArgument = getSettings().getArgument(this.portOptions);
			final StringArgument usernameArgument = getSettings().getArgument(this.usernameOptions);
			final StringArgument passwordArgument = getSettings().getArgument(this.passwordOptions);
			final DirectoryArgument cacheDirArgument = getSettings().getArgument(this.cacheDirOptions);
			
			if (internalArgument.getValue()) {
				// use internal proxy
				if ((hostArgument.getValue() != null) && !hostArgument.getValue().equalsIgnoreCase("localhost")
				        && !hostArgument.getValue().equalsIgnoreCase("127.0.0.1")) {
					if (Logger.logWarn()) {
						Logger.warn("Proxy '%s' argument is invalid. While internal proxy is enabled, we do not support '%s' arguments other than localhost.",
						            hostArgument.getTag(), hostArgument.getTag());
					}
				}
				
				// create temporary file to copy JAR to.
				final File jarFile = FileUtils.createRandomFile("jsucks5-", ".jar", FileShutdownAction.DELETE);
				
				try (InputStream inputStream = ProxyOptions.class.getResourceAsStream("/jsucks5.jar");
				        OutputStream outputStream = new FileOutputStream(jarFile)) {
					// copy JAR to temp file
					IOUtils.copyInputStream(inputStream, outputStream);
					final List<String> commandList = new LinkedList<>();
					// add java binary
					commandList.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
					commandList.add("-jar");
					commandList.add(jarFile.getAbsolutePath());
					commandList.add("" + portArgument.getValue());
					commandList.add(cacheDirArgument.getValue().getAbsolutePath());
					
					if (Logger.logDebug()) {
						Logger.debug(StringUtils.join(commandList, " "));
					}
					
					final ProcessBuilder builder = new ProcessBuilder(commandList);
					final Process process = builder.start();
					
					new Thread("Proxy-Errors") {
						
						private final InputStream threadInputStream = process.getErrorStream();
						
						@Override
						public void run() {
							try (BufferedReader reader = new BufferedReader(
							                                                new InputStreamReader(
							                                                                      this.threadInputStream))) {
								String line;
								while ((line = reader.readLine()) != null) {
									if (Logger.logError()) {
										Logger.error(line);
									}
								}
							} catch (final IOException e) {
								if (Logger.logError()) {
									Logger.error(e);
								}
							}
						};
					};
					
					Runtime.getRuntime().addShutdownHook(new Thread() {
						
						@Override
						public void run() {
							process.destroy();
						};
					});
					// wait for proxy to settle
					try {
						Thread.sleep(1000);
					} catch (final InterruptedException ignore) {
						// ignore
					}
					
					// TODO: check if there is a server listening on localhost:port
				} catch (final IOException e) {
					throw new UnrecoverableError(e);
				}
				
				final ProxyConfig proxyConfig = new ProxyConfig(hostArgument.getValue(), portArgument.getValue()
				                                                                                     .intValue(),
				                                                usernameArgument.getValue(),
				                                                passwordArgument.getValue(),
				                                                internalArgument.getValue());
				
				if (proxyConfig.getHost() != null) {
					System.getProperties().put("socksProxyHost", proxyConfig.getHost());
					if (Logger.logTrace()) {
						Logger.trace("Setting socksProxyHost=%s", proxyConfig.getHost());
					}
				} else {
					System.getProperties().put("socksProxyHost", "localhost");
					if (Logger.logTrace()) {
						Logger.trace("Setting socksProxyHost=localhost");
					}
				}
				if (Logger.logTrace()) {
					Logger.trace("Setting socksProxyPort=%s", proxyConfig.getPort());
				}
				
				System.getProperties().put("socksProxyPort", proxyConfig.getPort());
				
				return proxyConfig;
			}
			// TODO: check if there is a server listening on host:port
			
			return new ProxyConfig(hostArgument.getValue(), portArgument.getValue().intValue(),
			                       usernameArgument.getValue(), passwordArgument.getValue(),
			                       internalArgument.getValue());
			
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Req.
	 * 
	 * @param option
	 *            the option
	 * @param map
	 *            the map
	 */
	private final void req(final IOptions<?, ?> option,
	                       final Map<String, IOptions<?, ?>> map) {
		map.put(option.getName(), option);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	@NoneNull
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		// PRECONDITIONS
		
		try {
			final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			
			this.internalOptions = new BooleanArgument.Options(set, "internal", "Use internal proxy (recommended).",
			                                                   true, Requirement.required);
			req(this.internalOptions, map);
			
			this.hostOptions = new HostArgument.Options(set, "host", //$NON-NLS-1$
			                                            Messages.getString("ProxyOptions.proxyHost_description"), //$NON-NLS-1$
			                                            null, Requirement.equals(this.internalOptions, false));
			req(this.hostOptions, map);
			
			this.cacheDirOptions = new DirectoryArgument.Options(
			                                                     set,
			                                                     "cacheDir",
			                                                     "Cache directory for the internal proxy.",
			                                                     null,
			                                                     Requirement.and(required()
			                                                                               ? Requirement.required
			                                                                               : Requirement.optional,
			                                                                     Requirement.equals(this.internalOptions,
			                                                                                        true)), true);
			
			req(this.cacheDirOptions, map);
			
			this.portOptions = new PortArgument.Options(
			                                            set,
			                                            "port", Messages.getString("ProxyOptions.proxyPort_description"), 8584, //$NON-NLS-1$ //$NON-NLS-2$
			                                            Requirement.required, true);
			req(this.portOptions, map);
			
			this.usernameOptions = new StringArgument.Options(
			                                                  set,
			                                                  "username", //$NON-NLS-1$
			                                                  Messages.getString("ProxyOptions.proxyUser_description"), null, //$NON-NLS-1$
			                                                  Requirement.optional);
			
			req(this.usernameOptions, map);
			this.passwordOptions = new StringArgument.Options(
			                                                  set,
			                                                  "password", //$NON-NLS-1$
			                                                  Messages.getString("ProxyOptions.proxyPassword_description"), //$NON-NLS-1$
			                                                  null, Requirement.iff(this.usernameOptions), true);
			
			req(this.passwordOptions, map);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
