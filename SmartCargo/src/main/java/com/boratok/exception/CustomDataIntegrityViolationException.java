package com.boratok.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class CustomDataIntegrityViolationException extends DataIntegrityViolationException {

    public CustomDataIntegrityViolationException() {
        super("veri bütünlüğü hatası");
    }

    public CustomDataIntegrityViolationException(ErrorMessage msg) {
        super(msg.prepareErrorMessage());
    }
}
