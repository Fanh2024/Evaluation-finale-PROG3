package edu.hei.school.evaluation.exception;

/**
 * Exception levée lorsqu'une ressource est introuvable.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg) {
        super(msg);
    }
}