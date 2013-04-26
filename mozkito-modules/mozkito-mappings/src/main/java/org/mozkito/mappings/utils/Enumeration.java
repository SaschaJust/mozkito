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

package org.mozkito.mappings.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jregex.REFlags;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.simple.Positive;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Group;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;

import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.io.FileUtils;

/**
 * The Class Enumeration.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Enumeration implements Iterable<EnumerationEntry>, Collection<EnumerationEntry> {
	
	/**
	 * The Enum Type.
	 */
	public static enum Type {
		/** The romanic. */
		ROMANIC,
		/** The alphabetic. */
		ALPHABETIC,
		/** The numerical. */
		NUMERICAL;
	}
	
	/** The Constant typomap. */
	@SuppressWarnings ("serial")
	private static final Map<Character, Set<Character>> TYPO_MAP          = new TreeMap<Character, Set<Character>>() {
		                                                                      
		                                                                      {
			                                                                      put('a', new TreeSet<Character>() {
				                                                                      
				                                                                      {
					                                                                      add('q');
					                                                                      add('s');
					                                                                      add('z');
				                                                                      }
			                                                                      });
			                                                                      put('e', new TreeSet<Character>() {
				                                                                      
				                                                                      {
					                                                                      add('w');
					                                                                      add('r');
					                                                                      add('d');
					                                                                      add('3');
					                                                                      add('4');
					                                                                      add('s');
				                                                                      }
			                                                                      });
		                                                                      }
	                                                                      };
	
	/**
	 * 
	 * The Constant ALPHA_ENUM_BULLET.
	 * 
	 * X) and X ) X. X.) X] (X) and ( X ) [X] {X} X:
	 * 
	 * 
	 */
	public static final String                          ALPHA_ENUM_BULLET = "(^|[^a-zA-Z0-9])({BULLET}[([]\\s*[a-zA-Z]\\s*[)\\]]|[a-zA-Z]\\s?[)\\]]|[a-zA-Z][.:]|[a-zA-Z]\\.[)\\\\])"; //$NON-NLS-1$
	                                                                                                                                                                                   
	// @formatter:off
	/** The Constant ALPHA_BULLETS. */
	public static final String[] ALPHA_BULLETS = new String[] { 
		                                                       	/* matches (a) */
                                                               	"(?:^|\\W)" + "(?# makes sure we are behind a line begin or a non-word character, i.e. [^a-zA-Z_0-9])" 
	                                                           	+ "({BULLET}" + "(?# this is the match of the complete bullet incl. trailing whitespaces)"
	                                                           		+ "\\(" +"\\s*" + "({ID}\\p{Alpha})" + "\\s*" + "\\)" + "\\s+" 
	                                                           	+ ")",
	                                                          
	                                                           	/* matches [a] */
	                                                           	"(?:^|\\W)" + "(?# makes sure we are behind a line begin or a non-word character, i.e. [^a-zA-Z_0-9])" 
	    	                                                    + "({BULLET}" + "(?# this is the match of the complete bullet incl. trailing whitespaces)"
	    	                                                    	+ "\\[" +"\\s*" + "({ID}\\p{Alpha})" + "\\s*" + "\\]"  + "\\s+" 
	    	                                                    + ")",

	                                                           	/* matches a) */
	                                                           	"(?:^|\\W)" + "(?# makes sure we are behind a line begin or a non-word character, i.e. [^a-zA-Z_0-9])" 
	    	                                                    + "({BULLET}" + "(?# this is the match of the complete bullet incl. trailing whitespaces)"
	    	                                                    	+ "({ID}\\p{Alpha})" + "\\s*" + "\\)"  + "\\s+" 
	    	                                                    + ")",

	                                                           	/* matches a] */
	                                                           	"(?:^|\\W)" + "(?# makes sure we are behind a line begin or a non-word character, i.e. [^a-zA-Z_0-9])" 
	    	                                                    + "({BULLET}" + "(?# this is the match of the complete bullet incl. trailing whitespaces)"
	    	                                                    	+ "({ID}\\p{Alpha})" + "\\s*" + "\\]"  + "\\s+" 
	    	                                                    + ")",	

	                                                           	/* matches a. */
	                                                           	"(?:^|\\W)" + "(?# makes sure we are behind a line begin or a non-word character, i.e. [^a-zA-Z_0-9])" 
	    	                                                    + "({BULLET}" + "(?# this is the match of the complete bullet incl. trailing whitespaces)"
	    	                                                    	+ "({ID}\\p{Alpha})" + "\\s*" + "\\."  + "\\s+" 
	    	                                                    + ")",	

		                                                        /* matches a.) */
	                                                           	"(?:^|\\W)" + "(?# makes sure we are behind a line begin or a non-word character, i.e. [^a-zA-Z_0-9])" 
	    	                                                    + "({BULLET}" + "(?# this is the match of the complete bullet incl. trailing whitespaces)"
	    	                                                    	+ "({ID}\\p{Alpha})" + "\\s*" + "\\.\\)"  + "\\s+" 
	    	                                                    + ")",	
															  };
	// @formatter:on
	
	/**
	 * Extract.
	 * 
	 * @param text
	 *            the text
	 * @return the collection
	 */
	public static Collection<Enumeration> extract(final String text) {
		
		return extract(text, Type.values(), 0, text.length());
	}
	
	/**
	 * Extract.
	 * 
	 * @param text
	 *            the text
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the collection
	 */
	public static Collection<Enumeration> extract(final String text,
	                                              final int start,
	                                              final int end) {
		
		return extract(text, Type.values(), start, end);
	}
	
	/**
	 * Extract.
	 * 
	 * @param text
	 *            the text
	 * @param type
	 *            the type
	 * @return the collection
	 */
	public static Collection<Enumeration> extract(final String text,
	                                              final Type[] type) {
		
		return extract(text, type, 0, text.length());
	}
	
	/**
	 * Extract.
	 * 
	 * @param text
	 *            the text
	 * @param type
	 *            the type
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the collection
	 */
	public static Collection<Enumeration> extract(final String text,
	                                              final Type[] type,
	                                              final int start,
	                                              final int end) {
		final List<Enumeration> list = new LinkedList<>();
		
		if (ArrayUtils.contains(type, Type.ROMANIC)) {
			list.addAll(extractRoman(text, type, start, end));
		}
		
		if (ArrayUtils.contains(type, Type.ALPHABETIC)) {
			list.addAll(extractAlphabetic(text, type, start, end));
		}
		
		if (ArrayUtils.contains(type, Type.NUMERICAL)) {
			list.addAll(extractNumerical(text, type, start, end));
		}
		
		return list;
	}
	
	/**
	 * Extract alphabetic.
	 * 
	 * @param text
	 *            the text
	 * @param type
	 *            the type
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the collection
	 */
	private static Collection<Enumeration> extractAlphabetic(@NotNull final String text,
	                                                         @NotNull @NotEmpty final Type[] type,
	                                                         @NotNegative final int start,
	                                                         @Positive final int end) {
		// PRECONDITIONS
		
		try {
			final Collection<Enumeration> results = new LinkedList<>();
			int maxIndex = text.length();
			
			for (final String pattern : Enumeration.ALPHA_BULLETS) {
				final Regex bulletRegex = new Regex(pattern);
				final MultiMatch multiMatch = bulletRegex.findAll(text);
				
				if (multiMatch != null) {
					Enumeration enumeration = new Enumeration(Type.ALPHABETIC, text);
					final LinkedList<EnumerationEntry> list = new LinkedList<>();
					final LinkedList<Match> matchList = new LinkedList<>();
					final LinkedList<Match> matchList2 = new LinkedList<>();
					for (final Match match : multiMatch) {
						matchList.add(match);
						matchList2.add(match);
					}
					
					final ListIterator<Match> iterator = matchList.listIterator(matchList.size());
					
					while (iterator.hasPrevious()) {
						final Match match = iterator.previous();
						
						final Group group = match.getGroup("BULLET");
						char identifier = match.getGroup("ID").getMatch().charAt(0);
						String identifierString = "" + identifier;
						
						if ((group != null) && "A".equalsIgnoreCase(identifierString)) {
							boolean done = false;
							
							list.add(new EnumerationEntry(enumeration, identifierString, group.start(),
							                              match.getFullMatch().end()));
							
							while (!done) {
								++identifier;
								identifierString = "" + identifier;
								
								EnumerationEntry c = findFollowUp(enumeration, matchList2, group.end(), maxIndex,
								                                  identifierString);
								
								if (c == null) {
									// if correct typos
									c = findRelaxedFollowUp(enumeration, text, matchList2, identifierString,
									                        group.end(), maxIndex);
									
									if (c == null) {
										// find successor
										c = findFollowUp(enumeration, matchList2, group.end(), maxIndex,
										                 (identifier + 1) + "");
										
										// try harder finding intermediate enumeration
										if (c != null) {
											c = findTryHard(enumeration, text, identifier, group.end(), c.getStart());
											if (c == null) {
												done = true;
											} else {
												list.add(c);
											}
										} else {
											done = true;
										}
									} else {
										list.add(c);
									}
								} else {
									list.add(c);
								}
							}
							
							if (list.size() > 1) {
								EnumerationEntry previous = null;
								
								// check endings
								for (final EnumerationEntry entry : list) {
									if (previous != null) {
										previous.setEnd(entry.getStart() - 1);
									}
									
									previous = entry;
									enumeration.add(entry);
								}
								
								maxIndex = list.iterator().next().getStart() - 1;
								
								// remove all from multiMatch
								for (final EnumerationEntry entry : list) {
									CollectionUtils.filter(matchList2, new Predicate() {
										
										@Override
										public boolean evaluate(final Object object) {
											final Match match = (Match) object;
											return match.getFullMatch().start() < entry.getStart();
										}
									});
								}
								
								results.add(enumeration);
								
								// check if enumerations spread over several lines
								if (multipleLines(enumeration)) {
									// check if enumerations spread over several paragraphs
									if (multipleParagraphs(enumeration)) {
										// check for indentation
										final String indent = indentation(enumeration);
										if (indent != null) {
											// set last entry's end to first line violating indentation or end of text
											final String substring = enumeration.getText()
											                                    .substring(enumeration.last().getEnd() + 1);
											final String[] lines = substring.split(FileUtils.lineSeparator);
											final int offset = 0;
											for (final String line : lines) {
												if (!line.startsWith(indent)) {
													break;
												}
											}
											
											enumeration.last().setEnd(enumeration.last().getEnd() + offset);
										} else {
											// last last entry's end to end of text
											enumeration.last().setEnd(text.length());
										}
									} else {
										// set last entry's end to first empty line or end of text
										final String substring = enumeration.getText()
										                                    .substring(enumeration.last().getEnd() + 1);
										final Regex regex = new Regex("({TEXT}.*)\\n\\s*$", REFlags.MULTILINE);
										final Match emptylineMatch = regex.find(substring);
										enumeration.last().setEnd(enumeration.last().getEnd()
										                                  + emptylineMatch.getGroup("TEXT").end());
									}
								} else {
									// check if enumerations are one-liners ^enumerationEntry$
									if (oneLiners(enumeration)) {
										// set ending of last entry to end of line
										final String substring = enumeration.getText()
										                                    .substring(enumeration.last().getEnd() + 1);
										final int linebreak = substring.indexOf(FileUtils.lineSeparator);
										enumeration.last().setEnd(enumeration.last().getEnd() + linebreak);
									} else {
										// determine first sentence following the last entry's token and set ending of
										// the entry to end of sentence
										final List<String> sentences = TextSeparator.sentences(enumeration.getText()
										                                                                  .substring(enumeration.last()
										                                                                                        .getStart() + 2));
										if (!sentences.isEmpty()) {
											final String sentence = sentences.iterator().next();
											enumeration.last().setEnd(enumeration.last().getEnd() + sentence.length());
										}
									}
								}
								
								enumeration = new Enumeration(Type.ALPHABETIC, text);
								
								// TODO fix ending of last entry
							} else {
								// remove entry point
								final EnumerationEntry entry = list.iterator().next();
								CollectionUtils.filter(matchList2, new Predicate() {
									
									@Override
									public boolean evaluate(final Object object) {
										final Match match = (Match) object;
										return match.getFullMatch().start() != entry.getStart();
									}
								});
							}
							
							list.clear();
						}
					}
				}
			}
			
			return results;
		} finally {
			// POSTCONDITIONOSTCONDITIONS
		}
	}
	
	/**
	 * Extract numerical.
	 * 
	 * @param text
	 *            the text
	 * @param type
	 *            the type
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the collection
	 */
	private static Collection<Enumeration> extractNumerical(final String text,
	                                                        final Type[] type,
	                                                        final int start,
	                                                        final int end) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Extract roman.
	 * 
	 * @param text
	 *            the text
	 * @param type
	 *            the type
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the collection
	 */
	private static Collection<Enumeration> extractRoman(final String text,
	                                                    final Type[] type,
	                                                    final int start,
	                                                    final int end) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Find follow up.
	 * 
	 * @param enumeration
	 *            the enumeration
	 * @param matchList2
	 *            the multi match
	 * @param end
	 *            the end
	 * @param maxIndex
	 *            the max index
	 * @param string
	 *            the string
	 * @return the enumeration entry
	 */
	private static EnumerationEntry findFollowUp(final Enumeration enumeration,
	                                             final LinkedList<Match> matchList2,
	                                             final int end,
	                                             final int maxIndex,
	                                             final String string) {
		// PRECONDITIONS
		
		try {
			for (final Match match : matchList2) {
				final Group bulletGroup = match.getGroup("BULLET");
				final Group idGroup = match.getGroup("ID");
				if (idGroup.getMatch().equalsIgnoreCase(string) && (bulletGroup.start() > end)
				        && (bulletGroup.start() < maxIndex)) {
					return new EnumerationEntry(enumeration, idGroup.getMatch(), bulletGroup.start(),
					                            match.getFullMatch().end());
				}
			}
			
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Find relaxed follow up.
	 * 
	 * @param enumeration
	 *            the enumeration
	 * @param text
	 *            the text
	 * @param matchList2
	 *            the multi match
	 * @param bullet
	 *            the bullet
	 * @param lastEnd
	 *            the last end
	 * @param maxIndex
	 *            the max index
	 * @return the enumeration entry
	 */
	private static EnumerationEntry findRelaxedFollowUp(final Enumeration enumeration,
	                                                    final String text,
	                                                    final LinkedList<Match> matchList2,
	                                                    final String bullet,
	                                                    final int lastEnd,
	                                                    final int maxIndex) {
		// PRECONDITIONS
		
		try {
			bullet.charAt(0);
			final Set<Character> set = Enumeration.TYPO_MAP.get(bullet.charAt(0));
			
			if (set != null) {
				for (final Character character : set) {
					final String bulletId = "" + character;
					for (final Match match : matchList2) {
						if (match.getFullMatch().start() < maxIndex) {
							final Group group = match.getGroup("BULLET");
							final Group idGroup = match.getGroup("ID");
							if (idGroup.getMatch().equalsIgnoreCase(bulletId) && (group.start() > lastEnd)) {
								return new EnumerationEntry(enumeration, bulletId, group.start(), group.end());
							}
						}
					}
				}
			}
			
			// didn't find bullet with typos in the identifier. assuming typo in the brackets
			// final Regex regex = new Regex(ALPHA_ENUM_BULLET_RELAXED);
			// final MultiMatch multiMatch2 = regex.findAll(text);
			// for (final Match match : multiMatch2) {
			// final Group group = match.getGroup("BULLET");
			// if (group.getMatch().substring(0, 1).equalsIgnoreCase(bullet) && (group.start() > lastEnd)) {
			// return new EnumerationEntry(enumeration, group.getMatch().substring(0, 1), group.start(),
			// group.end());
			// }
			// }
			
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Find try hard.
	 * 
	 * @param enumeration
	 *            the enumeration
	 * @param text2
	 *            the text2
	 * @param identifier
	 *            the identifier
	 * @param end
	 *            the end
	 * @param start
	 *            the start
	 * @return the enumeration entry
	 */
	private static EnumerationEntry findTryHard(final Enumeration enumeration,
	                                            final String text2,
	                                            final char identifier,
	                                            final int end,
	                                            final int start) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Indentation.
	 * 
	 * @param enumeration
	 *            the enumeration
	 * @return the string
	 */
	private static String indentation(final Enumeration enumeration) {
		// PRECONDITIONS
		
		try {
			String indent = null;
			for (final EnumerationEntry entry : enumeration) {
				final String[] split = entry.getText().split(FileUtils.lineSeparator);
				for (int i = 1; i < split.length; ++i) {
					final Regex regex = new Regex("^({SPACE}\\s+)");
					final Match match = regex.find(split[i]);
					if (match != null) {
						final String whitespace = match.getGroup("SPACE").getMatch();
						if (indent == null) {
							indent = whitespace;
						} else {
							if (!indent.equals(whitespace)) {
								return null;
							}
						}
					} else {
						return null;
					}
				}
			}
			return indent;
			
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		final InputStream stream = Enumeration.class.getResourceAsStream("/org/mozkito/mappings/enum_test.txt");
		final Writer writer = new StringWriter();
		try {
			org.apache.commons.io.IOUtils.copy(stream, writer);
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			System.exit(0);
		}
		final String text = writer.toString();
		final Collection<Enumeration> extract = extract(text, new Type[] { Type.ALPHABETIC });
		for (final Enumeration enumeration : extract) {
			if (Logger.logAlways()) {
				Logger.always(enumeration.toString());
			}
		}
	}
	
	/**
	 * Multiple lines.
	 * 
	 * @param enumeration
	 *            the enumeration
	 * @return true, if successful
	 */
	private static boolean multipleLines(final Enumeration enumeration) {
		for (final EnumerationEntry entry : enumeration) {
			if (entry.getText().contains(FileUtils.lineSeparator)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Multiple paragraphs.
	 * 
	 * @param enumeration
	 *            the enumeration
	 * @return true, if successful
	 */
	private static boolean multipleParagraphs(final Enumeration enumeration) {
		// PRECONDITIONS
		
		try {
			final Regex regex = new Regex("\\n\\s*\\n", REFlags.MULTILINE);
			if (regex.find(enumeration.getText()) != null) {
				return true;
			}
			
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * One liners.
	 * 
	 * @param enumeration
	 *            the enumeration
	 * @return true, if successful
	 */
	private static boolean oneLiners(final Enumeration enumeration) {
		// PRECONDITIONS
		
		try {
			for (final EnumerationEntry entry : enumeration) {
				if ((entry.getStart() == 0)
				        || FileUtils.lineSeparator.endsWith(enumeration.getText().substring(entry.getStart() - 1,
				                                                                            entry.getStart()))) {
					// we start at the beginning of the line
					final String substring = enumeration.getText().substring(entry.getEnd());
					final int indexOf = substring.indexOf(FileUtils.lineSeparator);
					if (indexOf < 0) {
						continue;
					} else {
						if (!substring.substring(0, indexOf).matches("\\s")) {
							return false;
						}
					}
				} else {
					return false;
				}
			}
			
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/** The type. */
	private Type                               type;
	
	/** The enumeration entries. */
	private final LinkedList<EnumerationEntry> enumerationEntries = new LinkedList<>();
	
	/** The text. */
	private String                             text;
	
	/**
	 * Instantiates a new enumeration.
	 * 
	 * @param type
	 *            the type
	 * @param text
	 *            the text
	 */
	private Enumeration(@NotNull final Type type, @NotNull final String text) {
		// PRECONDITIONS
		
		try {
			this.type = type;
			this.text = text;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.type, "Field '%s' in '%s'.", "type", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.text, "Field '%s' in '%s'.", "text", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
	public boolean add(final EnumerationEntry e) {
		// PRECONDITIONS
		Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.enumerationEntries.add(e);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(final Collection<? extends EnumerationEntry> c) {
		// PRECONDITIONS
		Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.enumerationEntries.addAll(c);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		// PRECONDITIONS
		Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.enumerationEntries.clear();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final Object o) {
		// PRECONDITIONS
		Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			if (o instanceof EnumerationEntry) {
				return this.enumerationEntries.contains(o);
			} else {
				return false;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(final Collection<?> c) {
		// PRECONDITIONS
		Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return c.containsAll(c);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getClassName() {
		return JavaUtils.getHandle(Enumeration.class);
	}
	
	/**
	 * Gets the enumeration entries.
	 * 
	 * @return the enumeration entries
	 */
	public final List<EnumerationEntry> getEnumerationEntries() {
		// PRECONDITIONS
		
		try {
			return Collections.unmodifiableList(this.enumerationEntries);
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries",
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	public String getText() {
		// PRECONDITIONS
		Condition.notNull(this.text, "Field '%s' in '%s'.", "text", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.text;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.text, "Field '%s' in '%s'.", "text", getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public Type getType() {
		// PRECONDITIONS
		Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.type;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.type, "Field '%s' in '%s'.", "type", getClass().getSimpleName());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		// PRECONDITIONS
		Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.enumerationEntries.isEmpty();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	/**
	 * Iterator.
	 * 
	 * @return the iterator
	 */
	@Override
	public Iterator<EnumerationEntry> iterator() {
		// PRECONDITIONS
		Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.enumerationEntries.iterator();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Last.
	 * 
	 * @return the enumeration entry
	 */
	private EnumerationEntry last() {
		// PRECONDITIONS
		
		try {
			return this.enumerationEntries.isEmpty()
			                                        ? null
			                                        : this.enumerationEntries.listIterator(this.enumerationEntries.size())
			                                                                 .previous();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(final Object o) {
		// PRECONDITIONS
		Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.enumerationEntries.remove(o);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(final Collection<?> c) {
		// PRECONDITIONS
		Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.enumerationEntries.removeAll(c);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(final Collection<?> c) {
		// PRECONDITIONS
		Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.enumerationEntries.retainAll(c);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		// PRECONDITIONS
		Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.enumerationEntries.size();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		// PRECONDITIONS
		Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.enumerationEntries.toArray();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(final T[] a) {
		// PRECONDITIONS
		Condition.notNull(this.enumerationEntries, "Field '%s' in '%s'.", "enumerationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.enumerationEntries.toArray(a);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Enumeration [type=");
		builder.append(this.type);
		builder.append("]");
		for (final EnumerationEntry entry : this.enumerationEntries) {
			builder.append(FileUtils.lineSeparator);
			builder.append(entry).append(FileUtils.lineSeparator);
		}
		
		return builder.toString();
	}
	
}
