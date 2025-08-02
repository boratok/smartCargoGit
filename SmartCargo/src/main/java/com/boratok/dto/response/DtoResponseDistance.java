package com.boratok.dto.response;

import com.boratok.dto.request.DtoBase;
import com.boratok.entity.DeliveryPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoResponseDistance  {

    private DeliveryPoint startPointId;

    private DeliveryPoint finishPointId;

    private Double distanceBtw;

}
