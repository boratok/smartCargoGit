package com.boratok.repository;


import com.boratok.entity.CargoPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargoPackageRepository extends JpaRepository<CargoPackage,Long> {

    @Query(value="from CargoPackage as cp where cp.id=:routeAssgmnt and cp.deleteTime is null")
    List<CargoPackage> findByRouteAssgmnt(Long routeAssgmnt);

    @Query(value="from CargoPackage")
    List<CargoPackage> findAllPackages();

    @Query(value="from CargoPackage as cp where cp.status.id=5 order by cp.createTime ASC ")
    List<CargoPackage> getCargoPackagesOrderByCreateTime();


}
