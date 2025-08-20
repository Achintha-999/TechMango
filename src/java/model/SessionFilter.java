package model;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

@WebFilter(urlPatterns = {"/sign-in.html", "/sign-up.html"})
public class SessionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession ses = request.getSession(false);

        if (ses != null && ses.getAttribute("user") != null) {
          
            response.sendRedirect("index.html");
        } else {
          
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {
     
    }
}
