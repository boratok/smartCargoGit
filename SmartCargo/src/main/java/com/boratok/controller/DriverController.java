package com.boratok.controller;

import com.boratok.dto.request.DtoRequestDriver;
import com.boratok.dto.response.DtoResponseDriver;
import com.boratok.service.IDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/driver")
public class DriverController {

    @Autowired
    private IDriverService driverService;

    @PostMapping(path = "/save")
    private DtoRequestDriver saveDriver(@RequestBody DtoRequestDriver dtoRequestDriver) {
        return driverService.saveDriver(dtoRequestDriver);
    }

    @GetMapping(path = "/get/{id}")
    private DtoResponseDriver getDriverById(@PathVariable(name = "id") Long id) {
        return driverService.getDriverById(id);
    }

    @DeleteMapping(path = "/delete/{id}")
    private boolean deleteDriverById(@PathVariable(name = "id") Long id) {
        return driverService.deleteDriver(id);
    }

    @PutMapping(path = "/update/{id}")
    private DtoResponseDriver updateDriverById(@PathVariable(name = "id") Long id,
                                               @RequestBody DtoRequestDriver dtoRequestDriver) {
        return driverService.updateDriver(id, dtoRequestDriver);
    }
}
