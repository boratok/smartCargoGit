package com.boratok.controller;

import com.boratok.dto.request.DtoRequestCar;
import com.boratok.dto.response.DtoResponseCar;
import com.boratok.entity.CarType;
import com.boratok.service.ICarService;
import com.boratok.service.ICarTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/carType")
public class CarTypeController {

    @Autowired
    private ICarTypeService carTypeService;

    @PostMapping(name = "/save")
    private CarType saveCarType(@RequestBody CarType carType){
        return carTypeService.saveCarType(carType);
    }

    @GetMapping(path = "/get/{id}")
    private CarType getCarTypeById(@PathVariable(name = "id") Long id) {
        return carTypeService.getCarType(id);
    }

    @DeleteMapping(path = "/delete/{id}")
    private boolean deleteCarTypeById(@PathVariable(name = "id") Long id) {
        return carTypeService.deleteCarType(id);
    }

    @PutMapping(path = "/update/{id}")
    private CarType updateCarType(@PathVariable(name = "id") Long id, @RequestBody CarType carType) {
        return carTypeService.updateCarType(id, carType);
    }
}
