package com.boratok.exception;
import lombok.Data;
@Data
public class BaseException extends RuntimeException {
    public BaseException(String string) {
    }
    public BaseException(ErrorMessage errorMessage) {
        super(errorMessage.prepareErrorMessage());
    }
}
