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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The Class InputReader. Singleton. This class reads a file containing source code. It uses '{', '}', and ';' as
 * delimiters and filters out comments, lines starting with # and the character '$'. This class has a putBack method,
 * which can be used to put input that was just read, or a part of it, back into the input stream. For instance if a
 * call to next() returned the string "foo();", then putBack("foo();") could be called and the next call to next() would
 * again return "foo();"
 */
public class InputReader {
	
	/** The input. */
	private FileReader       input;
	
	/** The buffer. */
	private Queue<Character> buffer;
	
	/** boolean indicating if the end of file has been reached. */
	boolean                  eof;
	
	/** The current line number. */
	int                      lineNum;
	
	/** The name of the file being parsed. */
	String                   fileName;
	
	/**
	 * Instantiates a new input reader.
	 */
	public InputReader() {
		
	}
	
	/**
	 * Gets the line number.
	 * 
	 * @return the line number
	 */
	public int getLineNumber() {
		return this.lineNum;
	}
	
	/**
	 * Gets the current location in the file.
	 * 
	 * @return the file name and current line number
	 */
	public String getLocation() {
		
		return this.fileName + ":" + this.lineNum;
	}
	
	/**
	 * Gets the path.
	 * 
	 * @return the path
	 */
	public String getPath() {
		PRECONDITIONS: {
			// none
		}
		
		return this.fileName;
	}
	
	/**
	 * Checks if there is still input to be read.
	 * 
	 * @return true, if there is still input to be read
	 */
	public boolean hasNext() {
		return !this.eof;
	}
	
	/**
	 * Skips all characters until an apostrophe is found.
	 */
	private void matchChar() {
		String s = ""; // stores the skipped characters
		try {
			Character c = read();
			while (c != null) {
				if (c == '\n') {
					this.lineNum++;
				}
				// eliminate occurrences of double backslashes (\\), so that if the following sequence of characters
				// occurs: \\', the second backslash will not be interpreted as an escape character for the apostrophe
				if (c == '\\') {
					final Character c2 = read();
					if (c2 == null) {
						return;
					}
					if (c2 == '\\') {
						c = read();
						continue;
					}
					s += c;
					c = c2;
				} else if ((c == '\'') && ((s.length() == 0) || (s.charAt(s.length() - 1) != '\\'))) {
					// curr.append(c);
					// apostrophe is found, stop skipping
					return;
				} else {
					s += c;
					c = read();
				}
			}
			
		} catch (final IOException e) {
			return;
		}
		
	}
	
	/**
	 * skip all characters until the end of the string is found.
	 */
	private void matchString() {
		String s = "";
		try {
			Character c = read();
			while (c != null) {
				if (c == '\n') {
					this.lineNum++;
				}
				
				// eliminate occurrences of double backslashes (\\), so that if the following sequence of characters
				// occurs: \\", the second backslash will not be interpreted as an escape character for the "
				if (c == '\\') {
					final Character c2 = read();
					if (c2 == null) {
						return;
					}
					if (c2 == '\\') {
						c = read();
						continue;
					}
					s += c;
					c = c2;
				}
				
				else if ((c == '"') && ((s.length() == 0) || (s.charAt(s.length() - 1) != '\\'))) {
					// curr.append(c); // += c;
					// end of string is found, stop skipping
					return;
				} else {
					s += c;
					c = read();
				}
			}
			
		} catch (final IOException e) {
			return;
		}
	}
	
