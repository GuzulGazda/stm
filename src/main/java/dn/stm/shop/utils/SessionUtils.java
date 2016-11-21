package dn.stm.shop.utils;

import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {

    private static final String LOGGED_IN = "loggedIn";

    /**
     * get current HTTP Session
     *
     * @return current HttpSession object
     */
    public static synchronized HttpSession getSession() {
        return (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
    }

    /**
     * check if current use is logged in
     *
     * @param request - ServletRequest object of current user
     * @return true if user is logged in, false otherwise
     */
    public static synchronized boolean isLogged(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            if (session.getAttribute(LOGGED_IN) != null) {
                if (((boolean) session.getAttribute(LOGGED_IN))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * set LOGGED_IN attribute for current user session
     *
     * @param isLogged - attribute value to set
     */
    public static synchronized void setLogged(boolean isLogged) {
        HttpSession session = getSession();
        if (session != null) {
            session.setAttribute(LOGGED_IN, isLogged);
        }
    }
}
