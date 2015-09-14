package com.psu.hpa.application;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Redirect to the current URL plus a trailing slash. */
public class TrailingSlashFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;

		String destination = httpRequest.getRequestURI().toString() + "/";
		httpResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		httpResponse.setHeader("Location", destination);
	}

	@Override
	public void destroy() {
	}
}