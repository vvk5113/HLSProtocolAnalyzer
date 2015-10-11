package com.psu.hpa.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.psu.hpa.ErrorMailSender;
import com.psu.hpa.ImageProcessingException;
import com.psu.hpa.UploadNotFoundException;
import com.psu.hpa.application.FieldInfo;
import com.psu.hpa.application.GUID;
import com.psu.hpa.models.ErrorType;
import com.psu.hpa.models.UploadModel;
import com.psu.hpa.util.CommonUtils;
import com.psu.hpa.validators.HPABindingErrorProcessor;
import com.psu.hpa.validators.MasterPlaylistValidator;
import com.psu.hpa.validators.MediaPlaylistValidator;

@Controller
@SessionAttributes(MainController.FORM_NAME)
@RequestMapping("/{id}")
public class MainController extends SessionUploadFormController {
	private Logger log = LoggerFactory.getLogger(getClass());

	/** The standard date format. */
	public static final String DATE_FORMAT = "MM/dd/yyyy";

	/** Regex to match a map key with missing quotes around its contents. */
	public static final Pattern KEY_WITH_MISSING_QUOTES = Pattern.compile("\\[([^\"]+)\\]");

	/** Template (flash) attribute for a binding result with errors. */
	public static final String MODEL_BINDING_RESULT = "bindingResult";

	/** Template (flash) attribute for a list of error messages. */
	public static final String MODEL_ERRORS = "errors";

	/** Model attribute for field info lookup. */
	public static final String MODEL_FIELD_INFO = "fieldInfo";

	/** Model attribute for GUID. */
	public static final String MODEL_GUID = "guid";

	/** Model attribute for the current upload model data. */
	public static final String MODEL_UPLOAD = "upload";

	/** Relative URL of this page. */
	public static final String RELATIVE_URL = "upload";

	/** Relative URL of the results page. */
	public static final String RESULTS_URL = "results";
	
	/** Relative URL of the results page. */
	public static final String ERROR_PAGE = "internal_server_error.jsp";
		
	/** The default validator. */
	@Autowired
	private Validator defaultValidator;

	/** The field info lookup tool. */
	@Autowired
	private FieldInfo fieldInfo;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;
	
	/** The play list validator. */
	@Autowired
	private MasterPlaylistValidator masterPlaylistValidator;
	
	/** The play list validator. */
	//@Autowired
	//private MediaPlaylistValidator mediaPlaylistValidator;

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
		if(MainController.getForm(session).getCompleted().contains(id)) {
			return "redirect:" + RESULTS_URL;
		}

		model.put(MODEL_GUID, id.urlRepresentation);
		model.put(MODEL_FIELD_INFO, getFieldInfo());

