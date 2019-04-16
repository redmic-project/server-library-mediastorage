package es.redmic.mediastorage.service;

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