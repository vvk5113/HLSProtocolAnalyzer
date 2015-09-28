package com.psu.hpa.validators;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public interface PlaylistValidator {
	
	public void validate(List<String> contentList, FileWriter fileWriter, String mediaPlaylistURI) throws IOException;

}
