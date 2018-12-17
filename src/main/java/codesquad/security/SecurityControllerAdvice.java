package codesquad.security;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import support.domain.ErrorMessage;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice(annotations = Controller.class)
public class SecurityControllerAdvice {
    private static final Logger log = LoggerFactory.getLogger(SecurityControllerAdvice.class);

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void emptyResultData() {
        log.debug("EntityNotFoundException is happened!");
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public void unAuthorized() {
        log.debug("UnAuthorizedException is happened!");
    }

    @ExceptionHandler(UnAuthenticationException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public String unAuthentication() {
        log.debug("UnAuthenticationException is happened!");
        return "/user/login";
    }

//    @ExceptionHandler(CannotDeleteException.class)
//    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
//    public ErrorMessage CannotDeleteException(CannotDeleteException e) {
//        log.debug("CannotDeleteException is happened!");
//        return new ErrorMessage(e.getMessage());
//    }

    @ExceptionHandler(CannotDeleteException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public String CannotDeleteException(CannotDeleteException e) {
        log.debug("CannotDeleteException is happened!");
        return "redirect:/";
    }
}
