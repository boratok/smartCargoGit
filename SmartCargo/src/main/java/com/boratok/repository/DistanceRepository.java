package com.boratok.repository;

import com.boratok.entity.Distance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DistanceRepository extends JpaRepository<Distance,Long> {

    @Query("select d.distanceBtw from Distance as d where d.startPoint.id = :stp and d.finishPoint.id = :fp")
    int getDistanceBtw(@Param("stp")Long startPoint,@Param("fp")Long finishPoint);
}
