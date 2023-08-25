package com.symptomaster.ws.controller.filters;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by Nikolay Kormshchikov.
 * Date: 3/3/2017
 * Time: 12:48 PM
 */
public class EncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
