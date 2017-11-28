package com.sp.web.model;

import com.sp.web.service.image.FileStorage;

/**
 * Data needed to put a file into {@link FileStorage}.
 * 
 * @author pruhil
 */
public final class FileData {

	/** file name */
	private final String name;

	/** image file data */
	private final byte[] bytes;

	/** file content type */
	private final String contentType;

	/**
	 * Constuctor initializing the file data
	 * 
	 * @param name
	 *            of the file
	 * @param bytes
	 *            file data in bytes
	 * @param contentType
	 *            content type
	 */
	public FileData(String name, byte[] bytes, String contentType) {
		this.name = name;
		this.bytes = bytes;
		this.contentType = contentType;
	}

	/**
	 * The name of the file.
	 * 
	 * @return the name of the file
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The file data as a byte array.
	 * 
	 */
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * @return The file content type.
	 */
	public String getContentType() {
		return contentType;
	}

}