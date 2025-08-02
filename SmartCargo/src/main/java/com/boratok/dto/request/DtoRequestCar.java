package com.boratok.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoRequestCar extends DtoBase {

    private String licensePlate;

    private Long status;

    private Long carType;

}
