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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import es.redmic.exception.mediastorage.MSFileNotFoundException;
import es.redmic.exception.mediastorage.MSFileUploadException;
import es.redmic.models.es.common.dto.UrlDTO;

public class MediaStorageS3Service extends MediaStorageService {

	@Autowired
	private AmazonS3 amazonS3Client;

	@Value("${aws.bucket}")
	private String AWSBucket;

	public MediaStorageS3Service() {

	}

	/**
	 * Función para guardar multiples ficheros a partir de multipartFile
	 * 
	 * @param files
	 *            array de multipartFile enviado por post
	 * @param path
	 *            ruta donde se va a almacenar el fichero
	 * @param url
	 *            url para acceder al nuevo fichero
	 * @return objeto con la url del nuevo fichero
	 * 
	 */
	@Override
	public List<UrlDTO> uploadFiles(MultipartFile[] files, String path, String url) {
		List<UrlDTO> listUrl = new ArrayList<UrlDTO>();

		for (int i = 0; i < files.length; i++) {
			String fileName = FileUtils.getRandomFileNameFromMultipartFile(files[i]);
			listUrl.add(upload(files[i], path, url, fileName));
		}

		return listUrl;
	}

	@Override
	public UrlDTO uploadFile(MultipartFile file, String path, String url) {

		String fileName = FileUtils.getRandomFileNameFromMultipartFile(file);
		return upload(file, path, url, fileName);
	}

	/**
	 * Función para guardar un fichero a partir de un multipartFile
	 * 
	 * @param file
	 *            multipartFile enviado por post
	 * @param path
	 *            ruta donde se va a almacenar el fichero
	 * @param fileName
	 *            nombre del nuevo fichero
	 * @return objeto con la url del nuevo fichero
	 * 
	 */
	@Override
	public UrlDTO upload(MultipartFile file, String path, String fileName) {

		return upload(file, path, "", fileName);
	}

	/**
	 * Función para guardar un fichero a partir de un multipartFile
	 * 
	 * @param file
	 *            multipartFile enviado por post
	 * @param path
	 *            ruta donde se va a almacenar el fichero
	 * @param url
	 *            url para acceder al nuevo fichero
	 * @param fileName
	 *            nombre del nuevo fichero
	 * @return objeto con la url del nuevo fichero
	 */
	@Override
	public UrlDTO upload(MultipartFile file, String targetPath, String url, String fileName) {

		try {
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentType(file.getContentType());
			meta.setContentLength(file.getSize());
			meta.setHeader("filename", fileName);

			ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
			TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3Client).build();
			Upload result = transferManager.upload(AWSBucket + targetPath, fileName, bis, meta);
			result.waitForUploadResult();
		} catch (Exception e) {
			throw new MSFileUploadException(e);
		}

		UrlDTO urlDTO = new UrlDTO();
		urlDTO.setUrl(url + fileName);

		return urlDTO;
	}

	/**
	 * Función para guardar un fichero a partir de un byte[]
	 * 
	 * @param content
	 *            array de bytes
	 * @param contentType
	 *            tipo de contenido que se va a guardar
	 * @param path
	 *            ruta donde se va a almacenar el fichero
	 * @param fileName
	 *            nombre del nuevo fichero
	 * @return objeto con la url del nuevo fichero
	 * 
	 */
	@Override
	public void upload(byte[] content, String contentType, String path, String fileName) {
		try {

			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentType(contentType);
			meta.setContentLength(content.length);
			meta.setHeader("filename", fileName);

			ByteArrayInputStream bis = new ByteArrayInputStream(content);

			TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3Client).build();
			Upload result = transferManager.upload(AWSBucket + path, fileName, bis, meta);
			result.waitForUploadResult();
		} catch (Exception e) {
			throw new MSFileUploadException(e);
		}
	}

	/**
	 * Función para guardar un fichero a partir de una url
	 * 
	 * @param urlFile
	 *            url del fichero
	 * @param path
	 *            ruta donde se va a almacenar el fichero
	 * @param url
	 *            url para acceder al nuevo fichero
	 * @param filename
	 *            nombre del fichero
	 * @return objeto con la url del nuevo fichero
	 * 
	 */
	@Override
	public UrlDTO upload(URL urlFile, String path, String url, String fileName) {

		File file;
		try {
			file = new File(urlFile.toURI());
			TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3Client).build();
			Upload result = transferManager.upload(AWSBucket + path, fileName, file);
			result.waitForUploadResult();

		} catch (Exception e) {
			throw new MSFileUploadException(e);
		}

		UrlDTO urlDTO = new UrlDTO();
		urlDTO.setUrl(url + fileName);

		return urlDTO;
	}

	/**
	 * Función para cargar una imagen en el response
	 * 
	 * @param name
	 *            nombre del fichero
	 * @param response
	 *            objeto http donde se guarda la imagen para ser devuelta por el
	 *            controlador
	 * @param path
	 *            ruta donde se va a buscar el fichero
	 */
	@Override
	public void openImage(String name, HttpServletResponse response, String prefix) {
		response.reset();
		response.setHeader("Content-Disposition", "inline;filename=\"" + name + "\"");

		String type = name.split("\\.")[1];

		String pathResourse = AWSBucket + prefix;

		ServletOutputStream out;
		File file = new File(name);
		try {
			TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3Client).build();
			Download result = transferManager.download(pathResourse, name, file);
			result.waitForCompletion();
			out = response.getOutputStream();

			BufferedImage image = ImageIO.read(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, type, baos);
			byte[] imgBytes = baos.toByteArray();

			out.write(imgBytes);
			out.flush();
			out.close();
		} catch (Exception e) {
			throw new MSFileNotFoundException(file.getName(), pathResourse, e);
		}
	}

	/**
	 * Función para cargar un pdf en el response
	 * 
	 * @param name
	 *            nombre del fichero
	 * @param response
	 *            objeto http donde se guarda el documento para ser devuelta por el
	 *            controlador
	 * @param path
	 *            ruta donde se va a buscar el fichero
	 */
	@Override
	public void openDocumentPDF(String name, HttpServletResponse response, String prefix) {

		response.reset();
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "inline;filename=\"" + name + "\"");
		String pathResourse = AWSBucket + prefix;

		File file = new File(name);

		try {

			TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3Client).build();
			Download result = transferManager.download(pathResourse, name, file);
			result.waitForCompletion();

			IOUtils.copy(new FileInputStream(file), response.getOutputStream());

		} catch (Exception e) {
			throw new MSFileNotFoundException(file.getName(), pathResourse, e);
		}
	}

	@Override
	public File openFile(String prefix, String filename) {
		String pathResourse = AWSBucket + prefix;
		File file = new File(filename);
		TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3Client).build();

		try {
			Download result = transferManager.download(pathResourse, filename, file);
			result.waitForCompletion();
		} catch (Exception e) {
			throw new MSFileNotFoundException(file.getName(), pathResourse, e);
		}

		return file;
	}

	@Override
	public boolean checkFileExist(String path, String fileName) {

		return amazonS3Client.doesObjectExist(AWSBucket + path, fileName);
	}

	@Override
	public void deleteFile(String path, String fileName) {

		String awsPath = AWSBucket + path;

		if (amazonS3Client.doesObjectExist(awsPath, fileName))
			amazonS3Client.deleteObject(awsPath, fileName);
	}
}
