package com.psu.hpa.controllers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.psu.hpa.Constants;
import com.psu.hpa.ErrorMailSender;
import com.psu.hpa.ImageProcessingException;
import com.psu.hpa.UploadNotFoundException;
import com.psu.hpa.application.FieldInfo;
import com.psu.hpa.application.GUID;
import com.psu.hpa.models.DropResponse;
import com.psu.hpa.models.SubmitResults;
import com.psu.hpa.models.UploadModel;
import com.psu.hpa.util.FileValidationStatus;
import com.psu.hpa.validators.HPABindingErrorProcessor;
import com.psu.hpa.validators.MasterPlaylistValidator;
import com.psu.hpa.validators.MediaPlaylistValidator;

import freemarker.template.TemplateModelException;

/** Controller for dealing with individual uploads. */
@Controller
@SessionAttributes(MainController.FORM_NAME)
@RequestMapping("/{id}")
public class MainController extends SessionUploadFormController {
	private Logger log = LoggerFactory.getLogger(getClass());

	/** The standard date format. */
	public static final String DATE_FORMAT = "MM/dd/yyyy";

	/** Request parameter for the uploaded file. */
	private static final String FILE_PARAM = "file";

	/** Regex to match a map key with missing quotes around its contents. */
	public static final Pattern KEY_WITH_MISSING_QUOTES = Pattern.compile("\\[([^\"]+)\\]");

	/** Regex to match the major and minor versions from an MSIE user agent string. */
	private static final Pattern MATCH_MSIE_VERSION = Pattern.compile("; MSIE (\\d+)\\.(\\d+);");

	/** Template (flash) attribute for a binding result with errors. */
	public static final String MODEL_BINDING_RESULT = "bindingResult";

	/** Template attribute for whether the Documentation field has errors. */
	public static final String MODEL_DOCUMENTATION_HAS_ERRORS = "documentationHasErrors";

	/** Template (flash) attribute for a list of error messages. */
	public static final String MODEL_ERRORS = "errors";

	/** Model attribute for the attached file size in bytes. */
	public static final String MODEL_FILE_SIZE = "fileSize";

	/** Model attribute for field info lookup. */
	public static final String MODEL_FIELD_INFO = "fieldInfo";

	/** Model attribute for GUID. */
	public static final String MODEL_GUID = "guid";

	/** Model attribute for original filename. */
	public static final String MODEL_ORIGINAL_FILENAME = "originalFilename";

	/** Model attribute for whether drag-n-drop is supported. */
	public static final String MODEL_SUPPORTS_DROPZONE = "supportsDropzone";

	/** Model attribute for the current upload model data. */
	public static final String MODEL_UPLOAD = "upload";

	/** Model attribute for a reference to our ValueLookup. */
	public static final String MODEL_VALUE_LOOKUP = "valueLookup";

	/** Model attribute for a company name. */
	public static final String MODEL_COMPANY_NAME = "companyName";

	/** Relative URL of this page. */
	public static final String RELATIVE_URL = "upload";

	/** Relative URL of the results page. */
	public static final String RESULTS_URL = "results";

	/** Relative URL of the drop POST action. */
	public static final String DROP_URL = "drop";

	/** Relative URL of the delete attachment POST action. */
	public static final String DELETE_URL = "delete";

	/** The default validator. */
	@Autowired
	private Validator defaultValidator;

	/** The field info lookup tool. */
	@Autowired
	private FieldInfo fieldInfo;

	/** The FreeMarker configurer. */
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;
	
	/** The play list validator. */
	@Autowired
	private MasterPlaylistValidator masterPlaylistValidator;
	
	/** The play list validator. */
	@Autowired
	private MediaPlaylistValidator mediaPlaylistValidator;

	@InitBinder(MODEL_UPLOAD)
	public void initUploadFormBinder(WebDataBinder binder) {
    	binder.setBindingErrorProcessor(new HPABindingErrorProcessor());
    }

