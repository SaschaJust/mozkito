package net.ownhero.dev.andama.threads;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public enum AndamaThreadStatus {
	PREEXECUTION,
	PREINPUT,
	INPUT,
	POSTINPUT,
	PREPROCESSING,
	PROCESSING,
	POSTPROCESSING,
	PREOUTPUT,
	OUTPUT,
	POSTOUTPUT,
	POSTEXECUTION;
}
