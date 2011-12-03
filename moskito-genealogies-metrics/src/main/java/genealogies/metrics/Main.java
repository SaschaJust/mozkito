package genealogies.metrics;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetrics;


public class Main {
	
	static {
		KanuniAgent.initialize();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenealogyMetrics genealogyMetrics = new GenealogyMetrics();
		genealogyMetrics.run();
	}
	
}
