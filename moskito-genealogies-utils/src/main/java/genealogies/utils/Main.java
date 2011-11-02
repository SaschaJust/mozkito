package genealogies.utils;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import de.unisaarland.cs.st.reposuite.genealogies.utils.GenealogyUtils;


public class Main {
	
	static {
		KanuniAgent.initialize();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenealogyUtils.run();
	}
	
}
