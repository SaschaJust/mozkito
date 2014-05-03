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
package net.ownhero.dev.andama.threads.comparator;

import java.util.Comparator;

import net.ownhero.dev.andama.exceptions.UnsupportedThreadTypeException;
import net.ownhero.dev.andama.threads.Demultiplexer;
import net.ownhero.dev.andama.threads.Filter;
import net.ownhero.dev.andama.threads.INode;
import net.ownhero.dev.andama.threads.Multiplexer;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.andama.threads.Transformer;

/**
 * The Class AndamaThreadComparator.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@SuppressWarnings ("rawtypes")
public class AndamaThreadComparator implements Comparator<INode> {
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) Source < Multiplexer < Filter < Transformer
	 * < Demultiplexer < Sink
	 */
	/**
	 * {@inheritDoc}
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final INode arg0,
	                   final INode arg1) {
		if ((arg0 == null)) {
			if ((arg1 == null)) {
				return 0;
			} else {
				return -1;
			}
		} else {
			if ((arg1 == null)) {
				return 1;
			}
		}
		
		if (Source.class.isAssignableFrom(arg0.getClass())) {
			// arg0 = source
			if (Source.class.isAssignableFrom(arg1.getClass())) {
				return arg0.getThreadID().compareTo(arg1.getThreadID());
			} else {
				return -1;
			}
		} else if (Multiplexer.class.isAssignableFrom(arg0.getClass())) {
			// arg0 = multiplexer
			if (Source.class.isAssignableFrom(arg1.getClass())) {
				return 1;
			} else if (Multiplexer.class.isAssignableFrom(arg1.getClass())) {
				return arg0.getThreadID().compareTo(arg1.getThreadID());
			} else {
				return -1;
			}
		} else if (Filter.class.isAssignableFrom(arg0.getClass())) {
			// arg0 = filter
			if (Source.class.isAssignableFrom(arg1.getClass())) {
				return 1;
			} else if (Multiplexer.class.isAssignableFrom(arg1.getClass())) {
				return 1;
			} else if (Filter.class.isAssignableFrom(arg1.getClass())) {
				return arg0.getThreadID().compareTo(arg1.getThreadID());
			} else {
				return -1;
			}
		} else if (Transformer.class.isAssignableFrom(arg0.getClass())) {
			// arg0 = filter
			if (Transformer.class.isAssignableFrom(arg1.getClass())) {
				return arg0.getThreadID().compareTo(arg1.getThreadID());
			} else if (Demultiplexer.class.isAssignableFrom(arg1.getClass())) {
				return -1;
			} else if (Sink.class.isAssignableFrom(arg1.getClass())) {
				return -1;
			} else {
				return 1;
			}
		} else if (Demultiplexer.class.isAssignableFrom(arg0.getClass())) {
			// arg0 = demultiplexer
			if (Sink.class.isAssignableFrom(arg1.getClass())) {
				return -1;
			} else if (Demultiplexer.class.isAssignableFrom(arg1.getClass())) {
				return arg0.getThreadID().compareTo(arg1.getThreadID());
			} else {
				return 1;
			}
		} else if (Sink.class.isAssignableFrom(arg0.getClass())) {
			// arg0 = sink
			if (Sink.class.isAssignableFrom(arg1.getClass())) {
				return arg0.getThreadID().compareTo(arg1.getThreadID());
			} else {
				return 1;
			}
		}
		
		try {
			throw new UnsupportedThreadTypeException(arg0.getClass().getCanonicalName());
		} catch (final UnsupportedThreadTypeException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
