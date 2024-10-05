package server.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import server.exceptions.TagNotFoundException;

@ControllerAdvice
public class TagNotFoundAdvice {

    /**
     * This handler returns an HTTP response with the tag as body when a TagNotFoundException is thrown
     * The header of the HTTP response is set as 404 NOT_FOUND
     * @param e TagNotFoundException
     * @return The tag that is not found in the repository
     */
    @ResponseBody
    @ExceptionHandler(TagNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    long tagNotFoundHandler(TagNotFoundException e) {
        return e.getTag();
    }
}
