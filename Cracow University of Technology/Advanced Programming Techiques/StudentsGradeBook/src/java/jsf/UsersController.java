package jsf;

import entities.Users;
import sessb.UsersFacade;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import sessb.SessionUtils;

//https://www.journaldev.com/7252/jsf-authentication-login-logout-database-example
@Named("usersController")
@SessionScoped
public class UsersController implements Serializable {

    private Users current;
    
    @EJB
    private sessb.UsersFacade ejbFacade;

    private String pwd;
    private String msg;
    private String user;

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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String validateUsernamePassword() {
        List<Users> users = getFacade().findAll();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUname().equals(user) && users.get(i).getPassword().equals(pwd)) {
                HttpSession httpSession = SessionUtils.getSession();
                httpSession.setAttribute("username", user);
                return "index";
            }
        }
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Incorrect Username and Passowrd",
                        "Please enter correct username and Password"));

        return "login";
    }
    
    public String logout() {
        HttpSession session = SessionUtils.getSession();
        session.invalidate();
        return "login";
    }

    public UsersController() {
    }

    private UsersFacade getFacade() {
        return ejbFacade;
    }
}
