package com.boratok.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DtoRequestPackage extends DtoBase {

    private Double weight;

    private Double packageSize;

    private Long deliveryPointId;

    private Long routeAssignmentId;

    private Long status;

    private Long orderNumber;
}
