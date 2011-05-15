package org.pocketcampus.core.communication.pcp;

import org.apache.http.entity.mime.MultipartEntity;

public abstract class BinaryData {
	private String key_;
	
	/**
	 * @param httpPostKey the key that will be associated to this data in the HTTP request.
	 * This key will be used server-side to retrieve this data.
	 */
	public BinaryData(String httpPostKey) {
		key_ = httpPostKey;
	}
	
	/**
	 * @return the HTTP field key that should be used with this data.
	 */
	public String getKey() {
		return key_;
	}
	
	/**
	 * Adds this BinaryData to the given MultipartEntity.
	 * @param entity the MultipartEntity that will be modified.
	 * @return the modified entity, for method chaining purpose.
	 */
	abstract public MultipartEntity addToMultipartEntity(MultipartEntity entity);
}
