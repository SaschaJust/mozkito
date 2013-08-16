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

package research.persons;

import org.mozkito.research.persons.GraphAnalyzer;
import org.mozkito.research.persons.GraphBrowser;
import org.mozkito.research.persons.GraphGenerator;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			if (args.length == 0) {
				System.out.println("Please give a task 'generation', 'browsing', 'analysis'.");
			} else {
				if ("generation".equalsIgnoreCase(args[0])) {
					GraphGenerator.main(new String[0]);
				} else if ("browsing".equalsIgnoreCase(args[0])) {
					GraphBrowser.main(new String[0]);
				} else if ("analysis".equalsIgnoreCase(args[0])) {
					GraphAnalyzer.main(new String[0]);
				} else {
					System.out.println("Invalid command. Use 'generation' or 'browsing' instead.");
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
