package com.boratok.controller;

import com.boratok.entity.Distance;
import com.boratok.repository.DistanceRepository;
import com.boratok.service.impls.DistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/api/distance")
public class DistanceController {

    @Autowired
    private DistanceService distanceService;

    @GetMapping("/get/{id}")
    private Distance getDistanceById(@PathVariable Long id){
        return distanceService.getDistanceById(id);
    }
}