	/**
	 * Returns the next token. Uses '{', '}', and ';' as token-separating delimiters
	 * 
	 * @return the next token
	 */
	public String next() {
		final StringBuilder curr = new StringBuilder(); // stores the next token
		
		if (!this.eof) {
			try {
				Character c = read();
				
				while ((c != null) && !this.eof) {
					if (c == '\n') {
						// reduce line breaks to spaces
						curr.append(" ");
						this.lineNum++;
						c = read();
						continue;
					}
					
					switch (c) {
						case '"':
							// skip over the contents of the string
							matchString();
							curr.append("\"\"");
							c = read();
							break;
						case '\'':
							// skip over the char
							matchChar();
							curr.append("\'\'");
							c = read();
							break;
						case '/':
							/*
							 * if(curr.length() != 0){ if(!Character.isWhitespace(lastRead())){ curr.append(c); // += c;
							 * c = read(); break; } }
							 */
							final Character c2 = read();
							
							if (c2 == null) {
								return curr.toString();
							}
							
							if (c2 == '*') {
								skipComment();
								c = read();
								break;
							} else if (c2 == '/') {
								skipLineComment();
								c = read();
								break;
							} else {
								curr.append(c); // += c;
								c = c2;
								break;
							}
							
						case '{':
							// found delimiter, return string
							return curr.toString() + c;
						case '}':
							// found delimiter, return string
							return curr.toString() + c;
						case ';':
							// found delimiter, return string
							return curr.toString() + c;
						case '#':
							skipLine();
							c = read();
							break;
						
						default:
							curr.append(c);
							c = read();
					}
				}
				
			} catch (final IOException e) {
				return curr.toString();
			}
		}
		return curr.toString();
	}
	
	/**
	 * Puts a String back into the input stream. Works on a first-in-last-out principle. So the string that is put back
	 * is read completely before any more input is read from the file or strings that were previously put back.
	 * 
	 * @param s
	 *            the s
	 */
	public void putBack(final String s) {
		int i;
		for (i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '\n') {
				this.lineNum--;
			}
			this.buffer.add(s.charAt(i));
		}
	}
	
	/**
	 * read the next character in the input stream.
	 * 
	 * @return the next character
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Character read() throws IOException {
		// if the buffer is not empty, read from there:
		if (!this.buffer.isEmpty()) {
			return this.buffer.poll();
		}
		
		// if the buffer is empty, read from the file
		final int c = this.input.read();
		if (c < 0) {
			this.eof = true;
			return null;
		}
		
		final char ch = (char) c;
		
		// deal with different line endings, transform them all to \n
		if (ch == '\r') {
			final Character character = read();
			if (character != null) {
				final char c2 = character;
				if (c2 != '\n') {
					putBack(c2 + "");
				}
			}
			return '\n';
		}
		
		return (char) c;
	}
	
	/**
	 * Sets the file to be parsed.
	 * 
	 * @param file
	 *            the new file
	 * 
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	public void setFile(final File file) throws FileNotFoundException {
		this.fileName = file.getPath();
		this.input = new FileReader(file);
		this.buffer = new LinkedList<Character>();
		this.eof = false;
		this.lineNum = 1;
	}
	
	/**
	 * Skip all characters until the end of the comment is found: * /.
	 */
	private void skipComment() {
		try {
			Character c = read();
			
			while (c != null) {
				if (c == '\n') {
					this.lineNum++;
				}
				if (c == '*') {
					c = read();
					if (c == '/') {
						// end of comment found, stop skipping
						return;
					}
				} else {
					c = read();
				}
			}
		} catch (final IOException e) {
			return;
		}
		
	}
	
	/**
	 * Skip all characters until the end of the line, unless a start of comment character sequence appears, then skip
	 * until the end of the comment.
	 */
	private void skipLine() {
		try {
			Character c = read();
			while ((c != null) && (c != '\n')) {
				if (c == '/') {
					c = read();
					if (c == '*') {
						skipComment();
					} else {
						continue;
					}
				}
				if (c == '\\') {
					c = read();
					if (c != '\n') {
						continue;
					} else {
						this.lineNum++;
					}
				}
				c = read();
			}
			this.lineNum++;
		} catch (final IOException e) {
			return;
		}
		
	}
	
	/**
	 * Skip all characters until the end of the line.
	 */
	private void skipLineComment() {
		try {
			Character c = read();
			while ((c != null) && (c != '\n')) {
				c = read();
			}
			this.lineNum++;
		} catch (final IOException e) {
			return;
		}
		
	}
	
}
