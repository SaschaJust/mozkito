package genealogies.metrics;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricsToolChain;


public class Main {
	
	static {
		KanuniAgent.initialize();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenealogyMetricsToolChain genealogyMetrics = new GenealogyMetricsToolChain();
		genealogyMetrics.run();
	}
	
}
