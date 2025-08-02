package com.boratok.repository;

import com.boratok.entity.CargoPackage;
import com.boratok.entity.RouteAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteAssignmentRepository extends JpaRepository<RouteAssignment,Long> {



}
