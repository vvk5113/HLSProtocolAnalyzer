package com.psu.hpa.validators;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.RedirectView;

import com.psu.hpa.Constants;
import com.psu.hpa.models.ErrorType;
import com.psu.hpa.util.CommonUtils;

@Component
public class MediaPlaylistValidator {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	public static long seqNumber = 1;

	public void validate(List<String> contentList, FileWriter fileWriter, String mediaPlaylistURI, String masterStreamURI) throws IOException {
		log.info("******** Starting validation of the media playlist "+mediaPlaylistURI+" *********");
		
		if(CollectionUtils.isNotEmpty(contentList)) {
			
			BigDecimal EXT_X_TARGETDURATION_VALUE = null;
			
			if(!(contentList.get(0).equals(Constants.EXTM3U))) {
				String errorDetails = "Master playlist is missing the starting mandatory tag : "+Constants.EXTM3U;
				CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.INCORRECT_FORMAT, mediaPlaylistURI, errorDetails);
			}
			
			if(CommonUtils.hasDuplicate(contentList, Constants.EXTM3U)) {
				String errorDetails = "Media playlist contains duplicate "+Constants.EXTM3U+" tags";
				CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.DUPLICATE_TAG, mediaPlaylistURI, errorDetails);
			}
			
			Matcher matchEXTM3U = Constants.MATCH_EXTM3U.matcher(contentList.get(0));
			if(!matchEXTM3U.matches()) {
				String errorDetails = "The starting element "+Constants.EXTM3U+" is in incorrect format";
				CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.INCORRECT_FORMAT, mediaPlaylistURI, errorDetails);
			}
			
			if(!(StringUtils.join(contentList).contains(Constants.EXT_X_VERSION))) {
				String errorDetails = "Media playlist is missing the required tag: "+Constants.EXT_X_VERSION;
				CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.MISSING_TAG, mediaPlaylistURI, errorDetails);
			}
			
			boolean hasDuplicate_EXT_X_VERSION = false; 
			if(CommonUtils.hasDuplicate(contentList, Constants.EXT_X_VERSION)) {
				hasDuplicate_EXT_X_VERSION = true;
				String errorDetails = "Media playlist contains duplicate "+Constants.EXT_X_VERSION+" tags";
				CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.DUPLICATE_TAG, mediaPlaylistURI, errorDetails);
			}
						
			if(!(StringUtils.join(contentList).contains(Constants.EXT_X_TARGETDURATION))) {
				String errorDetails = "Media playlist is missing the required tag: "+Constants.EXT_X_TARGETDURATION;
				CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.MISSING_TAG, mediaPlaylistURI, errorDetails);
			}
			
			if(CommonUtils.hasDuplicate(contentList, Constants.EXT_X_TARGETDURATION)) {
				String errorDetails = "Media playlist contains duplicate "+Constants.EXT_X_TARGETDURATION+" tags";
				CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.DUPLICATE_TAG, mediaPlaylistURI, errorDetails);
			}
			
			if(!(StringUtils.join(contentList).contains(Constants.EXTINF))) {
				String errorDetails = "Media playlist is missing the required tag: "+Constants.EXTINF;
				CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.MISSING_TAG, mediaPlaylistURI, errorDetails);
			}
			
			//TODO: Implement rule: The EXT-X-MEDIA-SEQUENCE tag MUST appear before the first Media Segment in the Playlist.
			
			for(int i=0; i<contentList.size(); i++) {
				String lineContent = contentList.get(i);
				
				if(lineContent.contains(Constants.EXT_X_VERSION) && !hasDuplicate_EXT_X_VERSION) {
					validate_EXT_X_VERSION(contentList, lineContent, fileWriter, mediaPlaylistURI, seqNumber);
				}
								
				if(lineContent.contains(Constants.EXT_X_TARGETDURATION)) {
					Matcher matchEXT_X_TARGETDURATION = Constants.MATCH_EXT_X_TARGETDURATION.matcher(lineContent);
					if(!matchEXT_X_TARGETDURATION.matches()) {
						String errorDetails = Constants.EXT_X_TARGETDURATION+" is in incorrect format";
						CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.INCORRECT_FORMAT, mediaPlaylistURI, errorDetails);
					} else {
						String EXT_X_TARGETDURATION_VALUE1 = lineContent.substring(lineContent.indexOf(":")+1, lineContent.length());
						EXT_X_TARGETDURATION_VALUE = StringUtils.isNotBlank(EXT_X_TARGETDURATION_VALUE1) ? new BigDecimal(EXT_X_TARGETDURATION_VALUE1) : null;
						
					}
				}
				
				if(lineContent.contains(Constants.EXT_X_MEDIA_SEQUENCE)) {
					Matcher matchEXT_X_MEDIA_SEQUENCE = Constants.MATCH_EXT_X_MEDIA_SEQUENCE.matcher(lineContent);
					if(!matchEXT_X_MEDIA_SEQUENCE.matches()) {
						String errorDetails = Constants.EXT_X_MEDIA_SEQUENCE+" is in incorrect format";
						CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.INCORRECT_FORMAT, mediaPlaylistURI, errorDetails);
					}
				}
				
				if(lineContent.contains(Constants.EXTINF)) {
					Matcher matchMATCH_EXTINF = Constants.MATCH_EXTINF.matcher(lineContent);
					if(!matchMATCH_EXTINF.matches()) {
						String errorDetails = Constants.EXTINF+" is in incorrect format";
						CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.INCORRECT_FORMAT, mediaPlaylistURI, errorDetails);
					} else {
						String EXTINF_DURATION_VALUE = lineContent.substring(lineContent.indexOf(":")+1, lineContent.indexOf(","));
						BigDecimal EXTINF_DURATION_VALUE1 = StringUtils.isNotBlank(EXTINF_DURATION_VALUE) ? new BigDecimal(EXTINF_DURATION_VALUE) : null;
						
						if(EXT_X_TARGETDURATION_VALUE != null && EXTINF_DURATION_VALUE1 != null && EXTINF_DURATION_VALUE1.compareTo(EXT_X_TARGETDURATION_VALUE) == 1) {
							if(!(contentList.get(i+1).startsWith("#")) && contentList.get(i+1).endsWith(".ts")) {
								String errorDetails = "The EXTINF duration of Media Segment ("+contentList.get(i+1)+") must be less than or equal to the target duration "+Constants.EXT_X_TARGETDURATION;
								CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.FUNCTIONAL_TAG, mediaPlaylistURI, errorDetails);
							}
						}
						
					}
					
					if(contentList.get(i+1).startsWith("#") || !contentList.get(i+1).endsWith(".ts")) {
						String errorDetails = "Media stream file (.ts) is missing tag "+Constants.MATCH_EXT_X_STREAM_INF;
						CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.MISSING_TAG, mediaPlaylistURI, errorDetails);
					} else {
						Matcher matchEXTINF_STREAM_URI = Constants.MATCH_EXTINF_STREAM_URI.matcher(contentList.get(i+1));
						if(!matchEXTINF_STREAM_URI.matches()) {
							String errorDetails = "Media stream file name "+contentList.get(i+1)+" is in incorrect format";
							CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.INCORRECT_FORMAT, mediaPlaylistURI, errorDetails);
						}
					}
				}
				
				if(!lineContent.startsWith("#") && lineContent.endsWith(".ts")) {
					String transportStreamURL = masterStreamURI+lineContent;
					Object transportStream = null;
					try {
						transportStream = CommonUtils.readFile(new URL(transportStreamURL));
					} catch(FileNotFoundException fnfe) {
						String errorDetails = "Transport Stream file "+lineContent+" is missing";
						CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.MISSING_STREAM_FILE, mediaPlaylistURI, errorDetails);
						fnfe.printStackTrace();
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				
			}
			
			if(contentList.get(contentList.size()-1).contains(Constants.EXT_X_ENDLIST)) {
				if(!(contentList.get(contentList.size()-1).equals(Constants.EXT_X_ENDLIST))) {
					String errorDetails = "Media playlist contains the incorrect ending tag: "+contentList.get(contentList.size()-1);
					CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.INCORRECT_FORMAT, mediaPlaylistURI, errorDetails);
				}
			}
			
		}
		log.info("******** Validation of the media playlist ends *********");
	}
	
	private void validate_EXT_X_VERSION(List<String> contentList, String element, FileWriter fileWriter, String mediaPlaylistURI, long seqNumber) throws IOException {
		Matcher matchEXT_X_VERSION = Constants.MATCH_EXT_X_VERSION.matcher(element);
		if(!matchEXT_X_VERSION.matches()) {
			String errorDetails = Constants.EXT_X_VERSION+" is in incorrect format";
			CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.INCORRECT_FORMAT, mediaPlaylistURI, errorDetails);
		} else {
			//TODO: More rules need to be implemented for this from section "7. Protocol version compatibility"
			String EXT_X_VERSION_VALUE = element.substring(element.indexOf(":")+1, element.length());
			long EXT_X_VERSION_VALUE1 = StringUtils.isNotBlank(EXT_X_VERSION_VALUE) ? Long.parseLong(EXT_X_VERSION_VALUE) : -1;
			
			for(String lineContent : contentList) {
				if(lineContent.contains(Constants.EXT_X_KEY)) {
					if(!(EXT_X_VERSION_VALUE1 >= 2)) {
						String errorDetails = "A Media Playlist MUST indicate a EXT-X-VERSION of 2 or higher if it contains the IV attribute of the EXT-X-KEY tag.";
						CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.FUNCTIONAL_TAG, mediaPlaylistURI, errorDetails);
					}
				}
				if(lineContent.contains(Constants.EXTINF)) {
					String EXTINFDuration = lineContent.substring(lineContent.indexOf(":")+1, lineContent.indexOf(","));
					if((EXT_X_VERSION_VALUE1 >= 3) && !(EXTINFDuration.contains("."))) {
						String errorDetails = "Media Playlist must include all EXTINF duration values as Floating-point values if "+Constants.EXT_X_VERSION+" value is 3 or higher. Value of "+EXTINFDuration+" for "+Constants.EXTINF+" is not valid.";
						CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.FUNCTIONAL_TAG, mediaPlaylistURI, errorDetails);
					}
				}
				if(lineContent.contains(Constants.EXT_X_BYTERANGE) || lineContent.contains(Constants.EXT_X_I_FRAMES_ONLY)) {
					if(!(EXT_X_VERSION_VALUE1 >= 4)) {
						String errorDetails = "Media Playlist MUST indicate a EXT-X-VERSION of 4 or higher if it contains either EXT-X-BYTERANGE or EXT-X-I-FRAMES-ONLY tag.";
						CommonUtils.writeToCSVFile(fileWriter, seqNumber++, ErrorType.FUNCTIONAL_TAG, mediaPlaylistURI, errorDetails);
					}
				}
			}
		}

	}

}
