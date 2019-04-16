package es.redmic.test.unit.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import es.redmic.mediastorage.service.FileUtils;

public class FileUtilsTest {

	@Test
	public void createDirectoryIfNotExist_CreateDirectory_IfDirectoryNotExistInRedmic() throws Exception {

		String path = "/home/REDMIC/MEDIA_STORAGE_PRUEBA";

		FileUtils.createDirectoryIfNotExist(path);

		File f = new File(path);

		assertTrue(f.exists());

		f.delete();

		assertFalse(f.exists());
	}

	@Test
	public void createDirectoryIfNotExist_CreateMultiLevelDirectory_IfDirectoriesNotExistsInRedmic() throws Exception {

		String path = "/home/REDMIC/MEDIA_STORAGE_PRUEBA/Level2";

		FileUtils.createDirectoryIfNotExist(path);

		File f = new File(path);

		assertTrue(f.exists());

		f.delete();

		assertFalse(f.exists());
	}
}
