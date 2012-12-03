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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class TextMetadata.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TextMetadata {
	
	/**
	 * The Class StringInstanceIterator.
	 */
	private static final class StringInstanceIterator implements Iterator<Instance> {
		
		/** The iterator. */
		private Iterator<String> iterator;
		
		/**
		 * Instantiates a new string instance iterator.
		 * 
		 * @param stringIterator
		 *            the string iterator
		 */
		public StringInstanceIterator(final Iterator<String> stringIterator) {
			// PRECONDITIONS
			
			try {
				this.iterator = stringIterator;
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			// PRECONDITIONS
			
			try {
				return this.iterator.hasNext();
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Instance next() {
			// PRECONDITIONS
			
			try {
				final Instance carrier = new Instance(this.iterator.next(), null, null, null);
				return carrier;
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			// PRECONDITIONS
			
			try {
				throw new UnsupportedOperationException();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public static final String getHandle() {
		return JavaUtils.getHandle(TextMetadata.class);
	}
	
	/**
	 * Lines.
	 * 
	 * @param text
	 *            the text
	 * @return the int
	 */
	public static final int lines(final String text) {
		return TextSeparator.lines(text).size();
	}
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		final ArrayList<String> strings = new ArrayList<>(3);
		strings.add("Richard Hawes (1797–1877) was a United States Representative from Kentucky and the second Confederate Governor of Kentucky. Originally a Whig, Hawes became a Democrat following the dissolution of the Whig party in the 1850s. At the outbreak of the American Civil War, Hawes was a supporter of Kentucky's doctrine of armed neutrality. When the Commonwealth's neutrality was breached in September 1861, Hawes fled to Virginia and enlisted as a brigade commissary under Confederate general Humphrey Marshall. He was elected Confederate governor of the Commonwealth following the late George W. Johnson's death at the Battle of Shiloh. Hawes and the Confederate government traveled with Braxton Bragg's Army of Tennessee, and when Bragg invaded Kentucky in October 1862, he captured Frankfort and held an inauguration ceremony for Hawes. The ceremony was interrupted, however, by forces under Union general Don Carlos Buell, and the Confederates were driven from the Commonwealth following the Battle of Perryville. Hawes relocated to Virginia, where he continued to lobby President Jefferson Davis to attempt another invasion of Kentucky. Following the war, he returned to his home in Paris, Kentucky, swore an oath of allegiance to the Union, and was allowed to return to his law practice.");
		strings.add("Clem Hill (1877–1945) was an Australian cricketer who played 49 Test matches as a specialist batsman between 1896 and 1912. He captained the Australian team in ten Tests, winning five and losing five. A prolific run scorer, Hill scored 3,412 runs in Test cricket—a world record at the time of his retirement—at an average of 39.21 per innings, including seven centuries. In 1902, Hill was the first batsman to make 1,000 Test runs in a calendar year, a feat that would not be repeated for 45 years. His innings of 365 scored against New South Wales for South Australia in 1900–01 was a Sheffield Shield record for 27 years. His Test cricket career ended in controversy after he was involved in a brawl with cricket administrator and fellow Test selector Peter McAlister in 1912. He was one of the \"Big Six\", a group of leading Australian cricketers who boycotted the 1912 Triangular Tournament in England when the players were stripped of the right to appoint the tour manager. The boycott effectively ended his Test career. After retiring from cricket, Hill worked in the horse racing industry as a stipendiary steward and later as a handicapper for races including the Caulfield Cup.");
		strings.add("The rings of Uranus were discovered on March 10, 1977, by James L. Elliot, Edward W. Dunham, and Douglas J. Mink. Two additional rings were discovered in 1986 by the Voyager 2 spacecraft, and two outer rings were found in 2003–2005 by the Hubble Space Telescope. A number of faint dust bands and incomplete arcs may exist between the main rings. The rings are extremely dark—the Bond albedo of the rings' particles does not exceed 2%. They are likely composed of water ice with the addition of some dark radiation-processed organics. The majority of Uranus's rings are opaque and only a few kilometres wide. The ring system contains little dust overall; it consists mostly of large bodies 0.2–20 m in diameter. The relative lack of dust in the ring system is due to aerodynamic drag from the extended Uranian exosphere—corona. The rings of Uranus are thought to be relatively young, at not more than 600 million years. The mechanism that confines the narrow rings is not well understood. The Uranian ring system probably originated from the collisional fragmentation of a number of moons that once existed around the planet. After colliding, the moons broke up into numerous particles, which survived as narrow and optically dense rings only in strictly confined zones of maximum stability.");
		
		final ParallelTopicModel topics = topics(strings.iterator());
		if (Logger.logAlways()) {
			Logger.always(topics.getNumTopics() + "");
		}
	}
	
	/**
	 * Paragraphs.
	 * 
	 * @param text
	 *            the text
	 * @return the int
	 */
	public static final int paragraphs(final String text) {
		return TextSeparator.paragraphs(text).size();
	}
	
	/**
	 * Sentences.
	 * 
	 * @param text
	 *            the text
	 * @return the int
	 */
	public static final int sentences(final String text) {
		return TextSeparator.sentences(text).size();
	}
	
	/**
	 * Topics.
	 * 
	 * @param text
	 *            the text
	 * @return the list
	 */
	public static final ParallelTopicModel topics(final Iterator<String> text) {
		
		try {
			// Begin by importing documents from text to feature sequences
			final ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
			
			// Pipes: lowercase, tokenize, remove stopwords, map to features
			pipeList.add(new CharSequenceLowercase());
			pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
			final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			final URL resource = Information.class.getResource("/org/mozkito/mappings/stoplist_en.txt");
			try {
				IOUtils.copyInputStream(resource.openStream(), new FileOutputStream(file));
			} catch (final IOException e1) {
				if (Logger.logError()) {
					Logger.error(e1);
				}
				throw new UnrecoverableError(e1);
			}
			pipeList.add(new TokenSequenceRemoveStopwords(file, "UTF-8", false, false, false));
			pipeList.add(new TokenSequence2FeatureSequence());
			
			final InstanceList instances = new InstanceList(new SerialPipes(pipeList));
			
			// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
			// Note that the first parameter is passed as the sum over topics, while
			// the second is the parameter for a single dimension of the Dirichlet prior.
			final int numTopics = 10;
			final ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);
			
			try {
				instances.addThruPipe(new StringInstanceIterator(text)); // data, label, name fields
				
				model.addInstances(instances);
				
				// Use two parallel samplers, which each look at one half the corpus and combine
				// statistics after every iteration.
				model.setNumThreads(2);
				
				// Run the model for 50 iterations and stop (this is for testing only,
				// for real applications, use 1000 to 2000 iterations)
				model.setNumIterations(50);
				model.estimate();
				
				// Show the words and topics in the first instance
				
				// The data alphabet maps word IDs to strings
				final Alphabet dataAlphabet = instances.getDataAlphabet();
				
				final FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
				final LabelSequence topics = model.getData().get(0).topicSequence;
				
				Formatter out = new Formatter(new StringBuilder(), Locale.US);
				for (int position = 0; position < tokens.getLength(); position++) {
					out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), //$NON-NLS-1$
					           topics.getIndexAtPosition(position));
				}
				
				if (Logger.logDebug()) {
					Logger.debug(out.toString());
				}
				
				// Estimate the topic distribution of the first instance,
				// given the current Gibbs state.
				final double[] topicDistribution = model.getTopicProbabilities(0);
				
				// Get an array of sorted sets of word ID/count pairs
				final ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
				
				// Show top 5 words in topics with proportions for the first document
				for (int topic = 0; topic < numTopics; topic++) {
					final Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
					
					out = new Formatter(new StringBuilder(), Locale.US);
					out.format("%d\t%.3f\t", topic, topicDistribution[topic]); //$NON-NLS-1$
					int rank = 0;
					while (iterator.hasNext() && (rank < 5)) {
						final IDSorter idCountPair = iterator.next();
						out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), //$NON-NLS-1$
						           idCountPair.getWeight());
						rank++;
					}
					if (Logger.logDebug()) {
						Logger.debug(out.toString());
					}
				}
				
				// Create a new instance with high probability of topic 0
				final StringBuilder topicZeroText = new StringBuilder();
				final Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();
				
				int rank = 0;
				while (iterator.hasNext() && (rank < 5)) {
					final IDSorter idCountPair = iterator.next();
					topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " "); //$NON-NLS-1$
					rank++;
				}
				
				// Create a new instance named "test instance" with empty target and source fields.
				final InstanceList testing = new InstanceList(instances.getPipe());
				testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null)); //$NON-NLS-1$
				
				final TopicInferencer inferencer = model.getInferencer();
				final double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
				if (Logger.logDebug()) {
					Logger.debug("0\t" + testProbabilities[0]);//$NON-NLS-1$
				}
				
				out.close();
				
				return model;
			} catch (final IOException e) {
				throw new UnrecoverableError(e);
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Words.
	 * 
	 * @param text
	 *            the text
	 * @return the int
	 */
	public static final int words(final String text) {
		return TextSeparator.words(text).size();
	}
}
