package br.com.comexport.javachallenge.exceptions;

public class ResourceAlreadyUpdatedException extends RuntimeException {
    public ResourceAlreadyUpdatedException(String message) {
        super(message);
    }
}
