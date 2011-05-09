/**
 * 
 */
package persons;

import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.persons.Persons;
import net.ownhero.dev.kisa.Logger;

/**
 * @author just
 * 
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			Persons persons = new Persons();
			persons.setName(persons.getClass().getSimpleName());
			persons.start();
			persons.join();
		} catch (InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new Shutdown();
		}
	}
	
}
