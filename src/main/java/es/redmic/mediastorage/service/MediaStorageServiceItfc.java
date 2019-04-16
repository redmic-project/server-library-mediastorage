package es.redmic.mediastorage.service;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import es.redmic.models.es.common.dto.UrlDTO;

public interface MediaStorageServiceItfc {

	/*
	 * Subida de ficheros a s3
	 */

	public List<UrlDTO> uploadFiles(MultipartFile[] files, String path, String url);

	public UrlDTO uploadFile(MultipartFile file, String path, String url);

	public UrlDTO upload(MultipartFile file, String path, String fileName);

	public void upload(byte[] content, String contentType, String path, String fileName);

	public UrlDTO upload(MultipartFile file, String path, String url, String fileName);

	public UrlDTO upload(URL urlFile, String path, String url, String fileName);

	/*
	 * Subida de ficheros a directorio local
	 */

	public File uploadTempFile(MultipartFile file, String pathTempFile);

	public void saveTempFile(byte[] content, String pathTempFile, String fileName);

	/* Servir imágenes desde api con seguridad */

	public void openImage(String name, HttpServletResponse response, String path);

	public void openDocumentPDF(String name, HttpServletResponse response, String path);

	public File openFile(String path, String fileName);

	public File openTempFile(String path, String fileName);

	/*
	 * Comprobar existencia de ficheros
	 */

	// S3
	public boolean checkFileExist(String path, String fileName);

	// Local
	public boolean checkTempFileExist(String path, String fileName);

	/*
	 * Borrado de ficheros
	 */

	// S3
	public void deleteFile(String path, String fileName);

	// Local
	public void deleteTempFile(String path, String fileName);
}
