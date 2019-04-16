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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import es.redmic.exception.mediastorage.MSFileUploadException;

public abstract class FileUtils {

	public static void saveFile(byte[] content, File file) {

		try {

			BufferedOutputStream buffStream = new BufferedOutputStream(new FileOutputStream(file));
			buffStream.write(content);
			buffStream.close();
		} catch (IOException e) {
			throw new MSFileUploadException(e);
		}
	}

	public static String getRandomFileNameFromMultipartFile(MultipartFile file) {

		String extension = FilenameUtils.getExtension(file.getOriginalFilename());

		return UUID.randomUUID() + "." + extension.toLowerCase();
	}

	public static void createDirectoryIfNotExist(String path) {

		File directory = new File(path);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}
}
