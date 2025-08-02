package com.boratok.dto.response;

import com.boratok.entity.DeliveryPoint;
import com.boratok.entity.EventLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DtoResponsePackage {

    private Double weight;

    private Double width;

    private Double lenght;

    private Double height;

    private EventLog status;

    private DeliveryPoint deliveryPoint;

    private DtoResponseRouteAssgmnt responseRouteAssgmnt;

    private Integer orderNumber;


}
