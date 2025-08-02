package com.boratok.service.impls;

import com.boratok.entity.Distance;
import com.boratok.repository.DistanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DistanceService {

    @Autowired
    private DistanceRepository distanceRepository;

    public Distance getDistanceById(Long id) {
        Optional<Distance> optDistance = distanceRepository.findById(id);
        Distance distance = new Distance();
        distance.setStartPoint(optDistance.get().getStartPoint());
        distance.setFinishPoint(optDistance.get().getFinishPoint());
        distance.setDistanceBtw(optDistance.get().getDistanceBtw());
        return distance;

    }
}
