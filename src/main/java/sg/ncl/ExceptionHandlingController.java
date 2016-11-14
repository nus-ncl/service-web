package sg.ncl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import sg.ncl.testbed_interface.LoginForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Created by dcsyeoty on 10-Nov-16.
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandlingController {

    // global exception handler for 401 unauthorized
    // the model and view follows the MainController "/login"
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ModelAndView handleAppException(HttpServletRequest request, Exception ex, HttpSession session) {
        log.info("Request: {} raised: {} ...session invalidating", request.getRequestURL(), ex.getClass().getName());
        session.invalidate();

        LoginForm loginForm = new LoginForm();
        if (ex.getClass() == NullPointerException.class) {
            // for others, ex will be null
            loginForm.setErrorMsg("You must be logged on");
        } else {
            // for 401, ex class is some json exception error, e.g. JSON['XXX'] not found
            loginForm.setErrorMsg("Your session has expired. Please login again.");
        }
        return new ModelAndView("login", "loginForm", loginForm);
    }
}
