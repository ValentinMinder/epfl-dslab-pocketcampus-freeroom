package org.pocketcampus.core.communication.pcp;

import java.io.File;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;

public class FileData extends BinaryData {
	private File file_;

	public FileData(String httpPostKey, File file) {
		super(httpPostKey);
		file_ = file;
	}

	@Override
	public MultipartEntity addToMultipartEntity(MultipartEntity entity) {
		FileBody fb = new FileBody(file_);
		entity.addPart(super.getKey(), fb);
		return entity;
	}

}
