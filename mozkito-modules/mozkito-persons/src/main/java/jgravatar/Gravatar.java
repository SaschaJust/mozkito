/*******************************************************************************
 * Copyright 2013 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package jgravatar;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * A gravatar is a dynamic image resource that is requested from the gravatar.com server. This class calculates the
 * gravatar url and fetches gravatar images. See http://en.gravatar.com/site/implement/url .
 * 
 * This class is thread-safe, Gravatar objects can be shared.
 * 
 * Usage example:
 * 
 * <code>
 * Gravatar gravatar = new Gravatar();
 * gravatar.setSize(50);
 * gravatar.setRating(GravatarRating.GENERAL_AUDIENCES);
 * gravatar.setDefaultImage(GravatarDefaultImage.IDENTICON);
 * String url = gravatar.getUrl("iHaveAn@email.com");
 * byte[] jpg = gravatar.download("info@ralfebert.de");
 * </code>
 */
public final class Gravatar {
	
	/** The Constant DEFAULT_SIZE. */
	private static final int                  DEFAULT_SIZE          = 80;
	
	/** The Constant GRAVATAR_URL. */
	private static final String               GRAVATAR_URL          = "http://www.gravatar.com/avatar/";
	
	/** The Constant DEFAULT_RATING. */
	private static final GravatarRating       DEFAULT_RATING        = GravatarRating.GENERAL_AUDIENCES;
	
	/** The Constant DEFAULT_DEFAULT_IMAGE. */
	private static final GravatarDefaultImage DEFAULT_DEFAULT_IMAGE = GravatarDefaultImage.HTTP_404;
	
	public static void main(final String[] args) {
		System.out.println(new Gravatar().getUrl("sascha.just@own-hero.net"));
	}
	
	/** The size. */
	private int                  size         = DEFAULT_SIZE;
	
	/** The rating. */
	private GravatarRating       rating       = DEFAULT_RATING;
	
	/** The default image. */
	private GravatarDefaultImage defaultImage = DEFAULT_DEFAULT_IMAGE;
	
	/**
	 * Downloads the gravatar for the given URL using Java {@link URL} and returns a byte array containing the gravatar
	 * jpg, returns null if no gravatar was found.
	 * 
	 * @param email
	 *            the email
	 * @return the byte[]
	 * @throws GravatarDownloadException
	 *             the gravatar download exception
	 */
	public byte[] download(final String email) {
		InputStream stream = null;
		try {
			final URL url = new URL(getUrl(email));
			stream = url.openStream();
			return IOUtils.toByteArray(stream);
		} catch (final FileNotFoundException e) {
			return null;
		} catch (final Exception e) {
			throw new GravatarDownloadException(e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	/**
	 * Format url parameters.
	 * 
	 * @return the string
	 */
	private String formatUrlParameters() {
		final List<String> params = new ArrayList<String>();
		
		if (this.size != DEFAULT_SIZE) {
			params.add("s=" + this.size);
		}
		if (this.rating != DEFAULT_RATING) {
			params.add("r=" + this.rating.getCode());
		}
		if (this.defaultImage != GravatarDefaultImage.GRAVATAR_ICON) {
			params.add("d=" + this.defaultImage.getCode());
		}
		
		if (params.isEmpty()) {
			return "";
		} else {
			return "?" + StringUtils.join(params.iterator(), "&");
		}
	}
	
	/**
	 * Returns the Gravatar URL for the given email address.
	 * 
	 * @param email
	 *            the email
	 * @return the url
	 */
	public String getUrl(final String email) {
		Validate.notNull(email, "email");
		
		// hexadecimal MD5 hash of the requested user's lowercased email address
		// with all whitespace trimmed
		final String emailHash = DigestUtils.md5Hex(email.toLowerCase().trim());
		final String params = formatUrlParameters();
		return GRAVATAR_URL + emailHash + ".jpg" + params;
	}
	
	/**
	 * Specify the default image to be produced if no gravatar image was found.
	 * 
	 * @param defaultImage
	 *            the new default image
	 */
	public void setDefaultImage(final GravatarDefaultImage defaultImage) {
		Validate.notNull(defaultImage, "defaultImage");
		this.defaultImage = defaultImage;
	}
	
	/**
	 * Specify a rating to ban gravatar images with explicit content.
	 * 
	 * @param rating
	 *            the new rating
	 */
	public void setRating(final GravatarRating rating) {
		Validate.notNull(rating, "rating");
		this.rating = rating;
	}
	
	/**
	 * Specify a gravatar size between 1 and 512 pixels. If you omit this, a default size of 80 pixels is used.
	 * 
	 * @param sizeInPixels
	 *            the new size
	 */
	public void setSize(final int sizeInPixels) {
		Validate.isTrue((sizeInPixels >= 1) && (sizeInPixels <= 512), "sizeInPixels needs to be between 1 and 512");
		this.size = sizeInPixels;
	}
	
}