	/**
	 * Called on a new session to populate the model with defaults.
	 *
	 * @param form the upload form
	 * @param id the GUID from the path
	 * @return the upload model
	 * @throws UploadNotFoundException signals that the GUID was not in the user's session
	 */
	@ModelAttribute(MODEL_UPLOAD)
	public UploadModel populateCurrentUpload(
			@ModelAttribute(FORM_NAME) Form form,
			@PathVariable("id") GUID id) throws UploadNotFoundException {
		UploadModel result = null;
		if(form.getUploads() != null) {
			result = form.getUploads().get(id);
		}
		if(result == null) {
			throw new UploadNotFoundException();
		}
		return result;
	}

	/**
	 * Handle a GET request. Show the requested upload form.
	 *
	 * @param session the session
	 * @param model the model to update
	 * @param id the GUID from the path
	 * @param form the upload form
	 * @param userAgent the user agent string
	 * @return the template to render
	 */
	@RequestMapping(value = "/" + RELATIVE_URL, method = RequestMethod.GET)
	public String get(
			HttpSession session,
			ModelMap model,
			@PathVariable("id") GUID id,
			@ModelAttribute(MODEL_UPLOAD) UploadModel upload,
			@RequestHeader("User-Agent") String userAgent) {
		// If the user has already submitted this upload, redirect to the results page.
		if(MainController.getForm(session).getCompleted().contains(id)) {
			return "redirect:" + RESULTS_URL;
		}

		model.put(MODEL_GUID, id.urlRepresentation);
		model.put(MODEL_SUPPORTS_DROPZONE, userAgentSupportsDropzone(userAgent));
		model.put(MODEL_FIELD_INFO, getFieldInfo());

		// Check for errors on the Documentation field for IE9.
		boolean documentationHasErrors = false;
		BindingResult lastResult = (BindingResult)model.get(MODEL_BINDING_RESULT);
		if(lastResult != null) {
			if(lastResult.hasFieldErrors("fileName")) {
				documentationHasErrors = true;
			}
		}
		model.put(MODEL_DOCUMENTATION_HAS_ERRORS, documentationHasErrors);

		return "upload";
	}

