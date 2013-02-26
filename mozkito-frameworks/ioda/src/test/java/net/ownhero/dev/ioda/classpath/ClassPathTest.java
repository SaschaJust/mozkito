/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package net.ownhero.dev.ioda.classpath;

import static org.junit.Assert.fail;

import java.util.Collection;

import net.ownhero.dev.ioda.classpath.elements.CompilationUnit;
import net.ownhero.dev.ioda.classpath.elements.Resource;
import net.ownhero.dev.ioda.classpath.exceptions.ElementLoadingException;

import org.junit.Test;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class ClassPathTest {
	
	/**
	 * Test.
	 */
	@Test
	public final void testMineCUs() {
		final Collection<CompilationUnit> classes = ClassPath.findClasses();
		System.out.println("Found " + classes.size() + " compilation units.");
		for (final CompilationUnit unit : classes) {
			if (unit.getName().startsWith("net.ownhero.dev.")) {
				System.out.println(unit);
				Class<?> clazz;
				try {
					clazz = unit.load();
					System.out.println("Class: " + clazz);
					System.out.println("Full qualified name: " + clazz.getCanonicalName());
					System.out.println("Loaded by: " + ClassPath.who(clazz));
					System.out.println("Loaded from: " + ClassPath.where(clazz));
					System.out.println("Version: " + ClassPath.version(clazz));
				} catch (final ElementLoadingException e) {
					System.out.println("Missing reference: " + e.getMessage());
				} catch (final IncompatibleClassChangeError e) {
					System.out.println("Incompatible binary change detected. " + e.getMessage());
				} catch (final ExceptionInInitializerError e) {
					System.out.println("Missing transitive libraries: " + e.getMessage());
				} catch (final VerifyError e) {
					if (!unit.getName().contains("sun.tools.")) {
						fail();
					}
				}
			}
		}
	}
	
	/**
	 * Test mine resources.
	 */
	@Test
	public final void testMineResources() {
		final Collection<Resource> resources = ClassPath.getResources();
		System.out.println("Found " + resources.size() + " resources.");
		for (final Resource resource : resources) {
			System.out.println(resource);
		}
	}
}
