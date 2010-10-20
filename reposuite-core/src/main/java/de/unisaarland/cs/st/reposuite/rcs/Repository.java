package de.unisaarland.cs.st.reposuite.rcs;

import java.net.URI;

public abstract class Repository {
	
	public abstract void setup(URI address);
	
	public abstract void setup(URI address, String username, String password);
}
