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
public class MediaPlaylistValidator implements PlaylistValidator {

	private Logger log = LoggerFactory.getLogger(getClass());

	public void validate(List<String> contentList, StringBuilder sb, String mediaPlaylistURI) {
		sb.append("******** Starting validation of the media playlist "+mediaPlaylistURI+" *********");
		sb.append("\r\n");
		
		long EXT_X_VERSION = -1;
		
		if(CollectionUtils.isNotEmpty(contentList)) {
			if(!(contentList.get(0).equals(Constants.EXTM3U))) {
				sb.append("Master playlist is missing the starting mandatory element : "+Constants.EXTM3U);
				sb.append("\r\n");
			}
			
			if(CommonUtils.hasDuplicate(contentList, Constants.EXTM3U)) {
				sb.append("Media playlist contains duplicate "+Constants.EXTM3U+" elements");
				sb.append("\r\n");
			}
			
			Matcher matchEXTM3U = Constants.MATCH_EXTM3U.matcher(contentList.get(0));
			if(!matchEXTM3U.matches()) {
				sb.append("The starting element "+Constants.EXTM3U+" is in incorrect format");
				sb.append("\r\n");
			}
			
			if(!(StringUtils.join(contentList).contains(Constants.EXT_X_VERSION))) {
				sb.append("Media playlist is missing the required element: "+Constants.EXT_X_VERSION);
				sb.append("\r\n");
			}
			
			if(CommonUtils.hasDuplicate(contentList, Constants.EXT_X_VERSION)) {
				sb.append("Media playlist contains duplicate "+Constants.EXT_X_VERSION+" elements");
				sb.append("\r\n");
			}
						
			if(!(StringUtils.join(contentList).contains(Constants.EXT_X_TARGETDURATION))) {
				sb.append("Media playlist is missing the required element: "+Constants.EXT_X_TARGETDURATION);
				sb.append("\r\n");
			}
			
			if(CommonUtils.hasDuplicate(contentList, Constants.EXT_X_TARGETDURATION)) {
				sb.append("Media playlist contains duplicate "+Constants.EXT_X_TARGETDURATION+" elements");
				sb.append("\r\n");
			}
			
			if(!(StringUtils.join(contentList).contains(Constants.EXTINF))) {
				sb.append("Media playlist is missing the required element: "+Constants.EXTINF);
				sb.append("\r\n");
			}
			
			//TODO: Implement rule: The EXT-X-MEDIA-SEQUENCE tag MUST appear before the first Media Segment in the Playlist.
			
			for(int i=0; i<contentList.size(); i++) {
				String lineContent = contentList.get(i);
				if(lineContent.contains(Constants.EXT_X_VERSION)) {
					//TODO: More rules need to be implemented for this from section "7. Protocol version compatibility"
					String EXT_X_VERSION_VALUE = lineContent.substring(lineContent.indexOf(":")+1, lineContent.length());
					EXT_X_VERSION = StringUtils.isNotBlank(EXT_X_VERSION_VALUE) ? Long.parseLong(EXT_X_VERSION_VALUE) : -1;
					Matcher matchEXT_X_VERSION = Constants.MATCH_EXT_X_VERSION.matcher(lineContent);
					if(!matchEXT_X_VERSION.matches()) {
						sb.append(Constants.EXT_X_VERSION+" is in incorrect format");
						sb.append("\r\n");
					}
				}
								
				if(lineContent.contains(Constants.EXT_X_TARGETDURATION)) {
					Matcher matchEXT_X_TARGETDURATION = Constants.MATCH_EXT_X_TARGETDURATION.matcher(lineContent);
					if(!matchEXT_X_TARGETDURATION.matches()) {
						sb.append(Constants.EXT_X_TARGETDURATION+" is in incorrect format");
						sb.append("\r\n");
					}
				}
				
				if(lineContent.contains(Constants.EXT_X_MEDIA_SEQUENCE)) {
					Matcher matchEXT_X_MEDIA_SEQUENCE = Constants.MATCH_EXT_X_MEDIA_SEQUENCE.matcher(lineContent);
					if(!matchEXT_X_MEDIA_SEQUENCE.matches()) {
						sb.append(Constants.EXT_X_MEDIA_SEQUENCE+" is in incorrect format");
						sb.append("\r\n");
					}
				}
				
				if(lineContent.contains(Constants.EXTINF)) {
					Matcher matchMATCH_EXTINF = Constants.MATCH_EXTINF.matcher(lineContent);
					if(!matchMATCH_EXTINF.matches()) {
						sb.append(Constants.EXTINF+" is in incorrect format");
						sb.append("\r\n");
					}
					
					if(contentList.get(i+1).startsWith("#") || !contentList.get(i+1).endsWith(".ts")) {
						sb.append("Media stream file (.ts) is missing following element "+Constants.MATCH_EXT_X_STREAM_INF);
						sb.append("\r\n");
					} else {
						Matcher matchEXTINF_STREAM_URI = Constants.MATCH_EXTINF_STREAM_URI.matcher(contentList.get(i+1));
						if(!matchEXTINF_STREAM_URI.matches()) {
							sb.append("Media stream file name "+contentList.get(i+1)+" is in incorrect format");
							sb.append("\r\n");
						}
					}
				}
			}
			
			if(!(contentList.get(contentList.size()-1).equals(Constants.EXT_X_ENDLIST))) {
				sb.append("Media playlist is missing the ending tag: "+Constants.EXT_X_ENDLIST);
				sb.append("\r\n");
			}
			
		}
		sb.append("******** Validation of the media playlist ends *********");
		sb.append("\r\n");
		sb.append("\r\n");
		sb.append("\r\n");
	}

}
