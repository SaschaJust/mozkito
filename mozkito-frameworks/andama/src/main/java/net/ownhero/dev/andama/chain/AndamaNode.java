/**
 * 
 */
package net.ownhero.dev.andama.chain;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
	 * @return
	 */
	public AndamaNode deepCopy() {
		AndamaNode node = new AndamaNode(this.thread);
		node.inputs.addAll(this.inputs);
		node.outputs.addAll(this.outputs);
		return node;
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
	 * @return the thread
	 */
	public AndamaThread<?, ?> getThread() {
		return this.thread;
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
}
