/**
 * 
 */
package net.ownhero.dev.andama.threads.comparator;

import java.util.Comparator;

import net.ownhero.dev.andama.exceptions.UnsupportedThreadTypeException;
import net.ownhero.dev.andama.threads.AndamaDemultiplexer;
import net.ownhero.dev.andama.threads.AndamaFilter;
import net.ownhero.dev.andama.threads.AndamaMultiplexer;
import net.ownhero.dev.andama.threads.AndamaSink;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.AndamaThreadable;
import net.ownhero.dev.andama.threads.AndamaTransformer;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@SuppressWarnings ("rawtypes")
public class AndamaThreadComparator implements Comparator<AndamaThreadable> {
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) Source < Multiplexer < Filter < Transformer
	 * < Demultiplexer < Sink
	 */
	@Override
	public int compare(final AndamaThreadable arg0,
	                   final AndamaThreadable arg1) {
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
		
		if (AndamaSource.class.isAssignableFrom(arg0.getClass())) {
			// arg0 = source
			if (AndamaSource.class.isAssignableFrom(arg1.getClass())) {
				return arg0.getThreadID().compareTo(arg1.getThreadID());
			} else {
				return -1;
			}
		} else if (AndamaMultiplexer.class.isAssignableFrom(arg0.getClass())) {
			// arg0 = multiplexer
			if (AndamaSource.class.isAssignableFrom(arg1.getClass())) {
				return 1;
			} else if (AndamaMultiplexer.class.isAssignableFrom(arg1.getClass())) {
				return arg0.getThreadID().compareTo(arg1.getThreadID());
			} else {
				return -1;
			}
		} else if (AndamaFilter.class.isAssignableFrom(arg0.getClass())) {
			// arg0 = filter
			if (AndamaSource.class.isAssignableFrom(arg1.getClass())) {
				return 1;
			} else if (AndamaMultiplexer.class.isAssignableFrom(arg1.getClass())) {
				return 1;
			} else if (AndamaFilter.class.isAssignableFrom(arg1.getClass())) {
				return arg0.getThreadID().compareTo(arg1.getThreadID());
			} else {
				return -1;
			}
		} else if (AndamaTransformer.class.isAssignableFrom(arg0.getClass())) {
			// arg0 = filter
			if (AndamaTransformer.class.isAssignableFrom(arg1.getClass())) {
				return arg0.getThreadID().compareTo(arg1.getThreadID());
			} else if (AndamaDemultiplexer.class.isAssignableFrom(arg1.getClass())) {
				return -1;
			} else if (AndamaSink.class.isAssignableFrom(arg1.getClass())) {
				return -1;
			} else {
				return 1;
			}
		} else if (AndamaDemultiplexer.class.isAssignableFrom(arg0.getClass())) {
			// arg0 = demultiplexer
			if (AndamaSink.class.isAssignableFrom(arg1.getClass())) {
				return -1;
			} else if (AndamaDemultiplexer.class.isAssignableFrom(arg1.getClass())) {
				return arg0.getThreadID().compareTo(arg1.getThreadID());
			} else {
				return 1;
			}
		} else if (AndamaSink.class.isAssignableFrom(arg0.getClass())) {
			// arg0 = sink
			if (AndamaSink.class.isAssignableFrom(arg1.getClass())) {
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
