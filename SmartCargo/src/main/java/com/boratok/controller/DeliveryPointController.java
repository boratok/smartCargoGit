package com.boratok.controller;

import com.boratok.entity.DeliveryPoint;
import com.boratok.service.IDeliveryPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rest/api/delivery_point")
public class DeliveryPointController {

    @Autowired
    private IDeliveryPointService deliveryPointService;

    @PostMapping(path = "/save")
    private DeliveryPoint saveDeliveryPoint(@RequestBody DeliveryPoint DeliveryPoint) {
        return deliveryPointService.saveDeliveryPoint(DeliveryPoint);
    }
}
