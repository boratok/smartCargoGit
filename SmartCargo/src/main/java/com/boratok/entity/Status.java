package com.boratok.entity;

public enum Status {
    IN_STORAGE(1L, "DEPODA"),
    PACKAGE_ON_ROAD(2L, "PAKET YOLA ÇIKTI"),
    PACKAGE_DELIVERED(3L, "PAKET TESLİM EDİLDİ"),
    AVAILABLE(4L, "MÜSAİT"),
    NOT_AVAILABLE(5L, "MÜSAİT DEĞİL"),
    VEHICLE_FULL(6L, "ARAÇ DOLDU"),
    LOADED_TO_VEHICLE(7L, "ARACA YÜKLENDİ"),
    VEHICLE_ON_ROAD(8L, "ARAÇ YOLA ÇIKTI"),
    COMPLETED(9L, "TAMAMLANDI");

    private Long id;
    private String status;

    Status(Long id, String status) {
        this.id = id;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
}