	/**
	 * Handle a submit POST request.  Update the form attributes, and redirect to the requested page.
	 *
	 * @param request the current request
	 * @param session the current session
	 * @param upload the upload data model
	 * @param result the binding result
	 * @param id the GUID from the path
	 * @param file the attached file
	 * @param redirectAttrs the redirect attrs to update
	 * @return the view to return
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@RequestMapping(value = "/" + RELATIVE_URL, method = RequestMethod.POST)
	public View post(
			HttpServletRequest request,
			HttpSession session,
			@Valid @ModelAttribute(MODEL_UPLOAD) UploadModel upload,
			BindingResult result,
			@PathVariable("id") GUID id,
			RedirectAttributes redirectAttrs) throws IOException, ImageProcessingException {
		
		// Redirect back to this form on error.
		View errorRedirect = discardBadFieldsOrGetErrorRedirect(request, upload, result, redirectAttrs);
		if(errorRedirect != null) {
			return errorRedirect;
		}
		
		StringBuilder masterPlaylistSB = new StringBuilder();
		StringBuilder mediaPlaylistSB = new StringBuilder();		
		String masterPlaylistStreamURL = upload.getStreamURL();		
		String masterPlaylistName = masterPlaylistStreamURL.substring(masterPlaylistStreamURL.lastIndexOf("/")+1, masterPlaylistStreamURL.length());
		List<String> masterPlaylistContents = readFile(new URL(masterPlaylistStreamURL));
		
		masterPlaylistSB.append("******** Starting validation of the master playlist "+masterPlaylistName+" *********");
		masterPlaylistSB.append("\r\n");
		
		if(CollectionUtils.isNotEmpty(masterPlaylistContents)) {
			masterPlaylistValidator.validate(masterPlaylistContents, masterPlaylistSB, masterPlaylistName);
			for(String lineContent : masterPlaylistContents) {
				if(!lineContent.startsWith("#") && lineContent.endsWith(".m3u8")) {
					String mediaPlayListStreamURL = masterPlaylistStreamURL.replace(masterPlaylistName, lineContent);
					List<String> mediaPlaylistContents = null;
					try {
						mediaPlaylistContents = readFile(new URL(mediaPlayListStreamURL));
					} catch(FileNotFoundException fnfe) {
						masterPlaylistSB.append("Media playlist file "+lineContent+" is missing");
						masterPlaylistSB.append("\r\n");
						fnfe.printStackTrace();
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					if(CollectionUtils.isNotEmpty(mediaPlaylistContents)) {
						mediaPlaylistValidator.validate(mediaPlaylistContents, mediaPlaylistSB, lineContent);
					}
				}
			}	
		
			masterPlaylistSB.append("******** Validating of the master playlist ends *********");
			
			String dirPath = session.getServletContext().getRealPath("/"+id+"/hls-stream-validation-logs");
			File fileDir = new File(dirPath);
			fileDir.mkdirs();
			File masterPlaylistValidationLogFile = new File(fileDir, "MasterPlaylistValidation.log");
			File mediaPlaylistValidationLogFile = new File(fileDir, "MediaPlaylistValidation.log");
			
			FileUtils.writeStringToFile(masterPlaylistValidationLogFile, masterPlaylistSB.toString());
			FileUtils.writeStringToFile(mediaPlaylistValidationLogFile, mediaPlaylistSB.toString());
			
			ErrorMailSender.sendEmail(upload.getUserEmail(), session.getServletContext().getRealPath("/"+id+"/hls-stream-validation-logs/MediaPlaylistValidation.log"));
			
			upload.setMasterPlaylistValidationResult(session.getServletContext().getRealPath("/"+id+"/hls-stream-validation-logs/MasterPlaylistValidation.log"));
			upload.setMediaPlaylistValidationResult(session.getServletContext().getRealPath("/"+id+"/hls-stream-validation-logs/MediaPlaylistValidation.log"));
			
			return new RedirectView(RESULTS_URL);
		}

		result.reject("GenericError");
		return discardBadFieldsOrGetErrorRedirect(request, upload, result, redirectAttrs);
	}
	
	private List<String> readFile(URL streamURL)  throws FileNotFoundException, IOException {
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
	
	/**
	 * Gets the results page for an upload.
	 *
	 * @param model the freemarker model
	 * @param upload the upload data model
	 * @return the template name
	 */
	@RequestMapping(value = "/" + RESULTS_URL, method = RequestMethod.GET)
	public String getResults(
			ModelMap model,
			@ModelAttribute(MODEL_UPLOAD) UploadModel upload) {
		return "results";
	}

