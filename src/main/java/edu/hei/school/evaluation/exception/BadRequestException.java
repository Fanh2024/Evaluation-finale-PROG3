package edu.hei.school.evaluation.exception;

/**
 * Exception levée lorsqu'une requête est invalide.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String msg) {
        super(msg);
    }
}