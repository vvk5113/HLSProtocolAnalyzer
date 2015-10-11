package com.psu.hpa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Constants {
	/** EXTM3U */
	public static final String EXTM3U = "#EXTM3U";
	
	/** EXT-X-STREAM-INF */
	public static final String EXT_X_STREAM_INF = "#EXT-X-STREAM-INF";
	
	/** EXT-X-TARGETDURATION */
	public static final String EXT_X_TARGETDURATION = "#EXT-X-TARGETDURATION";
	
	/** EXT-X-MEDIA-SEQUENCE */
	public static final String EXT_X_MEDIA_SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
	
	/** EXTINF */
	public static final String EXTINF = "#EXTINF";
	
	/** EXT-X-BYTERANG */
	public static final String EXT_X_BYTERANGE = "#EXT-X-BYTERANGE";
	
	/** EXT-X-KEY */
	public static final String EXT_X_KEY = "#EXT-X-KEY";
	
	/** EXT-X-I-FRAMES-ONLY */
	public static final String EXT_X_I_FRAMES_ONLY = "#EXT-X-I-FRAMES-ONLY";
	
	/** EXT-X-VERSION */
	public static final String EXT_X_VERSION = "#EXT-X-VERSION";
	
	/** EXT-X-ENDLIST */
	public static final String EXT_X_ENDLIST = "#EXT-X-ENDLIST";
	
	/** Regex to match the first element in master playlist. */
	public static final Pattern MATCH_EXTM3U = Pattern.compile("^[A-Z0-9#]+$");
	
	/** Regex to match the MATCH_EXT_X_STREAM_INF element in master playlist. */
	public static final Pattern MATCH_EXT_X_STREAM_INF = Pattern.compile("^[A-Z0-9#-: ,=]+$");
	
	//TODO: improve this regex to check the after dot extension
	/** Regex to match the MATCH_EXT_X_STREAM_INF URI element in master playlist. */
	public static final Pattern MATCH_EXT_X_STREAM_INF_URI = Pattern.compile("^[a-z0-9-._/]+$");
	
	/** Regex to match the EXT_X_TARGETDURATION element in master playlist. */
	public static final Pattern MATCH_EXT_X_TARGETDURATION = Pattern.compile("^[A-Z0-9#-:]+$");
	
	/** Regex to match the EXT_X_MEDIA_SEQUENCE element in master playlist. */
	public static final Pattern MATCH_EXT_X_MEDIA_SEQUENCE = Pattern.compile("^[A-Z0-9#-:]+$");
	
	/** Regex to match the EXTINF element in master playlist. */
	public static final Pattern MATCH_EXTINF = Pattern.compile("^[A-Za-z0-9#:,. ]+$");
	
	//TODO: improve this regex to check the after dot extension
	/** Regex to match the stream uri element following the EXTINF element. */
	public static final Pattern MATCH_EXTINF_STREAM_URI = Pattern.compile("^[A-Za-z0-9-_.]+$");
	
	/** Regex to match the EXT-X-VERSION element. */
	public static final Pattern MATCH_EXT_X_VERSION = Pattern.compile("^[A-Z0-9-#:]+$");
	
	public static final Map<String, String> VALID_MEDIA_TAGS;
    static {
    	Map<String, String> validMediaTags = new HashMap<String, String>();
    	validMediaTags.put("#EXTM3U", "#EXTM3U");
    	validMediaTags.put("#EXT-X-STREAM-INF", "#EXT-X-STREAM-INF");
		validMediaTags.put("#EXT-X-TARGETDURATION", "#EXT-X-TARGETDURATION");
		validMediaTags.put("#EXT-X-MEDIA-SEQUENCE", "#EXT-X-MEDIA-SEQUENCE");
		validMediaTags.put("#EXTINF", "#EXTINF");
		validMediaTags.put("#EXT-X-BYTERANGE", "#EXT-X-BYTERANGE");
		validMediaTags.put("#EXT-X-KEY", "#EXT-X-KEY");
		validMediaTags.put("#EXT-X-I-FRAMES-ONLY", "#EXT-X-I-FRAMES-ONLY");
		validMediaTags.put("#EXT-X-VERSION", "#EXT-X-VERSION");
		validMediaTags.put("#EXT-X-ENDLIST", "#EXT-X-ENDLIST");
    	VALID_MEDIA_TAGS = Collections.unmodifiableMap(validMediaTags);
    }
	
}
