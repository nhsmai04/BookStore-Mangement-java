// File: NotBorrowedException.java
package org.librarymanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // 403 Forbidden
public class NotBorrowedException extends RuntimeException {
    public NotBorrowedException(String message) {
        super(message);
    }
}
