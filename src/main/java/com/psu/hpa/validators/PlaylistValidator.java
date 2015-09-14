package com.psu.hpa.validators;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public interface PlaylistValidator {
	
	public void validate(List<String> contentList, StringBuilder sb, String mediaPlaylistURI);

}
