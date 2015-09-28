package com.psu.hpa.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.psu.hpa.Constants;
import com.psu.hpa.models.ErrorType;

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
			if(line.contains(value)) {
				count++;
			}
		}
		
		if(count > 1)
			return true;
		
		return false;
	}
	
	public static List<String> readFile(URL streamURL)  throws FileNotFoundException, IOException {
		List<String> contentList = new ArrayList<String>();
		BufferedReader in;
		in = new BufferedReader(new InputStreamReader(streamURL.openStream()));
		String inputLine;
		
	    while ((inputLine = in.readLine()) != null) {
	        contentList.add(inputLine);
	    }
	    
	    in.close();
		return contentList;
	}
	
	public static void writeCSVFileHeaders(FileWriter fileWriter, String errorNumber, String errorType, String fileName, String errorDetails) throws IOException {
		fileWriter.append(errorNumber);
		fileWriter.append(",");
		fileWriter.append(errorType);
		fileWriter.append(",");
		fileWriter.append(fileName);
		fileWriter.append(",");
		fileWriter.append(errorDetails);
		fileWriter.append(",");
		fileWriter.append("\n");
	}
	
	public static void writeToCSVFile(FileWriter fileWriter, long errorNumber, ErrorType errorType, String fileName, String errorDetails) throws IOException {
		fileWriter.append(String.valueOf(errorNumber));
		fileWriter.append(",");
		fileWriter.append(errorType.getValue());
		fileWriter.append(",");
		fileWriter.append(fileName);
		fileWriter.append(",");
		fileWriter.append(errorDetails);
		fileWriter.append(",");
		fileWriter.append("\n");
	}

}