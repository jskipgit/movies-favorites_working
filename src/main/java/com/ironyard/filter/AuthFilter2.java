package com.ironyard.filter;

import com.ironyard.data.MovieUser;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by jasonskipper on 2/7/17.
 */
@WebFilter(filterName = "AuthFilter2")
public class AuthFilter2 implements Filter {
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = ((HttpServletRequest) request);
        HttpServletResponse resp = ((HttpServletResponse) response);
        // check session
        MovieUser usr = (MovieUser) req.getSession().getAttribute("user");
        boolean authorized = (usr != null);
        if(authorized) {
            chain.doFilter(request, response);
        }else{
            resp.sendRedirect("/open/login.jsp");
        }

    }


    public void init(FilterConfig config) throws ServletException {

    }

}
