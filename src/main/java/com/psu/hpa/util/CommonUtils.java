package com.psu.hpa.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.psu.hpa.Constants;

public class CommonUtils {
	/**
	 * Load file from temp directory.
	 *
	 * @param location the location of file
	 */
	public static String loadFile(String location) throws IOException {
		File file = new File(location);
		byte binaryData[] = loadFile(file);
		return Base64.encodeBase64String(binaryData);
	}

	/**
	 * Convert file into array of bytes.
	 *
	 * @param file the file to read contents from and convert to array of bytes
	 */
	public static byte[] loadFile(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		return IOUtils.toByteArray(in);
	}
	
	public static boolean hasDuplicate(List<String> list, String value) {
		int count = 0;
		
		for(String line : list) {
			if(line.contains(Constants.EXTM3U)) {
				count++;
			}
		}
		
		if(count > 1)
			return true;
		
		return false;
	}

}