package com.boratok.controller;

import com.boratok.dto.request.DtoRequestRouteAssgmnt;
import com.boratok.dto.response.DtoResponseRouteAssgmnt;
import com.boratok.service.IRouteAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/route")
public class RouteAssignmnetController {

    @Autowired
    private IRouteAssignmentService routeAssignmentService;

    @PostMapping("/save")
    private DtoResponseRouteAssgmnt saveRouteAssgmnt(@RequestBody DtoRequestRouteAssgmnt dtoRequestRouteAssgmnt) {
        return routeAssignmentService.saveRouteAssgmnt(dtoRequestRouteAssgmnt);
    }

    @GetMapping("/get/{id}")
    private DtoResponseRouteAssgmnt getRouteAssgmnt(@PathVariable(name = "id") Long id) {
        return routeAssignmentService.getRouteAssgmntById(id);
    }

    @DeleteMapping("/delete/{id}")
    private boolean deleteRouteAssgmnt(@PathVariable(name = "id") Long id) {
        return routeAssignmentService.deleteRouteAssigmnt(id);
    }

    @PutMapping("/update/{id}")
    private DtoResponseRouteAssgmnt updateRouteAssgmnt(@PathVariable(name = "id") Long id,
                                                       @RequestBody DtoRequestRouteAssgmnt dtoRequestRouteAssgmnt) {
        return routeAssignmentService.updateRouteAssigmnt(id, dtoRequestRouteAssgmnt);
    }


}
