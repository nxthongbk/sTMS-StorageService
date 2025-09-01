package com.scity.storage.exception;

public class InvalidFileTypeException extends RuntimeException {

  public InvalidFileTypeException(String message) {
    super(message);
  }

  public InvalidFileTypeException() {
    super("Invalid file type");
  }
}
