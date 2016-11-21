package dn.stm.shop.beans;

import dn.stm.shop.utils.SessionUtils;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class Login implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String CORRECT_USER = "1";
    private final String CORRECT_PASSWORD = "1";

    private String user;    // user
    private String pwd;     // password
    private String msg;     // error message

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * Validate user credentials
     *
     * @return administration page if validation success, otherwise - login page
     */
    public String validateUsernamePassword() {
        boolean valid = (CORRECT_USER.equals(user) && CORRECT_PASSWORD.equals(pwd));
        if (valid) {
            SessionUtils.setLogged(true);
            return "admin";
        } else {            
            FacesContext.getCurrentInstance().addMessage(
                    "loginForm:password",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "loginForm:password", 
                            "Неправильное имя или пароль. Попробуйте еще раз"));
            return "login";
        }
    }
}
