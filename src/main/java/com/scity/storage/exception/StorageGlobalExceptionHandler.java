package com.scity.storage.exception;

import com.scity.storage.model.dto.ResModel;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
public class StorageGlobalExceptionHandler {

  @ExceptionHandler(value = Exception.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public ResModel<String> handleException(Exception e) {
    log.debug("Exception: " + e.getMessage() + " with type: " + e.getClass());
    return new ResModel<>(1500, e.getMessage());
  }

  @ExceptionHandler(value = ServletException.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public ResModel<String> handleServletException(ServletException e) {
    log.debug("Exception: " + e.getMessage() + " with type: " + e.getClass());
    return new ResModel<>(1517, e.getMessage());
  }

  @ExceptionHandler(value = MaxUploadSizeExceedException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResModel<String> handleMaxUploadSizeExceedException(
      MaxUploadSizeExceedException e) {
    log.debug("Exception: " + e.getMessage() + " with type: " + e.getClass());
    return new ResModel<>(1515, e.getMessage());
  }

  @ExceptionHandler(value = MissingServletRequestPartException.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public ResModel<String> handleMissingServletRequestPartException(Exception e) {
    log.debug("Exception: " + e.getMessage() + " with type: " + e.getClass());
    return new ResModel<>(1510, e.getMessage());
  }

  @ExceptionHandler(value = MultipartException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResModel<String> handleMultipartException(Exception e) {
    log.debug("Exception: " + e.getMessage() + " with type: " + e.getClass());
    return new ResModel<>(1511, e.getMessage());
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResModel<String> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    FieldError error = ex.getBindingResult().getFieldError();
    String fieldName = error.getField();
    String errorMessage = error.getDefaultMessage();
    return new ResModel<>(1512, fieldName + ": " + errorMessage);
  }

  @ExceptionHandler(value = IllegalArgumentException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResModel<String> handleIllegalArgumentException(Exception e) {
    log.debug("Exception: " + e.getMessage() + " with type: " + e.getClass());
    return new ResModel<>(1505, e.getMessage());
  }

  @ExceptionHandler(value = InvalidFileTypeException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResModel<String> handleInvalidFileTypeException(Exception e) {
    log.debug("Exception: " + e.getMessage() + " with type: " + e.getClass());
    return new ResModel<>(1507, e.getMessage());
  }

  @ExceptionHandler(value = MaxUploadSizeExceededException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResModel<String> handleMaxUploadSizeExceededException(Exception e) {
    log.debug("Exception: " + e.getMessage() + " with type: " + e.getClass());
    return new ResModel<>(1509, e.getMessage());
  }

  @ExceptionHandler(value = EntityNotFoundException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public ResModel<String> handleNotFoundException(EntityNotFoundException e) {
    return new ResModel<>(1501, e.getMessage());
  }

  @ExceptionHandler(value = HttpMessageNotReadableException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResModel<String> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    return 
        new ResModel<>(1513,"Invalid request body");
  }

  @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ResModel<String> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    return new ResModel<>(1514, "Invalid Id");
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResModel<String> handlerRequestException(NotFoundException ex) {
    return new ResModel<>(HttpStatus.NOT_FOUND.value(), ex.getMessage());
  }

  @ExceptionHandler({
          BadRequestException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResModel<String> handlerRequestException(BadRequestException ex) {
    return new ResModel<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
  }

  @ExceptionHandler({
          AuthenticationException.class,
  })
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResModel<String> unauthorizedException(AuthenticationException ex) {
    return new ResModel<>(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
  }

  @ExceptionHandler({ForbiddenException.class})
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResModel<String> forbiddenException(ForbiddenException ex) {
    return new ResModel<>(HttpStatus.FORBIDDEN.value(), ex.getMessage());
  }
}
