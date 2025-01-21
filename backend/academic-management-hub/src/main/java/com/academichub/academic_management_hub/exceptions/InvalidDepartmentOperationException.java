// InvalidDepartmentOperationException.java
package com.academichub.academic_management_hub.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidDepartmentOperationException extends BaseException {
    public InvalidDepartmentOperationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
