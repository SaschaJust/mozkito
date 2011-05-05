/**
 * 
 */
package de.unisaarland.cs.st.reposuite.output.nodes.table;

import de.unisaarland.cs.st.reposuite.output.nodes.Element;
import de.unisaarland.cs.st.reposuite.output.nodes.Node;
import de.unisaarland.cs.st.reposuite.output.nodes.Text;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class Cell extends Element {
	
	Node[] nodes;
	
	public Cell() {
		this.nodes = new Node[] { new Text("") };
	}
	
	public Cell(final Node node) {
		this.nodes = new Node[] { node };
	}
	
	public Cell(final String string) {
		this.nodes = new Node[] { new Text(string) };
	}
	
	protected Node[] getNode() {
		return this.nodes;
	}
	
	protected void setNode(final Node[] nodes) {
		this.nodes = nodes;
	}
	
}
