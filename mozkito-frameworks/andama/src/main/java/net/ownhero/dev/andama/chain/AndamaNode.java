/**
 * 
 */
package net.ownhero.dev.andama.chain;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedList;

import net.ownhero.dev.andama.threads.AndamaDemultiplexer;
import net.ownhero.dev.andama.threads.AndamaFilter;
import net.ownhero.dev.andama.threads.AndamaMultiplexer;
import net.ownhero.dev.andama.threads.AndamaSink;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.AndamaThread;
import net.ownhero.dev.andama.threads.AndamaTransformer;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class AndamaNode {
	
	private final LinkedList<AndamaNode> inputs  = new LinkedList<AndamaNode>();
	
	private final LinkedList<AndamaNode> outputs = new LinkedList<AndamaNode>();
	
	private AndamaThread<?, ?>           thread  = null;
	
	public AndamaNode(final AndamaThread<?, ?> thread) {
		this.thread = thread;
	}
	
	@Override
	public AndamaNode clone() {
		return null;
	}
	
	/**
	 * @param node
	 */
	public void connectInput(final AndamaNode node) {
		this.inputs.add(node);
		if (!node.outputs.contains(this)) {
			node.outputs.add(this);
		}
	}
	
	/**
	 * @param node
	 */
	public void connectOutput(final AndamaNode node) {
		this.outputs.add(node);
		if (!node.inputs.contains(this)) {
			node.inputs.add(this);
		}
	}
	
	/**
	 * @param node
	 */
	public void disconnectInput(final AndamaNode node) {
		this.outputs.remove(node);
		if (node.inputs.contains(this)) {
			node.inputs.remove(this);
		}
	}
	
	/**
	 * @param node
	 */
	public void disconnectOutput(final AndamaNode node) {
		this.inputs.remove(node);
		if (node.outputs.contains(this)) {
			node.outputs.remove(this);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AndamaNode)) {
			return false;
		}
		AndamaNode other = (AndamaNode) obj;
		if (this.thread == null) {
			if (other.thread != null) {
				return false;
			}
		} else if (!this.thread.equals(other.thread)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the inputs
	 */
	public LinkedList<AndamaNode> getInputs() {
		return this.inputs;
	}
	
	/**
	 * @param thread
	 * @return the type of the input chunks of the given thread
	 */
	public Type getInputType() {
		if ((this.thread != null) && this.thread.hasInputConnector()) {
			ParameterizedType type = (ParameterizedType) this.thread.getClass().getGenericSuperclass();
			return type.getActualTypeArguments()[0];
		} else {
			return null;
		}
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return this.thread != null
		                          ? this.thread.getHandle()
		                          : null;
	}
	
	/**
	 * @return the outputs
	 */
	public LinkedList<AndamaNode> getOutputs() {
		return this.outputs;
	}
	
	/**
	 * @param thread
	 * @return the type of the output chunks of the given thread
	 */
	public Type getOutputType() {
		if ((this.thread != null) && this.thread.hasOutputConnector()) {
			ParameterizedType type = (ParameterizedType) this.thread.getClass().getGenericSuperclass();
			return (type.getActualTypeArguments().length > 1
			                                                ? type.getActualTypeArguments()[1]
			                                                : type.getActualTypeArguments()[0]);
		} else {
			return null;
		}
	}
	
	/**
	 * @param node
	 * @return
	 */
	public HashSet<AndamaNode> getSources() {
		HashSet<AndamaNode> set = new HashSet<AndamaNode>();
		
		if (isSource()) {
			set.add(this);
		} else {
			for (AndamaNode inputNode : getInputs()) {
				set.addAll(inputNode.getSources());
			}
		}
		
		return set;
	}
	
	/**
	 * @return the thread
	 */
	public AndamaThread<?, ?> getThread() {
		return this.thread;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.thread == null)
		                                                  ? 0
		                                                  : this.thread.hashCode());
		return result;
	}
	
	/**
	 * @return
	 */
	public AndamaNode headCopy() {
		AndamaNode node = new AndamaNode(this.thread);
		node.inputs.addAll(this.inputs);
		node.outputs.addAll(this.outputs);
		return node;
	}
	
	/**
	 * @return
	 */
	public boolean isDemultiplexer() {
		return (this.thread != null) && AndamaDemultiplexer.class.isAssignableFrom(this.thread.getClass());
	}
	
	/**
	 * @return
	 */
	public boolean isFilter() {
		return (this.thread != null) && AndamaFilter.class.isAssignableFrom(this.thread.getClass());
	}
	
	/**
	 * @return
	 */
	public boolean isMultiplexer() {
		return (this.thread != null) && AndamaMultiplexer.class.isAssignableFrom(this.thread.getClass());
	}
	
	/**
	 * @return
	 */
	public boolean isSink() {
		return (this.thread != null) && AndamaSink.class.isAssignableFrom(this.thread.getClass());
	}
	
	/**
	 * @return
	 */
	public boolean isSource() {
		return (this.thread != null) && AndamaSource.class.isAssignableFrom(this.thread.getClass());
	}
	
	/**
	 * @return
	 */
	public boolean isTransformer() {
		return (this.thread != null) && AndamaTransformer.class.isAssignableFrom(this.thread.getClass());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AndamaNode [group=");
		builder.append(this.thread);
		builder.append("]");
		return builder.toString();
	}
}
