package net.ownhero.dev.andama.chain;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.andama.threads.AndamaThread;

public class AndamaGraph {
	
	private final LinkedList<AndamaNode> openBranches   = new LinkedList<AndamaNode>();
	private final LinkedList<AndamaNode> closedBranches = new LinkedList<AndamaNode>();
	
	/**
	 * @param andamaNode
	 */
	public void addBranch(final AndamaNode andamaNode) {
		this.openBranches.add(andamaNode);
	}
	
	public void attach(final AndamaNode node,
	                   final AndamaThread<?, ?> thread) {
		AndamaNode andamaNode = new AndamaNode(thread);
		
		if (node.isMultiplexer()) {
			// branch
			AndamaNode branch = node.deepCopy();
			this.openBranches.add(branch);
		}
		
		if (andamaNode.isDemultiplexer()) {
			// close all branches
			for (AndamaNode matchingNode : getMatching(thread)) {
				matchingNode.connectOutput(andamaNode);
				this.openBranches.remove(matchingNode);
			}
			this.openBranches.add(andamaNode);
		} else if (andamaNode.isSink()) {
			node.connectOutput(andamaNode);
			this.openBranches.remove(node);
			this.getClosedBranches().add(andamaNode);
		} else if (andamaNode.isSource()) {
			this.openBranches.add(andamaNode);
		} else {
			node.connectOutput(andamaNode);
			this.openBranches.remove(node);
			this.openBranches.add(andamaNode);
		}
	}
	
	public void detach(final AndamaThread thread) {
		// TODO implement
	}
	
	/**
	 * @return the closedBranches
	 */
	public LinkedList<AndamaNode> getClosedBranches() {
		return this.closedBranches;
	}
	
	public List<AndamaNode> getMatching(final AndamaThread<?, ?> thread) {
		LinkedList<AndamaNode> list = new LinkedList<AndamaNode>();
		
		if (thread.hasInputConnector()) {
			for (AndamaNode node : this.openBranches) {
				if (node.getOutputType() == (new AndamaNode(thread)).getInputType()) {
					list.add(node);
				}
			}
		}
		return list;
	}
}
