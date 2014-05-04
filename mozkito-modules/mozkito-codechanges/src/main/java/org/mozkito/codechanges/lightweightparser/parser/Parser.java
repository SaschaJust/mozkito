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
package org.mozkito.codechanges.lightweightparser.parser;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

/**
 * The Class Parser.
 */
public class Parser {
	
	/** The input reader instance. */
	private final InputReader input;
	
	/** The top level parser instance. */
	private final TopLevel    topLevel;
	
	/** The regular expression which a file name should match. */
	private String            regexName;
	
	/** The regular expression which a file path should match. */
	private String            regexPath;
	
	/**
	 * Instantiates a new parser.
	 * 
	 * 
	 * @param input
	 *            the input reader instance
	 * @param tl
	 *            the top level parser instance
	 * @param regexName
	 *            the regular expression that file names should match, or null in which case regexName is set to ".*"
	 * @param regexPath
	 *            the regular expression that file paths should match, or null in which case regexPath is set to ".*"
	 */
	public Parser(final InputReader input, final TopLevel tl, final String regexName, final String regexPath) {
		this.input = input;
		this.topLevel = tl;
		if (regexName == null) {
			this.regexName = ".*";
		} else {
			this.regexName = regexName;
		}
		
		if (regexPath == null) {
			this.regexPath = ".*";
		} else {
			this.regexPath = regexPath;
		}
	}
	
	/**
	 * Parses the all files whose name matches regexName and whose path matches regexPath and all subdirectories whose
	 * path matches regexPath.
	 * 
	 * @param file
	 *            the file or directory to be parsed
	 */
	public void parse(final File file) {
		// System.out.println(file);
		if (file.isFile()) {
			try {
				this.input.setFile(file);
				this.topLevel.parse();
			} catch (final FileNotFoundException e) {
				System.out.println("Could not open file: " + file.getName());
			}
		} else if (file.isDirectory()) {
			final FileFilter ff = new FileFilter() {
				
				public boolean accept(final File f) {
					return f.getPath().matches(Parser.this.regexPath)
					        && (f.getName().matches(Parser.this.regexName) || f.isDirectory());
					
				}
			};
			
			for (final File s : file.listFiles(ff)) {
				parse(s);
			}
		} else {
			System.out.println(file + " is not a valid file or directory path");
		}
		
	}
	
}
