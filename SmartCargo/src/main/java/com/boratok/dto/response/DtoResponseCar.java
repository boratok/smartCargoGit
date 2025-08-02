package com.boratok.dto.response;

import com.boratok.dto.request.DtoBase;
import com.boratok.entity.CarType;
import com.boratok.entity.EventLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoResponseCar {
    private String licensePlate;

    private EventLog status;

    private CarType carType;

}
