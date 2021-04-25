package by.azgaar.storage.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ResponseBody
	@ExceptionHandler
	public String exceptionHandlerBadRequest(Exception e) {
		log.error("Exception occurred:", e);
		return e.getMessage();
	}

}