package com.boratok.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@RestControllerAdvice// exceptionları yakalamak için bunu kullanıyoruz
// Bu anotasyon, bu sınıfın uygulamanın tamamında istisna yakalama (exception handling) yeteneğine sahip olduğunu belirtir.
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ApiError> handleBaseException(BaseException exception, WebRequest webRequest) {
        return ResponseEntity.badRequest().body(createApiError(exception.getMessage(), webRequest));
    }

    private String getHostName() {
        try {
            InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.out.println("hata olustu " + e.getMessage());
        }
        return null;
    }

    public <E> ApiError<E> createApiError(E message, WebRequest webRequest) {
        ApiError<E> apiError = new ApiError<>();
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());

        Exception<E> exception = new Exception<>();
        exception.setCreateTime(LocalDateTime.now());
        exception.setHostName(getHostName());
        exception.setPath(webRequest.getDescription(false));//web request ve buradaki yazdıklarım bana path i dönmesini sağlıyor
        exception.setMessage(message);
        apiError.setException(exception);

        return apiError;
    }
}
