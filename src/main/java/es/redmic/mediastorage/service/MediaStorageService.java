package es.redmic.mediastorage.service;

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
