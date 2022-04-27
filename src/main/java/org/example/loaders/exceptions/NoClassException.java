package org.example.loaders.exceptions;

public class NoClassException extends Exception{
    public NoClassException() {
        System.err.println(".class file was not found in directory");
    }
}
