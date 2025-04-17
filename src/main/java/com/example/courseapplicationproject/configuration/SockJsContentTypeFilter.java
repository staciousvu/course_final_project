//package com.example.courseapplicationproject.configuration;
//
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//
//
//import javax.servlet.*;
//import java.io.IOException;
//
//@Component
//public class SockJsContentTypeFilter implements Filter {
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, javax.servlet.ServletException {
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//        String requestURI = ((javax.servlet.http.HttpServletRequest) request).getRequestURI();
//
//        if (requestURI.contains("/ws/iframe.html")) {
//            httpResponse.setContentType("text/html");
//        } else if (requestURI.contains("/ws/") && requestURI.endsWith("/jsonp")) {
//            httpResponse.setContentType("application/javascript");
//        } else if (requestURI.contains("/ws/") && requestURI.contains("/eventsource")) {
//            httpResponse.setContentType("text/event-stream");
//        }
//
//        chain.doFilter(request, response);
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//}