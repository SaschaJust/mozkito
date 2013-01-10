package jgravatar;

/**
 * The Enum GravatarDefaultImage.
 */
public enum GravatarDefaultImage {

	/** The gravatar icon. */
	GRAVATAR_ICON(""),

	/** The identicon. */
	IDENTICON("identicon"),

	/** The monsterid. */
	MONSTERID("monsterid"),

	/** The wavatar. */
	WAVATAR("wavatar"),

	/** The HTT p_404. */
	HTTP_404("404");

	/** The code. */
	private String code;

	/**
	 * Instantiates a new gravatar default image.
	 *
	 * @param code the code
	 */
	private GravatarDefaultImage(String code) {
		this.code = code;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

}
