package com.boratok.service;

import com.boratok.dto.request.DtoRequestCar;
import com.boratok.dto.request.DtoRequestPackage;
import com.boratok.dto.response.DtoResponsePackage;
import com.boratok.entity.CargoPackage;

import java.util.List;

public interface IPackageService {
    public boolean savePackage(DtoRequestPackage dtoRequestPackage);

    public DtoResponsePackage getPackageInfoById(Long id);

    public boolean deletePackage(Long id);

    public DtoResponsePackage updatePackage(Long id, DtoRequestPackage dtoRequestPackage);

    //public List<CargoPackage> getAllPackages();
}
