package com.boratok.controller;

import com.boratok.dto.request.DtoRequestPackage;
import com.boratok.dto.response.DtoResponsePackage;
import com.boratok.service.IPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/package")
public class CargoPackageController {

    @Autowired
    private IPackageService packageService;

    @PostMapping("/save")
    private boolean savePackage(@RequestBody DtoRequestPackage dtoRequestPackage) {
        return packageService.savePackage(dtoRequestPackage);
    }

    @GetMapping("/get/{id}")
    private DtoResponsePackage getPackegeInfoById(@PathVariable(name = "id") Long id) {
        return packageService.getPackageInfoById(id);
    }

    @DeleteMapping(path = "/delete/{id}")
    private boolean deleteCarById(@PathVariable(name = "id") Long id) {
        return packageService.deletePackage(id);
    }

    @PutMapping(path = "/update/{id}")
    private DtoResponsePackage updateCar(@PathVariable(name = "id") Long id, @RequestBody DtoRequestPackage dtoRequestPackage) {
        return packageService.updatePackage(id, dtoRequestPackage);
    }
}