	/**
	 * Handle a max upload size exceeded exception by adding an error message and returning
	 * to the form.
	 *
	 * @param exception the exception
	 * @param attributes the redirect attributes to update
	 * @return the view name
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException exception, HttpServletRequest request) {
		// Build an error message.
		String maxSize = humanReadableByteCount(exception.getMaxUploadSize(), false);
		String message = messageSource.getMessage(
				"MaxUploadSizeExceeded",
				new Object[] { maxSize },
				LocaleContextHolder.getLocale());

		// Add the error to the flash attributes.
		List<String> errors = new ArrayList<String>();
		errors.add(message);
		FlashMap redirectFlashMap = RequestContextUtils.getOutputFlashMap(request);
		if(redirectFlashMap != null) {
			redirectFlashMap.put("errors", errors);
		}

		// Redirect back to the current page to see the form.
		String path = request.getRequestURI().substring(request.getContextPath().length());
		return "redirect:" + path; // Return to the form
	}


	/**
	 * Human readable byte count.
	 *
	 * Based on http://stackoverflow.com/a/3758880/180891
	 * but does not use decimal point
	 *
	 * @param bytes the number of bytes
	 * @param si use SI units
	 * @return the human readable version of bytes
	 */
	private static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%d %sB", bytes / (long)Math.pow(unit, exp), pre);
	}

	/**
	 * If the binding result has errors, build the error redirect back to the current page.
	 *
	 * @param request the request
	 * @param target the binding target
	 * @param bindingResult the binding result
	 * @param attrs the redirect attributes
	 * @return the redirect view if errors were found.  If there are no errors, returns null.
	 */
	protected View discardBadFieldsOrGetErrorRedirect(HttpServletRequest request, Object target, BindingResult bindingResult, RedirectAttributes attrs) {
		View result = null;
		SpelExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext(target);

		if(bindingResult.hasErrors()) {
			for(FieldError error : bindingResult.getFieldErrors()) {
				String path = error.getField();
				path = fixMapKeyQuotes(path);
				Expression expression = parser.parseExpression(path);
				Object value = expression.getValue(context);
				if(value != null) {
					// Special case: always clear dates with errors
					// This is because on date binding failure, the previous result would remain.
					if(value instanceof Date) {
						expression.setValue(context, null);
					}
				}
			}

			result = postErrorRedirect(request, bindingResult, attrs);

		}

		return result;
	}

	/**
	 * Return an error redirect from a post.
	 *
	 * The errors will be added as flash attributes for the next get.
	 *
	 * @param request the request
	 * @param bindingResult the binding result
	 * @param redirectAttrs the redirect attrs to update
	 * @return the view
	 */
	protected View postErrorRedirect(HttpServletRequest request, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
		redirectAttrs.addFlashAttribute(MODEL_BINDING_RESULT, bindingResult);

		// Build all messages for the current locale.
		List<String> errors = new ArrayList<String>();
		for(ObjectError error : bindingResult.getAllErrors()) {
			String message = messageSource.getMessage(error, LocaleContextHolder.getLocale());
			errors.add(message);
		}
		Collections.sort(errors); // Message order is random, so sort them alphabetically.
		redirectAttrs.addFlashAttribute(MODEL_ERRORS, errors);

		String thisPage = RELATIVE_URL;
		return new RedirectView(thisPage);
	}

	/**
	 * Create a copy of this multipart file in a temporary directory.
	 *
	 * @param file the file to copy
	 * @return the path to the copy
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private File tempCopyOf(MultipartFile file) throws IOException {
		File result = File.createTempFile("upload", ".tmp");
		FileUtils.copyInputStreamToFile(file.getInputStream(), result);
		return result;
	}

	/**
	 * Check if a user agent supports dropzone.
	 *
	 * @param userAgent the user agent
	 * @return true, if successful
	 */
	private boolean userAgentSupportsDropzone(String userAgent) {
		Matcher match = MATCH_MSIE_VERSION.matcher(userAgent);
		if(match.find()) {
			int ieVersion = Integer.valueOf(match.group(1));
			return (ieVersion >= 10); // Only IE10+ supports drag-n-drop.
		}
		return true; // Assume all non-IE browsers support drag-n-drop.
	}

	/**
	 * Fix map key quotes.  Change path[key] to path["key"]
	 *
	 * @param path the fixed path
	 * @return the input path
	 */
	protected String fixMapKeyQuotes(String path) {
		Matcher matcher = KEY_WITH_MISSING_QUOTES.matcher(path);
		return matcher.replaceAll("[\"$1\"]");
	}

	/**
	 * Gets the field info.
	 *
	 * @return the field info
	 */
	public FieldInfo getFieldInfo() {
		return fieldInfo;
	}

	/**
	 * Sets the field info.
	 *
	 * @param fieldInfo the new field info
	 */
	public void setFieldInfo(FieldInfo fieldInfo) {
		this.fieldInfo = fieldInfo;
	}

	/**
	 * Gets the message source.
	 *
	 * @return the message source
	 */
	public MessageSource getMessageSource() {
		return messageSource;
	}

	/**
	 * Sets the message source.
	 *
	 * @param messageSource the new message source
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * Gets the default validator.
	 *
	 * @return the default validator
	 */
	public Validator getDefaultValidator() {
		return defaultValidator;
	}

	/**
	 * Sets the default validator.
	 *
	 * @param defaultValidator the new default validator
	 */
	public void setDefaultValidator(Validator defaultValidator) {
		this.defaultValidator = defaultValidator;
	}

}
