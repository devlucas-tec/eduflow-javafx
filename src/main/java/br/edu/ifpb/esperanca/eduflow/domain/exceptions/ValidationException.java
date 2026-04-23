package br.edu.ifpb.esperanca.eduflow.domain.exceptions;

public class ValidationException extends RuntimeException {
  public ValidationException(String message) {
    super(message);
  }
}
