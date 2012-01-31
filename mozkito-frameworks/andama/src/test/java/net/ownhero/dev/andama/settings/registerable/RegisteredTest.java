/**
 * 
 */
package net.ownhero.dev.andama.settings.registerable;

import java.util.HashSet;
import java.util.LinkedList;

import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.AndamaSettings;

import org.junit.Test;

/**
 * @author just
 * 
 */
public class RegisteredTest {
	
	@Test
	public void test() {
		final Registered registered = new Registered() {
			
			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void register(final AndamaSettings settings,
			                     final AndamaArgumentSet<?> arguments) {
				// TODO Auto-generated method stub
				
			}
		};
		
		new LinkedList<String>();
		// list.add("Andama");
		StringBuilder builder = new StringBuilder();
		String string = registered.deriveSettingsClassificationString(MappingSpecificSettings.class,
		                                                              new HashSet<String>(), builder);
		if (string.length() > 0) {
			if (builder.length() > 0) {
				builder.insert(0, '.');
			}
			builder.insert(0, string);
		}
		System.err.println(builder);
		builder = new StringBuilder();
		string = registered.deriveSettingsClassificationString(MappingSettings.class, new HashSet<String>(), builder);
		if (string.length() > 0) {
			if (builder.length() > 0) {
				builder.insert(0, '.');
			}
			builder.insert(0, string);
		}
		System.err.println(builder);
		builder = new StringBuilder();
		string = registered.deriveSettingsClassificationString(AndamaSpecificSettings.class, new HashSet<String>(),
		                                                       builder);
		if (string.length() > 0) {
			if (builder.length() > 0) {
				builder.insert(0, '.');
			}
			builder.insert(0, string);
		}
		System.err.println(builder);
		builder = new StringBuilder();
	}
}
