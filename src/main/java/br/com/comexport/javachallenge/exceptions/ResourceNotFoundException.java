package br.com.comexport.javachallenge.exceptions;

public class ResourceNotFoundException  extends  RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
