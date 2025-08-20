package model;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns = {"/admin-panel.html"})
public class AdminSignInCheck implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("admin") != null) {
            Boolean verified = (Boolean) session.getAttribute("verified");

            if (Boolean.TRUE.equals(verified)) {
           
                chain.doFilter(req, res);
                return;
            } else {
         
                response.sendRedirect("admin-verification.html");
                return;
            }
        }


        response.sendRedirect("admin-sign-in.html");
    }

    @Override
    public void destroy() {
    }
}
