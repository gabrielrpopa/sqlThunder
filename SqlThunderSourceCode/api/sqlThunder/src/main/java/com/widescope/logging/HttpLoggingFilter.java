package com.widescope.logging;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.servlet.*;
import java.io.IOException;

public class HttpLoggingFilter implements Filter {
    private static final Logger PERF_LOG = LoggerFactory.getLogger("PERF");
    private static final Marker PERF_MARKER = MarkerFactory.getMarker("PERF");

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {


        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {

        }

    }


    private static class HttpStatusResponse extends HttpServletResponseWrapper {

        public HttpStatusResponse(HttpServletResponse response) {
            super(response);
        }
    }


}
