package org.pocketcampus.core.communication;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;

public class FileUploadParameters {
	HashMap<String, File> params_ = new HashMap<String, File>();
	
	public void addParameter(String key, File file) {
		params_.put(key, file);
	}
	
	public MultipartEntity setParameters(MultipartEntity mpEntity) {
		for (Entry<String, File> entry : params_.entrySet()) {
			String key = entry.getKey();
			FileBody fb = new FileBody(entry.getValue());
			
			mpEntity.addPart(key, fb);
		}
		return mpEntity;
	}
}
