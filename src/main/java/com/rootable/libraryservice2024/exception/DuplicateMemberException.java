package com.rootable.libraryservice2024.exception;

public class DuplicateMemberException extends RuntimeException {

    public DuplicateMemberException() {
    }

    public DuplicateMemberException(String message) {
        super(message);
    }

    public DuplicateMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateMemberException(Throwable cause) {
        super(cause);
    }

}
