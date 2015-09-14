package com.psu.hpa.models;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;

/**
 * Contains content for a thumbnail image entity.
 */
public class ThumbnailImage {

	/** The data content. */
	private byte[] content;

	/** The MIME content type. */
	private MediaType contentType;


	/**
	 * Instantiate a new thumbnail image from an input stream.
	 *
	 * @param input the input stream
	 * @param contentType the content type
	 * @return the thumbnail image
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ThumbnailImage fromStream(InputStream input, MediaType contentType) throws IOException {
		byte[] content = IOUtils.toByteArray(input);
		return new ThumbnailImage(content, contentType);
	}

	/**
	 * Instantiates a new thumbnail image.
	 *
	 * @param content the data content
	 * @param contentType the content type
	 */
	public ThumbnailImage(byte[] content, MediaType contentType) {
		this.contentType = contentType;
		this.content = content;
	}

	/**
	 * Gets the content type.
	 *
	 * @return the content type
	 */
	public MediaType getContentType() {
		return contentType;
	}

	/**
	 * Sets the content type.
	 *
	 * @param contentType the new content type
	 */
	public void setContentType(MediaType contentType) {
		this.contentType = contentType;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}

}
