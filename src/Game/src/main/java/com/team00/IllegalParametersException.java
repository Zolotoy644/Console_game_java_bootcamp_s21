package com.team00;

public class IllegalParametersException extends Exception {
    public IllegalParametersException(String message) {
        super(message);
        System.exit(-1);
    }
}
