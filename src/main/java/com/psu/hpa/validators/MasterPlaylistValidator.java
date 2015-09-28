package com.psu.hpa.validators;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.psu.hpa.Constants;
import com.psu.hpa.models.ErrorType;
import com.psu.hpa.util.CommonUtils;

@Component
public class MasterPlaylistValidator {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	public void validate(List<String> contentList, FileWriter fileWriter, String masterPlaylistURI) throws IOException {
		
		if(CollectionUtils.isNotEmpty(contentList)) {
			if(!(contentList.get(0).equals(Constants.EXTM3U))) {
				String errorDetails = "Master playlist is missing the starting mandatory element : "+Constants.EXTM3U;
				CommonUtils.writeToCSVFile(fileWriter, 1, ErrorType.MISSING_TAG, masterPlaylistURI, errorDetails);
				
			}
			
			if(CommonUtils.hasDuplicate(contentList, Constants.EXTM3U)) {
				String errorDetails = "Master playlist contains duplicate "+Constants.EXTM3U+" tags";
				CommonUtils.writeToCSVFile(fileWriter, 2, ErrorType.DUPLICATE_TAG, masterPlaylistURI, errorDetails);
				
			}
			
			//TODO: Implement check to make sure first character is always #
			Matcher matchEXTM3U = Constants.MATCH_EXTM3U.matcher(contentList.get(0));
			if(!matchEXTM3U.matches()) {
				String errorDetails = "The starting element "+Constants.MATCH_EXTM3U+" is in incorrect format";
				CommonUtils.writeToCSVFile(fileWriter, 3, ErrorType.INCORRECT_FORMAT, masterPlaylistURI, errorDetails);
			}
			
			if(!(StringUtils.join(contentList).contains(Constants.EXT_X_STREAM_INF))) {
				String errorDetails = "Master playlist is missing the Variant Stream "+Constants.EXT_X_STREAM_INF+" , a set of Renditions which can be combined to play the presentation";
				CommonUtils.writeToCSVFile(fileWriter, 4, ErrorType.MISSING_TAG, masterPlaylistURI, errorDetails);
			}
			
			for(int i=0; i<contentList.size(); i++) {
				if(contentList.get(i).contains(Constants.EXT_X_STREAM_INF)) {
					Matcher matchEXT_X_STREAM_INF = Constants.MATCH_EXT_X_STREAM_INF.matcher(contentList.get(i));
					if(!matchEXT_X_STREAM_INF.matches()) {
						String errorDetails = Constants.EXT_X_STREAM_INF+" is in incorrect format";
						CommonUtils.writeToCSVFile(fileWriter, i, ErrorType.INCORRECT_FORMAT, masterPlaylistURI, errorDetails);
					}
					
					if(contentList.get(i+1).startsWith("#") || !contentList.get(i+1).endsWith(".m3u8")) {
						String errorDetails = Constants.EXT_X_STREAM_INF+" uri missing";
						CommonUtils.writeToCSVFile(fileWriter, i, ErrorType.MISSING_SEGMENT_FILE, masterPlaylistURI, errorDetails);
						
					} else {
						Matcher matchEXT_X_STREAM_INF_URI = Constants.MATCH_EXT_X_STREAM_INF_URI.matcher(contentList.get(i+1));
						if(!matchEXT_X_STREAM_INF_URI.matches()) {
							String errorDetails = "Segment stream uri name "+contentList.get(i+1)+" is in incorrect format";
							CommonUtils.writeToCSVFile(fileWriter, i, ErrorType.INCORRECT_FORMAT, masterPlaylistURI, errorDetails);
							
						}
					}
				}
			}
		}
	}
}
