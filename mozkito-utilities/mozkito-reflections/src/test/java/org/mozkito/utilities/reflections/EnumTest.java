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

package org.mozkito.utilities.reflections;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class EnumTest {
	
	public class Human {
		
		public void sing(final HumanState state) {
			switch (state) {
				case HAPPY:
					singHappySong();
					break;
				case SAD:
					singDirge();
					break;
				default:
					new IllegalStateException("Invalid State: " + state);
			}
		}
		
		private void singDirge() {
			System.out.println("Don't cry for me Argentina, ...");
		}
		
		private void singHappySong() {
			System.out.println("When you're happy and you know it ...");
		}
	}
	
	public enum HumanState {
		HAPPY, SAD
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	private Human human;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.human = new Human();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	@Ignore
	public final void test() {
		final EnumReflection<HumanState> buster = new EnumReflection<HumanState>(HumanState.class, EnumTest.class);
		try {
			for (final HumanState state : HumanState.values()) {
				System.err.println(state);
				// switch (state) {
				// case HAPPY:
				// case SAD:
				// break;
				// default:
				// fail("Unknown state");
				// }
			}
			
			System.err.println("DELETE_BY_VALUE: HAPPY");
			
			buster.deleteByValue(HumanState.HAPPY);
			for (final HumanState state : HumanState.values()) {
				System.err.println("Trying: " + state);
				switch (state) {
					case SAD:
						System.err.println("Success.");
						break;
					case HAPPY:
					default:
						fail();
				}
			}
			
			buster.undo();
			buster.deleteByValue(HumanState.SAD);
			
			System.err.println("UNDO");
			
			System.err.println("DELETE_BY_VALUE: SAD");
			
			for (final HumanState state : HumanState.values()) {
				System.err.println(state);
			}
			
			buster.deleteByValue(HumanState.HAPPY);
			System.err.println("DELETE_BY_VALUE: HAPPY");
			
			for (final HumanState state : HumanState.values()) {
				System.err.println(state);
			}
			
		} finally {
			buster.restore();
			
			System.err.println("RESTORE");
			for (final HumanState state : HumanState.values()) {
				System.err.println(state);
			}
		}
	}
	
}
