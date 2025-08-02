package com.boratok.dto.response;

import com.boratok.dto.request.DtoBase;
import com.boratok.entity.DeliveryPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DtoResponseRouteAssgmnt{

    private DeliveryPoint deliveryPoint;
    private Date startDate;
    private Date finishDate;
    private DtoResponseDriver driver;
    private DtoResponseCar car;
    private List<DtoResponsePackage> cargoPackage;

}
