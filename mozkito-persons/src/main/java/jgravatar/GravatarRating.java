package jgravatar;

/**
 * The Enum GravatarRating.
 */
public enum GravatarRating {

	/** The general audiences. */
	GENERAL_AUDIENCES("g"),

	/** The parental guidance suggested. */
	PARENTAL_GUIDANCE_SUGGESTED("pg"),

	/** The restricted. */
	RESTRICTED("r"),

	/** The xplicit. */
	XPLICIT("x");

	/** The code. */
	private String code;

	/**
	 * Instantiates a new gravatar rating.
	 *
	 * @param code the code
	 */
	private GravatarRating(String code) {
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