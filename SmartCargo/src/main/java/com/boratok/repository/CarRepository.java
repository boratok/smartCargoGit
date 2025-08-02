package com.boratok.repository;

import com.boratok.dto.response.DtoResponseCarWithCapacityInfo;
import com.boratok.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("from Car as c JOIN c.carType ct order by ct.capacity desc ")
    Car getMaxCarCapacity(@Param("carTypeID")Long carTypeId);


    @Query("SELECT new com.boratok.dto.response.DtoResponseCarWithCapacityInfo(c.id, ct.capacity, ct.carSize) " +
            "FROM Car c JOIN c.carType ct " +
            "WHERE c.status.id = 5 " +
            "ORDER BY ct.capacity DESC")
    List<DtoResponseCarWithCapacityInfo> avaibleCarsWithCapacity();

    @Query("from Car c where c.status.id = 5 ")
    List<Car> avaibleCars();

}