		return "upload";
	}
	
	@RequestMapping(value = "/download", method = RequestMethod.GET)
    public String download(
				HttpSession session,
				ModelMap model,
				@PathVariable("id") GUID id,
				@ModelAttribute(MODEL_UPLOAD) UploadModel upload,
				@RequestHeader("User-Agent") String userAgent,
				HttpServletResponse response) {
		
		String masterPlaylistValidationErrorFile = session.getServletContext().getRealPath("/"+id+"/hls-stream-validation-result/MasterPlaylistValidationResult.csv");
		String mediaPlaylistValidationErrorFile = session.getServletContext().getRealPath("/"+id+"/hls-stream-validation-result/MediaPlaylistValidationResult.csv");
		String validationResultsArchiveFile = session.getServletContext().getRealPath("/"+id+"/hls-stream-validation-result/HLSValidationResults.zip");
		String[] ValidationResultFiles = {masterPlaylistValidationErrorFile, mediaPlaylistValidationErrorFile};
		
		compress(ValidationResultFiles, validationResultsArchiveFile);
		
        File file = new File (validationResultsArchiveFile);
        
        try {
        	InputStream fileInputStream = new FileInputStream(file);
            OutputStream output = response.getOutputStream();
            response.reset();
            response.setContentType("application/octet-stream");
            response.setContentLength((int) (file.length()));
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            IOUtils.copyLarge(fileInputStream, output);
            output.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return "download";
    }
	

    public void compress(String[] ValidationResultFiles, String validationResultsArchiveFile) {
      try {
    	  	byte[] buffer = new byte[1024];
            FileOutputStream fos = new FileOutputStream(validationResultsArchiveFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (int i=0; i < ValidationResultFiles.length; i++) {
                File srcFile = new File(ValidationResultFiles[i]);
                FileInputStream fis = new FileInputStream(srcFile);
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
               fis.close();
            }
            zos.close();
        }
        catch (IOException ioe) {
        	log.error("Error creating zip file: " + ioe);
       }
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
			RedirectAttributes redirectAttrs) throws Exception {
		
		// Redirect back to this form on error.
		View errorRedirect = discardBadFieldsOrGetErrorRedirect(request, upload, result, redirectAttrs);
		if(errorRedirect != null) {
			return errorRedirect;
		}
		
		long masterPlaylistErrSeqNumber = 1;
		
		FileWriter masterPlaylistValidationErrorFileWriter = null;
		FileWriter mediaPlaylistValidationErrorFileWriter = null;
		MediaPlaylistValidator mediaPlaylistValidator = new MediaPlaylistValidator();
		
		try{
			
			String dirPath = session.getServletContext().getRealPath("/"+id+"/hls-stream-validation-result");
			File fileDir = new File(dirPath);
			fileDir.mkdirs();
			
			String masterPlaylistValidationErrorFile = session.getServletContext().getRealPath("/"+id+"/hls-stream-validation-result/MasterPlaylistValidationResult.csv");
			String mediaPlaylistValidationErrorFile = session.getServletContext().getRealPath("/"+id+"/hls-stream-validation-result/MediaPlaylistValidationResult.csv");
			masterPlaylistValidationErrorFileWriter = new FileWriter(masterPlaylistValidationErrorFile);
			mediaPlaylistValidationErrorFileWriter = new FileWriter(mediaPlaylistValidationErrorFile);
			CommonUtils.writeCSVFileHeaders(masterPlaylistValidationErrorFileWriter, "Error Number", "Error Type", "File Name", "Error Details");
			CommonUtils.writeCSVFileHeaders(mediaPlaylistValidationErrorFileWriter, "Error Number", "Error Type", "File Name", "Error Details");
	
			String masterPlaylistStreamURL = upload.getStreamURL();		
			String masterStreamURI = masterPlaylistStreamURL.substring(0, masterPlaylistStreamURL.lastIndexOf("/")+1);
			String masterPlaylistName = masterPlaylistStreamURL.substring(masterPlaylistStreamURL.lastIndexOf("/")+1, masterPlaylistStreamURL.length());
			List<String> masterPlaylistContents = null;

			try {
				masterPlaylistContents = CommonUtils.readFile(new URL(masterPlaylistStreamURL));
			} catch(FileNotFoundException fnfe) {
				String errorDetails = "Master playlist file "+masterPlaylistName+" is missing";
				CommonUtils.writeToCSVFile(masterPlaylistValidationErrorFileWriter, masterPlaylistErrSeqNumber++, ErrorType.MISSING_SEGMENT_FILE, masterPlaylistName, errorDetails);
				fnfe.printStackTrace();
			} catch(Exception ex) {
				ex.printStackTrace();
				new RedirectView(ERROR_PAGE);
			}
			
			log.info("******** Starting validation of the master playlist "+masterPlaylistName+" *********");
			
			if(CollectionUtils.isNotEmpty(masterPlaylistContents)) {
				masterPlaylistValidator.validate(masterPlaylistContents, masterPlaylistValidationErrorFileWriter, masterPlaylistName);
				for(String lineContent : masterPlaylistContents) {
					if(!lineContent.startsWith("#") && lineContent.endsWith(".m3u8")) {
						String mediaPlayListStreamURL = masterPlaylistStreamURL.replace(masterPlaylistName, lineContent);
						List<String> mediaPlaylistContents = null;
						try {
							mediaPlaylistContents = CommonUtils.readFile(new URL(mediaPlayListStreamURL));
						} catch(FileNotFoundException fnfe) {
							String errorDetails = "Media playlist file "+lineContent+" is missing";
							CommonUtils.writeToCSVFile(masterPlaylistValidationErrorFileWriter, masterPlaylistErrSeqNumber++, ErrorType.MISSING_SEGMENT_FILE, masterPlaylistName, errorDetails);
							fnfe.printStackTrace();
						} catch(Exception ex) {
							ex.printStackTrace();
							new RedirectView(ERROR_PAGE);
						}
						if(CollectionUtils.isNotEmpty(mediaPlaylistContents)) {
							mediaPlaylistValidator.validate(mediaPlaylistContents, mediaPlaylistValidationErrorFileWriter, lineContent, masterStreamURI);
						}
					}
				}	
			
				log.info("******** Validating of the master playlist ends *********");
								
				masterPlaylistValidationErrorFileWriter.flush();
				masterPlaylistValidationErrorFileWriter.close();
				
				mediaPlaylistValidationErrorFileWriter.flush();
				mediaPlaylistValidationErrorFileWriter.close();
				
				if(StringUtils.isNotBlank(upload.getUserEmail())) {
					String[] attachFiles = new String[2];
			        attachFiles[0] = masterPlaylistValidationErrorFile;
			        attachFiles[1] = mediaPlaylistValidationErrorFile;

			       	ErrorMailSender.sendEmail(upload.getUserEmail(), attachFiles);
				}
				
				upload.setMasterPlaylistValidationResult(masterPlaylistValidationErrorFile);
				upload.setMediaPlaylistValidationResult(mediaPlaylistValidationErrorFile);
				
				upload.setGuid(id.urlRepresentation);
				
				return new RedirectView(RESULTS_URL);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return new RedirectView(ERROR_PAGE);
		} 

		result.reject("GenericError");
		return discardBadFieldsOrGetErrorRedirect(request, upload, result, redirectAttrs);
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
