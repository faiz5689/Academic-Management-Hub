package com.academichub.academic_management_hub.exceptions;

import org.springframework.http.HttpStatus;
import java.util.UUID;

public class EntityNotFoundException extends BaseException {
    public EntityNotFoundException(String entityName, UUID identifier) {
        super(
            String.format("%s not found with identifier: %s", entityName, identifier),
            HttpStatus.NOT_FOUND
        );
    }

    public EntityNotFoundException(String entityName, String identifier) {
        super(
            String.format("%s not found with identifier: %s", entityName, identifier),
            HttpStatus.NOT_FOUND
        );
    }
}