package model;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

@WebFilter(filterName = "SessionFilter", urlPatterns = {
    "/admin-sign-in.html",
    "/admin-verification.html"
})
public class AdminSessionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false); // Don't create new session

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Expires", "0");

        String uri = req.getRequestURI();

        if (session == null || session.getAttribute("admin") == null) {

            if (uri.endsWith("admin-verification.html")) {
                res.sendRedirect("admin-sign-in.html");
                return;
            }

            chain.doFilter(request, response);
            return;
        }

        Boolean verified = (Boolean) session.getAttribute("verified");

        if (Boolean.TRUE.equals(verified)) {

            res.sendRedirect("admin-panel.html");
            return;
        } else {

            if (uri.endsWith("admin-sign-in.html")) {
                res.sendRedirect("admin-verification.html");
                return;
            } else {
                chain.doFilter(request, response);
                return;
            }
        }
    }

    @Override
    public void destroy() {
    }
}
