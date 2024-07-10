package com.jsp.onlineshoppingapplication.securityfilters;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsp.onlineshoppingapplication.util.ErrorStructure;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletResponse;

public class FilterHelper {

	
	public static void handleException(HttpServletResponse httpServletResponse, String message,String rootcause) throws IOException, StreamWriteException, DatabindException, java.io.IOException{
		httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
		
		ErrorStructure error = new ErrorStructure<>()
		               .setStatus(HttpStatus.UNAUTHORIZED.value())
		               .setMessage("Failed to Authenticate")
		               .setRootCause("The token is already expired");
		               
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(httpServletResponse.getOutputStream(), error);
	}
	

}
