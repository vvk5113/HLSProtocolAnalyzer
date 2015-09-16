package com.psu.hpa;

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


	
    /** Transaction result code for successful hpa submit. */
    public static final int TRANS_RESULT_SUCCESS = 4;

    /** Transaction result code with information. */
    public static final int TRANS_RESULT_SUCCESS_WITH_INFORMATION = 2;

    /** Transaction result code failure. */
    public static final int TRANS_RESULT_FAILURE = 5;
	
    /** ResultInfoSeverity success. */
    public static final String MESSAGE_SEVERITY_SUCCESS = "1";

    /** ResultInfoSeverity severe. */
    public static final String MESSAGE_SEVERITY_SEVERE = "2";

    /** ResultInfoSeverity warning. */
    public static final String MESSAGE_SEVERITY_WARNING = "3";
    
    /** FinActivityType for 1035 exchange proceeds. */
	public static final String FIN_ACTIVITY_TYPE_1035_EXCHANGE = "192";
	
	/** FinActivityType for Non 1035 exchange proceeds. */
	public static final String FIN_ACTIVITY_TYPE_NOT_1035_EXCHANGE = "7";
}
