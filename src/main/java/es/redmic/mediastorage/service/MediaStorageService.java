package es.redmic.mediastorage.service;

/*-
 * #%L
 * Mediastorage
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import es.redmic.exception.mediastorage.MSFileUploadException;

public abstract class MediaStorageService implements MediaStorageServiceItfc {

	protected static final int BUFFER_SIZE = 4096;

	public MediaStorageService() {
	}

	@Override
	public File uploadTempFile(MultipartFile file, String pathTempFile) {

		FileUtils.createDirectoryIfNotExist(pathTempFile);

		String fileName = FileUtils.getRandomFileNameFromMultipartFile(file);
		File fileOut = new File(pathTempFile + "/" + fileName);

		try {
			FileUtils.saveFile(file.getBytes(), fileOut);
		} catch (IOException e) {
			throw new MSFileUploadException(e);
		}

		return fileOut;
	}

	@Override
	public void saveTempFile(byte[] content, String pathTempFile, String fileName) {

		FileUtils.createDirectoryIfNotExist(pathTempFile);

		FileUtils.saveFile(content, new File(pathTempFile + "/" + fileName));
	}

	@Override
	public File openTempFile(String path, String fileName) {
		File file = new File(path + "/" + fileName);

		return file;
	}

	@Override
	public boolean checkTempFileExist(String path, String fileName) {

		return new File(path + "/" + fileName).exists();
	}

	@Override
	public void deleteTempFile(String path, String fileName) {

		File file = new File(path + "/" + fileName);

		if (file.exists())
			file.delete();
	}
}
