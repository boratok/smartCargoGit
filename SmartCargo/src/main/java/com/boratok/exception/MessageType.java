package com.boratok.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum MessageType {

    NO_RECORD_EXIST("1001", "kayit bulunmadi"),
    DUPLICATE_RECORD("1005", "Aynı veri daha önce eklenmiş"),
    UNEXPECTED_NULL("1006", "Beklenmeyen null değeri"),
    UNKNOWN_ERROR("9999", "Bilinmeyen bir hata olustu");

    private String code;
    private String message;

    MessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
