package com.psu.hpa;

public class ACORDTypeCodes {
	/** MimeTypeTC for Unknown. */
	public static final String MIME_TYPE_UNKNOWN = "0";

	/** MimeTypeTC for image/tiff. */
	public static final String MIME_TYPE_TIFF = "11";

	/** MimeTypeTC for application/pdf. */
	public static final String MIME_TYPE_PDF = "2147483647";

	/** ImageType for Unknown. */
	public static final String IMAGE_TYPE_UNKNOWN = "0";

	/** ImageType for TIFF. */
	public static final String IMAGE_TYPE_TIFF = "3";

	/** ImageType for PDF. */
	public static final String IMAGE_TYPE_PDF = "4";
	
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
