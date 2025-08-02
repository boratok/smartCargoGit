package com.boratok.controller;

import com.boratok.dto.request.DtoRequestCar;
import com.boratok.dto.response.DtoResponseCar;
import com.boratok.service.ICarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/car")
public class CarController {

    @Autowired
    private ICarService carService;

    @PostMapping("/save")
    private com.boratok.dto.request.DtoRequestCar saveCar(@RequestBody com.boratok.dto.request.DtoRequestCar dtoRequestCar) {
        return carService.saveCar(dtoRequestCar);
    }

    @GetMapping(path = "/get/{id}")
    private DtoResponseCar getCarById(@PathVariable(name = "id") Long id) {
        return carService.findCarById(id);
    }

    @DeleteMapping(path = "/delete/{id}")
    private boolean deleteCarById(@PathVariable(name = "id") Long id) {
        return carService.deleteCar(id);
    }

    @PutMapping(path = "/update/{id}")
    private DtoResponseCar updateCar(@PathVariable(name = "id") Long id, @RequestBody DtoRequestCar dtoRequestCar) {
        return carService.updateCar(id, dtoRequestCar);
    }

}
