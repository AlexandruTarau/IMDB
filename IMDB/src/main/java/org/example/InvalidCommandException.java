package org.example;

public class InvalidCommandException extends Exception {
    public InvalidCommandException(String message) {
        super(message);
    }
    public InvalidCommandException() {
        super("Invalid command provided.");
    }
}
