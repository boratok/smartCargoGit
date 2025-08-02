package com.boratok.service.impls;

import com.boratok.entity.DeliveryPoint;
import com.boratok.repository.DeliveryPointRepository;
import com.boratok.service.IDeliveryPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryPointService implements IDeliveryPointService {

    @Autowired
    private DeliveryPointRepository deliveryPointRepository;

    @Override
    public DeliveryPoint saveDeliveryPoint(DeliveryPoint deliveryPoint) {
        DeliveryPoint deliveryPointt = new DeliveryPoint();
        deliveryPointt.setDeliveryPoint(deliveryPoint.getDeliveryPoint());
        if (deliveryPoint.getDeliveryPoint() != null)
            deliveryPointRepository.save(deliveryPoint);
        return deliveryPoint;
    }
}
