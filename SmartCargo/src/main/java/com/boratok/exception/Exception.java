package com.boratok.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Exception<E> {
    // <E> nedir ?Generic metotlar, belirli bir veri türüne bağlı kalmadan çalışabilen esnek metotlardır.
    // Bu sayede, aynı metodu farklı türlerdeki verilerle tekrar tekrar kullanabilirsiniz.
    private String hostName;

    private String path;

    private LocalDateTime createTime;

    private E message;
}
