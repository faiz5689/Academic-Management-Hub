// InvalidProfessorOperationException.java
package com.academichub.academic_management_hub.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidProfessorOperationException extends BaseException {
    public InvalidProfessorOperationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}