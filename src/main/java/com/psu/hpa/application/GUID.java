package com.psu.hpa.application;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * HPA GUIDs.
 */
@JsonSerialize(using = GUIDJSONSerializer.class)
public class GUID implements Serializable {
	private static final long serialVersionUID = 8457572446359639060L;

	/** The URL-safe String representation. */
	public final String urlRepresentation;

	/** The XML String representation. */
	public final String xmlRepresentation;

	/** The byte representation. */
	public final byte[] byteRepresentation;

	/**
	 * Instantiates a new, random GUID.
	 */
	public GUID() {
		this(bytesForUUID(UUID.randomUUID()));
	}

	/**
	 * Instantiates a new GUID.
	 *
	 * @param byteRepresentation the byte representation
	 */
	public GUID(byte[] byteRepresentation) {
		this.byteRepresentation = (byteRepresentation == null) ? null : byteRepresentation.clone();
		this.urlRepresentation = Base64.encodeBase64URLSafeString(byteRepresentation);
		this.xmlRepresentation = Hex.encodeHexString(this.byteRepresentation);
	}

	/**
	 * Instantiates a new GUID.
	 *
	 * @param urlRepresentation the URL-safe String representation
	 */
	public GUID(String urlRepresentation) {
		this.urlRepresentation = urlRepresentation;
		this.byteRepresentation = Base64.decodeBase64(urlRepresentation);
		this.xmlRepresentation = Hex.encodeHexString(byteRepresentation);
	}

	@Override
	public String toString() {
		return urlRepresentation;
	}

	/**
	 * Return a byte array for a Java UUID.
	 *
	 * @param uuid the UUID
	 * @return the byte array
	 */
	private static byte[] bytesForUUID(UUID uuid) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(byteStream);
		try {
			dataOut.writeLong(uuid.getMostSignificantBits());
			dataOut.writeLong(uuid.getLeastSignificantBits());
			dataOut.flush();
		} catch (IOException e) {
			// Writing bytes to memory should never fail.
			throw new RuntimeException("Unexpected failure serializing GUID", e);
		}
		return byteStream.toByteArray();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((urlRepresentation == null) ? 0 : urlRepresentation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		GUID other = (GUID)obj;
		if(urlRepresentation == null) {
			if(other.urlRepresentation != null) {
				return false;
			}
		} else if(!urlRepresentation.equals(other.urlRepresentation)) {
			return false;
		}
		return true;
	}


}
