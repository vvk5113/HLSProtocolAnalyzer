package com.psu.hpa.validators;

import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.psu.hpa.Constants;
import com.psu.hpa.util.CommonUtils;

@Component
public class MasterPlaylistValidator implements PlaylistValidator {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	public void validate(List<String> contentList, StringBuilder sb, String masterPlaylistURI) {
		sb.append("******** Starting validation of the master playlist "+masterPlaylistURI+" *********");
		sb.append("\n\r");
		
		if(CollectionUtils.isNotEmpty(contentList)) {
			if(!(contentList.get(0).equals(Constants.EXTM3U))) {
				sb.append("Master playlist is missing the starting mandatory element : "+Constants.EXTM3U);
				sb.append("\n\r");
			}
			
			if(CommonUtils.hasDuplicate(contentList, Constants.EXTM3U)) {
				sb.append("Master playlist contains duplicate "+Constants.EXTM3U+" elements");
				sb.append("\n\r");
			}
			
			//TODO: Implement check to make sure first character is always #
			Matcher matchEXTM3U = Constants.MATCH_EXTM3U.matcher(contentList.get(0));
			if(!matchEXTM3U.matches()) {
				sb.append("The starting element is in incorrect format");
				sb.append("\n\r");
			}
			
			if(!(StringUtils.join(contentList).contains(Constants.EXT_X_STREAM_INF))) {
				sb.append("Master playlist is missing the Variant Stream, a set of Renditions which can be combined to play the presentation");
			}
			
			for(int i=0; i<contentList.size(); i++) {
				if(contentList.get(i).contains(Constants.EXT_X_STREAM_INF)) {
					Matcher matchEXT_X_STREAM_INF = Constants.MATCH_EXT_X_STREAM_INF.matcher(contentList.get(i));
					if(!matchEXT_X_STREAM_INF.matches()) {
						sb.append(Constants.EXT_X_STREAM_INF+" is in incorrect format");
						sb.append("\n\r");
					}
					
					if(contentList.get(i+1).startsWith("#") || !contentList.get(i+1).endsWith(".m3u8")) {
						sb.append(Constants.EXT_X_STREAM_INF+" uri missing");
						sb.append("\n\r");
					} else {
						Matcher matchEXT_X_STREAM_INF_URI = Constants.MATCH_EXT_X_STREAM_INF_URI.matcher(contentList.get(i+1));
						if(!matchEXT_X_STREAM_INF_URI.matches()) {
							sb.append("Segment stream uri name "+contentList.get(i+1)+" is in incorrect format");
							sb.append("\n\r");
						}
					}
				}
			}
		}
		sb.append("******** Validating of the master playlist ends *********");
		sb.append("\n\r");
		sb.append("\n\r");
		sb.append("\n\r");
	}
}
