package dn.stm.shop.filters;

import dn.stm.shop.utils.SessionUtils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = {"/admin.xhtml", "/DataReadServlet", "/DataSaveServlet"})
public class AuthorizationFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AuthorizationFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponce = (HttpServletResponse) response;

            if (SessionUtils.isLogged(request)) {
                chain.doFilter(request, response);
            } else {
                httpResponce.sendRedirect(httpRequest.getContextPath() + "/login.xhtml");
            }
        } catch (IOException | ServletException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

}
